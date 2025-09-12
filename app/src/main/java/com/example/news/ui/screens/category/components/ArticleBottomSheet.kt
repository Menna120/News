package com.example.news.ui.screens.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.domain.model.Source
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleBottomSheet(
    article: Article,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onOpenArticle: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
        contentWindowInsets = { WindowInsets.safeContent }
    ) {
        Card(
            modifier = modifier
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
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = article.title ?: stringResource(R.string.article_image),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(id = R.drawable.ic_news_logo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.77f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = article.content ?: article.description
                    ?: stringResource(R.string.no_content_available),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.background
                )

                Button(
                    onClick = {
                        article.url?.let { onOpenArticle(it) }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) onDismiss()
                        }
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
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ArticleBottomSheetPreview() {
    val article = Article(
        source = Source(
            id = "the-verge",
            name = "The Verge",
            description = "The Verge is an American technology news and media network operated by Vox Media.",
            url = "https://www.theverge.com/",
            category = "technology",
            language = "en",
            country = "us"
        ),
        author = "Richard Lawler",
        title = "Netflix’s latest hit Is a true crime series about a terrifying real-life home invasion",
        description = "Netflix’s latest hit series is American Nightmare, a three-part true crime documentary about a home invasion and kidnapping that police initially dismissed as a hoax.",
        url = "https://www.theverge.com/2024/1/18/24042674/netflix-american-nightmare-true-crime-gone-girl",
        urlToImage = "https://cdn.vox-cdn.com/thumbor/MMB6b30OBQRN2C2LqyAXfQz0n1c=/0x0:1920x1080/1200x628/filters:focal(960x540:961x541)/cdn.vox-cdn.com/uploads/chorus_asset/file/25232145/American_Nightmare_S1_E1_00_12_18_13_R.jpg",
        publishedAt = "2024-01-18T16:00:00Z",
        content = "American Nightmare. Cr. Courtesy of Netflix © 2023\r\n" +
                "\r\n" +
                "Netflix’s latest hit series is American Nightmare, a three-part true crime documentary about a home invasion and kidnapping that police initially di… [+1701 chars]"
    )
    ArticleBottomSheet(article = article, onDismiss = {}, onOpenArticle = {})
}
