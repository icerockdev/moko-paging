/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.http.fullPath
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test

class IntegrationTests : BaseTestsClass() {
    private val httpClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                when (request.url.fullPath) {
                    "http://api.icndb.com/jokes/random" -> {
                        delay(200)
                        respondOk("""
                            {
                              "type": "success",
                              "value": {
                                "id": 318,
                                "joke": "If you work in an office with Chuck Norris, don't ask him for his three-hole-punch.",
                                "categories": []
                              }
                            }
                        """.trimIndent())
                    }
                    else -> error("Unhandled ${request.url.fullPath}")
                }
            }
        }
    }

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