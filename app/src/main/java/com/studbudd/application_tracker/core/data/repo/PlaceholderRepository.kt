package com.studbudd.application_tracker.core.data.repo

import com.studbudd.application_tracker.core.data.models.Resource
import kotlinx.coroutines.flow.Flow

interface PlaceholderRepository {

    suspend fun getPlaceholderData(): Flow<Resource<Map<String, String>>>
    suspend fun updatePlaceholderData(updatedData: Map<String, String>): Resource<Unit>

}