/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.core

interface PagedListDataSource<T> {
    suspend fun loadPage(currentList: List<T>?): List<T>
}
