package com.kglabs28.sampleapp.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.presentation.bookmark.BookmarkScreen
import com.kglabs28.sampleapp.presentation.detail.DetailScreen
import com.kglabs28.sampleapp.presentation.news.FeedScreen
import kotlin.reflect.typeOf

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val isFeed = currentDestination?.hierarchy?.any { it.route?.contains("Feed") == true } == true
            val isBookmark = currentDestination?.hierarchy?.any { it.route?.contains("Bookmarks") == true } == true

            if (isFeed || isBookmark) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Feed") },
                        selected = isFeed,
                        onClick = {
                            navController.navigate(Route.Feed) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text("Bookmarks") },
                        selected = isBookmark,
                        onClick = {
                            navController.navigate(Route.Bookmarks) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Feed,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<Route.Feed> {
                FeedScreen(
                    onArticleClick = { article ->
                        navController.navigate(Route.Detail(article))
                    }
                )
            }
            composable<Route.Bookmarks> {
                BookmarkScreen(
                    onArticleClick = { article ->
                        navController.navigate(Route.Detail(article))
                    }
                )
            }
            composable<Route.Detail>(
                typeMap = mapOf(typeOf<Article>() to Route.ArticleNavType)
            ) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
