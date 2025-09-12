package com.example.news.domain.usecase

import android.util.Log
import com.example.news.data.repository.NewsRepository
import com.example.news.domain.model.ArticlesPageData
import com.example.news.domain.model.NewsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

private const val TAG = "GetSourceNewsUseCase"

class GetSourceArticlesUseCase @Inject constructor(
    private val repo: NewsRepository
) {
    operator fun invoke(
        sourceId: String,
        page: Int,
        pageSize: Int
    ): Flow<NewsResult<ArticlesPageData>> = flow {
        try {
            Log.d(TAG, "Fetching news for source: $sourceId, page: $page, pageSize: $pageSize")
            emit(NewsResult.Loading())
            val response =
                repo.getSourceArticles(
                    sourceId = sourceId,
                    page = page,
                    pageSize = pageSize
                )
            if (response.status == "ok") {
                val articles = response.articles
                val totalResults = response.totalResults
                Log.d(
                    TAG,
                    "Successfully fetched ${articles.size} articles for source: $sourceId. Total results: $totalResults"
                )
                emit(NewsResult.Success(ArticlesPageData(articles, totalResults)))
            } else {
                val errorMessage = response.message
                    ?: "Unknown API error while fetching news for source $sourceId"
                Log.e(
                    TAG,
                    "API error fetching news for source $sourceId: $errorMessage, Code: ${response.code}"
                )
                emit(NewsResult.Error(errorMessage, response.code))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching news for source: $sourceId", e)
            emit(NewsResult.NetworkError())
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error fetching news for source: $sourceId", e)
            emit(NewsResult.Error(e.message ?: "An unexpected error occurred."))
        }
    }
}
