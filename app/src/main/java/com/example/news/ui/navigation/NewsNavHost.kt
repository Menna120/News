package com.example.news.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.news.ui.screens.category.CategoryNewsScreen
import com.example.news.ui.screens.home.HomeScreen
import com.example.news.ui.screens.search.SearchScreen

@Composable
fun NewsNavHost(
    navController: NavHostController,
    searchQueryFromMain: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {

        composable<Home> {
            HomeScreen { categoryName ->
                navController.navigate(CategoryNews(categoryName)) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        composable<CategoryNews> {
            CategoryNewsScreen()
        }

        composable<Search> {
            SearchScreen(searchQueryFromMain)
        }
    }
}
