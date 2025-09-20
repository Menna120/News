package com.example.news.ui.screens.category.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.ui.components.NewsArticleItem

@Composable
fun SourceArticlePage(
    sourceName: String,
    articlesPagingItems: LazyPagingItems<Article>,
    onArticleClicked: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    val loadState = articlesPagingItems.loadState

    Box(modifier = modifier.fillMaxSize()) {
        when (loadState.refresh) {
            is LoadState.Loading -> LoadingState(Modifier.fillMaxSize())
            is LoadState.Error -> {
                val error = (loadState.refresh as LoadState.Error).error
                ErrorState(
                    modifier = Modifier.fillMaxSize(),
                    message = error.localizedMessage
                        ?: stringResource(R.string.error_loading_articles),
                    onRetry = { articlesPagingItems.retry() }
                )
            }

            else -> {
                if (loadState.refresh is LoadState.NotLoading && articlesPagingItems.itemCount == 0) {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.no_articles_from_source, sourceName)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            count = articlesPagingItems.itemCount,
                            key = articlesPagingItems.itemKey { it }
                        ) { index ->
                            val article = articlesPagingItems[index]
                            article?.let {
                                NewsArticleItem(
                                    article = it,
                                    onClick = { onArticleClicked(it) })
                            }
                        }

                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                                    }
                                }
                            }

                            is LoadState.Error -> {
                                val error = (loadState.append as LoadState.Error).error
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = error.localizedMessage
                                                ?: stringResource(R.string.error_loading_more_articles),
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Button(onClick = { articlesPagingItems.retry() }) {
                                            Text(stringResource(R.string.retry))
                                        }
                                    }
                                }
                            }

                            is LoadState.NotLoading -> {
                                if (loadState.append.endOfPaginationReached && articlesPagingItems.itemCount > 0) {
                                    item {
                                        Text(
                                            stringResource(
                                                R.string.all_articles_loaded_for_source,
                                                sourceName
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
