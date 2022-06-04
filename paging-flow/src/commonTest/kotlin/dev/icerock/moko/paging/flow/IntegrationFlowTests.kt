/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.flow

import dev.icerock.moko.paging.core.PagedListDataSource
import dev.icerock.moko.paging.test.IntegrationTests
import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test

class IntegrationFlowTests: IntegrationTests() {

    override fun createPagination(
        parentScope: CoroutineScope,
        dataSource: PagedListDataSource<String>,
        comparator: Comparator<String>,
        nextPageListener: (Result<List<String>>) -> Unit,
        refreshListener: (Result<List<String>>) -> Unit
    ): Pagination<String> {
        return Pagination(
            parentScope = parentScope,
            dataSource = dataSource,
            comparator = comparator,
            nextPageListener = nextPageListener,
            refreshListener = refreshListener
        )
    }

    @Test
    override fun parallelRequests() = super.parallelRequests()

    @Test
    override fun parallelRequestsAndSetData() = super.parallelRequestsAndSetData()

    @Test
    override fun closingScope() = super.closingScope()
}
