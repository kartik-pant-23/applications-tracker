package com.studbudd.application_tracker.feature_applications.domain.models

data class JobApplication(
    private val job: Job,
    private val status: ApplicationStatus,
    val notes: String?
)
