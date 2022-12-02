package com.studbudd.application_tracker.common.ui.main_activity

import com.studbudd.application_tracker.feature_user.domain.models.User

sealed class MainActivityState(
    val loading: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val user: User? = null
) {

    class Loading: MainActivityState(true)
    class LoggedIn(user: User): MainActivityState(false, true, user)
    class LoggedOut(): MainActivityState(false, false, null)

}