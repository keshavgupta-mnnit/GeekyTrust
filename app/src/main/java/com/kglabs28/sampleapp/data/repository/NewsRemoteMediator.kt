package com.kglabs28.sampleapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.utils.BasicUtils
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val remoteManager: RemoteManager,
    private val localManager: LocalManager
) : RemoteMediator<Int, Article>() {

    companion object {
        private var lastRequestTime = 0L
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            // GNews API free tier has a strict limit of 1 request per second.
            // Paging 3 often triggers APPEND immediately after REFRESH.
            val currentTime = System.currentTimeMillis()
            val timeSinceLastRequest = currentTime - lastRequestTime
            if (timeSinceLastRequest < 1500) {
                val delayTime = 1500 - timeSinceLastRequest
                BasicUtils.log("NewsApp", "NewsRemoteMediator :: Rate limiting - delaying for ${delayTime}ms")
                kotlinx.coroutines.delay(delayTime)
            }
            lastRequestTime = System.currentTimeMillis()

            val freshArticles = when (loadType) {
                LoadType.REFRESH -> {
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: REFRESH")
                    val newestTimestamp = localManager.getLatestTimestamp()
                    val fromDateIso = newestTimestamp?.let { BasicUtils.parseTimestampLongToString(it) }
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: Loading latest news since $fromDateIso")
                    remoteManager.getLatestNews(fromDateIso)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: APPEND")
                    val lastItem = state.lastItemOrNull()
                    
                    // If lastItem is null, it might be an initial load or empty DB.
                    // We can't fetch "more" if we don't know where the "end" is.
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = false)
                    }

                    val toDateIso = BasicUtils.parseTimestampLongToString(lastItem.lastUpdated)
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: Loading older news before $toDateIso")
                    remoteManager.fetchNews(toDateIso)
                }
            }

            localManager.insertArticles(freshArticles)

            val endOfPagination = freshArticles.isEmpty() && loadType == LoadType.APPEND
            MediatorResult.Success(endOfPaginationReached = endOfPagination)

        } catch (e: IOException) {
            BasicUtils.log("NewsApp","NewsRemoteMediator :: Error loading news  msg: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            BasicUtils.log("NewsApp","NewsRemoteMediator :: HTTP error loading news msg: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}