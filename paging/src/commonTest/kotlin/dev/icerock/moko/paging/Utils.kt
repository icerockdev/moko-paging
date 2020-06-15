/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import kotlinx.coroutines.CoroutineScope

expect fun <T> runTest(block: suspend CoroutineScope.() -> T): T

fun <T> List<T>.compareWith(list: List<T>): Boolean {
    if(size != list.size) return false

    return zip(list).all { (item1, item2) ->
        item1 == item2
    }
}
