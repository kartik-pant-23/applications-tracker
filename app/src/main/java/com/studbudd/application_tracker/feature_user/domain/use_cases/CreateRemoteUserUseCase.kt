package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateRemoteUserUseCase @Inject constructor(
    private val preferencesManager: SharedPreferencesManager,
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(token: String) : Resource<Boolean> = withContext(dispatcher) {
        when (val res = userRepository.loginUser(token)) {
            is Resource.Success -> {
                preferencesManager.accessToken = res.data!!.accessToken
                preferencesManager.refreshToken = res.data.refreshToken
                Resource.Success(true, "User logged in successfully")
            }
            else -> {
                Resource.Failure(res.message, false)
            }
        }
    }
}