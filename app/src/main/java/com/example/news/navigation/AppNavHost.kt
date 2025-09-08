package com.example.news.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.news.ui.screens.CategoryNewsScreen
import com.example.news.ui.screens.HomeScreen
import com.example.news.ui.screens.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    searchQueryFromMain: String
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        composable<Home> {
            HomeScreen(navController = navController)
        }
        composable<CategoryNews> { backStackEntry ->
            val categoryNews: CategoryNews = backStackEntry.toRoute()
            CategoryNewsScreen(categoryNews = categoryNews)
        }
        composable<Search> { backStackEntry ->
            SearchScreen(
                navController = navController,
                searchQuery = searchQueryFromMain
            )
        }
    }
}
