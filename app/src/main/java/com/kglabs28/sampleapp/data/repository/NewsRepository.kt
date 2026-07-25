package com.kglabs28.sampleapp.data.repository

import androidx.paging.PagingData
import com.kglabs28.sampleapp.data.local.db.entity.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(): Flow<PagingData<Article>>
    suspend fun searchNews(query: String): List<Article>
    fun getBookmarks(): Flow<List<Article>>
    suspend fun updateBookmark(article: Article)
}
