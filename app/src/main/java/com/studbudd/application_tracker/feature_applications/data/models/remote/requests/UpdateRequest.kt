package com.studbudd.application_tracker.feature_applications.data.models.remote.requests


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateRequest(
    @Json(name = "notes")
    val notes: String?,
    @Json(name = "status")
    val status: Long
)