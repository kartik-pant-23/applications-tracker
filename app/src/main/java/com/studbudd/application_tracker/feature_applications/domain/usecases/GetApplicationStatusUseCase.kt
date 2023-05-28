package com.studbudd.application_tracker.feature_applications.domain.usecases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetApplicationStatusUseCase(
    private val repo: JobApplicationsRepository
) {

    /**
     * Returns the list of [ApplicationStatus] that have importance value
     * in the range - `[lowerRange, upperRange]`.
     */
    suspend operator fun invoke(
        lowerRange: Int = Int.MIN_VALUE,
        upperRange: Int = Int.MAX_VALUE
    ): Flow<Resource<List<ApplicationStatus>>> = flow {
        repo.getApplicationStatus().collect {
            if (it is Resource.Success) {
                emit(Resource.Success(it.data!!.filter { status ->
                    (lowerRange <= status.importanceValue).and(status.importanceValue <= upperRange)
                }))
            } else {
                emit(Resource.Failure("Oops.. Failed to fetch application status"))
            }
        }
    }

}