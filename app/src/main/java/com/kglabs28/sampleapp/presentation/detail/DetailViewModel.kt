package com.kglabs28.sampleapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import com.kglabs28.sampleapp.ui.navigation.Route
import com.kglabs28.sampleapp.utils.BasicUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookmarkUseCase: BookmarkUseCase
) : ViewModel() {

    private val detailRoute = try {
        savedStateHandle.toRoute<Route.Detail>(
            typeMap = mapOf(typeOf<Article>() to Route.ArticleNavType)
        )
    } catch (e: Exception) {
        // Fallback for tests or process death issues where arguments might be missing
        null
    }
    
    private val _article = MutableStateFlow<Article>(detailRoute?.article ?: Article(
        id = "", title = "", description = "", content = "", imageUrl = "", url = "", sourceName = "", lastUpdated = 0L
    ))
    val article = _article.asStateFlow()

    init {
        detailRoute?.let {
            BasicUtils.log("NewsApp", "Displaying article detail for: ${it.article.title}")
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val currentArticle = _article.value
            bookmarkUseCase.toggle(currentArticle)
            val updatedArticle = currentArticle.copy(
                bookmarkedAt = if (currentArticle.bookmarkedAt == null) System.currentTimeMillis() else null
            )
            _article.value = updatedArticle
        }
    }
}
