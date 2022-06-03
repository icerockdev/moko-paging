/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.livedata

import dev.icerock.moko.mvvm.ResourceState
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.asLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.core.Pagination
import kotlinx.coroutines.CoroutineScope

class Pagination<Item>(
    private val parentScope: CoroutineScope,
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

    private val _state: MutableLiveData<ResourceState<List<Item>, Throwable>> =
        MutableLiveData(initValue.asStateNullIsLoading())

    val state: LiveData<ResourceState<List<Item>, Throwable>>
        get() = _state.readOnly()

    val refreshLoading: LiveData<Boolean>
        get() = mRefreshLoading.asLiveData(parentScope)

    val nextPageLoading: LiveData<Boolean>
        get() = mNextPageLoading.asLiveData(parentScope)

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
        _state.value = ResourceState.Failed(error)
    }

    override fun setupLoadingState() {
        _state.value = ResourceState.Loading()
    }
}

fun <T, E> List<T>?.asStateNullIsEmpty() = asState {
    ResourceState.Empty<List<T>, E>()
}

fun <T, E> List<T>?.asStateNullIsLoading() = asState {
    ResourceState.Loading<List<T>, E>()
}
