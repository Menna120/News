package com.example.news.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.news.R
import com.example.news.ui.navigation.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsTopBar(
    title: String,
    navController: NavController,
    isCurrentSearchRoute: Boolean,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    onSendSearchQueryClick: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            if (isCurrentSearchRoute)
                NewsSearchBar { onSendSearchQueryClick(it) }
            else Text(title, style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
            if (!isCurrentSearchRoute) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = stringResource(id = R.string.open_drawer)
                    )
                }
            }
        },
        actions = {
            if (!isCurrentSearchRoute) {
                IconButton(
                    onClick = {
                        navController.navigate(Search) {
                            launchSingleTop = true
                            restoreState = true

                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NewsTopBarPreview() {
    NewsTopBar(
        title = "News App",
        navController = rememberNavController(),
        isCurrentSearchRoute = false,
        onMenuClick = {},
        onSendSearchQueryClick = {}
    )
}
