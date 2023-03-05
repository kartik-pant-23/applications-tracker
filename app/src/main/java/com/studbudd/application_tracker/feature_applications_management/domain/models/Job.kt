package com.studbudd.application_tracker.feature_applications_management.domain.models

data class Job(
    val id: String? = null,
    val company: String,
    val role: String,
    val jobUrl: String,
    val companyLogo: String?,
    val applicationDeadline: String?
)