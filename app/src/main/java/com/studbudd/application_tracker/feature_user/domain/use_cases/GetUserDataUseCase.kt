package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.common.models.Resource
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetUserDataUseCase(
    private val userRepository: UserRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * ## Strategy for sending back the user data
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
    suspend operator fun invoke(): Flow<Resource<User>> = channelFlow {
        withContext(defaultDispatcher) {
            launch {
                userRepository.getLocalUser().collect {
                    if (it != null) send(Resource.Success(it.user))
                    else send(Resource.LoggedOut())
                }
            }
            if (userRepository.isConnectedWithRemoteDatabase()) {
                userRepository.getRemoteUser()
            }
        }
    }

}