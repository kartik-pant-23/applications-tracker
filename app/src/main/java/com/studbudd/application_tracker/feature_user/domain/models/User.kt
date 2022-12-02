package com.studbudd.application_tracker.feature_user.domain.models

data class User(
    val name: String,
    val email: String,
    val photoUrl: String?,
    val placeholderKeys: List<String>,
    val placeholderValues: List<String>,
    val token: String?
)
