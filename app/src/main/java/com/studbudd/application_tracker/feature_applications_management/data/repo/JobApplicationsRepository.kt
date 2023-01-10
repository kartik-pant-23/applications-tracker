package com.studbudd.application_tracker.feature_applications_management.data.repo

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import kotlinx.coroutines.flow.Flow

interface JobApplicationsRepository {

    // CRUD operation for job-applications in the local database
    suspend fun addJobApplication(application: JobApplication): Resource<Long>
    fun getJobApplications(): Flow<List<JobApplication>>
    fun getJobApplicationById(id: Int): Flow<JobApplication>
    suspend fun updateJobApplication(newApplication: JobApplication)
    fun deleteJobApplicationById(id: Int)

    // TODO - CRUD operation for job-applications in the remote database

}