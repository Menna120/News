package com.example.news.domain.usecase

import android.util.Log
import com.example.news.data.repository.NewsRepository
import com.example.news.domain.model.NewsResult
import com.example.news.domain.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

private const val TAG = "GetCategorySourcesUseCase"

class GetCategorySourcesUseCase @Inject constructor(
    private val repo: NewsRepository
) {
    operator fun invoke(categoryName: String): Flow<NewsResult<List<Source>>> = flow {
        try {
            Log.d(TAG, "Fetching sources for category: $categoryName")
            emit(NewsResult.Loading())
            val response = repo.getCategorySources(category = categoryName)
            if (response.status == "ok") {
                val sources = response.sources ?: emptyList()
                Log.d(
                    TAG,
                    "Successfully fetched ${sources.size} sources for category: $categoryName"
                )
                emit(NewsResult.Success(sources))
            } else {
                val errorMessage = response.message ?: "Unknown API error while fetching sources"
                Log.e(TAG, "API error fetching sources: $errorMessage, Code: ${response.code}")
                emit(NewsResult.Error(errorMessage, response.code))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching sources for category: $categoryName", e)
            emit(NewsResult.NetworkError())
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error fetching sources for category: $categoryName", e)
            emit(NewsResult.Error(e.message ?: "An unexpected error occurred."))
        }
    }
}
