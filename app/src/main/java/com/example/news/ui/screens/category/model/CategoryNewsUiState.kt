package com.example.news.ui.screens.category.model

import com.example.news.domain.model.Source

data class CategoryNewsUiState(
    val categoryDisplayName: String = "",
    val isLoadingSources: Boolean = true,
    val sources: List<Source> = emptyList(),
    val selectedSourceId: String? = null,
    val globalErrorMessage: String? = null
)
