package com.example.news.ui.news_app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.news.R
import com.example.news.ui.components.NewsDrawer
import com.example.news.ui.components.NewsTopBar
import com.example.news.ui.navigation.CategoryNews
import com.example.news.ui.navigation.FullArticle
import com.example.news.ui.navigation.Home
import com.example.news.ui.navigation.NewsNavHost
import com.example.news.ui.navigation.Search
import com.example.news.ui.theme.NewsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApp(viewModel: NewsAppViewModel = hiltViewModel()) {

    val themePreference by viewModel.themePreference.collectAsState()
    val languagePreferenceCode by viewModel.languagePreferenceCode.collectAsState()
    val appBarSearchQuery by viewModel.appBarSearchQuery.collectAsState()

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        Home::class.qualifiedName -> stringResource(id = R.string.home)
        CategoryNews::class.qualifiedName + "/{categoryName}" -> currentBackStackEntry!!.toRoute<CategoryNews>().categoryName
        else -> stringResource(id = R.string.app_name)
    }

    NewsTheme(themePreference = themePreference) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NewsDrawer(
                    navController = navController,
                    currentThemePreference = themePreference,
                    onThemePreferenceChanged = { viewModel.updateThemePreference(it) },
                    currentLanguageCode = languagePreferenceCode,
                    onLanguagePreferenceChanged = { viewModel.updateLanguagePreferenceCode(it) },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            },
            gesturesEnabled = currentRoute != FullArticle::class.qualifiedName + "/{articleUrl}"
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (currentRoute != FullArticle::class.qualifiedName + "/{articleUrl}") {
                        NewsTopBar(
                            title = topBarTitle,
                            navController = navController,
                            isCurrentSearchRoute = currentRoute == Search::class.qualifiedName,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSendSearchQueryClick = { viewModel.updateAppBarSearchQuery(it) }
                        )
                    }
                }
            ) { innerPadding ->
                NewsNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    searchQueryFromMain = appBarSearchQuery
                )
            }
        }
    }
}
