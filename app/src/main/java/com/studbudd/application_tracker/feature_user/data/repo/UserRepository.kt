package com.studbudd.application_tracker.feature_user.data.repo

import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.models.UserLocal
import com.studbudd.application_tracker.feature_user.data.models.response.LoginResponse

interface UserRepository {

    suspend fun createAnonymousUser(userLocal: UserLocal): Long

    suspend fun loginUser(token: String): Resource<LoginResponse>

    suspend fun getLocalUser(): UserLocal?

}