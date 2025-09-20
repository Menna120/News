package com.example.news.ui.screens.search.model

sealed interface SearchScreenUiState {
    data object Idle : SearchScreenUiState
    data class ActiveSearch(val query: String) : SearchScreenUiState
}
