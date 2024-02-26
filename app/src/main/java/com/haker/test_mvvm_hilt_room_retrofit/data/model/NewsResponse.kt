package com.haker.test_mvvm_hilt_room_retrofit.data.model

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)