package com.studbudd.application_tracker.feature_applications.data.models.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Job(
    @Json(name = "applicationDeadline")
    val applicationDeadline: String?,
    @Json(name = "company")
    val company: String,
    @Json(name = "companyLogo")
    val companyLogo: String?,
    @Json(name = "id")
    val id: String,
    @Json(name = "jobUrl")
    val jobUrl: String,
    @Json(name = "role")
    val role: String
)
