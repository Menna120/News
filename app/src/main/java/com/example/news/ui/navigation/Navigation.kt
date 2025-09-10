package com.example.news.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class CategoryNews(val categoryName: String)

@Serializable
data class FullArticle(val articleUrl: String)

@Serializable
data object Search
