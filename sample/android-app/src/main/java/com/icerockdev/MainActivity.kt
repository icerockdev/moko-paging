/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.icerockdev.library.ListViewModel
import dev.icerock.moko.mvvm.getViewModel
import dev.icerock.moko.mvvm.livedata.data
import dev.icerock.moko.units.TableUnitItem
import dev.icerock.moko.units.adapter.UnitsRecyclerViewAdapter

class MainActivity : AppCompatActivity() {

    private val unitsFactory = object : ListViewModel.UnitsFactory {
        override fun createProductUnit(id: Long, title: String): TableUnitItem {
            // databinding generated unit
            return UnitProduct().also {
                it.itemId = id
                it.title = title
            }
        }

        override fun createLoading(): TableUnitItem {
            // manual created unit
            return LoadingUnitItem()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val viewModel = getViewModel { ListViewModel(unitsFactory) }
        val unitsAdapter = UnitsRecyclerViewAdapter(this)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = unitsAdapter
        }

        viewModel.isRefreshing.ld().observe(this, Observer { swipeRefreshLayout.isRefreshing = it })
        viewModel.state.data().ld().observe(this, Observer { unitsAdapter.units = it.orEmpty() })

        swipeRefreshLayout.setOnRefreshListener { viewModel.onRefresh() }

        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {}

            override fun onChildViewAttachedToWindow(view: View) {
                val count = unitsAdapter.itemCount
                val position = recyclerView.getChildAdapterPosition(view)
                if (position != count - 1) return

                viewModel.onLoadNextPage()
            }
        })
    }
}
