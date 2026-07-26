package com.kglabs28.sampleapp.data.local

import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.utils.BasicUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalManagerImpl @Inject constructor(private val database: NewsDatabase) : LocalManager {
    private val dao = database.newsArticleDao

    override fun pagingSource() = dao.pagingSource()

    override suspend fun insertArticles(articles: List<Article>) = withContext(Dispatchers.IO) {
        BasicUtils.log("NewsApp", "LocalManagerImpl :: Saving ${articles.size} articles in db")
        dao.insertArticles(articles)
    }

    override suspend fun getLatestTimestamp() = withContext(Dispatchers.IO) {
        dao.getNewestTimestamp()
    }

    override suspend fun enforceCacheLimit(limit: Int) = withContext(Dispatchers.IO) {
        dao.enforceCacheLimit(limit)
    }

    override suspend fun updateBookmarkStatus(article: Article) = withContext(Dispatchers.IO) {
        BasicUtils.log("NewsApp", "LocalManagerImpl :: updateBookmarkStatus for id: ${article.id}")
        dao.upsertArticle(article)
    }

    override suspend fun searchLocalFeed(query: String): List<Article> =
        withContext(Dispatchers.IO) {
            BasicUtils.log("NewsApp", "LocalManagerImpl :: searchLocalFeed query: $query")
            dao.searchLocalFeed(query)
        }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return dao.getBookmarkedArticles()
    }
}