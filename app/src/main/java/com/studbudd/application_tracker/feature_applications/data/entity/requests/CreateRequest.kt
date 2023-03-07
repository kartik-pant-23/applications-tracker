package com.studbudd.application_tracker.feature_applications.data.entity.requests


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateRequest(
    @Json(name = "description")
    val description: String?,
    @Json(name = "job_details")
    val jobDetails: JobDetails,
    @Json(name = "status")
    val status: Int
) {
    @JsonClass(generateAdapter = true)
    data class JobDetails(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "company")
        val company: String,
        @Json(name = "role")
        val role: String,
        @Json(name = "url")
        val url: String
    )
}