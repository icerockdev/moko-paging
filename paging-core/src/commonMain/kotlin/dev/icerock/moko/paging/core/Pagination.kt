/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext

abstract class Pagination<Item>(
    parentScope: CoroutineScope,
    private val dataSource: PagedListDataSource<Item>,
    private val comparator: Comparator<Item>,
    private val nextPageListener: (Result<List<Item>>) -> Unit,
    private val refreshListener: (Result<List<Item>>) -> Unit,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = parentScope.coroutineContext

    protected val mNextPageLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected val mRefreshLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val mEndOfList: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var loadNextPageDeferred: Deferred<List<Item>>? = null
    private val listMutex = Mutex()

    fun loadFirstPage() {
        launch { loadFirstPageSuspend() }
    }

    fun loadNextPage() {
        launch { loadNextPageSuspend() }
    }

    fun refresh() {
        launch { refreshSuspend() }
    }

    fun setData(items: List<Item>?) {
        launch { setDataSuspend(items) }
    }

    protected abstract fun dataValue(): List<Item>?
    protected abstract fun saveState(items: List<Item>)
    protected abstract fun saveStateNullIsEmpty(items: List<Item>?)
    protected abstract fun saveStateNullIsLoading(items: List<Item>?)
    protected abstract fun saveErrorState(error: Exception)
    protected abstract fun setupLoadingState()

    suspend fun loadFirstPageSuspend() {
        loadNextPageDeferred?.cancel()

        listMutex.lock()

        mEndOfList.value = false
        mNextPageLoading.value = false
        setupLoadingState()

        @Suppress("TooGenericExceptionCaught")
        try {
            val items: List<Item> = dataSource.loadPage(null)
            saveState(items)
        } catch (error: Exception) {
            saveErrorState(error)
        }
        listMutex.unlock()
    }

    @Suppress("ReturnCount")
    suspend fun loadNextPageSuspend() {
        if (mNextPageLoading.value) return
        if (mRefreshLoading.value) return
        if (mEndOfList.value) return

        listMutex.lock()

        mNextPageLoading.value = true

        @Suppress("TooGenericExceptionCaught")
        try {
            loadNextPageDeferred = this.async {
                val currentList = dataValue()
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
                    saveState(newList)
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

    suspend fun refreshSuspend() {
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
            saveState(items)
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

    suspend fun setDataSuspend(items: List<Item>?) {
        listMutex.lock()
        saveStateNullIsEmpty(items)
        mEndOfList.value = false
        listMutex.unlock()
    }
}


