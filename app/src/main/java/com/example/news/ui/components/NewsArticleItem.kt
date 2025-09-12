package com.example.news.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.domain.model.Source
import com.example.news.ui.theme.NewsTheme
import java.text.NumberFormat
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun NewsArticleItem(
    article: Article,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val title = article.title ?: stringResource(R.string.no_title)
    val imageUrl = article.urlToImage
    val publishedTime = article.publishedAt?.formatPublishedDate() ?: "_"
    val displayAuthor =
        article.author?.let { stringResource(R.string.by_author_prefix, article.author) } ?: "_"

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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayAuthor,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = publishedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
fun String.formatPublishedDate(): String {
    val now = ZonedDateTime.now()
    val zonedDateTime = ZonedDateTime.parse(this)
    val duration = Duration.between(zonedDateTime, now)

    val currentLocale = LocalResources.current.configuration.locales[0]
    val numberFormat = NumberFormat.getInstance(currentLocale)

    return when {
        duration.toDays() == 0L -> stringResource(R.string.recently)
        duration.toDays() == 1L -> stringResource(R.string.yesterday)
        duration.toDays() < 7L -> stringResource(
            R.string.days_ago,
            numberFormat.format(duration.toDays())
        )

        duration.toDays() < 30L -> stringResource(
            R.string.weeks_ago,
            numberFormat.format(duration.toDays() / 7)
        )

        duration.toDays() < 365L -> stringResource(
            R.string.months_ago,
            numberFormat.format(duration.toDays() / 30)
        )

        duration.toDays() == 365L -> stringResource(
            R.string.since_year,
            numberFormat.format(duration.toDays() / 365)
        )

        else -> stringResource(
            R.string.years_ago, numberFormat.format(duration.toDays() / 365)
        )
    }
}

@Preview
@Preview(locale = "ar")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NewsArticleItemPreview() {
    val article = Article(
        source = Source(id = "cnn", name = "CNN"),
        author = "John Doe, Jane Smith",
        title = "Breaking News: A Major Event Happened Today and It\'s Big",
        description = "This is a longer description of the major event that happened today. It provides more details and context to the news article.",
        url = "https://www.example.com/news/breaking-news-event",
        urlToImage = "https://www.example.com/images/breaking-news.jpg",
        publishedAt = "2023-10-27T10:30:00Z",
        content = "Detailed content of the news article goes here..."
    )
    NewsTheme {
        NewsArticleItem(
            article = article,
            onClick = {}
        )
    }
}
