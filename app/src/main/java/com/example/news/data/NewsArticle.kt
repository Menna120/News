package com.example.news.data

import com.example.news.R

data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val time: String,
    val articleUrl: String,
    val imageUrl: String? = null,
    val placeholderImageResId: Int = R.drawable.ic_news_logo,
    val authors: List<String> = listOf("Unknown Author"),
    val content: String = "This is a sample article content. It is a long piece of text that describes the details of the news article. This content can span multiple paragraphs and provide in-depth information about the topic."
) {
    companion object {
        fun getSampleNewsForCategory(categoryName: String): List<NewsArticle> {
            return List(10) { index ->
                val articleId = "$categoryName-${index + 1}"
                NewsArticle(
                    id = articleId,
                    title = "Article Title ${index + 1} for $categoryName: A Long Title That Might Wrap to Multiple Lines",
                    source = "News Source ${index + 1}",
                    time = "${(index + 1) * 5} minutes ago",
                    articleUrl = "https://developer.android.com/develop/ui/compose/navigation#kotlin",
                    imageUrl = if (index % 2 == 0) "https://picsum.photos/seed/${
                        categoryName.replace(
                            " ",
                            ""
                        )
                    }-$index/600/400" else null,
                    authors = listOf(
                        "Author ${index + 1}A",
                        "Author ${index + 1}B"
                    ),
                    content = "This is the full content for Article Title ${index + 1} for $categoryName. It provides more details and context about the news story. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                )
            }
        }
    }
}
