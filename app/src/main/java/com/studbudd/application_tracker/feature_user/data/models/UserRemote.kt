package com.studbudd.application_tracker.feature_user.data.models

import com.studbudd.application_tracker.feature_user.domain.models.User

data class UserRemote(
    val name: String,
    val email: String,
    val photoUrl: String,
    val placeholderKeys: List<String>,
    val placeholderValues: List<String>,
    val accessToken: String
) {

    val user
        get() = User(name, email, photoUrl, placeholderKeys, placeholderValues, accessToken)

}
