package com.studbudd.application_tracker.feature_user.data.dao

import com.studbudd.application_tracker.common.data.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.entity.UserRemote
import com.studbudd.application_tracker.feature_user.data.entity.response.LoginResponse
import retrofit2.Response
import retrofit2.http.GET

interface AuthUserRemoteDao {

    @GET("users/details")
    suspend fun getUserData(): Response<ApiResponse<UserRemote>>

    @GET("users/refreshAuth")
    suspend fun refreshAuthTokens(): Response<ApiResponse<LoginResponse>>

}
