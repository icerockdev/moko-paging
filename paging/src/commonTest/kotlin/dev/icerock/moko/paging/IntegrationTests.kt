/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IntegrationTests : BaseTestsClass() {
    private val httpClient = HttpClient()

    @Test
    fun parallelRequests() = runTest {
        val pagination = Pagination<String>(
            parentScope = this,
            dataSource = LambdaPagedListDataSource {
                println("start load new page with $it")
                val randomJoke: String = httpClient.get("http://api.icndb.com/jokes/random")
                println("respond new item $randomJoke")
                listOf(randomJoke)
            },
            comparator = Comparator { a, b -> a.compareTo(b) },
            nextPageListener = { },
            refreshListener = { }
        )

        for (i in 0..10) {
            println("--- ITERATION $i START ---")
            println("start load first page")
            pagination.loadFirstPageSuspend()
            println("end load first page")

            (0..3).flatMap {
                listOf(
                    async {
                        println("--> $it refresh start")
                        pagination.refreshSuspend()
                        println("<-- $it refresh end")
                    },
                    async {
                        println("--> $it load next page start")
                        pagination.loadNextPageSuspend()
                        println("<-- $it load next page end")
                    }
                )
            }.forEach { it.await() }
        }
    }

    @Test
    fun parallelRequestsAndSetData() = runTest {
        val pagination = Pagination<String>(
            parentScope = this,
            dataSource = LambdaPagedListDataSource {
                println("start load new page with $it")
                val randomJoke: String = httpClient.get("http://api.icndb.com/jokes/random")
                println("respond new item $randomJoke")
                listOf(randomJoke)
            },
            comparator = Comparator { a, b -> a.compareTo(b) },
            nextPageListener = { },
            refreshListener = { }
        )

        for (i in 0..10) {
            println("--- ITERATION $i START ---")
            println("start load first page")
            pagination.loadFirstPageSuspend()
            println("end load first page")

            (0..1).flatMap {
                listOf(
                    async {
                        println("--> $it refresh start")
                        pagination.refreshSuspend()
                        println("<-- $it refresh end")
                    },
                    async {
                        println("--> $it load next page start")
                        pagination.loadNextPageSuspend()
                        println("<-- $it load next page end")
                    },
                    async {
                        println("--> $it set data start")
                        val data = pagination.state.value.dataValue().orEmpty()
                        val newData = data.plus("new item")
                        pagination.setDataSuspend(newData)
                        println("--> $it set data end")
                    }
                )
            }.forEach { it.await() }
        }
    }

    @Test
    fun closingScope() = runTest {
        val exc = runCatching {
            coroutineScope {
                val pagination = Pagination<String>(
                    parentScope = this,
                    dataSource = LambdaPagedListDataSource {
                        println("start load new page with $it")
                        val randomJoke: String = httpClient.get("http://api.icndb.com/jokes/random")
                        println("respond new item $randomJoke")
                        listOf(randomJoke)
                    },
                    comparator = Comparator { a, b -> a.compareTo(b) },
                    nextPageListener = { },
                    refreshListener = { }
                )

                launch {
                    println("start load")
                    pagination.loadFirstPageSuspend()
                    println("end load")
                }

                delay(50)
                println("cancel scope")
                cancel()
            }
        }

        println(exc)
    }
}