package com.kglabs28.sampleapp.data.mapper

import com.kglabs28.sampleapp.data.remote.dto.ArticleDto
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.utils.BasicUtils

fun ArticleDto.toArticle(): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        content = content,
        imageUrl = image,
        url = url,
        sourceName = source.name,
        lastUpdated = BasicUtils.parseTimestampStringToLong(publishedAt),
    )
}
