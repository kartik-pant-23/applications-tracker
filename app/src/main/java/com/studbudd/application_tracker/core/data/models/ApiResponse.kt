package com.studbudd.application_tracker.core.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "data")
    val data: T?,
    @Json(name = "message")
    val message: String,
    @Json(name = "success")
    val success: Boolean
)