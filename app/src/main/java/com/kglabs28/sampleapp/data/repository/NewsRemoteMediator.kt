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
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val remoteManager: RemoteManager,
    private val localManager: LocalManager
) : RemoteMediator<Int, Article>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            var fromDateIso: String? = null
            var toDateIso: String? = null

            when (loadType) {
                LoadType.REFRESH -> {
                    val newestTimestamp = localManager.getLatestTimestamp()
                    if (newestTimestamp != null) {
                        fromDateIso = BasicUtils.parseTimestampLongToString(newestTimestamp)
                    }
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    // If Room is empty, there is nothing to append. (Refresh will handle empty states).
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    toDateIso = BasicUtils.parseTimestampLongToString(lastItem.lastUpdated)
                }
            }

            val freshArticles = if (toDateIso != null) {
                Timber.d("Loading older news before $toDateIso")
                remoteManager.fetchNews(toDateIso)
            } else {
                Timber.d("Loading latest news since $fromDateIso")
                remoteManager.getLatestNews(fromDateIso)
            }

            localManager.withTransaction {
                localManager.insertArticles(freshArticles)
            }

            MediatorResult.Success(endOfPaginationReached = freshArticles.isEmpty())

        } catch (e: IOException) {
            Timber.e(e, "Error loading news")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error loading news")
            MediatorResult.Error(e)
        }
    }
}