/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import dev.icerock.moko.units.TableUnitItem

class LoadingUnitItem : TableUnitItem {
    override val itemId: Long = -2 // we have only one loading in list
    override val viewType: Int = R.layout.unit_loading

    override fun createViewHolder(
        parent: ViewGroup,
        lifecycleOwner: LifecycleOwner
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val loadingView = inflater.inflate(R.layout.unit_loading, parent, false)
        return object : RecyclerView.ViewHolder(loadingView) {}
    }

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder) {
        // do nothing - it's just loading item
    }
}