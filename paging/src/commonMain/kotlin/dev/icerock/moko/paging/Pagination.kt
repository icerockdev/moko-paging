/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import dev.icerock.moko.mvvm.State
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Pagination<Item>(
    parentScope: CoroutineScope,
    private val dataSource: PagedListDataSource<Item>,
    private val comparator: Comparator<Item>,
    private val nextPageListener: (Result<List<Item>>) -> Unit,
    private val refreshListener: (Result<List<Item>>) -> Unit,
    initValue: List<Item>? = null
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = parentScope.coroutineContext

    private val mStateStorage =
        MutableLiveData<State<List<Item>, Throwable>>(initValue.asStateNullIsEmpty())

    val state = mStateStorage.readOnly()

    private val mNextPageLoading = MutableLiveData(false)
    val nextPageLoading = mNextPageLoading.readOnly()

    private val mEndOfList = MutableLiveData(false)

    private val mRefreshLoading = MutableLiveData(false)
    val refreshLoading = mRefreshLoading.readOnly()

    fun loadFirstPage() {
        mEndOfList.value = false
        mNextPageLoading.value = false

        launch {
            try {
                val items: List<Item> = dataSource.loadPage(null)
                mStateStorage.value = items.asState()
            } catch (error: Throwable) {
                mStateStorage.value = State.Error(error)
            }
        }
    }

    fun loadNextPage() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return
        if (mEndOfList.value) return

        val currentList = mStateStorage.value.dataValue()
            ?: throw IllegalStateException("Try to load next page when list is empty")

        mNextPageLoading.postValue(true)

        launch {
            try {
                // load next page items
                val items = dataSource.loadPage(currentList)
                // remove already exist items
                val newItems = items.filter { item ->
                    val existsItem = currentList.firstOrNull { comparator.compare(item, it) == 0 }
                    existsItem == null
                }
                // append new items to current list
                val newList = newItems.plus(currentList)
                // mark end of list if no new items
                if (newItems.isEmpty()) {
                    mEndOfList.value = true
                } else {
                    // save
                    mStateStorage.value = newList.asState()
                }
                // flag
                mNextPageLoading.value = false
                // notify
                nextPageListener(Result.success(newList))
            } catch (error: Throwable) {
                // flag
                mNextPageLoading.value = false
                // notify
                nextPageListener(Result.failure(error))
            }
        }
    }

    fun refresh() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return

        mRefreshLoading.postValue(true)

        launch {
            try {
                // load first page items
                val items = dataSource.loadPage(null)
                // save
                mStateStorage.value = items.asState()
                // flag
                mEndOfList.value = false
                mRefreshLoading.value = false
                // notify
                refreshListener(Result.success(items))
            } catch (error: Throwable) {
                mStateStorage.value = State.Error(error)
                // flag
                mRefreshLoading.value = false
                // notify
                refreshListener(Result.failure(error))
            }
        }
    }

    fun setData(items: List<Item>?) {
        mStateStorage.value = items.asStateNullIsEmpty()
        mEndOfList.value = false
    }
}

fun <T, E> T?.asStateNullIsEmpty() = asState {
    State.Loading<T, E>()
}

interface IdEntity {
    val id: Long
}

class IdComparator<T : IdEntity> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return if (a.id == b.id) 0 else 1
    }
}