/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.state

typealias ResourceStateThrow<T> = ResourceState<T, Throwable>

sealed interface ResourceState<out T, out E> {
    object Empty : ResourceState<Nothing, Nothing>
    object Loading : ResourceState<Nothing, Nothing>
    data class Data<T>(val data: T) : ResourceState<T, Nothing>
    data class Error<E>(val error: E) : ResourceState<Nothing, E>

    fun dataValue(): T? = (this as? Data<T>)?.data

    val isEmpty: Boolean get() = this is Empty
    val isLoading: Boolean get() = this is Loading
    val isError: Boolean get() = this is Error<*>
    val isData: Boolean get() = this is Data<*>
}
