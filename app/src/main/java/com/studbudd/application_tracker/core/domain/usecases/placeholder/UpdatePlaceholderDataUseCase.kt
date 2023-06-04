package com.studbudd.application_tracker.core.domain.usecases.placeholder

import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository

class UpdatePlaceholderDataUseCase(
    private val repo: PlaceholderRepository
) {
    suspend operator fun invoke(updatedData: Map<String, String>) =
        repo.updatePlaceholderData(updatedData)
}