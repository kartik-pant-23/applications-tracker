package com.studbudd.application_tracker.feature_applications.data.repo

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import kotlinx.coroutines.flow.Flow

interface JobApplicationsRepository {

    suspend fun create(
        company: String,
        role: String,
        jobUrl: String,
        status: Long,
        notes: String?
    ): Resource<JobApplication>

    suspend fun getApplications(
        pageSize: Int,
        pageNum: Int
    ): Flow<Resource<List<JobApplication>>>

    suspend fun getApplicationStatus(): Flow<Resource<List<ApplicationStatus>>>

    suspend fun getApplicationDetails(id: Long): Flow<Resource<JobApplication>>

}