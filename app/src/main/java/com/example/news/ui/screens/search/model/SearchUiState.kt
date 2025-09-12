package com.example.news.ui.screens.search.model

import com.example.news.domain.model.Article

sealed interface SearchUiState {
    data object Initial : SearchUiState
    data object Loading : SearchUiState
    data class Success(
        val query: String,
        val articles: List<Article>,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true,
        val currentPage: Int = 1,
        val paginationErrorMessage: String? = null
    ) : SearchUiState

    data class Error(val message: String, val query: String?) : SearchUiState
    data class NoResults(val query: String) : SearchUiState
}
