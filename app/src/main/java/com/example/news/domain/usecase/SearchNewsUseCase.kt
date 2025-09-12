package com.example.news.domain.usecase

import com.example.news.data.repository.NewsRepository
import com.example.news.domain.model.Article
import com.example.news.domain.model.NewsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(private val newsRepository: NewsRepository) {
    operator fun invoke(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = "relevancy"
    ): Flow<NewsResult<List<Article>>> = flow {
        emit(NewsResult.Loading())

        if (query.isBlank()) {
            emit(NewsResult.Error("Query cannot be empty."))
        } else {
            try {
                val response = newsRepository.searchNews(
                    query = query,
                    sortBy = sortBy,
                    page = page,
                    pageSize = pageSize
                )

                if (response.status == "ok") {
                    emit(NewsResult.Success(response.articles))
                } else {
                    emit(
                        NewsResult.Error(
                            message = response.message ?: "API error during search.",
                            code = response.code
                        )
                    )
                }
            } catch (_: IOException) {
                emit(NewsResult.NetworkError())
            } catch (e: Exception) {
                emit(
                    NewsResult.Error(
                        message = e.message ?: "An unexpected error occurred during search."
                    )
                )
            }
        }
    }
}
