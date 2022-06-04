/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.sample.declarativeui.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.paging.sample.declarativeui.ListUnit
import dev.icerock.moko.paging.sample.declarativeui.ListViewModel
import dev.icerock.moko.paging.state.ResourceState

@Composable
fun ListScreen(
    viewModel: ListViewModel = viewModel(
        factory = createViewModelFactory {
            ListViewModel(
                withError = false,
                withInitialValue = false
            )
                .apply { start() }
        }
    )
) {

    val state: ResourceState<List<ListUnit>, String> by viewModel.state.collectAsState()
    val isRefreshing: Boolean by viewModel.isRefreshing.collectAsState()

    ListStateBody(
        state = state,
        onRetryPressed = { viewModel.onRetryPressed() },
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() },
        loadNextPage = { viewModel.onLoadNextPage() }
    )
}

@Composable
fun ListStateBody(
    state: ResourceState<List<ListUnit>, String>,
    onRefresh: () -> Unit,
    onRetryPressed: () -> Unit,
    loadNextPage: () -> Unit,
    isRefreshing: Boolean
) {
    val refreshState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    SwipeRefresh(
        state = refreshState,
        onRefresh = onRefresh
    ) {
        when (state) {
            is ResourceState.Data -> ListStateContent(
                data = state.data,
                loadNextPage = loadNextPage
            )
            ResourceState.Empty -> EmptyStateView()
            is ResourceState.Error -> ErrorStateView(
                message = state.error,
                onRetryPressed = onRetryPressed
            )
            ResourceState.Loading -> LoadingStateView()
        }
    }

}

@Composable fun ListStateContent(
    data: List<ListUnit>,
    loadNextPage: () -> Unit
) {
    val state: LazyListState = rememberLazyListState()
    println(remember { derivedStateOf { state.layoutInfo } })

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            items = data
        ) { index, unit ->
            if (index > 0.7 * data.size) {
                loadNextPage()
            }
            ListItem(unit)
        }
    }
}

@Composable
fun ListItem(unit: ListUnit) {
    when (unit) {
        ListUnit.Loading -> LoadingListItem()
        is ListUnit.ProductUnit -> ProductListItem(unit)
    }
}

@Composable
fun LoadingListItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .width(15.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding()
        )
    }
}

@Composable
fun ProductListItem(unit: ListUnit.ProductUnit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(modifier = Modifier.padding(16.dp), text = unit.title)
    }
}

@Composable
fun LoadingStateView() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun EmptyStateView() {
    Box(Modifier.fillMaxSize()) {
        Text(modifier = Modifier.align(Alignment.Center), text = "Empty")
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetryPressed: (() -> Unit)?
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (onRetryPressed != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = onRetryPressed) {
                    Text(
                        text = "Retry",
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, group = "load")
@Composable
fun ListStatePreview() {
    ListStateBody(
        state = ResourceState.Data(
            data = getTestData()
        ),
        onRefresh = {},
        onRetryPressed = {},
        isRefreshing = true,
        loadNextPage = {}
    )
}

@Preview(showSystemUi = true, group = "load")
@Composable
fun ListStateLoadingPreview() {
    ListStateBody(
        state = ResourceState.Loading,
        onRefresh = {},
        onRetryPressed = {},
        isRefreshing = false,
        loadNextPage = {}
    )
}

@Preview(showSystemUi = true, group = "load")
@Composable
fun ListStateEmptyPreview() {
    ListStateBody(
        state = ResourceState.Empty,
        onRefresh = {},
        onRetryPressed = {},
        isRefreshing = false,
        loadNextPage = {}
    )
}

@Preview(showSystemUi = true, group = "load")
@Composable
fun ListStateErrorPreview() {
    ListStateBody(
        state = ResourceState.Error(error = "Some error"),
        onRefresh = {},
        onRetryPressed = {},
        isRefreshing = false,
        loadNextPage = {}
    )
}

fun getTestData(withLoading: Boolean = true): List<ListUnit> =
    (1..5).map {
        ListUnit.ProductUnit(
            title = "Product #$it",
            id = it.toLong()
        )
    } + if (withLoading) listOf(ListUnit.Loading) else emptyList()

