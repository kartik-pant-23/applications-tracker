package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity

@JsonClass(generateAdapter = true)
data class JobApplicationDto(
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

    fun toJobApplicationEntity(): JobApplicationEntity {
        throw NotImplementedError()
    }

}