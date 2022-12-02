package com.studbudd.application_tracker.feature_user.domain.repo

import android.util.Log
import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.common.domain.GetResourceFromApiResponse
import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.models.UserLocal
import com.studbudd.application_tracker.feature_user.data.models.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.models.response.LoginResponse
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository

class UserRepository_Impl(
    private val userLocalDao: UserLocalDao,
    private val userRemoteDao: UserRemoteDao
) : UserRepository {

    @WorkerThread
    override suspend fun createAnonymousUser(userLocal: UserLocal) =
        userLocalDao.insertNewUser(userLocal)

    @WorkerThread
    override suspend fun loginUser(token: String): Resource<LoginResponse> {
        try {
            GetResourceFromApiResponse<LoginResponse>().let { getResponse ->
                return getResponse(
                    response = userRemoteDao.loginUser(LoginRequest(token)),
                    TAG = TAG
                )
            }
        } catch(e: Exception) {
            Log.e(TAG, "exception: $e")
            return Resource.Failure("Possible connection error occurred!")
        }
    }

    @WorkerThread
    override suspend fun getLocalUser(): UserLocal? = userLocalDao.getUser()

    companion object {
        const val TAG = "UserRepository"
    }

}