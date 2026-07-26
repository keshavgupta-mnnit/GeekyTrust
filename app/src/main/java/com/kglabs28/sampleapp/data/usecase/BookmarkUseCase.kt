package com.kglabs28.sampleapp.data.usecase

import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class BookmarkUseCase(private val repository: NewsRepository) {
    fun getAll(): Flow<List<Article>> = repository.getBookmarks()
    
    suspend fun toggle(article: Article) {
        val updatedArticle = article.copy(
            bookmarkedAt = if (article.bookmarkedAt == null) System.currentTimeMillis() else null
        )
        repository.updateBookmark(updatedArticle)
    }
}
