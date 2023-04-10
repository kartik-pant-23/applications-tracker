package com.studbudd.application_tracker.feature_applications.data.models.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.studbudd.application_tracker.feature_applications.domain.models.Job

@JsonClass(generateAdapter = true)
data class JobDto(
    @Json(name = "applicationDeadline")
    val applicationDeadline: String?,
    @Json(name = "company")
    val company: CompanyDto,
    @Json(name = "id")
    val id: String,
    @Json(name = "jobUrl")
    val jobUrl: String,
    @Json(name = "role")
    val role: String
) {

    fun toJob(): Job {
        return Job(
            company = company.name,
            role = role,
            url = jobUrl,
            companyLogo = company.logoUrl,
            _deadline = applicationDeadline
        )
    }

}