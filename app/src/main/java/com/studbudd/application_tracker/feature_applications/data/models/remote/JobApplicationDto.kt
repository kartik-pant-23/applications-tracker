package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

@JsonClass(generateAdapter = true)
data class JobApplicationDto(
    @Json(name = "applicationStatus")
    val applicationStatus: ApplicationStatusDto,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "id")
    val id: String,
    @Json(name = "job")
    val job: JobDto,
    @Json(name = "updatedAt")
    val updatedAt: String
) {

    fun toJobApplication(entityId: Long): JobApplication {
        return JobApplication(
            id = entityId,
            job = job.toJob(),
            status = applicationStatus.toApplicationStatus(),
            notes = description,
            createdAt = createdAt,
            modifiedAt = updatedAt
        )
    }

}