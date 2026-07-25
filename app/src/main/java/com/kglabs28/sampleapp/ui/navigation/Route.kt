package com.kglabs28.sampleapp.ui.navigation

sealed class Route(val route: String) {
    data object Feed : Route("feed")
    data object Bookmarks : Route("bookmarks")
    data object Detail : Route("detail/{url}") {
        fun createRoute(url: String) = "detail/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
}
