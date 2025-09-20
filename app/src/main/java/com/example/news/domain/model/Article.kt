package com.example.news.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val source: Source?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
) : java.io.Serializable {
    companion object {
        val empty = Article(
            source = null,
            author = null,
            title = "",
            description = null,
            url = "",
            urlToImage = null,
            publishedAt = "",
            content = null
        )
    }
}
