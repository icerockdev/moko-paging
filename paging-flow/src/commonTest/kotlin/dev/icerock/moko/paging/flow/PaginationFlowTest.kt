/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.flow

import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.state.ResourceState
import dev.icerock.moko.paging.test.PaginationTest
import kotlinx.coroutines.CoroutineScope
import kotlin.test.BeforeTest
import kotlin.test.Test

class PaginationFlowTest: PaginationTest() {

    override fun createPagination(
        parentScope: CoroutineScope,
        dataSource: PagedListDataSource<Int>,
        comparator: Comparator<Int>,
        nextPageListener: (Result<List<Int>>) -> Unit,
        refreshListener: (Result<List<Int>>) -> Unit
    ): Pagination<Int> {
        return Pagination(
            parentScope = parentScope,
            dataSource = dataSource,
            comparator = comparator,
            nextPageListener = nextPageListener,
            refreshListener = refreshListener
        )
    }

    override fun isSuccessState(pagination: dev.icerock.moko.paging.core.Pagination<Int>): Boolean {
        return (pagination as Pagination).state.value is ResourceState.Data<*>
    }

    @BeforeTest
    override fun setup() = super.setup()

    @Test
    override fun `load first page`() = super.`load first page`()

    @Test
    override fun `load next page`() = super.`load next page`()

    @Test
    override fun `refresh pagination`() = super.`refresh pagination`()

    @Test
    override fun `set data`() = super.`set data`()

    @Test
    override fun `double refresh`() = super.`double refresh`()
}
