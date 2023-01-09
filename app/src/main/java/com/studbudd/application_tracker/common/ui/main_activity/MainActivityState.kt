package com.studbudd.application_tracker.common.ui.main_activity

sealed class MainActivityState(
    val loading: Boolean = false,
    val loaderMessage: String = "",
    val errorMessage: String? = null
) {

    class Default : MainActivityState()

    /**
     * State when the screen should show loader screen
     */
    class Loading(
        loaderMessage: String? = null,
    ) : MainActivityState(true, loaderMessage ?: "Loading")

    /**
     * State when some error needs to be displayed using the snack bar
     */
    class Info(message: String? = null) :
        MainActivityState(false, "", message ?: "Something went wrong")

    class StartLoggingOut: MainActivityState()
    /**
     * State when the user is logged by any of the following reasons -
     * 1. Access token expired
     * 2. Logged out from inside the app - Profile Screen
     */
    class LoggedOut : MainActivityState()

    class StartConnectingWithGoogle(): MainActivityState()
    /**
     * State when the app gets connected with Google - Profile Screen
     */
    class ConnectedWithGoogle : MainActivityState()

}