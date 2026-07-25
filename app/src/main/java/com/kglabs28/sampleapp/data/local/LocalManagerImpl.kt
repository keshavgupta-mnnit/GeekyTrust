package com.kglabs28.sampleapp.data.local

import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.utils.BasicUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalManagerImpl @Inject constructor(private val database: NewsDatabase) : LocalManager {
    private val dao = database.newsArticleDao

    override fun pagingSource() = dao.pagingSource()

    override suspend fun getNewsArticleById(id: String) = dao.getArticleById(id)


    override suspend fun insertArticles(articles: List<Article>) {
        BasicUtils.log("NewsApp", "LocalManagerImpl :: Saving ${articles.size} articles in db")
        dao.insertArticles(articles)
    }

    override suspend fun getLatestTimestamp() = dao.getNewestTimestamp()

    override suspend fun enforceCacheLimit(limit: Int) = dao.enforceCacheLimit(limit)

    override suspend fun updateBookmarkStatus(id: String, timestamp: Long?) =
        dao.updateBookmarkStatus(id, timestamp)

    override suspend fun searchLocalFeed(query: String) = dao.searchLocalFeed(query)

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return dao.getBookmarkedArticles()
    }
}