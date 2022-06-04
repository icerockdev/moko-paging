/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.core

interface IdEntity {
    val id: Long
}

class IdComparator<T : IdEntity> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return if (a.id == b.id) 0 else 1
    }
}
