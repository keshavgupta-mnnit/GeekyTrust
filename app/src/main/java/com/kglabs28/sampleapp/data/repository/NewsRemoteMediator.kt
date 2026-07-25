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

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            var fromDateIso: String? = null
            var toDateIso: String? = null

            when (loadType) {
                LoadType.REFRESH -> {
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: REFRESH")
                    val newestTimestamp = localManager.getLatestTimestamp()
                    if (newestTimestamp != null) {
                        fromDateIso = BasicUtils.parseTimestampLongToString(newestTimestamp)
                    }
                }

                LoadType.PREPEND -> {
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: PREPEND")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    BasicUtils.log("NewsApp", "NewsRemoteMediator :: APPEND")
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    toDateIso = BasicUtils.parseTimestampLongToString(lastItem.lastUpdated)
                }
            }

            val freshArticles = if (toDateIso != null) {
                BasicUtils.log("NewsApp", "NewsRemoteMediator :: Loading older news before $toDateIso")
                remoteManager.fetchNews(toDateIso)
            } else {
                BasicUtils.log("NewsApp", "NewsRemoteMediator :: Loading latest news since $fromDateIso")
                remoteManager.getLatestNews(fromDateIso)
            }

            localManager.insertArticles(freshArticles)

            val endOfPagination = if (loadType == LoadType.REFRESH) {
                false
            } else {
                freshArticles.isEmpty()
            }

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