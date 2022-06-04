/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.sample.declarativeui

import dev.icerock.moko.paging.core.IdEntity

data class Product(
    override val id: Long,
    val title: String
) : IdEntity