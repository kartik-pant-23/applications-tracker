package com.studbudd.application_tracker.feature_user.domain.models

import com.studbudd.application_tracker.common.domain.ParseTimestampUseCase

data class User(
    private val remoteId: String? = null,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val placeholderKeys: List<String>,
    val placeholderValues: List<String>,
    private val createdAt: String? = null
) {

    val joinedOn: String
        get() = ParseTimestampUseCase(createdAt ?: "")()

    val isAnonymousUser = remoteId == null

}
