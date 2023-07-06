package com.studbudd.application_tracker.core.domain.repo

import com.studbudd.application_tracker.core.data.dao.PlaceholderApi
import com.studbudd.application_tracker.core.data.dao.PlaceholderDao
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.data.models.UpdatePlaceholderRequest
import com.studbudd.application_tracker.core.data.repo.PlaceholderRepository
import com.studbudd.application_tracker.core.domain.usecases.HandleApiCallUseCase
import com.studbudd.application_tracker.core.domain.usecases.HandleExceptionUseCase
import com.studbudd.application_tracker.core.domain.usecases.MapConverterUseCase
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import kotlinx.coroutines.flow.*

class PlaceholderRepository_Impl(
    private val dao: PlaceholderDao,
    private val api: PlaceholderApi,
    private val handleApiCall: HandleApiCallUseCase,
    private val handleException: HandleExceptionUseCase,
    private val userDao: UserDao,
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

    override suspend fun updatePlaceholderData(updatedData: Map<String, String>): Resource<Unit> {
        return try {
            if (isRemoteUser()) {
                when (val res = handleApiCall(
                    apiCall = { api.updatePlaceholders(UpdatePlaceholderRequest(placeholderData = updatedData)) },
                    TAG = TAG
                )) {
                    is Resource.Success -> {
                        dao.updatePlaceholderData(res.data!!.placeholderMap)
                        Resource.Success(Unit, "Placeholder updated successfully.")
                    }
                    is Resource.Failure -> {
                        Resource.Failure("Failed to update the placeholders.")
                    }
                    else -> Resource.LoggedOut()
                }
            } else {
                dao.updatePlaceholderData(updatedData)
                Resource.Success(Unit, "Placeholder updated successfully.")
            }
        } catch (e: Exception) {
            handleException(e = e, TAG = TAG)
            Resource.Failure("Failed to update the placeholders. Try again later.")
        }
    }

    private suspend fun isRemoteUser(): Boolean {
        userDao.getUser().catch { handleException(e = Exception(it), TAG = TAG) }.firstOrNull()
            ?.let { return it.remoteId != null }
            ?: return false
    }

}