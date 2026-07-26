package com.kglabs28.sampleapp.data.remote

import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.mapper.toArticle
import com.kglabs28.sampleapp.utils.BasicUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteManagerImpl @Inject constructor(private val newsApi: NewsApi) : RemoteManager {
    override suspend fun getLatestNews(latestTimeStamp: String?): List<Article> =
        withContext(Dispatchers.IO) {
            BasicUtils.log(
                "NewsApp",
                "RemoteManagerImpl :: getLatestNews called latestTimeStamp: $latestTimeStamp"
            )
            newsApi.getLatestNews(latestTimeStamp = latestTimeStamp ?: "")
                .articles.map { it.toArticle() }
        }

    override suspend fun fetchNews(oldestTimeStamp: String): List<Article> =
        withContext(Dispatchers.IO) {
            BasicUtils.log(
                "NewsApp",
                "RemoteManagerImpl :: fetchNews called oldestTimeStamp: $oldestTimeStamp"
            )
            newsApi.getMoreNews(oldestTimeStamp = oldestTimeStamp)
                .articles.map { it.toArticle() }
        }

    override suspend fun searchNews(query: String): List<Article> = withContext(Dispatchers.IO) {
        BasicUtils.log("NewsApp", "RemoteManagerImpl :: searchNewsquery: $query")
        newsApi.searchNews(query = query).articles.map { it.toArticle() }
    }
}