package com.studbudd.application_tracker.feature_user.data.models.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "token")
    val token: String
)