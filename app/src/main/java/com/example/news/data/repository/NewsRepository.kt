package com.example.news.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.news.data.remote.NETWORK_PAGE_SIZE
import com.example.news.data.remote.NewsApiService
import com.example.news.data.remote.NewsPagingSource
import com.example.news.data.remote.model.SourceResponse
import com.example.news.domain.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService
) {

    suspend fun getCategorySources(category: String? = null): SourceResponse =
        newsApiService.getSources(category = category)

    fun getArticlesBySourceStream(sourceId: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(newsApiService = newsApiService, sources = sourceId)
            }
        ).flow
    }

    fun searchNewsStream(query: String, sortBy: String? = null): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    newsApiService = newsApiService,
                    query = query,
                    sortBy = sortBy
                )
            }
        ).flow
    }
}
