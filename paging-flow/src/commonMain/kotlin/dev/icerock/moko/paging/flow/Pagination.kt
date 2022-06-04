/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.flow

import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.core.Pagination
import dev.icerock.moko.paging.state.ResourceState
import dev.icerock.moko.paging.state.asState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

typealias PaginationState<Item> = ResourceState<List<Item>, Throwable>

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

    private val _state: MutableStateFlow<PaginationState<Item>> =
        MutableStateFlow(initValue.asStateNullIsLoading())

    val state: CStateFlow<ResourceState<List<Item>, Throwable>>
        get() = _state.cStateFlow()

    val refreshLoading: CStateFlow<Boolean>
        get() = mRefreshLoading.cStateFlow()

    val nextPageLoading: CStateFlow<Boolean>
        get() = mNextPageLoading.cStateFlow()

    override fun dataValue(): List<Item>? =
        _state.value.dataValue()

    override fun saveState(items: List<Item>) {
        _state.value = items.asState()
    }

    override fun saveStateNullIsEmpty(items: List<Item>?) {
        _state.value = items.asStateNullIsEmpty()
    }

    override fun saveStateNullIsLoading(items: List<Item>?) {
        _state.value = items.asStateNullIsLoading()
    }

    override fun saveErrorState(error: Exception) {
        _state.value = error.asState()
    }

    override fun setupLoadingState() {
        _state.value = ResourceState.Loading
    }
}

fun <T, E> List<T>?.asStateNullIsEmpty(): ResourceState<List<T>, E> = asState {
    ResourceState.Empty
}

fun <T, E> List<T>?.asStateNullIsLoading(): ResourceState<List<T>, E> = asState {
    ResourceState.Loading
}
