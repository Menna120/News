package com.example.news.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.news.ui.screens.FullArticleScreen
import com.example.news.ui.screens.category.CategoryNewsScreen
import com.example.news.ui.screens.home.HomeScreen
import com.example.news.ui.screens.search.SearchScreen

@Composable
fun NewsNavHost(
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
            HomeScreen { categoryName ->
                navController.navigate(CategoryNews(categoryName)) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        composable<CategoryNews> { backStackEntry ->
            CategoryNewsScreen { articleUrl ->
                navController.navigate(FullArticle(articleUrl)) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        composable<FullArticle> { backStackEntry ->
            val article: FullArticle = backStackEntry.toRoute()

            FullArticleScreen(article.articleUrl)
        }

        composable<Search> { backStackEntry ->
            SearchScreen(searchQueryFromMain) { articleUrl ->
                navController.navigate(
                    FullArticle(articleUrl)
                ) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}
