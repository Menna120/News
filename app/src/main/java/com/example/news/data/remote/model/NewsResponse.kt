package com.example.news.data.remote.model

import com.example.news.domain.model.Article
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status: String,
    val articles: List<Article>,
    val totalResults: Int,
    val code: String? = null,
    val message: String? = null
)
