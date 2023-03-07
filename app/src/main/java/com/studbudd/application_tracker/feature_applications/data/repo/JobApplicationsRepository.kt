package com.studbudd.application_tracker.feature_applications.data.repo

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.entity.RemoteJobApplication

interface JobApplicationsRepository {

    suspend fun createLocalApplication(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        description: String?
    ): Resource<Long>

    suspend fun updateRemoteIdOfLocalEntity(
        localApplicationId: Long,
        remoteId: String
    )

    suspend fun createRemoteApplication(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        description: String?
    ): Resource<RemoteJobApplication>

}