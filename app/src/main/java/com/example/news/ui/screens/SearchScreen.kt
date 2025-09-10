package com.example.news.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.news.data.NewsArticle.Companion.getSampleNewsForCategory
import com.example.news.ui.screens.category.NewsArticleItem

@Composable
fun SearchScreen(
    searchQuery: String?,
    modifier: Modifier = Modifier,
    onFullArticleNavigate: (String) -> Unit
) {
    val currentQuery = searchQuery ?: ""
    val searchResults = remember(currentQuery) {
        if (currentQuery.isNotBlank())
            getSampleNewsForCategory("Search results for '$currentQuery'").take(5)
        else emptyList()
    }

    if (currentQuery.isNotBlank()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (searchResults.isEmpty()) {
                Text(
                    "No results found for '$currentQuery'",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(searchResults) { article ->
                        NewsArticleItem(
                            article = article,
                            onClick = { onFullArticleNavigate(article.articleUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SearchScreenPreview() {
    SearchScreen(searchQuery = "example query") {}
}
