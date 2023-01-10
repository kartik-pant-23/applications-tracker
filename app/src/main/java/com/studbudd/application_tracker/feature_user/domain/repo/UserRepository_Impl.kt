package com.studbudd.application_tracker.feature_user.domain.repo

import android.util.Log
import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.common.domain.GetResourceFromApiResponse
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserRemoteDao
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.entity.UserLocal
import com.studbudd.application_tracker.feature_user.data.entity.UserRemote
import com.studbudd.application_tracker.feature_user.data.entity.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.entity.response.LoginResponse
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository

class UserRepository_Impl(
    private val userLocalDao: UserLocalDao,
    private val userRemoteDao: UserRemoteDao,
    private val authUserRemoteDao: AuthUserRemoteDao,
    private val preferencesManager: SharedPreferencesManager
) : UserRepository {

    @WorkerThread
    override suspend fun createLocalUser(userLocal: UserLocal): Long {
        userLocal.id = 1
        return userLocalDao.insertNewUser(userLocal)
    }

    override fun getLocalUser() = userLocalDao.getUser()

    @WorkerThread
    override fun deleteLocalUser() = userLocalDao.deleteLocalUser()

    @WorkerThread
    override suspend fun createRemoteUser(token: String): Resource<LoginResponse> {
        try {
            GetResourceFromApiResponse<LoginResponse>().let { getResponse ->
                return getResponse(
                    response = userRemoteDao.loginUser(LoginRequest(token)), TAG = TAG
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "exception: $e")
            return Resource.Failure("Possibly a connection error occurred!")
        }
    }

    @WorkerThread
    override suspend fun getRemoteUser(): Resource<UserRemote> {
        return try {
            GetResourceFromApiResponse<UserRemote>()(
                TAG = TAG,
                response = authUserRemoteDao.getUserData()
            )
        } catch (e: Exception) {
            Log.e(TAG, "exception: $e")
            Resource.Failure("Possibly a connection error occurred")
        }
    }

    override fun saveAuthenticationTokens(accessToken: String, refreshToken: String) {
        preferencesManager.accessToken = accessToken
        preferencesManager.refreshToken = refreshToken
    }

    override fun isConnectedWithRemoteDatabase(): Boolean {
        return preferencesManager.accessToken != null && preferencesManager.refreshToken != null
    }

    companion object {
        const val TAG = "UserRepository"
    }

}
