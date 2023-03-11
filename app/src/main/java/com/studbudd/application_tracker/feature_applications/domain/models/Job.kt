package com.studbudd.application_tracker.feature_applications.domain.models

import com.studbudd.application_tracker.core.utils.TimestampHelper

data class Job(
    val company: String,
    val role: String,
    val url: String,
    val companyLogo: String?,
    private val _deadline: String?
) {

    val deadline: String?
        get() = _deadline?.let {
            TimestampHelper.getFormattedString(it, TimestampHelper.DEFAULT)
        }

}