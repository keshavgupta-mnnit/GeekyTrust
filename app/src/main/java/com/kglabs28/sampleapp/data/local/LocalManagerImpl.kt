package com.kglabs28.sampleapp.data.local

import androidx.room.withTransaction
import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.local.db.entity.Article
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class LocalManagerImpl @Inject constructor(private val database: NewsDatabase) : LocalManager {
    private val dao = database.newsArticleDao

    override fun pagingSource() = dao.pagingSource()

    override suspend fun getNewsArticleById(id: String) = dao.getArticleById(id)


    override suspend fun insertArticles(articles: List<Article>){
        Timber.d("Saving ${articles.size} articles in db")
        dao.insertArticles(articles)
    }

    override suspend fun getLatestTimestamp() = dao.getNewestTimestamp()

    override suspend fun enforceCacheLimit(limit: Int) = dao.enforceCacheLimit(limit)

    override suspend fun updateBookmarkStatus(id: String, timestamp: Long?) =
        dao.updateBookmarkStatus(id, timestamp)

    override suspend fun searchLocalFeed(query: String) = dao.searchLocalFeed(query)

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return database.withTransaction {
            block()
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return dao.getBookmarkedArticles()
    }
}