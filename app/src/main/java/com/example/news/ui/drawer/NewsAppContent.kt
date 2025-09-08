package com.example.news.ui.drawer

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.news.R
import com.example.news.navigation.AppNavHost
import com.example.news.navigation.CategoryNews
import com.example.news.navigation.Home
import com.example.news.navigation.Search
import com.example.news.ui.components.NewsAppBar
import com.example.news.ui.theme.NewsTheme
import com.example.news.utils.AppPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppContent(
    initialTheme: String,
    initialLanguageCode: String
) {
    val context = LocalContext.current
    var themePreference by remember { mutableStateOf(initialTheme) }
    var languagePreferenceCode by remember { mutableStateOf(initialLanguageCode) }
    var appBarSearchQuery by remember { mutableStateOf("") }

    NewsTheme(themePreference = themePreference) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        Log.d("MainAppContent", currentRoute.toString())

        val topBarTitle = when (currentRoute) {
            Home::class.qualifiedName -> stringResource(id = R.string.home)
            CategoryNews::class.qualifiedName + "/{categoryName}" -> currentBackStackEntry!!.toRoute<CategoryNews>().categoryName
            else -> stringResource(id = R.string.app_name)
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = drawerState,
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerContentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    NewsDrawerContent(
                        navController = navController,
                        currentThemePreference = themePreference,
                        onThemePreferenceChanged = { newPreference ->
                            themePreference = newPreference
                            AppPreferences.saveThemePreference(context, newPreference)
                        },
                        currentLanguageCode = languagePreferenceCode,
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    NewsAppBar(
                        title = topBarTitle,
                        isCurrentSearchRoute = currentRoute == Search::class.qualifiedName,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSendSearchQueryClick = { appBarSearchQuery = it },
                        onSearchNavigate = { navController.navigate(Search) }
                    )
                }
            ) { innerPadding ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    searchQueryFromMain = appBarSearchQuery
                )
            }
        }
    }
}
