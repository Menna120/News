package com.example.news.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class CategoryNews(val categoryName: String)

@Serializable
data object Search
