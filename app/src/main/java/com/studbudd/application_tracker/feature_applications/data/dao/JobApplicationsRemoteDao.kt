package com.studbudd.application_tracker.feature_applications.data.dao

import com.studbudd.application_tracker.core.data.models.ApiResponse
import com.studbudd.application_tracker.feature_applications.data.entity.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications.data.entity.RemoteJobApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobApplicationsRemoteDao {

    @POST("applications/create")
    suspend fun createApplication(
        @Body body: CreateRequest
    ): Response<ApiResponse<RemoteJobApplication>>

}