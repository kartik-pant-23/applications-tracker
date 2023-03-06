package com.studbudd.application_tracker.feature_user.domain.repo

import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.common.domain.HandleApiCall
import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.AuthUserApi
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import com.studbudd.application_tracker.feature_user.data.dao.UserApi
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.data.models.remote.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.models.remote.response.LoginResponse
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepository_Impl(
    private val dao: UserDao,
    private val api: UserApi,
    private val authApi: AuthUserApi,
    private val preferencesManager: SharedPreferencesManager,
    private val handleApiCall: HandleApiCall
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
        launch {
            dao.getUser().collect {
                if (it != null)
                    send(Resource.Success(it.toUser()))
                else
                    send(Resource.LoggedOut())
            }
        }

        if (isConnectedWithRemoteDatabase()) {
            val res = handleApiCall(
                apiCall = { authApi.getUserData() },
                TAG = TAG
            )
            if (res is Resource.Success) {
                val newUser = res.data!!.toUserEntity()
                newUser.id = 1
                dao.updateUser(newUser)
            }
        }
    }

    @WorkerThread
    override suspend fun connectWithRemoteDatabase(token: String): Resource<LoginResponse> {
        return handleApiCall(
            apiCall = { api.loginUser(LoginRequest(token = token)) },
            TAG = TAG
        )
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
