package com.studbudd.application_tracker.feature_user.domain.use_cases

data class UserUseCases(
    val getUser: GetUserDataUseCase,
    val createLocalUser: CreateLocalUserUseCase,
    val createRemoteUser: CreateRemoteUserUseCase
)