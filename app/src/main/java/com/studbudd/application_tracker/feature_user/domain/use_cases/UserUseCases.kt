package com.studbudd.application_tracker.feature_user.domain.use_cases

data class UserUseCases(
    val getUser: GetUserDataUseCase,
    val createAnonymousUser: AnonymousSignInUseCase,
    val signInRemoteUser: RemoteSignInUseCase
)