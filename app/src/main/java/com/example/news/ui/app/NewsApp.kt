package com.example.news.ui.app

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
import com.example.news.ui.navigation.NewsNavHost
import com.example.news.ui.navigation.Search
import com.example.news.ui.theme.NewsTheme
import com.example.news.utils.Category.Companion.toCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApp(viewModel: NewsAppViewModel = hiltViewModel()) {

    val theme by viewModel.theme.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val appBarSearchQuery by viewModel.appBarSearchQuery.collectAsState()

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        CategoryNews::class.qualifiedName + "/{categoryName}" -> stringResource(
            currentBackStackEntry!!.toRoute<CategoryNews>().categoryName.toCategory().title
        )

        else -> stringResource(id = R.string.home)
    }

    NewsTheme(theme = theme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NewsDrawer(
                    navController = navController,
                    currentTheme = theme,
                    onThemeChanged = viewModel::updateTheme,
                    currentLanguageCode = languageCode,
                    onLanguageChanged = viewModel::updateLanguageCode,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    NewsTopBar(
                        title = topBarTitle,
                        navController = navController,
                        isCurrentSearchRoute = currentRoute == Search::class.qualifiedName,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSendSearchQueryClick = { viewModel.updateAppBarSearchQuery(it) }
                    )
                }
            ) { innerPadding ->
                NewsNavHost(
                    navController = navController,
                    searchQueryFromMain = appBarSearchQuery,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
