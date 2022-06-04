/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.sample.declarativeui

import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.paging.core.IdComparator
import dev.icerock.moko.paging.core.LambdaPagedListDataSource
import dev.icerock.moko.paging.flow.Pagination
import dev.icerock.moko.paging.state.ResourceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

private const val PAGE_LOAD_DURATION_MS: Long = 2000

class ListViewModel(
    withInitialValue: Boolean = false,
    withError: Boolean = false
) : ViewModel() {
    private val pagination: Pagination<Product> = Pagination(
        parentScope = viewModelScope,
        dataSource = LambdaPagedListDataSource {
            delay(PAGE_LOAD_DURATION_MS)
            if (withError) throw IllegalStateException()
            it?.plus(generatePack(it.size.toLong())) ?: generatePack()
        },
        comparator = IdComparator(),
        nextPageListener = ::onNextPageResult,
        refreshListener = ::onRefreshResult,
        initValue = if (withInitialValue) generatePack() else null
    )

    fun start() {
        pagination.loadFirstPage()
    }

    val isRefreshing: CStateFlow<Boolean> get() = pagination.refreshLoading

    val state: CStateFlow<ResourceState<List<ListUnit>, String>> = combine(
        pagination.state,
        pagination.nextPageLoading
    )  { state, nextPageLoading ->
        return@combine when (state) {
            is ResourceState.Data -> {
                if (state.data.isEmpty()) return@combine ResourceState.Empty

                val data: List<ListUnit> = buildList {
                    state.data.map {
                        ListUnit.ProductUnit(id = it.id, title = it.title)
                    }.let { addAll(it) }
                    if (nextPageLoading) add(ListUnit.Loading)
                }

                ResourceState.Data(data)
            }
            is ResourceState.Error -> ResourceState.Error(state.error.toString())
            ResourceState.Empty -> ResourceState.Empty
            ResourceState.Loading -> ResourceState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResourceState.Loading).cStateFlow()

    fun onRetryPressed() {
        pagination.loadFirstPage()
    }

    fun onLoadNextPage() {
        pagination.loadNextPage()
    }

    fun onRefresh() {
        pagination.refresh()
    }

    suspend fun onRefreshSuspend() {
        withContext(viewModelScope.coroutineContext) {
            pagination.refreshSuspend()
        }
    }

    private fun onNextPageResult(result: Result<List<Product>>) {
        if (result.isSuccess) {
            println("next page successful loaded")
        } else {
            println("next page loading failed ${result.exceptionOrNull()}")
        }
    }

    private fun onRefreshResult(result: Result<List<Product>>) {
        if (result.isSuccess) {
            println("refresh successful")
        } else {
            println("refresh failed ${result.exceptionOrNull()}")
        }
    }

    @Suppress("MagicNumber")
    private fun generatePack(startId: Long = 0): List<Product> {
        return List(20) { idx ->
            val id = startId + idx
            Product(
                id = id,
                title = "Product $id"
            )
        }
    }
}
