package com.example.news.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.domain.model.Source
import com.example.news.ui.components.NewsArticleItem
import com.example.news.ui.screens.search.model.SearchUiState
import com.example.news.ui.theme.NewsTheme

@Composable
fun SearchScreen(
    searchQueryFromMain: String?,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onFullArticleNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchQueryFromMain) {
        searchQueryFromMain?.let {
            viewModel.processExternalSearchQuery(it)
        }
    }

    SearchScreenContent(
        modifier = modifier,
        uiState = uiState,
        onLoadMore = { viewModel.loadMoreResults() },
        onRetry = {
            when (val currentUiState = uiState) {
                is SearchUiState.Success -> {
                    if (currentUiState.paginationErrorMessage != null) {
                        viewModel.retryLoadMore()
                    }
                }

                is SearchUiState.Error, is SearchUiState.NoResults -> {
                    viewModel.retrySearch()
                }

                else -> Unit
            }
        },
        onArticleClick = { articleUrl ->
            onFullArticleNavigate(articleUrl)
        }
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        when (uiState) {
            is SearchUiState.Initial -> {
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

            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
            }

            is SearchUiState.Success -> {
                val listState = rememberLazyListState()
                val articles = uiState.articles

                val shouldLoadMore by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        if (layoutInfo.totalItemsCount == 0 || !uiState.canLoadMore || uiState.isLoadingMore || uiState.paginationErrorMessage != null) {
                            return@derivedStateOf false
                        }
                        val lastVisibleItem =
                            layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
                        lastVisibleItem.index >= layoutInfo.totalItemsCount - 2
                    }
                }

                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore) {
                        onLoadMore()
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(
                        articles,
                        key = { _, article ->
                            article.url ?: article.title ?: article.publishedAt.toString()
                        }
                    ) { _, article ->
                        NewsArticleItem(
                            article = article,
                            onClick = { article.url?.let { onArticleClick(it) } }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }

                    if (uiState.paginationErrorMessage != null) {
                        item {
                            PaginationErrorItem(
                                message = uiState.paginationErrorMessage,
                                onRetry = onRetry
                            )
                        }
                    }
                }
            }

            is SearchUiState.NoResults -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_search_results_for, uiState.query),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            is SearchUiState.Error -> {
                FullScreenErrorUI(
                    modifier = Modifier.fillMaxSize(),
                    message = uiState.message,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
fun FullScreenErrorUI(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_fetching_news_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.retry), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PaginationErrorItem(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.retry), color = MaterialTheme.colorScheme.primary)
        }
    }
}


@Preview(showBackground = true, name = "Search Initial Prompt")
@Composable
fun SearchScreenInitialPreview() {
    NewsTheme {
        SearchScreenContent(
            uiState = SearchUiState.Initial,
            onLoadMore = {},
            onRetry = {},
            onArticleClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Search Success with Pagination Error")
@Composable
fun SearchScreenSuccessWithPaginationErrorPreview() {
    val sampleArticles = List(5) { index ->
        Article(
            source = Source(
                id = "source-$index", name = "News Source ${index + 1}",
                description = null,
                url = null,
                category = null,
                language = null,
                country = null,
            ),
            author = "Author ${index + 1}",
            title = "Sample Article Title ${index + 1}",
            description = "This is a sample description for article ${index + 1}.",
            url = "http://example.com/article/$index",
            urlToImage = "https://picsum.photos/seed/article$index/600/400",
            publishedAt = "2023-10-2${7 - index}T10:00:00Z",
            content = "Sample content for article ${index + 1}..."
        )
    }
    NewsTheme {
        SearchScreenContent(
            uiState = SearchUiState.Success(
                query = "Technology",
                articles = sampleArticles,
                isLoadingMore = false,
                canLoadMore = true,
                currentPage = 1,
                paginationErrorMessage = "Failed to load more articles. Please check your connection."
            ),
            onLoadMore = {},
            onRetry = {},
            onArticleClick = {}
        )
    }
}
