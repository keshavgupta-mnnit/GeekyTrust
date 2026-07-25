package com.kglabs28.sampleapp.data.remote

import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.mapper.toArticle
import com.kglabs28.sampleapp.utils.BasicUtils
import javax.inject.Inject

class RemoteManagerImpl @Inject constructor(private val newsApi: NewsApi) : RemoteManager {
    override suspend fun getLatestNews(latestTimeStamp: String?): List<Article> {
        BasicUtils.log("NewsApp", "RemoteManagerImpl :: getLatestNews called latestTimeStamp: $latestTimeStamp")
        return newsApi.getLatestNews(latestTimeStamp = latestTimeStamp ?: "")
            .articles.map { it.toArticle() }
    }

    override suspend fun fetchNews(oldestTimeStamp: String): List<Article> {
        BasicUtils.log("NewsApp", "RemoteManagerImpl :: fetchNews called oldestTimeStamp: $oldestTimeStamp")
        return newsApi.getMoreNews(oldestTimeStamp = oldestTimeStamp)
            .articles.map { it.toArticle() }
    }

    override suspend fun searchNews(query: String): List<Article> {
        BasicUtils.log("NewsApp", "RemoteManagerImpl :: searchNewsquery: $query")
        return newsApi.searchNews(query = query).articles.map { it.toArticle() }
    }
}