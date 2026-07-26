package com.kglabs28.sampleapp.presentation.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.presentation.components.ErrorScreen
import com.kglabs28.sampleapp.presentation.components.NewsItem
import com.kglabs28.sampleapp.presentation.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery
    val news = viewModel.news.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("News Feed") })
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = { /* Handled automatically by debounce in ViewModel */ }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                // 1. Initial Loading State (FullScreen)
                news.loadState.refresh is LoadState.Loading && news.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                // 2. Initial Error State (FullScreen)
                news.loadState.refresh is LoadState.Error && news.itemCount == 0 -> {
                    ErrorScreen(
                        message = "No Internet Connection or Error Occurred",
                        onRetry = { news.retry() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. Empty State (FullScreen)
                news.loadState.refresh is LoadState.NotLoading && news.loadState.append.endOfPaginationReached && news.itemCount == 0 -> {
                    ErrorScreen(
                        message = "No articles found.",
                        onRetry = { news.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 4. Content State (Main List)
                else -> {
                    PullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = news.loadState.refresh is LoadState.Loading, // Only true for pull-to-refresh when itemCount > 0
                        onRefresh = { news.refresh() }
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                count = news.itemCount,
                                key = news.itemKey { it.id }
                            ) { index ->
                                val article = news[index]
                                if (article != null) {
                                    NewsItem(
                                        article = article,
                                        onClick = { onArticleClick(article) },
                                        onBookmarkClick = { viewModel.onToggleBookmark(article) }
                                    )
                                }
                            }

                            // Pagination loading/error items at the bottom
                            news.apply {
                                if (loadState.append is LoadState.Loading) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                if (loadState.append is LoadState.Error) {
                                    item {
                                        ErrorScreen(
                                            message = "Could not load more news.",
                                            onRetry = { news.retry() },
                                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
