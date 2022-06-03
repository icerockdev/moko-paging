/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.flow

import dev.icerock.moko.paging.core.ReachEndNotifierList
import dev.icerock.moko.paging.core.withReachEndNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<List<T>>.withLoadingItem(
    loading: Flow<Boolean>,
    itemFactory: () -> T
): Flow<List<T>> = combine(this, loading) { items, nextPageLoading ->
    if (nextPageLoading) {
        items + itemFactory()
    } else {
        items
    }
}

fun <T> Flow<List<T>>.withReachEndNotifier(
    action: (Int) -> Unit
): Flow<ReachEndNotifierList<T>> = map { list ->
    list.withReachEndNotifier(action)
}

fun <T> Flow<List<T>>.stateWithLoadingItem(
    parentScope: CoroutineScope,
    loading: Flow<Boolean>,
    itemFactory: () -> T
): StateFlow<List<T>> = this.withLoadingItem(
    loading = loading,
    itemFactory = itemFactory
).stateIn(parentScope, SharingStarted.Lazily, emptyList())

fun <T> Flow<List<T>>.stateWithReachEndNotifier(
    parentScope: CoroutineScope,
    action: (Int) -> Unit
): StateFlow<ReachEndNotifierList<T>> =
    this.withReachEndNotifier(action)
        .stateIn(
            parentScope,
            SharingStarted.Lazily,
            ReachEndNotifierList(emptyList(), action)
        )
