package com.example.news.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.news.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppBar(
    title: String,
    isCurrentSearchRoute: Boolean,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    onSendSearchQueryClick: (String) -> Unit,
    onSearchNavigate: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            if (isCurrentSearchRoute)
                NewsSearchBar(onSendSearchQueryClick)
            else Text(title)
        },
        navigationIcon = {
            if (!isCurrentSearchRoute) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(id = R.string.open_drawer)
                    )
                }
            }
        },
        actions = {
            if (!isCurrentSearchRoute) {
                IconButton(onClick = onSearchNavigate) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            }
        }
    )
}
