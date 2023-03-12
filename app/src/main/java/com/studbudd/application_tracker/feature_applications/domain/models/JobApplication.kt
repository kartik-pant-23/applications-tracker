package com.studbudd.application_tracker.feature_applications.domain.models

import com.studbudd.application_tracker.core.utils.TimestampHelper

data class JobApplication(
    val id: Long,
    val job: Job,
    val status: ApplicationStatus,
    val notes: String?,
    val createdAt: String,
    val modifiedAt: String
) {

    val createdAtDay
        get() = TimestampHelper.getFormattedString(createdAt, "dd")

    val createdAtMonthYear
        get() = TimestampHelper.getFormattedString(createdAt, "MMM, YYYY")

}