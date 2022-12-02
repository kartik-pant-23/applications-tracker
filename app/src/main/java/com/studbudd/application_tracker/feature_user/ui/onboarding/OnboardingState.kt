package com.studbudd.application_tracker.feature_user.ui.onboarding

sealed class OnboardingState(
    val loading: Boolean = false,
    val loaderMessage: String? = null,
    val snackBarMessage: String? = null
) {

    class Loading(message: String? = null) : OnboardingState(true, message ?: "Loading")
    class SignInSuccess(message: String? = null) :
        OnboardingState(snackBarMessage = message ?: "User authentication successful!")

    class SignInFailure(message: String? = null) :
        OnboardingState(snackBarMessage = message ?: "User authentication failed!")

}
