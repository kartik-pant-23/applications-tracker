package com.studbudd.application_tracker.core.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePlaceholderRequest(
    @Json(name = "placeholderData")
    val placeholderData: Map<String, String>
)