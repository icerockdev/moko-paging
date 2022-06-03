package dev.icerock.moko.paging.test

import kotlinx.coroutines.CoroutineScope

actual fun <T> runTest(block: suspend CoroutineScope.() -> T): T =
    kotlinx.coroutines.runBlocking(block = block)
