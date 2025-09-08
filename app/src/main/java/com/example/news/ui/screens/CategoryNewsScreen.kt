package com.example.news.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.news.R
import com.example.news.navigation.CategoryNews
import com.example.news.ui.theme.NewsTheme

// 1. Data class for News Article
data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val time: String, // e.g., "15 minutes ago"
    val imageUrl: String? = null, // For actual image loading later
    val placeholderImageResId: Int = R.drawable.ic_news_logo // Default placeholder
)

// 2. Sample News Data (per category)
fun getSampleNewsForCategory(categoryName: String): List<NewsArticle> {
    return List(10) { index ->
        NewsArticle(
            id = "$categoryName-$index",
            title = "Article Title ${index + 1} for $categoryName: A Long Title That Might Wrap to Multiple Lines",
            source = "News Source ${index + 1}",
            time = "${(index + 1) * 5} minutes ago"
        )
    }
}

@Composable
fun CategoryNewsScreen(categoryNews: CategoryNews, modifier: Modifier = Modifier) {
    val articles = getSampleNewsForCategory(categoryNews.categoryName)

    // 3. LazyColumn to display articles
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(articles) { article ->
            NewsArticleItem(article = article)
            Spacer(modifier = Modifier.height(8.dp)) // Space between items
        }
    }
}

// 4. NewsArticleItem Composable
@Composable
fun NewsArticleItem(article: NewsArticle, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = article.placeholderImageResId),
                contentDescription = article.title,
                modifier = Modifier
                    .size(100.dp) // Adjust size as needed
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryNewsScreenPreview() {
    NewsTheme {
        CategoryNewsScreen(CategoryNews("Technology"))
    }
}

@Preview(showBackground = true)
@Composable
fun NewsArticleItemPreview() {
    NewsTheme {
        NewsArticleItem(
            article = NewsArticle(
                id = "preview-1",
                title = "Sample Article: A Very Long Title Example That Will Show How Ellipsis Works",
                source = "Preview Source",
                time = "Just now"
            )
        )
    }
}
