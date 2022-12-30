package com.studbudd.application_tracker.feature_user.domain.use_cases

import android.util.Log
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserDataUseCase(
    private val preferencesManager: SharedPreferencesManager,
    private val userRepository: UserRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(): Resource<User> = withContext(defaultDispatcher) {
        val token = preferencesManager.refreshToken
        if (token == null) {
            Resource.LoggedOut()
        } else if (token == "LOCAL") {
            preferencesManager.accessToken = null
            when (val res = userRepository.getLocalUser()) {
                is Resource.Success -> Resource.Success(res.data!!, res.message)
                else -> Resource.Failure(res.message)
            }
        } else {
            when (val res = userRepository.getRemoteUser()) {
                is Resource.Success -> Resource.Success(res.data!!)
                is Resource.LoggedOut -> {
                    // TODO - Take user to refresh the access token
                    Resource.Failure("will refresh the token later")
                }
                else -> Resource.Failure(res.message)
            }
        }
    }

}