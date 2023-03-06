package com.studbudd.application_tracker.feature_user.domain.repo

import android.util.Log
import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.common.domain.GetResourceFromApiResponse
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserRemoteDao
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.dao.UserRemoteDao
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.data.models.remote.UserDto
import com.studbudd.application_tracker.feature_user.data.models.remote.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.models.remote.response.LoginResponse
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserRepository_Impl(
    private val dao: UserLocalDao,
    private val api: UserRemoteDao,
    private val authApi: AuthUserRemoteDao,
    private val preferencesManager: SharedPreferencesManager
) : UserRepository {

    @WorkerThread
    override suspend fun createLocalUser(userEntity: UserEntity): Long {
        userEntity.id = 1
        return dao.insertNewUser(userEntity)
    }

    /**
     * ### Strategy for sending back the user data
     * 1. Send the data you have taken from the local database.
     * 2. If the user is a remote user, then try to get the data from the remote database.
     * There are following three scenarios, we can run into -
     *      1. Request successful - the new user would be updated in the database and will
     *         automatically get updated through the local database.
     *      2. Request failed -
     *          - **User logged out** - In such a case we should send back the logged
     *            out response, and should be handled appropriately.
     *          - **Some other reason** - Keep showing user details fetched from the
     *            local database.
     */
    override fun getUser(): Flow<Resource<User>> = channelFlow {
        send(Resource.Loading("Fetching user data..."))
        dao.getUser().collectLatest { userEntity ->
            if (userEntity != null) {
                if (isConnectedWithRemoteDatabase()) {
                    send(
                        Resource.Loading(
                            message = "Syncing user data...",
                            data = userEntity.toUser()
                        )
                    )
                    val response = GetResourceFromApiResponse<UserDto>()(
                        response = authApi.getUserData(),
                        TAG = TAG
                    )
                    if (response is Resource.Success) {
                        val newUser = response.data!!.toUserEntity()
                        newUser.id = 1
                        if (newUser != userEntity)
                            dao.updateUser(newUser)
                    } else if (response is Resource.Failure) {
                        send(
                            Resource.Failure(
                                data = userEntity.toUser(),
                                message = response.message
                            )
                        )
                    }
                } else {
                    send(Resource.Success(userEntity.toUser()))
                }
            } else {
                send(Resource.LoggedOut())
            }
        }
    }

    @WorkerThread
    override fun deleteLocalUser() = dao.deleteLocalUser()

    @WorkerThread
    override suspend fun connectWithRemoteDatabase(token: String): Resource<LoginResponse> {
        try {
            GetResourceFromApiResponse<LoginResponse>().let { getResponse ->
                return getResponse(
                    response = api.loginUser(LoginRequest(token)), TAG = TAG
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "exception: $e")
            return Resource.Failure("Possibly a connection error occurred!")
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
