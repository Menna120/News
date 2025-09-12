package com.example.news.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.model.Article
import com.example.news.domain.model.NewsResult
import com.example.news.domain.usecase.GetCategorySourcesUseCase
import com.example.news.domain.usecase.GetSourceNewsUseCase
import com.example.news.ui.screens.category.model.CategoryNewsUiState
import com.example.news.ui.screens.category.model.SourceArticlesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 5

@HiltViewModel
class CategoryNewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCategorySourcesUseCase: GetCategorySourcesUseCase,
    private val getSourceNewsUseCase: GetSourceNewsUseCase
) : ViewModel() {

    private val categoryName: String = savedStateHandle.get<String>("categoryName") ?: ""

    private val _uiState =
        MutableStateFlow(CategoryNewsUiState(categoryDisplayName = categoryName.replaceFirstChar { it.uppercase() }))
    val uiState: StateFlow<CategoryNewsUiState> = _uiState.asStateFlow()

    init {
        fetchSourcesForCategory()
    }

    private fun fetchSourcesForCategory() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoadingSources = true, globalErrorMessage = null)
            }
            getCategorySourcesUseCase(categoryName)
                .collectLatest { result ->
                    when (result) {
                        is NewsResult.Success -> {
                            val fetchedSources = result.data ?: emptyList()
                            _uiState.update {
                                it.copy(
                                    isLoadingSources = false,
                                    sources = fetchedSources,
                                    selectedSourceId = fetchedSources.firstOrNull()?.id
                                )
                            }
                            _uiState.value.selectedSourceId?.let { sourceId ->
                                if (_uiState.value.articlesBySource[sourceId]?.articles?.isEmpty() != false) {
                                    fetchArticlesForSource(sourceId, isInitialLoad = true)
                                }
                            }
                        }

                        is NewsResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoadingSources = false,
                                    globalErrorMessage = result.message ?: "Error fetching sources"
                                )
                            }
                        }

                        is NewsResult.NetworkError -> {
                            _uiState.update {
                                it.copy(
                                    isLoadingSources = false,
                                    globalErrorMessage = "Network error fetching sources. Please check your connection."
                                )
                            }
                        }

                        is NewsResult.Loading -> {
                        }
                    }
                }
        }
    }

    fun onSourceSelected(sourceId: String) {
        val currentSourceState = _uiState.value.articlesBySource[sourceId]
        _uiState.update { it.copy(selectedSourceId = sourceId) }

        if (currentSourceState == null || currentSourceState.articles.isEmpty()) {
            fetchArticlesForSource(sourceId, isInitialLoad = true)
        }
    }

    private fun fetchArticlesForSource(sourceId: String, isInitialLoad: Boolean) {
        viewModelScope.launch {
            val currentSourceData =
                _uiState.value.articlesBySource[sourceId] ?: SourceArticlesUiState()
            val nextPage = if (isInitialLoad) 1 else currentSourceData.currentPage + 1

            _uiState.update { currentState ->
                val updatedMap = currentState.articlesBySource.toMutableMap()
                updatedMap[sourceId] = currentSourceData.copy(isLoading = true, errorMessage = null)
                currentState.copy(articlesBySource = updatedMap)
            }

            getSourceNewsUseCase(
                sourceId,
                page = nextPage,
                pageSize = PAGE_SIZE
            )
                .collectLatest { result ->
                    val currentArticlesMap = _uiState.value.articlesBySource
                    val sourceStateToUpdate =
                        currentArticlesMap[sourceId] ?: SourceArticlesUiState()


                    when (result) {
                        is NewsResult.Success -> {
                            val articlesPageData = result.data
                            val newArticles =
                                articlesPageData?.articles?.mapNotNull { it.validateArticle() }
                                    ?: emptyList()
                            val totalResultsForSource = articlesPageData?.totalArticles ?: 0

                            val updatedArticles =
                                if (isInitialLoad) newArticles else sourceStateToUpdate.articles + newArticles
                            _uiState.update { currentState ->
                                val updatedMap = currentState.articlesBySource.toMutableMap()
                                updatedMap[sourceId] = sourceStateToUpdate.copy(
                                    articles = updatedArticles,
                                    isLoading = false,
                                    currentPage = nextPage,
                                    allArticlesLoaded = updatedArticles.size >= totalResultsForSource || newArticles.isEmpty(),
                                    totalArticles = totalResultsForSource
                                )
                                currentState.copy(articlesBySource = updatedMap)
                            }
                        }

                        is NewsResult.Error -> {
                            _uiState.update { currentState ->
                                val updatedMap = currentState.articlesBySource.toMutableMap()
                                updatedMap[sourceId] = sourceStateToUpdate.copy(
                                    isLoading = false,
                                    errorMessage = result.message ?: "Error fetching articles"
                                )
                                currentState.copy(articlesBySource = updatedMap)
                            }
                        }

                        is NewsResult.NetworkError -> {
                            _uiState.update { currentState ->
                                val updatedMap = currentState.articlesBySource.toMutableMap()
                                updatedMap[sourceId] = sourceStateToUpdate.copy(
                                    isLoading = false,
                                    errorMessage = "Network error. Please check your connection."
                                )
                                currentState.copy(articlesBySource = updatedMap)
                            }
                        }

                        is NewsResult.Loading -> {
                        }
                    }
                }
        }
    }

    fun loadMoreArticles() {
        val selectedSourceId = _uiState.value.selectedSourceId ?: return
        val currentSourceState = _uiState.value.articlesBySource[selectedSourceId] ?: return

        if (!currentSourceState.isLoading && !currentSourceState.allArticlesLoaded) {
            fetchArticlesForSource(selectedSourceId, isInitialLoad = false)
        }
    }

    fun retryFetchingNews() {
        if (_uiState.value.sources.isEmpty() && _uiState.value.globalErrorMessage != null) {
            fetchSourcesForCategory()
        } else {
            _uiState.value.selectedSourceId?.let { sourceId ->
                val sourceState = _uiState.value.articlesBySource[sourceId]
                if (sourceState != null && sourceState.articles.isEmpty() && sourceState.errorMessage != null) {
                    fetchArticlesForSource(sourceId, isInitialLoad = true)
                }
            }
        }
    }

    private fun Article.validateArticle(): Article? {
        if (this.url.isNullOrBlank()
            || this.title.isNullOrBlank()
            || this.source?.id.isNullOrBlank()
            || this.source.name.isNullOrBlank()
        ) return null

        return this
    }
}
