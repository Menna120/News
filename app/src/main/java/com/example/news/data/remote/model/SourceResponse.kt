package com.example.news.data.remote.model

import com.example.news.domain.model.Source

data class SourceResponse(
    val status: String,
    val sources: List<Source>? = null,
    val code: String? = null,
    val message: String? = null
)
