package com.example.news.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.news.domain.model.Article
import com.example.news.domain.model.NewsResult
import com.example.news.domain.usecase.GetCategorySourcesUseCase
import com.example.news.domain.usecase.GetSourceArticlesUseCase
import com.example.news.ui.screens.category.model.CategoryNewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryNewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCategorySourcesUseCase: GetCategorySourcesUseCase,
    private val getSourceArticlesUseCase: GetSourceArticlesUseCase
) : ViewModel() {

    private val categoryName: String = savedStateHandle.get<String>("categoryName") ?: ""

    private val _uiState =
        MutableStateFlow(CategoryNewsUiState(categoryDisplayName = categoryName.replaceFirstChar { it.uppercase() }))
    val uiState: StateFlow<CategoryNewsUiState> = _uiState.asStateFlow()

    val articlesPagingData: Flow<PagingData<Article>> = uiState
        .map { it.selectedSourceId }
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { sourceId ->
            getSourceArticlesUseCase(sourceId)
                .map { pagingData ->
                    pagingData.map { article ->
                        article.validateArticle() ?: Article.empty
                    }
                }
                .map { pagingData ->
                    pagingData.filter { it != Article.empty }
                }
        }
        .cachedIn(viewModelScope)

    init {
        fetchSourcesForCategory()
    }

    private fun fetchSourcesForCategory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSources = true, globalErrorMessage = null) }
            getCategorySourcesUseCase(categoryName).collectLatest { result ->
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

                    is NewsResult.Loading -> Unit
                }
            }
        }
    }

    fun onSourceSelected(sourceId: String) {
        _uiState.update { it.copy(selectedSourceId = sourceId) }
    }

    fun retryFetchingSources() = fetchSourcesForCategory()

    private fun Article.validateArticle(): Article? {
        if (this.url.isNullOrBlank()
            || this.title.isNullOrBlank()
            || this.source?.id.isNullOrBlank()
            || this.source.name.isNullOrBlank()
        ) return null
        return this
    }
}
