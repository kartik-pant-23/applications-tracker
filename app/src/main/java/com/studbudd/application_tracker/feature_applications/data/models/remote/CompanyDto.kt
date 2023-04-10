package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompanyDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "logoUrl")
    val logoUrl: String?,
    @Json(name = "name")
    val name: String
)