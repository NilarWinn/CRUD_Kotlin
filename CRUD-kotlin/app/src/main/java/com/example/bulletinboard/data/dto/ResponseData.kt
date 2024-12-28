package com.example.bulletinboard.data.dto

data class ResponseData<T> (
    val status: String,
    val data: T,
)

data class ResponseDataList<T>(
    val data: List<T>,
)

data class ResponseResult (
    val error: String?,
    val message: String,
)