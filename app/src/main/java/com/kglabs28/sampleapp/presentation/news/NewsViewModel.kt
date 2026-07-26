package com.kglabs28.sampleapp.presentation.news

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import com.kglabs28.sampleapp.data.usecase.GetNewsUseCase
import com.kglabs28.sampleapp.utils.BasicUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val bookmarkUseCase: BookmarkUseCase
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    // Single source of truth for the news data (Feed or Search)
    val news = snapshotFlow { _searchQuery.value }
        .debounce(500L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getNewsUseCase.execute()
            } else {
                BasicUtils.log("NewsApp", "Searching for: $query")
                flow {
                    val results = getNewsUseCase.search(query)
                    emit(PagingData.from(results))
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onToggleBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkUseCase.toggle(article)
        }
    }
}
