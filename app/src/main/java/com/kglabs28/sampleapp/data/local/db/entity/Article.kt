package com.kglabs28.sampleapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val url: String,
    val sourceName: String,
    val lastUpdated: Long,
    val bookmarkedAt: Long? = null,
)