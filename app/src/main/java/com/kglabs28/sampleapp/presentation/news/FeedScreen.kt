package com.kglabs28.sampleapp.presentation.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kglabs28.sampleapp.presentation.components.ErrorScreen
import com.kglabs28.sampleapp.presentation.components.NewsItem
import com.kglabs28.sampleapp.presentation.components.SearchBar

@Composable
fun FeedScreen(
    onArticleClick: (String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val news = viewModel.news.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    Scaffold(
        topBar = {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = { /* Handled automatically by debounce in ViewModel */ }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // CRITICAL: use news.itemKey { it.id } to prevent UI flickering on refresh!
            items(
                count = news.itemCount,
                key = news.itemKey { it.id }
            ) { index ->
                val article = news[index]
                if (article != null) {
                    // Check against ID instead of URL for stability
                    val isBookmarked = bookmarkedIds.contains(article.id)
                    NewsItem(
                        article = article,
                        isBookmarked = isBookmarked,
                        onClick = { onArticleClick(article.id) }, // Pass ID to Detail Screen (if needed) or article.url
                        onBookmarkClick = { viewModel.onToggleBookmark(article) }
                    )
                }
            }

            news.apply {
                when {
                    // Initial load (Refresh)
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    // Bottom-of-list pagination load (Append)
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    // Empty list state
                    loadState.refresh is LoadState.NotLoading && news.itemCount == 0 -> {
                        item {
                            ErrorScreen(
                                message = "No articles found.",
                                onRetry = { news.refresh() },
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    }
                    // Error state on initial load
                    loadState.refresh is LoadState.Error -> {
                        if (news.itemCount == 0) {
                            item {
                                ErrorScreen(
                                    message = "No Internet Connection or Error Occurred",
                                    onRetry = { news.retry() },
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}