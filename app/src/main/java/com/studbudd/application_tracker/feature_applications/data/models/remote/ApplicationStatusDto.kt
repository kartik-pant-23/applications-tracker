package com.studbudd.application_tracker.feature_applications.data.models.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApplicationStatus(
    @Json(name = "id")
    val id: Int,
    @Json(name = "color")
    val color: String,
    @Json(name = "tag")
    val tag: String
)