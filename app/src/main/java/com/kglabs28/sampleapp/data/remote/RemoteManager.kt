package com.kglabs28.sampleapp.data.remote

import com.kglabs28.sampleapp.data.local.db.entity.Article

interface RemoteManager {
    suspend fun getLatestNews(latestTimeStamp: String?): List<Article>
    suspend fun fetchNews(oldestTimeStamp: String): List<Article>
    suspend fun searchNews(query: String): List<Article>
}