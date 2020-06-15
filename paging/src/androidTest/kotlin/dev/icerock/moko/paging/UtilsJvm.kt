/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import kotlinx.coroutines.CoroutineScope

actual fun <T> runTest(block: suspend CoroutineScope.() -> T): T =
    kotlinx.coroutines.runBlocking(block = block)
