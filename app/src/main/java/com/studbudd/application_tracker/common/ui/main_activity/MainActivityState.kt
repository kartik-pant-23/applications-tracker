package com.studbudd.application_tracker.common.ui.main_activity

import com.studbudd.application_tracker.feature_user.domain.models.User

sealed class MainActivityState(
    val loading: Boolean = false,
    val loaderMessage: String = "",
    val user: User? = null,
    val errorMessage: String? = null
) {

    class Loading(
        loaderMessage: String? = null,
        user: User? = null
    ) : MainActivityState(true, loaderMessage ?: "Loading", user)

    class SigningInProgress(oldUser: User?) :
        MainActivityState(true, "Creating a remote entity for you", oldUser)

    class SigningInFailed(oldUser: User?) :
        MainActivityState(user = oldUser, errorMessage = "Failed to create a remote user!")

    class SigningInSuccess() : MainActivityState()

    class LoggedIn(user: User) : MainActivityState(false, "", user)

    class LoggedOut : MainActivityState(false)

}