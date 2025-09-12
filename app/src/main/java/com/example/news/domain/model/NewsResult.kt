package com.example.news.domain.model

sealed class NewsResult<T>(
    val data: T? = null,
    val message: String? = null,
    val code: String? = null
) {
    class Success<T>(data: T?) : NewsResult<T>(data)
    class Loading<T>(data: T? = null) : NewsResult<T>(data)
    class NetworkError<T>(data: T? = null) : NewsResult<T>(data)
    class Error<T>(
        message: String,
        code: String? = null,
        data: T? = null
    ) : NewsResult<T>(data, message, code)
}
