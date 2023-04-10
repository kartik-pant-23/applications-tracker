package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
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

    fun toJobApplicationEntity_Old(): JobApplicationEntity_Old {
        return JobApplicationEntity_Old(
            companyName = job.company.name,
            companyLogo = job.company.logoUrl,
            role = job.role,
            notes = description,
            jobLink = job.jobUrl,
            status = applicationStatus.id - 1L, // TODO - fix when final implementation
            applicationDeadline = job.applicationDeadline,
            createdAtCalendar = TimestampHelper.getCalendar(createdAt)!!,
            modifiedAtCalendar = TimestampHelper.getCalendar(updatedAt)!!,
        )
    }

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