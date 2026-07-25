package com.kglabs28.sampleapp.data.usecase

import androidx.paging.PagingData
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetNewsUseCase(private val repository: NewsRepository) {
    fun execute(): Flow<PagingData<Article>> = repository.getNews()
    
    suspend fun search(query: String): List<Article> = repository.searchNews(query)
    
    suspend fun getById(id: String): Article = repository.getNewsArticleById(id)
}
