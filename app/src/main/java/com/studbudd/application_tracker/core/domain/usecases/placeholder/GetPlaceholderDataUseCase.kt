package com.studbudd.application_tracker.core.domain.usecases.placeholder

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository
import kotlinx.coroutines.flow.Flow

class GetPlaceholderDataUseCase(
    private val repo: PlaceholderRepository
) {

    suspend operator fun invoke(): Flow<Resource<Map<String, String>>>
        = repo.getPlaceholderData()

}