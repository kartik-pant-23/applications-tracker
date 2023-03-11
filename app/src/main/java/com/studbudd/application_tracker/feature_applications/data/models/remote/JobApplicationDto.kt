package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old

@JsonClass(generateAdapter = true)
data class JobApplicationDto(
    @Json(name = "applicationStatus")
    val applicationStatus: ApplicationStatusDto,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "id")
    val id: String,
    @Json(name = "job")
    val job: Job,
    @Json(name = "updatedAt")
    val updatedAt: String
) {

    fun toJobApplicationEntity(): JobApplicationEntity_Old {
        return JobApplicationEntity_Old(
            companyName = job.company,
            companyLogo = job.companyLogo,
            role = job.role,
            notes = description,
            jobLink = job.jobUrl,
            status = applicationStatus.id - 1, // TODO - fix when final implementation
            applicationDeadline = job.applicationDeadline,
            createdAtCalendar = TimestampHelper.getCalendar(createdAt)!!,
            modifiedAtCalendar = TimestampHelper.getCalendar(updatedAt)!!,
        )
    }

}