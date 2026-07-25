package com.kglabs28.sampleapp.data.local

import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.utils.BasicUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalManagerImpl @Inject constructor(private val database: NewsDatabase) : LocalManager {
    private val dao = database.newsArticleDao

    override fun pagingSource() = dao.pagingSource()

    override suspend fun insertArticles(articles: List<Article>) {
        BasicUtils.log("NewsApp", "LocalManagerImpl :: Saving ${articles.size} articles in db")
        dao.insertArticles(articles)
    }

    override suspend fun getLatestTimestamp() = dao.getNewestTimestamp()

    override suspend fun enforceCacheLimit(limit: Int) = dao.enforceCacheLimit(limit)

    override suspend fun updateBookmarkStatus(article: Article) {
        BasicUtils.log("NewsApp", "LocalManagerImpl :: updateBookmarkStatus for id: ${article.id}")
        val newTimestamp = if (article.bookmarkedAt != null) null else System.currentTimeMillis()
        dao.upsertArticle(article.copy(bookmarkedAt = newTimestamp))
    }
    
    override suspend fun searchLocalFeed(query: String): List<Article>{
        BasicUtils.log("NewsApp", "LocalManagerImpl :: searchLocalFeed query: $query")
        return dao.searchLocalFeed(query)  
    } 

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return dao.getBookmarkedArticles()
    }
}