package com.example.news.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.ui.screens.search.components.HandlePagingResults
import com.example.news.ui.screens.search.model.SearchScreenUiState
import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchScreen(
    searchQueryFromMain: String?,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val screenUiState by viewModel.screenState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.searchResults.collectAsLazyPagingItems()

    LaunchedEffect(searchQueryFromMain, viewModel) {
        searchQueryFromMain?.let { query ->
            if (query.isNotBlank() && query != viewModel.searchQuery.value) {
                viewModel.onSearchQueryChanged(query)
            }
        }
    }

    SearchScreenContent(
        modifier = modifier,
        screenUiState = screenUiState,
        pagingItems = pagingItems
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    screenUiState: SearchScreenUiState,
    pagingItems: LazyPagingItems<Article>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        when (screenUiState) {
            SearchScreenUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.search_prompt),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            is SearchScreenUiState.ActiveSearch -> {
                HandlePagingResults(
                    pagingItems = pagingItems,
                    queryForNoResultsMessage = screenUiState.query
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenContentPreview() {
    val articles = List(10) { index ->
        Article(
            source = com.example.news.domain.model.Source(id = "bbc-news", name = "BBC News"),
            author = "BBC News",
            title = "Article Title $index",
            description = "This is a sample article description.",
            url = "https://www.bbc.com/news/article-$index",
            urlToImage = null,
            publishedAt = "2023-01-01T12:00:00Z",
            content = "This is the full article content."
        )
    }
    SearchScreenContent(
        screenUiState = SearchScreenUiState.ActiveSearch("test query"),
        pagingItems = flowOf(PagingData.from(articles)).collectAsLazyPagingItems()
    )
}
