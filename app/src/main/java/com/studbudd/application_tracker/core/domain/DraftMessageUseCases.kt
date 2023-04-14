package com.studbudd.application_tracker.core.domain

data class DraftMessageUseCases(
    val get: GetDraftMessageUseCase,
    val parse: ParseDraftMessageUseCase
)