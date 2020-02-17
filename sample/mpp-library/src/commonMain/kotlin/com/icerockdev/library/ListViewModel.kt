/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.mvvm.State
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.dataTransform
import dev.icerock.moko.mvvm.livedata.errorTransform
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.mergeWith
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.paging.IdComparator
import dev.icerock.moko.paging.IdEntity
import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import dev.icerock.moko.units.TableUnitItem
import kotlinx.coroutines.delay

class ListViewModel(
    private val unitsFactory: UnitsFactory
) : ViewModel() {
    private val pagination: Pagination<Product> = Pagination(
        parentScope = viewModelScope,
        dataSource = LambdaPagedListDataSource {
            delay(2000)

            it?.plus(generatePack(it.size.toLong())) ?: generatePack()
        },
        comparator = IdComparator(),
        nextPageListener = ::onNextPageResult,
        refreshListener = ::onRefreshResult,
        initValue = generatePack()
    )

    val isRefreshing: LiveData<Boolean> = pagination.refreshLoading
    val state: LiveData<State<List<TableUnitItem>, String>> = pagination.state
        .dataTransform {
            map { productList ->
                productList.map { product ->
                    unitsFactory.createProductUnit(
                        id = product.id,
                        title = product.title
                    )
                }
            }.mergeWith(pagination.nextPageLoading) { items, nextPageLoading ->
                if (nextPageLoading) {
                    items.plus(unitsFactory.createLoading())
                } else {
                    items
                }
            }
        }
        .errorTransform {
            map { it.toString() }
        }

    fun onRetryPressed() {
        pagination.loadFirstPage()
    }

    fun onLoadNextPage() {
        pagination.loadNextPage()
    }

    fun onRefresh() {
        pagination.refresh()
    }

    private fun onNextPageResult(result: Result<List<Product>>) {
        if (result.isSuccess) {
            println("next page successful loaded")
        } else {
            println("next page loading failed ${result.exceptionOrNull()}")
        }
    }

    private fun onRefreshResult(result: Result<List<Product>>) {
        if (result.isSuccess) {
            println("refresh successful")
        } else {
            println("refresh failed ${result.exceptionOrNull()}")
        }
    }

    private fun generatePack(startId: Long = 0): List<Product> {
        return List(10) { idx ->
            val id = startId + idx
            Product(
                id = id,
                title = "Product $id"
            )
        }
    }

    data class Product(
        override val id: Long,
        val title: String
    ) : IdEntity

    interface UnitsFactory {
        fun createProductUnit(id: Long, title: String): TableUnitItem
        fun createLoading(): TableUnitItem
    }
}
