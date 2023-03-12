package com.studbudd.application_tracker.feature_applications.ui.create

sealed class AddNewApplicationUiState (val message: String = "") {

    class Default(): AddNewApplicationUiState()
    class Info(message: String): AddNewApplicationUiState(message)
    class Success(): AddNewApplicationUiState("Application added successfully..")
    class RequestNotificationPermission(): AddNewApplicationUiState()

}