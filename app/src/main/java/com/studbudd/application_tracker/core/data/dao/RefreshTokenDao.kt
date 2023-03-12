package com.studbudd.application_tracker.core.data.dao

import com.studbudd.application_tracker.core.data.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.models.remote.response.LoginResponse
import retrofit2.Response
import retrofit2.http.GET

interface RefreshTokenDao {

    @GET("users/refreshAuth")
    suspend fun refreshAuthTokens(): Response<ApiResponse<LoginResponse>>

}