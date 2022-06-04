/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
@file:Suppress("Indentation")

package dev.icerock.moko.paging.test

import dev.icerock.moko.paging.core.LambdaPagedListDataSource
import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.core.Pagination
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.fullPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test

abstract class IntegrationTests : BaseTestsClass() {

    abstract fun createPagination(
        parentScope: CoroutineScope,
        dataSource: PagedListDataSource<String> = paginationDataSource,
        comparator: Comparator<String> = itemsComparator,
        nextPageListener: (Result<List<String>>) -> Unit = {},
        refreshListener: (Result<List<String>>) -> Unit = {},
    ): Pagination<String>

    private val paginationDataSource: LambdaPagedListDataSource<String> =
        LambdaPagedListDataSource<String> {
            println("start load new page with $it")
            val randomJoke: String = httpClient
                .get(API_URL)
                .bodyAsText()
            println("respond new item $randomJoke")
            listOf(randomJoke)
        }

    private val itemsComparator = Comparator { a: String, b: String ->
        a.compareTo(b)
    }

    @Suppress("MaxLineLength")
    private val httpClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                when (request.url.toString()) {
                    API_URL -> {
                        delay(200)
                        respondOk(
                            """
                            {
                              "type": "success",
                              "value": {
                                "id": 318,
                                "joke": "If you work in an office with Chuck Norris, don't ask him for his three-hole-punch.",
                                "categories": []
                              }
                            }
                        """.trimIndent()
                        )
                    }
                    else -> error("Unhandled ${request.url.fullPath}")
                }
            }
        }
    }

    @Test
    open fun parallelRequests() = runTest {
        val pagination: Pagination<String> = createPagination(this)

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
    open fun parallelRequestsAndSetData() = runTest {
        val pagination = createPagination(this)

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
                        val data = pagination.dataValue().orEmpty()
                        val newData = data.plus("new item")
                        pagination.setDataSuspend(newData)
                        println("--> $it set data end")
                    }
                )
            }.forEach { it.await() }
        }
    }

    @Test
    open fun closingScope() = runTest {
        val exc = runCatching {
            coroutineScope {
                val pagination = createPagination(this)

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

    companion object {
        const val API_URL = "http://api.icndb.com/jokes/random"
    }
}
