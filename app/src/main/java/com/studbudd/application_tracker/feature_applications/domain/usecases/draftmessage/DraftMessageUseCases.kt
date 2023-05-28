package com.studbudd.application_tracker.feature_applications.domain.usecases.draftmessage

data class DraftMessageUseCases(
    val get: GetDraftMessageUseCase,
    val parse: ParseDraftMessageUseCase
)