package com.studbudd.application_tracker.feature_user.data.repo

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.data.models.remote.UserDto
import com.studbudd.application_tracker.feature_user.data.models.remote.response.LoginResponse
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun createLocalUser(userEntity: UserEntity): Long

    fun getUser(): Flow<Resource<User>>

    fun deleteLocalUser()

    suspend fun connectWithRemoteDatabase(token: String): Resource<LoginResponse>

    fun saveAuthenticationTokens(accessToken: String, refreshToken: String)

    fun isConnectedWithRemoteDatabase(): Boolean

}