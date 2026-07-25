package com.kglabs28.sampleapp.data.remote

import com.kglabs28.sampleapp.data.remote.dto.NewsResponse
import com.kglabs28.sampleapp.utils.AppConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun getLatestNews(
        @Query("apikey") apiKey: String = AppConstants.API_KEY,
        @Query("category") category: String = AppConstants.NEWS_CATEGORY,
        @Query("max") max: Int = 10,
        @Query("lang") lang: String = "en",
        @Query("from") latestTimeStamp: String = ""
    ): NewsResponse

    @GET("top-headlines")
    suspend fun getMoreNews(
        @Query("apikey") apiKey: String = AppConstants.API_KEY,
        @Query("category") category: String = AppConstants.NEWS_CATEGORY,
        @Query("max") max: Int = 10,
        @Query("lang") lang: String = "en",
        @Query("to") oldestTimeStamp: String
    ): NewsResponse

    @GET("search")
    suspend fun searchNews(
        @Query("apikey") apiKey: String= AppConstants.API_KEY,
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("max") max: Int = 20
    ): NewsResponse

}
