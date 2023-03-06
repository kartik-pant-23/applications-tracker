package com.studbudd.application_tracker.feature_user.domain.use_cases

data class UserUseCases(
    val get: GetUserDataUseCase,
    val create: CreateLocalUserUseCase,
    val login: CreateRemoteUserUseCase
)