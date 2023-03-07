package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import com.studbudd.application_tracker.feature_user.domain.models.User
import kotlinx.coroutines.flow.Flow

class GetUserDataUseCase(
    private val userRepository: UserRepository,
) {

    operator fun invoke(): Flow<Resource<User>> = userRepository.getUser()

}