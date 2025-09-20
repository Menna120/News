package com.example.news.ui.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.ui.components.NewsArticleItem
import com.example.news.utils.OpenUrlInExternalBrowser
import kotlinx.coroutines.flow.flowOf

@Composable
fun HandlePagingResults(
    pagingItems: LazyPagingItems<Article>,
    queryForNoResultsMessage: String,
    modifier: Modifier = Modifier
) {
    val loadState = pagingItems.loadState
    val context = LocalContext.current

    when {
        loadState.refresh is LoadState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        }

        loadState.refresh is LoadState.Error -> {
            val error = (loadState.refresh as LoadState.Error).error
            FullScreenErrorUI(
                modifier = modifier.fillMaxSize(),
                message = error.localizedMessage
                    ?: stringResource(R.string.unknown_error_occurred),
                onRetry = { pagingItems.retry() }
            )
        }

        loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 && loadState.append.endOfPaginationReached -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_search_results_for, queryForNoResultsMessage),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it }
                ) { index ->
                    val article = pagingItems[index]
                    article?.let { articleItem ->
                        NewsArticleItem(
                            article = articleItem,
                            onClick = {
                                articleItem.url?.let { url ->
                                    OpenUrlInExternalBrowser.openBrowser(
                                        context,
                                        url
                                    )
                                }
                            }
                        )
                    }
                }

                if (loadState.append is LoadState.Loading) {
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

                if (loadState.append is LoadState.Error) {
                    val error = (loadState.append as LoadState.Error).error
                    item {
                        PaginationErrorItem(
                            message = error.localizedMessage
                                ?: stringResource(R.string.error_loading_more_articles),
                            onRetry = { pagingItems.retry() }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HandlePagingResultsPreview() {
    val articles = List(10) { index ->
        Article(
            source = com.example.news.domain.model.Source(id = "cnn", name = "CNN"),
            author = "John Doe",
            title = "Breaking News $index",
            description = "Detailed description of breaking news article $index.",
            url = "https://www.cnn.com/breaking-$index",
            urlToImage = null,
            publishedAt = "2023-10-27T10:00:00Z",
            content = "Full content of the breaking news article $index."
        )
    }
    HandlePagingResults(
        pagingItems = flowOf(PagingData.from(articles)).collectAsLazyPagingItems(),
        queryForNoResultsMessage = "test"
    )
}
