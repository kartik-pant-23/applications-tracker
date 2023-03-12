package com.studbudd.application_tracker.feature_applications.ui.home

sealed class ApplicationsUiState(
    message: String? = null
) {

    class Default: ApplicationsUiState()

    class Info(message: String): ApplicationsUiState(message)

    class LoggedOut: ApplicationsUiState()

}