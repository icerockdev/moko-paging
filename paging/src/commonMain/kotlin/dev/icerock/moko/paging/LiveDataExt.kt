/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.mediatorOf

fun <T> LiveData<List<T>>.withLoadingItem(
    loading: LiveData<Boolean>,
    itemFactory: () -> T
): LiveData<List<T>> = mediatorOf(this, loading) { items, nextPageLoading ->
    if (nextPageLoading) {
        items + itemFactory()
    } else {
        items
    }
}

fun <T> LiveData<List<T>>.withReachEndNotifier(
    action: (Int) -> Unit
): LiveData<ReachEndNotifierList<T>> = map { list ->
    list.withReachEndNotifier(action)
}
