package dev.icerock.moko.paging.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext

abstract class Pagination<Item>(
    parentScope: CoroutineScope,
    protected val dataSource: PagedListDataSource<Item>,
    protected val comparator: Comparator<Item>,
    protected val nextPageListener: (Result<List<Item>>) -> Unit,
    protected val refreshListener: (Result<List<Item>>) -> Unit,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = parentScope.coroutineContext

    protected var loadNextPageDeferred: Deferred<List<Item>>? = null
    protected val listMutex = Mutex()

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

    abstract suspend fun loadFirstPageSuspend()
    abstract suspend fun loadNextPageSuspend()
    abstract suspend fun refreshSuspend()
    abstract suspend fun setDataSuspend(items: List<Item>?)
}
