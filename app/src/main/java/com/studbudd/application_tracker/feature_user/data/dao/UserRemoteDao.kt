package com.studbudd.application_tracker.feature_user.data.dao

import com.studbudd.application_tracker.common.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.models.UserRemote
import com.studbudd.application_tracker.feature_user.data.models.requests.LoginRequest
import com.studbudd.application_tracker.feature_user.data.models.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserRemoteDao {

    @POST("users/login")
    suspend fun loginUser(@Body token: LoginRequest): Response<ApiResponse<LoginResponse>>

}