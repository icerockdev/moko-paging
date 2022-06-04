/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.sample.declarativeui

sealed interface ListUnit {
    data class ProductUnit(val id: Long, val title: String) : ListUnit
    object Loading : ListUnit
}