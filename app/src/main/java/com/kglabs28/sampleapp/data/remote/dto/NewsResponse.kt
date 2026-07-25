package com.kglabs28.sampleapp.data.remote.dto

data class NewsResponse(
    val totalArticles: Int,
    val articles: List<ArticleDto>
)

data class ArticleDto(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val image: String?,
    val publishedAt: String,
    val source: SourceDto
)

data class SourceDto(
    val id: String,
    val name: String,
    val url: String,
    val country: String
)


