package com.example.news.data.remote

import com.example.news.data.remote.model.NewsResponse
import com.example.news.data.remote.model.SourceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines/sources")
    suspend fun getSources(@Query("category") category: String? = null): SourceResponse

    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sources") sources: String? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("page") page: Int? = null
    ): NewsResponse
}
