package com.kglabs28.sampleapp.ui.navigation

import android.os.Bundle
import android.net.Uri
import androidx.navigation.NavType
import com.kglabs28.sampleapp.data.local.db.entity.Article
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Route {
    @Serializable
    data object Feed : Route()

    @Serializable
    data object Bookmarks : Route()

    @Serializable
    data class Detail(val article: Article) : Route()

    companion object {
        val ArticleNavType = object : NavType<Article>(isNullableAllowed = false) {
            override fun get(bundle: Bundle, key: String): Article? {
                return bundle.getString(key)?.let { Json.decodeFromString(it) }
            }

            override fun parseValue(value: String): Article {
                return Json.decodeFromString(Uri.decode(value))
            }

            override fun put(bundle: Bundle, key: String, value: Article) {
                bundle.putString(key, Json.encodeToString(value))
            }

            override fun serializeAsValue(value: Article): String {
                return Uri.encode(Json.encodeToString(value))
            }
        }
    }
}
