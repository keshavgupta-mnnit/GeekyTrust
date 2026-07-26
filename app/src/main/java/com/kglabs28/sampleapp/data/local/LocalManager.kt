package com.kglabs28.sampleapp.data.local

import androidx.paging.PagingSource
import com.kglabs28.sampleapp.data.local.db.entity.Article
import kotlinx.coroutines.flow.Flow

interface LocalManager {
    fun pagingSource(): PagingSource<Int, Article>
    suspend fun insertArticles(articles: List<Article>)
    suspend fun enforceCacheLimit(limit: Int)
    suspend fun updateBookmarkStatus(article: Article)
    suspend fun searchLocalFeed(query: String): List<Article>
    suspend fun getLatestTimestamp(): Long?
    fun getBookmarkedArticles(): Flow<List<Article>>
}