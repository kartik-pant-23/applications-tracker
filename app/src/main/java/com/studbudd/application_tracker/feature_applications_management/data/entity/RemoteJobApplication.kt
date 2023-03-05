package com.studbudd.application_tracker.feature_applications_management.data.entity.responses


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateApplicationResponse(
    @Json(name = "applicationStatus")
    val applicationStatus: ApplicationStatus,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "job")
    val job: Job,
    @Json(name = "updatedAt")
    val updatedAt: String
) {
    @JsonClass(generateAdapter = true)
    data class ApplicationStatus(
        @Json(name = "color")
        val color: String,
        @Json(name = "tag")
        val tag: String
    )

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
}