package com.kglabs28.sampleapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.kglabs28.sampleapp.presentation.bookmark.BookmarkScreen
import com.kglabs28.sampleapp.presentation.detail.DetailScreen
import com.kglabs28.sampleapp.presentation.news.FeedScreen

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on Feed and Bookmarks screens
            if (currentDestination?.route == Route.Feed.route || currentDestination?.route == Route.Bookmarks.route) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Feed") },
                        selected = currentDestination.hierarchy.any { it.route == Route.Feed.route },
                        onClick = {
                            navController.navigate(Route.Feed.route) {
                                popUpTo(Route.Feed.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text("Bookmarks") },
                        selected = currentDestination.hierarchy.any { it.route == Route.Bookmarks.route },
                        onClick = {
                            navController.navigate(Route.Bookmarks.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Feed.route) {
                FeedScreen(
                    onArticleClick = { url ->
                        navController.navigate(Route.Detail.createRoute(url))
                    }
                )
            }
            composable(Route.Bookmarks.route) {
                BookmarkScreen(
                    onArticleClick = { url ->
                        navController.navigate(Route.Detail.createRoute(url))
                    }
                )
            }
            composable(
                route = Route.Detail.route,
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
