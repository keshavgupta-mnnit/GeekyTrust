package com.kglabs28.sampleapp.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kglabs28.sampleapp.data.local.db.entity.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    @Query("SELECT * FROM articles ORDER BY lastUpdated DESC")
    fun pagingSource(): PagingSource<Int, Article>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<Article>)

    @Upsert
    suspend fun upsertArticle(article: Article)

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): Article


    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    suspend fun searchLocalFeed(query: String): List<Article>


    @Query("SELECT MAX(lastUpdated) FROM articles")
    suspend fun getNewestTimestamp(): Long?


    @Query("SELECT * FROM articles WHERE bookmarkedAt IS NOT NULL ORDER BY bookmarkedAt DESC")
    fun getBookmarkedArticles(): Flow<List<Article>>

    @Query("DELETE FROM articles WHERE bookmarkedAt IS NULL AND id NOT IN (SELECT id FROM articles WHERE bookmarkedAt IS NULL ORDER BY lastUpdated DESC LIMIT :limit)")
    suspend fun enforceCacheLimit(limit: Int)
}

