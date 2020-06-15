/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import kotlinx.coroutines.CoroutineScope
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PaginationTest : BaseTestsClass() {

    var paginationDataSource = TestListDataSource(3, 5)

    val itemsComparator = Comparator { a: Int, b: Int ->
        a - b
    }

    @BeforeTest
    fun setup() {
        paginationDataSource = TestListDataSource(3, 5)
    }

    @Test
    fun `load first page`() = runTest {
        val pagination = createPagination()

        pagination.loadFirstPageSuspend()

        assertTrue {
            pagination.state.value.isSuccess()
        }
        assertTrue {
            pagination.state.value.dataValue()!!.compareWith(listOf(0, 1, 2))
        }
    }

    @Test
    fun `load next page`() = runTest {
        val pagination = createPagination()

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()

        assertTrue {
            pagination.state.value.dataValue()!!.compareWith(listOf(0, 1, 2, 3, 4, 5))
        }

        pagination.loadNextPageSuspend()

        assertTrue {
            pagination.state.value.dataValue()!!.compareWith(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
        }
    }

    @Test
    fun `refresh pagination`() = runTest {
        val pagination = createPagination()

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()
        pagination.refreshSuspend()

        assertTrue {
            pagination.state.value.dataValue()!!.compareWith(listOf(0, 1, 2))
        }
    }

    @Test
    fun `set data`() = runTest {

        val pagination = createPagination()

        pagination.loadFirstPageSuspend()
        pagination.loadNextPageSuspend()

        val setList = listOf(5, 2, 3, 1, 4)
        pagination.setDataSuspend(setList)

        assertTrue {
            pagination.state.value.dataValue()!!.compareWith(setList)
        }
    }

    private fun CoroutineScope.createPagination(
        nextPageListener: (Result<List<Int>>) -> Unit = {},
        refreshListener: (Result<List<Int>>) -> Unit = {}
    ) = Pagination<Int>(
        parentScope = this,
        dataSource = paginationDataSource,
        comparator = itemsComparator,
        nextPageListener = nextPageListener,
        refreshListener = refreshListener
    )
}
