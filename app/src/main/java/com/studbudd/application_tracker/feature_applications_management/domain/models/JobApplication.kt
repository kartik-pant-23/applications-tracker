package com.studbudd.application_tracker.feature_applications_management.domain.models

import com.studbudd.application_tracker.common.domain.ParseTimestampUseCase
import com.studbudd.application_tracker.feature_applications_management.data.entity.RemoteJobApplication

data class JobApplication(
    private val jobDetails: Job,
    private val applicationStatus: RemoteJobApplication.ApplicationStatus,
    val description: String?
) {

    val company
        get() = jobDetails.company
    val role
        get() = jobDetails.role
    val jobLink
        get() = jobDetails.jobUrl
    val companyLogo: String?
        get() = jobDetails.companyLogo
    val applicationDeadline: String?
        get() = jobDetails.applicationDeadline?.let { ParseTimestampUseCase(it)() }

    val status
        get() = applicationStatus.tag

}
