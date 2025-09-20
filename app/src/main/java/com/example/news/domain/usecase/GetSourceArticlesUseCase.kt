package com.example.news.domain.usecase

import androidx.paging.PagingData
import com.example.news.data.repository.NewsRepository
import com.example.news.domain.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSourceArticlesUseCase @Inject constructor(private val repo: NewsRepository) {
    operator fun invoke(sourceId: String): Flow<PagingData<Article>> =
        repo.getArticlesBySourceStream(sourceId)
}
