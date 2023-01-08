package com.studbudd.application_tracker.feature_user.domain.repo

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studbudd.application_tracker.common.domain.GetResourceFromApiResponse
import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserRemoteDao
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.models.UserLocal
import com.studbudd.application_tracker.feature_user.data.models.UserRemote
import com.studbudd.application_tracker.feature_user.data.models.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.models.response.LoginResponse
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User

class UserRepository_Impl(
    private val userLocalDao: UserLocalDao,
    private val userRemoteDao: UserRemoteDao,
    private val authUserRemoteDao: AuthUserRemoteDao
) : UserRepository {

    @WorkerThread
    override suspend fun createAnonymousUser(userLocal: UserLocal): Long {
        userLocal.id = 1
        return userLocalDao.insertNewUser(userLocal)
    }

    @WorkerThread
    override suspend fun loginUser(token: String): Resource<LoginResponse> {
        try {
            GetResourceFromApiResponse<LoginResponse>().let { getResponse ->
                return getResponse(
                    response = userRemoteDao.loginUser(LoginRequest(token)),
                    TAG = TAG
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "exception: $e")
            return Resource.Failure("Possibly a connection error occurred!")
        }
    }

    override fun getLocalUser() = userLocalDao.getUser()

    @WorkerThread
    override suspend fun getRemoteUser(): Resource<Boolean> {
        try {
            return when (val res = GetResourceFromApiResponse<UserRemote>()(
                response = authUserRemoteDao.getUserData(),
                TAG = TAG
            )) {
                is Resource.Success -> res.data!!.let {
                    createAnonymousUser(
                        UserLocal(
                            remoteId = it.id,
                            name = it.name,
                            email = it.email,
                            photoUrl = it.photoUrl,
                            placeholderKeys = it.placeholderKeys ?: listOf(),
                            placeholderValues = it.placeholderValues ?: listOf(),
                            createdAt = it.createdAt
                        )
                    )
                    Resource.Success(true)
                }
                is Resource.LoggedOut -> {
                    // TODO - Improve what happens when access token expires
                    //        we should try to refresh the access token first
                    userLocalDao.deleteLocalUser()
                    Resource.LoggedOut()
                }
                else -> Resource.Failure(res.message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "exception: $e")
            return Resource.Failure("Possibly a connection error occurred")
        }
    }

    companion object {
        const val TAG = "UserRepository"
    }

}
