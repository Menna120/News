package com.example.news.ui.screens.category.model

import com.example.news.domain.model.Article

data class SourceArticlesUiState(
    val articles: List<Article> = emptyList(),
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val allArticlesLoaded: Boolean = false,
    val errorMessage: String? = null,
    val totalArticles: Int = 0
)
