package com.kglabs28.sampleapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kglabs28.sampleapp.data.local.db.dao.NewsArticleDao
import com.kglabs28.sampleapp.data.local.db.entity.Article

@Database(
    entities = [Article::class],
    version = 1
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsArticleDao: NewsArticleDao
}