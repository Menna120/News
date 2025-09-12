package com.example.news.data.repository

import com.example.news.data.remote.NewsApiService
import com.example.news.data.remote.model.NewsResponse
import com.example.news.data.remote.model.SourceResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService
) {

    suspend fun getCategorySources(category: String? = null): SourceResponse =
        newsApiService.getSources(category = category)

    suspend fun getSourceArticles(
        sourceId: String,
        page: Int,
        pageSize: Int
    ): NewsResponse {
        return newsApiService.getNews(
            sources = sourceId,
            page = page,
            pageSize = pageSize
        )
    }

    suspend fun searchNews(
        query: String,
        sortBy: String? = null,
        pageSize: Int? = null,
        page: Int? = null
    ): NewsResponse {
        return newsApiService.getNews(
            query = query,
            sortBy = sortBy,
            pageSize = pageSize,
            page = page
        )
    }
}
