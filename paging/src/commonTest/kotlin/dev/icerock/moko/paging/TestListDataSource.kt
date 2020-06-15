/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

class TestListDataSource(val pageSize: Int, val totalPagesCount: Int) : PagedListDataSource<Int> {
    val dataList = (0 .. pageSize * totalPagesCount).map { it }

    override suspend fun loadPage(currentList: List<Int>?): List<Int> {
        val offset = currentList?.size ?: 0
        return dataList.subList(offset, offset + pageSize)
    }
}
