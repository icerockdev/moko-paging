/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import dev.icerock.moko.mvvm.ResourceState
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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
        MutableLiveData<ResourceState<List<Item>, Throwable>>(initValue.asStateNullIsEmpty())

    val state = mStateStorage.readOnly()

    private val mNextPageLoading = MutableLiveData(false)
    val nextPageLoading = mNextPageLoading.readOnly()

    private val mEndOfList = MutableLiveData(false)

    private val mRefreshLoading = MutableLiveData(false)
    val refreshLoading = mRefreshLoading.readOnly()

    private val listMutex = Mutex()

    fun loadFirstPage() {
        launch {
            loadFirstPageSuspend()
        }
    }

    suspend fun loadFirstPageSuspend() {
        listMutex.lock()

        mEndOfList.value = false
        mNextPageLoading.value = false
        mStateStorage.value = ResourceState.Loading()

        @Suppress("TooGenericExceptionCaught")
        try {
            val items: List<Item> = dataSource.loadPage(null)
            mStateStorage.value = items.asState()
        } catch (error: Throwable) {
            mStateStorage.value = ResourceState.Failed(error)
        }
        listMutex.unlock()
    }

    fun loadNextPage() {
        launch {
            loadNextPageSuspend()
        }
    }

    @Suppress("ReturnCount")
    suspend fun loadNextPageSuspend() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return
        if (mEndOfList.value) return

        val currentList = mStateStorage.value.dataValue()
            ?: throw IllegalStateException("Try to load next page when list is empty")

        listMutex.lock()

        mNextPageLoading.value = true

        @Suppress("TooGenericExceptionCaught")
        try {
            // load next page items
            val items = dataSource.loadPage(currentList)
            // remove already exist items
            val newItems = items.filter { item ->
                val existsItem = currentList.firstOrNull { comparator.compare(item, it) == 0 }
                existsItem == null
            }
            // append new items to current list
            val newList = currentList.plus(newItems)
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
        listMutex.unlock()
    }

    fun refresh() {
        launch {
            refreshSuspend()
        }
    }

    suspend fun refreshSuspend() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return

        listMutex.lock()

        mRefreshLoading.value = true

        @Suppress("TooGenericExceptionCaught")
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
            mStateStorage.value = ResourceState.Failed(error)
            // flag
            mRefreshLoading.value = false
            // notify
            refreshListener(Result.failure(error))
        }
        listMutex.unlock()
    }

    fun setData(items: List<Item>?) {
        launch {
            setDataSuspend(items)
        }
    }

    suspend fun setDataSuspend(items: List<Item>?) {
        listMutex.lock()
        mStateStorage.value = items.asStateNullIsEmpty()
        mEndOfList.value = false
        listMutex.unlock()
    }
}

fun <T, E> T?.asStateNullIsEmpty() = asState {
    ResourceState.Loading<T, E>()
}

interface IdEntity {
    val id: Long
}

class IdComparator<T : IdEntity> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return if (a.id == b.id) 0 else 1
    }
}
