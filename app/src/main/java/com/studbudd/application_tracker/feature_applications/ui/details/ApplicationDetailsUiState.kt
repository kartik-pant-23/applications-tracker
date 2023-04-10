package com.studbudd.application_tracker.feature_applications.ui.details

sealed class ApplicationDetailsUiState(val message: String? = null) {

    class Default: ApplicationDetailsUiState()

    class Info(message: String): ApplicationDetailsUiState(message)

    class EditMode(message: String? = null): ApplicationDetailsUiState(message)

}