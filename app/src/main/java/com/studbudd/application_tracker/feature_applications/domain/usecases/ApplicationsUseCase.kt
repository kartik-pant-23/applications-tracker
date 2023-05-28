package com.studbudd.application_tracker.feature_applications.domain.usecases

data class ApplicationsUseCase(
    val create: CreateJobApplicationUseCase,
    val get: GetJobApplicationsUseCase,
    val getApplicationStatus: GetApplicationStatusUseCase,
    val getDetails: GetJobApplicationDetailsUseCase,
    val update: UpdateJobApplicationUseCase,
    val delete: DeleteJobApplicationUseCase
)
