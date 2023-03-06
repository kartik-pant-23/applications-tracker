package com.studbudd.application_tracker.feature_applications_management.data.dao

import com.studbudd.application_tracker.common.data.models.ApiResponse
import com.studbudd.application_tracker.feature_applications_management.data.entity.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications_management.data.entity.RemoteJobApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobApplicationsRemoteDao {

    @POST("applications/create")
    suspend fun createApplication(
        @Body body: CreateRequest
    ): Response<ApiResponse<RemoteJobApplication>>

}