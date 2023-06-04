package com.studbudd.application_tracker.core.domain.repo

import com.studbudd.application_tracker.core.data.dao.PlaceholderDao
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository
import com.studbudd.application_tracker.core.domain.usecases.HandleExceptionUseCase
import com.studbudd.application_tracker.core.domain.usecases.MapConverterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PlaceholderRepository_Impl(
    private val dao: PlaceholderDao,
    private val handleException: HandleExceptionUseCase
) : PlaceholderRepository {

    companion object {
        const val TAG = "PlaceholderRepository"
    }

    override suspend fun getPlaceholderData(): Flow<Resource<Map<String, String>>> = flow {
        dao.getPlaceholderData()
            .catch {
                handleException(TAG = TAG, e = Exception(it))
            }
            .collect { emit(Resource.Success(MapConverterUseCase().fromStringToMap(it))) }
    }

    /**
     * Updates the remote information first and then updates the local.
     */
    override suspend fun updatePlaceholderData(updatedData: Map<String, String>): Resource<Unit> {
        return try {
            // TODO - update the remote user entity
            dao.updatePlaceholderData(updatedData)
            Resource.Success(Unit, "Placeholder updated successfully.")
        } catch (e: Exception) {
            handleException(e = e, TAG = TAG)
            Resource.Failure("Failed to update the placeholders. Try again later.")
        }
    }

}