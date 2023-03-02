package com.studbudd.application_tracker.feature_user.data.dao

import com.studbudd.application_tracker.common.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.models.UserRemote
import com.studbudd.application_tracker.feature_user.data.models.response.LoginResponse
import retrofit2.Response
import retrofit2.http.GET

interface AuthUserRemoteDao {

    @GET("users/details")
    suspend fun getUserData(): Response<ApiResponse<UserRemote>>

    @GET("users/refreshAuth")
    suspend fun refreshAuthTokens(): Response<ApiResponse<LoginResponse>>

}
