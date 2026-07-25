package com.kglabs28.sampleapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.utils.BasicUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRepositoryImpl @Inject constructor(
    private val localManager: LocalManager,
    private val remoteManager: RemoteManager

) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3, // Start fetching local/remote earlier to ensure smooth scroll
                enablePlaceholders = false
            ),
            remoteMediator = NewsRemoteMediator(
                remoteManager = remoteManager,
                localManager = localManager
            ),
            pagingSourceFactory = { localManager.pagingSource() }
        ).flow
    }

    override suspend fun getNewsArticleById(id: String): Article {
        return localManager.getNewsArticleById(id)
    }


    override suspend fun searchNews(query: String): List<Article> {
        return if (BasicUtils.isNetworkConnected()) {
            try {
                remoteManager.searchNews(query)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            localManager.searchLocalFeed(query)
        }
    }

    override suspend fun updateBookmark(article: Article) {
        val isCurrentlyBookmarked = article.bookmarkedAt != null
        val newTimestamp = if (isCurrentlyBookmarked) null else System.currentTimeMillis()
        localManager.updateBookmarkStatus(article.id, newTimestamp)
    }

    suspend fun enforceCacheLimit(limit: Int) {
        localManager.enforceCacheLimit(limit)
    }

    override fun getBookmarks(): Flow<List<Article>> {
        return localManager.getBookmarkedArticles()
    }

}
