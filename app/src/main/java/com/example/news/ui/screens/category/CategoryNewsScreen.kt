package com.example.news.ui.screens.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.data.NewsArticle
import com.example.news.data.NewsArticle.Companion.getSampleNewsForCategory
import com.example.news.ui.navigation.FullArticle
import com.example.news.ui.theme.NewsTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryNewsScreen(
    categoryName: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val allArticles = remember(categoryName) {
        getSampleNewsForCategory(categoryName)
    }
    val sources = remember(allArticles) { allArticles.map { it.source }.distinct() }
    var selectedSourceIndex by remember { mutableIntStateOf(0) }

    val filteredArticles = if (sources.isNotEmpty()) {
        allArticles.filter { it.source == sources[selectedSourceIndex] }
    } else {
        emptyList()
    }
    var showSheet by remember { mutableStateOf(false) }
    var selectedArticle by remember { mutableStateOf<NewsArticle?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        if (sources.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedSourceIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSourceIndex]),
                        width = tabPositions[selectedSourceIndex].contentWidth,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                    )
                },
                divider = {}
            ) {
                sources.forEachIndexed { index, source ->
                    Tab(
                        selected = selectedSourceIndex == index,
                        onClick = { selectedSourceIndex = index },
                        text = {
                            Text(
                                text = source,
                                style = if (selectedSourceIndex == index) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredArticles, key = { it.id }) { article ->
                NewsArticleItem(article) {
                    selectedArticle = article
                    showSheet = true
                }
            }
        }
    }

    if (showSheet && selectedArticle != null) {
        val sheetState = rememberModalBottomSheetState(true)
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = {},
            containerColor = Color.Transparent,
            contentWindowInsets = { WindowInsets.safeContent }
        ) {
            Card(
                modifier = modifier // Changed: Used the modifier from parameters
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = selectedArticle!!.imageUrl,
                        contentDescription = selectedArticle!!.title,
                        error = painterResource(id = R.drawable.ic_broken_image),
                        placeholder = painterResource(id = R.drawable.ic_news_logo),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.77f)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = selectedArticle?.content
                            ?: stringResource(R.string.no_content_available),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Button(
                        onClick = {
                            navController.navigate(FullArticle(selectedArticle!!.articleUrl)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            showSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.view_full_article),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryNewsScreenPreview() {
    val navController = rememberNavController()

    NewsTheme {
        CategoryNewsScreen(categoryName = "Technology", navController = navController)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun NewsArticleItemPreview() {
    val article = NewsArticle(
        id = "preview-1", // Changed to String
        title = "Sample Article Title: A Long Title That Might Wrap to Multiple Lines",
        source = "Sample News Source",
        time = "10 minutes ago",
        articleUrl = "https://developer.android.com/develop/ui/compose/navigation#kotlin", // Added for preview
        imageUrl = "https://picsum.photos/seed/sample/600/400",
        authors = listOf("Author A", "Author B"),
        content = "This is a sample article content. It is a long piece of text that describes the details of the news article."
    )

    NewsTheme {
        NewsArticleItem(article = article) {}
    }
}


@Composable
fun NewsArticleItem(article: NewsArticle, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.ic_news_logo),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.77f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .2f)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "By ${article.authors.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = article.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
