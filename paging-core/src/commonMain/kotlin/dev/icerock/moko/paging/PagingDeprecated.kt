/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("IdEntity", "dev.icerock.moko.paging.core"),
    level = DeprecationLevel.WARNING
)
typealias IdEntity = dev.icerock.moko.paging.core.IdEntity

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("IdComparator", "dev.icerock.moko.paging.core"),
    level = DeprecationLevel.WARNING
)
typealias IdComparator<T> = dev.icerock.moko.paging.core.IdComparator<T>

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("LambdaPagedListDataSource", "dev.icerock.moko.paging.core"),
    level = DeprecationLevel.WARNING
)
typealias LambdaPagedListDataSource<T> = dev.icerock.moko.paging.core.LambdaPagedListDataSource<T>

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("PagedListDataSource", "dev.icerock.moko.paging.core"),
    level = DeprecationLevel.WARNING
)
typealias PagedListDataSource<T> = dev.icerock.moko.paging.core.PagedListDataSource<T>

@Deprecated(
    message = "deprecated due to package renaming",
    replaceWith = ReplaceWith("ReachEndNotifierList", "dev.icerock.moko.paging.core"),
    level = DeprecationLevel.WARNING
)
typealias ReachEndNotifierList<T> = dev.icerock.moko.paging.core.ReachEndNotifierList<T>
