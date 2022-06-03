/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.livedata

import dev.icerock.moko.mvvm.ResourceState
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.core.Pagination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class Pagination<Item>(
    parentScope: CoroutineScope,
    dataSource: PagedListDataSource<Item>,
    comparator: Comparator<Item>,
    nextPageListener: (Result<List<Item>>) -> Unit,
    refreshListener: (Result<List<Item>>) -> Unit,
    initValue: List<Item>? = null
) : Pagination<Item>(
    parentScope = parentScope,
    dataSource = dataSource,
    comparator = comparator,
    nextPageListener = nextPageListener,
    refreshListener = refreshListener,
) {

    override val coroutineContext: CoroutineContext = parentScope.coroutineContext

    private val mStateStorage =
        MutableLiveData<ResourceState<List<Item>, Throwable>>(initValue.asStateNullIsLoading())

    val state = mStateStorage.readOnly()

    private val mNextPageLoading = MutableLiveData(false)
    val nextPageLoading = mNextPageLoading.readOnly()

    private val mEndOfList = MutableLiveData(false)

    private val mRefreshLoading = MutableLiveData(false)
    val refreshLoading = mRefreshLoading.readOnly()

    override suspend fun loadFirstPageSuspend() {
        loadNextPageDeferred?.cancel()

        listMutex.lock()

        mEndOfList.value = false
        mNextPageLoading.value = false
        mStateStorage.value = ResourceState.Loading()

        @Suppress("TooGenericExceptionCaught")
        try {
            val items: List<Item> = dataSource.loadPage(null)
            mStateStorage.value = items.asState()
        } catch (error: Exception) {
            mStateStorage.value = ResourceState.Failed(error)
        }
        listMutex.unlock()
    }

    @Suppress("ReturnCount")
    override suspend fun loadNextPageSuspend() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return
        if (mEndOfList.value) return

        listMutex.lock()

        mNextPageLoading.value = true

        @Suppress("TooGenericExceptionCaught")
        try {
            loadNextPageDeferred = this.async {
                val currentList = mStateStorage.value.dataValue()
                    ?: throw IllegalStateException("Try to load next page when list is empty")
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
                newList
            }
            val newList = loadNextPageDeferred!!.await()

            // flag
            mNextPageLoading.value = false
            // notify
            nextPageListener(Result.success(newList))
        } catch (error: Exception) {
            // flag
            mNextPageLoading.value = false
            // notify
            nextPageListener(Result.failure(error))
        }
        listMutex.unlock()
    }

    override suspend fun refreshSuspend() {
        loadNextPageDeferred?.cancel()
        listMutex.lock()

        if (mRefreshLoading.value) {
            listMutex.unlock()
            return
        }
        if (mNextPageLoading.value) {
            listMutex.unlock()
            return
        }

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
        } catch (error: Exception) {
            // flag
            mRefreshLoading.value = false
            // notify
            refreshListener(Result.failure(error))
        }
        listMutex.unlock()
    }

    override suspend fun setDataSuspend(items: List<Item>?) {
        listMutex.lock()
        mStateStorage.value = items.asStateNullIsEmpty()
        mEndOfList.value = false
        listMutex.unlock()
    }
}

fun <T, E> List<T>?.asStateNullIsEmpty() = asState {
    ResourceState.Empty<List<T>, E>()
}

fun <T, E> List<T>?.asStateNullIsLoading() = asState {
    ResourceState.Loading<List<T>, E>()
}
