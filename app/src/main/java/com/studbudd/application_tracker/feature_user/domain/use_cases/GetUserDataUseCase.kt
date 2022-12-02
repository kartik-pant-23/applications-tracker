package com.studbudd.application_tracker.feature_user.domain.use_cases

import android.content.SharedPreferences
import android.util.Log
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserDataUseCase(
    private val preferencesManager: SharedPreferencesManager,
    private val userRepository: UserRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(): User? = withContext(defaultDispatcher) {
        preferencesManager.refreshToken?.let { refreshToken ->
            if (refreshToken == "LOCAL") {
                val localUser = userRepository.getLocalUser()
                if (localUser == null) preferencesManager.refreshToken = null
                localUser?.user
            } else {
                // TODO - get user from remote database
                User(
                    "Kartik Pant",
                    "pantkartik23@gmail.com",
                    null,
                    listOf(),
                    listOf(),
                    refreshToken
                )
            }
        }
    }

}