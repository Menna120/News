package com.example.news.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.news.R
import com.example.news.ui.theme.NewsTheme

@Composable
fun NewsSearchBar(
    modifier: Modifier = Modifier,
    onSendSearchQueryClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text(stringResource(id = R.string.search)) },
        leadingIcon = {
            IconButton(onClick = { onSendSearchQueryClick(query) }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search Icon"
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.clear)
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Preview
@Composable
fun NewsSearchBarPreview() {
    NewsTheme { NewsSearchBar {} }
}
