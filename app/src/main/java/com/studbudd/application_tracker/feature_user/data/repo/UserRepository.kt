package com.studbudd.application_tracker.feature_user.data.repo

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.entity.UserLocal
import com.studbudd.application_tracker.feature_user.data.entity.UserRemote
import com.studbudd.application_tracker.feature_user.data.entity.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // CRUD operations for local user
    suspend fun createLocalUser(userLocal: UserLocal): Long
    fun getLocalUser(): Flow<UserLocal?>
    fun deleteLocalUser()

    // CRUD operations for remote user
    suspend fun createRemoteUser(token: String): Resource<LoginResponse>
    suspend fun getRemoteUser(): Resource<UserRemote>

    fun saveAuthenticationTokens(accessToken: String, refreshToken: String)

    fun isConnectedWithRemoteDatabase(): Boolean

}