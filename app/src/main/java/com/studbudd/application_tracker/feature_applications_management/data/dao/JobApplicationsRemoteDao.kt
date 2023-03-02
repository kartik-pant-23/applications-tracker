package com.studbudd.application_tracker.feature_applications_management.data.dao

import com.studbudd.application_tracker.common.data.models.ApiResponse
import com.studbudd.application_tracker.feature_applications_management.data.entity.requests.CreateApplicationRequest
import com.studbudd.application_tracker.feature_applications_management.data.entity.responses.CreateApplicationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobApplicationsRemoteDao {

    @POST
    suspend fun createApplication(@Body body: CreateApplicationRequest): Response<ApiResponse<CreateApplicationResponse>>

}