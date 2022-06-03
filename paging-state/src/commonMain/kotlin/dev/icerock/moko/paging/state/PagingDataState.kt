/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.state

sealed interface PagingDataState<T> {
    val items: List<T>
    data class Normal<T>(override val items: List<T>) : PagingDataState<T>
    data class Refresh<T>(override val items: List<T>) : PagingDataState<T>
    data class LoadNextPage<T>(override val items: List<T>) : PagingDataState<T>
    data class End<T>(override val items: List<T>) : PagingDataState<T>

    val isNormal: Boolean get() = this is Normal<*>
    val isRefresh: Boolean get() = this is Refresh<*>
    val isLoadingNextPage: Boolean get() = this is LoadNextPage<*>
    val isEnd: Boolean get() = this is End<*>

    val isLoading: Boolean get() = !isRefresh
}
