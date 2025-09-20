package com.example.news.ui.screens.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.domain.model.Source
import com.example.news.ui.screens.category.components.ArticleBottomSheet
import com.example.news.ui.screens.category.components.EmptyState
import com.example.news.ui.screens.category.components.ErrorState
import com.example.news.ui.screens.category.components.LoadingState
import com.example.news.ui.screens.category.components.SourceArticlePage
import com.example.news.ui.screens.category.model.CategoryNewsUiState
import com.example.news.ui.theme.NewsTheme
import com.example.news.utils.OpenUrlInExternalBrowser
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryNewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val articlesPagingItems = viewModel.articlesPagingData.collectAsLazyPagingItems()
    val context = LocalContext.current

    var showSheet by remember { mutableStateOf(false) }
    var selectedArticle by remember { mutableStateOf<Article?>(null) }

    CategoryNewsContent(
        modifier = modifier,
        uiState = uiState,
        articlesPagingItems = articlesPagingItems,
        onArticleClicked = { article ->
            selectedArticle = article
            showSheet = true
        },
        onRetrySources = viewModel::retryFetchingSources,
        onSourceSelected = viewModel::onSourceSelected
    )

    if (showSheet && selectedArticle != null) {
        ArticleBottomSheet(
            article = selectedArticle!!,
            onDismiss = { showSheet = false },
            onOpenArticle = { url ->
                OpenUrlInExternalBrowser.openBrowser(context, url)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsContent(
    modifier: Modifier = Modifier,
    uiState: CategoryNewsUiState,
    articlesPagingItems: LazyPagingItems<Article>,
    onArticleClicked: (Article) -> Unit,
    onRetrySources: () -> Unit,
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
                    onRetry = onRetrySources
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
                    verticalAlignment = Alignment.Top,
                    key = { pageIndex -> uiState.sources[pageIndex].id!! }
                ) { pageIndex ->
                    val source = uiState.sources.getOrNull(pageIndex)
                    if (source?.id != null) {
                        SourceArticlePage(
                            sourceName = source.name ?: stringResource(R.string.unknown_source),
                            articlesPagingItems = articlesPagingItems,
                            onArticleClicked = onArticleClicked
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

@Preview(showBackground = true, name = "Content - Loading Sources")
@Composable
fun CategoryNewsContentPreview_LoadingSources() {
    NewsTheme {
        val emptyPagingItems = emptyFlow<PagingData<Article>>().collectAsLazyPagingItems()
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Tech",
                isLoadingSources = true
            ),
            articlesPagingItems = emptyPagingItems,
            onArticleClicked = {},
            onRetrySources = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Global Error (No Sources)")
@Composable
fun CategoryNewsContentPreview_GlobalError() {
    NewsTheme {
        val emptyPagingItems = emptyFlow<PagingData<Article>>().collectAsLazyPagingItems()
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Science",
                isLoadingSources = false,
                sources = emptyList(),
                globalErrorMessage = "Network error fetching sources."
            ),
            articlesPagingItems = emptyPagingItems,
            onArticleClicked = {},
            onRetrySources = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - No Sources Found")
@Composable
fun CategoryNewsContentPreview_NoSources() {
    NewsTheme {
        val emptyPagingItems = emptyFlow<PagingData<Article>>().collectAsLazyPagingItems()
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "Lifestyle",
                isLoadingSources = false,
                sources = emptyList(),
                globalErrorMessage = null
            ),
            articlesPagingItems = emptyPagingItems,
            onArticleClicked = {},
            onRetrySources = {},
            onSourceSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - With Sources (Articles not shown in preview)")
@Composable
fun CategoryNewsContentPreview_WithSources() {
    val sampleSource1 = Source(id = "cnn", name = "CNN")
    val sampleSource2 = Source(id = "bbc", name = "BBC News")
    NewsTheme {
        val emptyPagingItems = emptyFlow<PagingData<Article>>().collectAsLazyPagingItems()
        CategoryNewsContent(
            uiState = CategoryNewsUiState(
                categoryDisplayName = "General",
                isLoadingSources = false,
                sources = listOf(sampleSource1, sampleSource2),
                selectedSourceId = "cnn"
            ),
            articlesPagingItems = emptyPagingItems,
            onArticleClicked = {},
            onRetrySources = {},
            onSourceSelected = {}
        )
    }
}
