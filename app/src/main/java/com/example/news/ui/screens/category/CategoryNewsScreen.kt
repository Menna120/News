package com.example.news.ui.screens.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.domain.model.Source
import com.example.news.ui.components.NewsArticleItem
import com.example.news.ui.screens.category.components.ArticleBottomSheet
import com.example.news.ui.screens.category.components.EmptyState
import com.example.news.ui.screens.category.components.ErrorState
import com.example.news.ui.screens.category.components.LoadingState
import com.example.news.ui.screens.category.model.CategoryNewsUiState
import com.example.news.ui.screens.category.model.SourceArticlesUiState
import com.example.news.ui.theme.NewsTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryNewsViewModel = hiltViewModel(),
    onNavigateToFullArticle: (url: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var selectedArticle by remember { mutableStateOf<Article?>(null) }

    CategoryNewsContent(
        modifier = modifier,
        uiState = uiState,
        onArticleClicked = { article ->
            selectedArticle = article
            showSheet = true
        },
        onRetry = viewModel::retryFetchingNews,
        onLoadMoreArticles = viewModel::loadMoreArticles,
        onSourceSelected = viewModel::onSourceSelected
    )

    if (showSheet && selectedArticle != null) {
        ArticleBottomSheet(
            article = selectedArticle!!,
            onDismiss = { showSheet = false },
            onOpenArticle = { url ->
                onNavigateToFullArticle(url)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryNewsContent(
    modifier: Modifier = Modifier,
    uiState: CategoryNewsUiState,
    onArticleClicked: (Article) -> Unit,
    onRetry: () -> Unit,
    onLoadMoreArticles: () -> Unit,
    onSourceSelected: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    when {
        uiState.isLoadingSources -> LoadingState(modifier = modifier.fillMaxSize())
        uiState.sources.isEmpty() -> {
            if (uiState.globalErrorMessage != null) {
                ErrorState(
                    modifier = modifier.fillMaxSize(),
                    message = uiState.globalErrorMessage,
                    onRetry = onRetry
                )
            } else {
                EmptyState(
                    modifier = modifier.fillMaxSize(),
                    message = stringResource(
                        R.string.no_sources_found_for_category,
                        uiState.categoryDisplayName
                    )
                )
            }
        }

        else -> {
            val pagerState = rememberPagerState(pageCount = { uiState.sources.size })
            LaunchedEffect(uiState.selectedSourceId, uiState.sources) {
                val selectedIndex =
                    uiState.sources.indexOfFirst { it.id == uiState.selectedSourceId }
                if (selectedIndex != -1 && selectedIndex != pagerState.currentPage) {
                    scope.launch { pagerState.animateScrollToPage(selectedIndex) }
                }
            }

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .distinctUntilChanged()
                    .collect { pageIndex ->
                        uiState.sources.getOrNull(pageIndex)?.id?.let { sourceId ->
                            if (uiState.selectedSourceId != sourceId) {
                                onSourceSelected(sourceId)
                            }
                        }
                    }
            }


            Column(modifier = modifier.fillMaxSize()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (pagerState.currentPage < tabPositions.size) {
                            TabRowDefaults.PrimaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                width = tabPositions[pagerState.currentPage].contentWidth,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                            )
                        }
                    },
                    divider = {}
                ) {
                    uiState.sources.forEachIndexed { index, source ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    text = source.name
                                        ?: stringResource(id = R.string.unknown_source),
                                    style = if (pagerState.currentPage == index) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.onBackground,
                            unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.7f
                            )
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    val source = uiState.sources.getOrNull(pageIndex)
                    if (source?.id != null) {
                        SourceArticlePage(
                            sourceId = source.id,
                            sourceName = source.name ?: stringResource(R.string.unknown_source),
                            categoryNewsUiState = uiState,
                            onArticleClicked = onArticleClicked,
                            onLoadMoreArticles = onLoadMoreArticles,
                            onRetryForSourceArticles = onRetry
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.loading_source_data),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SourceArticlePage(
    sourceId: String,
    sourceName: String,
    categoryNewsUiState: CategoryNewsUiState,
    onArticleClicked: (Article) -> Unit,
    onLoadMoreArticles: () -> Unit,
    onRetryForSourceArticles: () -> Unit
) {
    val sourceState = categoryNewsUiState.articlesBySource[sourceId]
    val listState = rememberLazyListState()
    LaunchedEffect(listState, sourceState?.isLoading, sourceState?.allArticlesLoaded) {
        if (sourceState == null || sourceState.isLoading || sourceState.allArticlesLoaded) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                visibleItems.lastOrNull()?.index ?: -1
            }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                val totalItems = sourceState.articles.size
                if (totalItems > 0 && lastVisibleItemIndex >= totalItems - 5) {
                    if (categoryNewsUiState.selectedSourceId == sourceId) {
                        onLoadMoreArticles()
                    }
                }
            }
    }

    when {
        sourceState == null || (sourceState.isLoading && sourceState.articles.isEmpty()) -> {
            LoadingState(Modifier.fillMaxSize())
        }

        sourceState.errorMessage != null -> {
            ErrorState(
                modifier = Modifier.fillMaxSize(),
                message = sourceState.errorMessage,
                onRetry = onRetryForSourceArticles
            )
        }

        sourceState.articles.isEmpty() -> {
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(R.string.no_articles_from_source, sourceName)

            )
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    sourceState.articles,
                    key = { _, article -> article.url ?: article.hashCode().toString() }
                ) { _, article ->
                    NewsArticleItem(article = article, onClick = { onArticleClicked(article) })
                }

                if (sourceState.isLoading) {
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

                if (sourceState.allArticlesLoaded && sourceState.articles.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.all_articles_loaded_for_source, sourceName),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Content - Loading Sources")
@Composable
fun CategoryNewsContentPreview_LoadingSources() {
    NewsTheme {
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Tech",
                isLoadingSources = true
            ),
            onArticleClicked = {},
            onRetry = {},
            onLoadMoreArticles = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Global Error (No Sources)")
@Composable
fun CategoryNewsContentPreview_GlobalError() {
    NewsTheme {
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Science",
                isLoadingSources = false,
                sources = emptyList(),
                globalErrorMessage = "Network error fetching sources."
            ),
            onArticleClicked = {},
            onRetry = {},
            onLoadMoreArticles = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - No Sources Found")
@Composable
fun CategoryNewsContentPreview_NoSources() {
    NewsTheme {
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Lifestyle",
                isLoadingSources = false,
                sources = emptyList(),
                globalErrorMessage = null
            ),
            onArticleClicked = {},
            onRetry = {},
            onLoadMoreArticles = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - With Sources and Articles")
@Composable
fun CategoryNewsContentPreview_WithData() {
    val sampleSource1 = Source(id = "cnn", name = "CNN")
    val sampleSource2 = Source(id = "bbc", name = "BBC News")
    val sampleArticle1 = Article(
        source = sampleSource1,
        author = "John Doe",
        title = "CNN Article 1",
        url = "u1",
        description = "d1",
        publishedAt = "p1",
        urlToImage = "i1",
        content = "c1"
    )
    val sampleArticle2 = Article(
        source = sampleSource1,
        author = "Jane Smith",
        title = "CNN Article 2",
        url = "u2",
        description = "d2",
        publishedAt = "p2",
        urlToImage = "i2",
        content = "c2"
    )
    val sampleArticle3 = Article(
        source = sampleSource2,
        author = "AI News Bot",
        title = "BBC Article 1",
        url = "u3",
        description = "d3",
        publishedAt = "p3",
        urlToImage = "i3",
        content = "c3"
    )

    NewsTheme {
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "General",
                isLoadingSources = false,
                sources = listOf(sampleSource1, sampleSource2),
                selectedSourceId = "cnn",
                articlesBySource = mapOf(
                    "cnn" to SourceArticlesUiState(
                        articles = listOf(sampleArticle1, sampleArticle2),
                        isLoading = false,
                        allArticlesLoaded = false,
                        currentPage = 1,
                        totalArticles = 10
                    ),
                    "bbc" to SourceArticlesUiState(
                        articles = listOf(sampleArticle3),
                        isLoading = false,
                        allArticlesLoaded = true,
                        currentPage = 1,
                        totalArticles = 1
                    )
                )
            ),
            onArticleClicked = {},
            onRetry = {},
            onLoadMoreArticles = {},
            onSourceSelected = {}
        )
    }
}


@Preview(showBackground = true, name = "Page - Loading Articles for Source")
@Composable
fun SourceArticlePagePreview_Loading() {
    NewsTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SourceArticlePage(
                sourceId = "tech-crunch",
                sourceName = "TechCrunch",
                categoryNewsUiState = CategoryNewsUiState(
                    selectedSourceId = "tech-crunch",
                    articlesBySource = mapOf(
                        "tech-crunch" to SourceArticlesUiState(
                            isLoading = true,
                            articles = emptyList()
                        )
                    )
                ),
                onArticleClicked = {},
                onLoadMoreArticles = {},
                onRetryForSourceArticles = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Page - Error Articles for Source")
@Composable
fun SourceArticlePagePreview_Error() {
    NewsTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SourceArticlePage(
                sourceId = "verge",
                sourceName = "The Verge",
                categoryNewsUiState = CategoryNewsUiState(
                    selectedSourceId = "verge",
                    articlesBySource = mapOf(
                        "verge" to SourceArticlesUiState(errorMessage = "Failed to load articles for this source.")
                    )
                ),
                onArticleClicked = {},
                onLoadMoreArticles = {},
                onRetryForSourceArticles = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Page - Empty Articles for Source")
@Composable
fun SourceArticlePagePreview_Empty() {
    NewsTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SourceArticlePage(
                sourceId = "reuters",
                sourceName = "Reuters",
                categoryNewsUiState = CategoryNewsUiState(
                    selectedSourceId = "reuters",
                    articlesBySource = mapOf(
                        "reuters" to SourceArticlesUiState(
                            articles = emptyList(),
                            isLoading = false
                        )
                    )
                ),
                onArticleClicked = {},
                onLoadMoreArticles = {},
                onRetryForSourceArticles = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Page - Loading More Articles")
@Composable
fun SourceArticlePagePreview_LoadingMore() {
    val sampleSource1 = Source(id = "ap", name = "Associated Press")
    val sampleArticle1 = Article(
        source = sampleSource1,
        author = "Reporter X",
        title = "AP Article 1",
        url = "u1",
        description = "d1",
        publishedAt = "p1",
        urlToImage = "i1",
        content = "c1"
    )
    NewsTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SourceArticlePage(
                sourceId = "ap",
                sourceName = "Associated Press",
                categoryNewsUiState = CategoryNewsUiState(
                    selectedSourceId = "ap",
                    articlesBySource = mapOf(
                        "ap" to SourceArticlesUiState(
                            articles = listOf(sampleArticle1),
                            isLoading = true,
                            allArticlesLoaded = false,
                            currentPage = 2
                        )
                    )
                ),
                onArticleClicked = {},
                onLoadMoreArticles = {},
                onRetryForSourceArticles = {}
            )
        }
    }
}
