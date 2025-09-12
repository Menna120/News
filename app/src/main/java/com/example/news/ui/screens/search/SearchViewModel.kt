package com.example.news.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.model.NewsResult
import com.example.news.domain.usecase.SearchNewsUseCase
import com.example.news.ui.screens.search.model.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var activeQuery: String = ""

    @Volatile
    private var isFetchInProgress = false
    private val searchResultsPerPage = 20
    fun processExternalSearchQuery(query: String?) {
        val trimmedQuery = query?.trim()

        if (trimmedQuery.isNullOrBlank()) {
            if (_uiState.value !is SearchUiState.Initial) {
                clearSearch()
            }
        } else {
            val currentState = _uiState.value
            val currentQueryInState = when (currentState) {
                is SearchUiState.Success -> currentState.query
                is SearchUiState.Error -> currentState.query
                is SearchUiState.NoResults -> currentState.query
                is SearchUiState.Loading -> activeQuery
                is SearchUiState.Initial -> null
            }

            if (trimmedQuery != currentQueryInState || currentState is SearchUiState.Error) {
                performSearch(trimmedQuery)
            }
        }
    }

    fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        searchJob?.cancel()

        if (trimmedQuery.isEmpty()) {
            clearSearch()
            return
        }
        activeQuery = trimmedQuery
        initiateSearchExecution(trimmedQuery)
    }

    private fun initiateSearchExecution(query: String) {
        if (isFetchInProgress && _uiState.value is SearchUiState.Loading && activeQuery == query) {
            return
        }

        _uiState.value = SearchUiState.Loading
        executeSearch(query, pageToLoad = 1, isInitialSearch = true)
    }

    private fun executeSearch(query: String, pageToLoad: Int, isInitialSearch: Boolean) {
        if (isFetchInProgress) {
            return
        }
        isFetchInProgress = true

        if (!isInitialSearch && _uiState.value is SearchUiState.Success) {
            val currentSuccessState = _uiState.value as SearchUiState.Success
            if (currentSuccessState.query == query) {
                _uiState.value =
                    currentSuccessState.copy(isLoadingMore = true, paginationErrorMessage = null)
            } else {
                isFetchInProgress = false
                initiateSearchExecution(query)
                return
            }
        } else if (isInitialSearch && _uiState.value !is SearchUiState.Loading) {
            _uiState.value = SearchUiState.Loading
        }

        viewModelScope.launch {
            try {
                searchNewsUseCase(
                    query = query,
                    page = pageToLoad,
                    pageSize = searchResultsPerPage
                ).collect { newsResult ->
                    when (newsResult) {
                        is NewsResult.Success -> {
                            val newArticles = newsResult.data ?: emptyList()
                            _uiState.update { currentState ->
                                val existingArticles =
                                    if (!isInitialSearch && currentState is SearchUiState.Success && currentState.query == query) {
                                        currentState.articles
                                    } else {
                                        emptyList()
                                    }
                                val combinedArticles =
                                    (existingArticles + newArticles).distinctBy { it.url }

                                if (isInitialSearch && combinedArticles.isEmpty()) {
                                    SearchUiState.NoResults(query)
                                } else {
                                    SearchUiState.Success(
                                        query = query,
                                        articles = combinedArticles,
                                        isLoadingMore = false,
                                        canLoadMore = newArticles.isNotEmpty() && newArticles.size == searchResultsPerPage,
                                        currentPage = pageToLoad,
                                        paginationErrorMessage = null
                                    )
                                }
                            }
                        }

                        is NewsResult.Error, is NewsResult.NetworkError -> {
                            val errorMessage = when (newsResult) {
                                is NewsResult.Error -> "Error: ${newsResult.message} (Code: ${newsResult.code ?: "N/A"})"
                                is NewsResult.NetworkError -> "Network error. Please check your connection."
                                else -> "An unknown error occurred."
                            }

                            if (isInitialSearch) {
                                _uiState.value = SearchUiState.Error(errorMessage, query)
                            } else {
                                _uiState.update { currentState ->
                                    if (currentState is SearchUiState.Success && currentState.query == query) {
                                        currentState.copy(
                                            isLoadingMore = false,
                                            paginationErrorMessage = errorMessage
                                        )
                                    } else {
                                        SearchUiState.Error(errorMessage, query)
                                    }
                                }
                            }
                        }

                        is NewsResult.Loading -> {
                        }
                    }
                }
            } finally {
                isFetchInProgress = false
            }
        }
    }

    fun loadMoreResults() {
        val currentState = _uiState.value
        if (currentState is SearchUiState.Success &&
            !currentState.isLoadingMore &&
            currentState.canLoadMore &&
            !isFetchInProgress &&
            currentState.paginationErrorMessage == null
        ) {
            val nextPage = currentState.currentPage + 1
            executeSearch(currentState.query, nextPage, isInitialSearch = false)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        activeQuery = ""
        isFetchInProgress = false
        _uiState.value = SearchUiState.Initial
    }

    fun retrySearch() {
        val queryToRetry = when (val currentUiStateVal = _uiState.value) {
            is SearchUiState.Error -> currentUiStateVal.query
            is SearchUiState.NoResults -> currentUiStateVal.query
            else -> null
        }
        queryToRetry?.let {
            if (it.isNotBlank()) {
                performSearch(it)
            }
        }
    }

    fun retryLoadMore() {
        val currentState = _uiState.value
        if (currentState is SearchUiState.Success &&
            currentState.paginationErrorMessage != null &&
            !currentState.isLoadingMore &&
            !isFetchInProgress
        ) {
            val pageToRetry = currentState.currentPage + 1
            executeSearch(currentState.query, pageToRetry, isInitialSearch = false)
        }
    }
}
