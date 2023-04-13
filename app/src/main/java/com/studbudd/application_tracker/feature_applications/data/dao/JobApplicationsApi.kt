package com.studbudd.application_tracker.feature_applications.data.dao

import com.studbudd.application_tracker.core.data.models.ApiResponse
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications.data.models.remote.JobApplicationDto
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.UpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface JobApplicationsApi {

    @POST(ENDPOINT_CREATE)
    suspend fun createApplication(
        @Body body: CreateRequest
    ): Response<ApiResponse<JobApplicationDto>>

    @PATCH(ENDPOINT_UPDATE)
    suspend fun updateApplication(
        @Path("id") remoteId: String,
        @Body body: UpdateRequest
    ): Response<ApiResponse<JobApplicationDto>>

    @DELETE(ENDPOINT_DELETE)
    suspend fun deleteApplication(
        @Path("id") remoteId: String
    ): Response<ApiResponse<JobApplicationDto>>

    companion object {
        private const val ENDPOINT_CREATE = "applications/create"
        private const val ENDPOINT_UPDATE = "applications/update/{id}"
        private const val ENDPOINT_DELETE = "applications/{id}"
    }

}