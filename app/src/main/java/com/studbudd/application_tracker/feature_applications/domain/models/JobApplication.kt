package com.studbudd.application_tracker.feature_applications.domain.models

import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.data.models.remote.ApplicationStatus

data class JobApplication(
    private val jobDetails: Job,
    private val applicationStatus: ApplicationStatus,
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
        get() = jobDetails.applicationDeadline?.let { TimestampHelper.getFormattedString(it) }

    val status
        get() = applicationStatus.tag

}
