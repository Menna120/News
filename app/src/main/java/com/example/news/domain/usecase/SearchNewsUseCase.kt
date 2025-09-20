package com.example.news.domain.usecase

import androidx.paging.PagingData
import com.example.news.data.repository.NewsRepository
import com.example.news.domain.model.Article
// import com.example.news.domain.model.NewsResult //  No longer used by this use case
import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.flow // No longer needed for manual flow construction
// import java.io.IOException // No longer handled here directly
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(private val newsRepository: NewsRepository) {
    operator fun invoke(
        query: String,
        sortBy: String? = "relevancy"
    ): Flow<PagingData<Article>> =
        newsRepository.searchNewsStream(query = query, sortBy = sortBy)
}
