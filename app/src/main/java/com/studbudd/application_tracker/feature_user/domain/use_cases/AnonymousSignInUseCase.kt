package com.studbudd.application_tracker.feature_user.domain.use_cases

import android.util.Log
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_user.data.models.UserLocal
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnonymousSignInUseCase @Inject constructor(
    private val preferencesManager: SharedPreferencesManager,
    private val repo: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(name: String) = withContext(dispatcher) {
        repo.createAnonymousUser(UserLocal(name = name))
        preferencesManager.refreshToken = "LOCAL"
    }
}