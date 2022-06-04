/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.test

import dev.icerock.moko.paging.core.LambdaPagedListDataSource
import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.core.Pagination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

abstract class PaginationTest : BaseTestsClass() {

    abstract fun createPagination(
        parentScope: CoroutineScope,
        dataSource: PagedListDataSource<Int> = paginationDataSource,
        comparator: Comparator<Int> = itemsComparator,
        nextPageListener: (Result<List<Int>>) -> Unit = {},
        refreshListener: (Result<List<Int>>) -> Unit = {},
    ): Pagination<Int>

    abstract fun isSuccessState(pagination: Pagination<Int>): Boolean

    private var paginationDataSource = TestListDataSource(3, 5)

    private val itemsComparator = Comparator { a: Int, b: Int ->
        a - b
    }

    @BeforeTest
    open fun setup() {
        paginationDataSource = TestListDataSource(3, 5)
    }

    @Test
    open fun `load first page`() = runTest {
        val pagination = createPagination(this)

        pagination.loadFirstPageSuspend()

        assertTrue {
            isSuccessState(pagination)
        }
        assertTrue {
            pagination.dataValue()!!.compareWith(listOf(0, 1, 2))
        }
    }

    @Test
    open fun `load next page`() = runTest {
        val pagination = createPagination(this)

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()

        assertTrue {
            pagination.dataValue()!!.compareWith(listOf(0, 1, 2, 3, 4, 5))
        }

        pagination.loadNextPageSuspend()

        assertTrue {
            pagination.dataValue()!!.compareWith(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
        }
    }

    @Test
    open fun `refresh pagination`() = runTest {
        val pagination = createPagination(this)

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()
        pagination.refreshSuspend()

        assertTrue {
            pagination.dataValue()!!.compareWith(listOf(0, 1, 2))
        }
    }

    @Test
    open fun `set data`() = runTest {
        val pagination = createPagination(this)

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()

        val setList = listOf(5, 2, 3, 1, 4)
        pagination.setDataSuspend(setList)

        assertTrue {
            pagination.dataValue()!!.compareWith(setList)
        }
    }

    @Test
    open fun `double refresh`() = runTest {
        var counter = 0
        val pagination = createPagination(
            this,
            dataSource = LambdaPagedListDataSource {
                val load = counter++
                println("start load new page with $it")
                delay(100)
                println("respond new list $load")
                listOf(1, 2, 3, 4)
            }
        )

        println("start load first page")
        pagination.loadFirstPageSuspend()
        println("end load first page")

        println("start double refresh")
        val r1 = async {
            pagination.refreshSuspend()
            println("first refresh end")
        }
        val r2 = async {
            pagination.refreshSuspend()
            println("second refresh end")
        }

        r1.await()
        r2.await()
    }
}
