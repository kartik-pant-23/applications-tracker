package com.studbudd.application_tracker.feature_user.domain.models

import com.studbudd.application_tracker.core.utils.TimestampHelper

data class User(
    private val remoteId: String? = null,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val placeholderMap: Map<String, String>,
    private val createdAt: String? = null
) {

    val joinedOn: String
        get() = createdAt?.let { TimestampHelper.getFormattedString(it) } ?: "-"

    val isAnonymousUser = remoteId == null

}
