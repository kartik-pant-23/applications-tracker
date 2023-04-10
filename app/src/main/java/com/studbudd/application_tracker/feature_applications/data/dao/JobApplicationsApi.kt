package com.studbudd.application_tracker.feature_applications.data.dao

import com.studbudd.application_tracker.core.data.models.ApiResponse
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications.data.models.remote.JobApplicationDto
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.UpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface JobApplicationsApi {

    @POST("applications/create")
    suspend fun createApplication(
        @Body body: CreateRequest
    ): Response<ApiResponse<JobApplicationDto>>

    @PATCH("applications/update/{id}")
    suspend fun updateApplication(
        @Path("id") remoteId: String,
        @Body body: UpdateRequest
    ): Response<ApiResponse<JobApplicationDto>>

}