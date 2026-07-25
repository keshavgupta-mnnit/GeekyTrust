package com.kglabs28.sampleapp.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.utils.BasicUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localManager: LocalManager,
    private val remoteManager: RemoteManager

) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3, // Start fetching local/remote earlier to ensure smooth scroll
                enablePlaceholders = false,
                initialLoadSize = 10 // Avoid triggering APPEND immediately on fresh launch
            ),
            remoteMediator = NewsRemoteMediator(
                remoteManager = remoteManager,
                localManager = localManager
            ),
            pagingSourceFactory = { localManager.pagingSource() }
        ).flow
    }

    override suspend fun searchNews(query: String): List<Article> {
        return if (BasicUtils.isNetworkConnected(context)) {
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
        localManager.updateBookmarkStatus(article)
    }

    suspend fun enforceCacheLimit(limit: Int) {
        localManager.enforceCacheLimit(limit)
    }

    override fun getBookmarks(): Flow<List<Article>> {
        return localManager.getBookmarkedArticles()
    }

}
