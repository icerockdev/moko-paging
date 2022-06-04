/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
@file:Suppress("Filename")

package dev.icerock.moko.paging

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("Pagination", "dev.icerock.moko.paging.livedata"),
    level = DeprecationLevel.WARNING
)
typealias Pagination<Item> = dev.icerock.moko.paging.livedata.Pagination<Item>
