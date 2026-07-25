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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val bookmarkUseCase: BookmarkUseCase
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _query = MutableStateFlow("news")

    val news = _query.flatMapLatest { query ->
        if (query.isBlank() || query == "news") {
            // Load paginated data from DB/Network when not searching
            getNewsUseCase.execute()
        } else {
            // Handle active search by mapping the simple List back to PagingData for the UI
            flow {
                val results = getNewsUseCase.search(query)
                emit(PagingData.from(results))
            }
        }
    }.cachedIn(viewModelScope)

    // Map to a set of IDs for O(1) fast lookup in the UI
    val bookmarkedIds = bookmarkUseCase.getAll()
        .map { bookmarks -> bookmarks.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        viewModelScope.launch {
            snapshotFlow { _searchQuery.value }
                .debounce(500L) // Wait half a second after the user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    Timber.d("Searching for: $query")
                    _query.value = if (query.isBlank()) "news" else query
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onToggleBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkUseCase.toggle(article)
        }
    }
}
