package com.studbudd.application_tracker.feature_applications.domain.use_cases

data class ApplicationsUseCase (
    val create: CreateJobApplicationUseCase,
    val get: GetJobApplicationsUseCase
)
