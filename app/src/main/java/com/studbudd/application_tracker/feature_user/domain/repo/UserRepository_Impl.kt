package com.studbudd.application_tracker.feature_user.domain.repo

import android.util.Log
import androidx.annotation.WorkerThread
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
import kotlinx.coroutines.delay
import java.net.ConnectException

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
            return Resource.Failure("Possible connection error occurred!")
        }
    }

    @WorkerThread
    override suspend fun getLocalUser(): Resource<User> =
        userLocalDao.getUser()?.let { user ->
            Resource.Success(user.user)
        } ?: Resource.Failure("Something went wrong!")

    @WorkerThread
    override suspend fun getRemoteUser(): Resource<User> {
        try {
            return when (val remoteUserResource = GetResourceFromApiResponse<UserRemote>()(
                response = authUserRemoteDao.getUserData(),
                TAG = TAG
            )) {
                is Resource.Success -> {
                    val createdUser = remoteUserResource.data!!
                    createAnonymousUser(
                        UserLocal(
                            createdUser.id,
                            createdUser.name,
                            createdUser.email,
                            createdUser.photoUrl,
                            createdUser.placeholderKeys ?: listOf(),
                            createdUser.placeholderValues ?: listOf(),
                            createdUser.createdAt
                        )
                    )
                    return Resource.Success(createdUser.user)
                }
                is Resource.LoggedOut -> Resource.LoggedOut()
                else -> getLocalUser()
            }
        } catch (e: Exception) {
            return when (e) {
                is ConnectException -> {
                    // Case when internet connection not available
                    Log.e(TAG, "Internet connection not available")
                    getLocalUser()
                }
                else -> {
                    Log.e(TAG, "exception: $e")
                    Resource.Failure("Possible connection error occurred!")
                }
            }
        }
    }

    companion object {
        const val TAG = "UserRepository"
    }

}
