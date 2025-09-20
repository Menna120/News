package com.example.news.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.news.domain.model.Article
import retrofit2.HttpException
import java.io.IOException

const val NEWS_API_STARTING_PAGE_INDEX = 1
const val NETWORK_PAGE_SIZE = 20

class NewsPagingSource(
    private val newsApiService: NewsApiService,
    private val query: String? = null,
    private val sources: String? = null,
    private val sortBy: String? = null
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val pageNumber = params.key ?: NEWS_API_STARTING_PAGE_INDEX
        val pageSize = params.loadSize.coerceAtMost(NETWORK_PAGE_SIZE)
        return try {
            val response = newsApiService.getNews(
                query = query,
                sources = sources,
                sortBy = sortBy,
                page = pageNumber,
                pageSize = pageSize
            )
            val articles = response.articles

            val prevKey = if (pageNumber == NEWS_API_STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey =
                if (articles.isEmpty() || (pageNumber * pageSize >= response.totalResults)) null else pageNumber + 1

            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
