package com.studbudd.application_tracker.feature_applications.domain.models

data class JobApplication(
    val id: Long,
    val job: Job,
    val status: ApplicationStatus,
    val notes: String?,
    val createdAt: String,
    val modifiedAt: String
)