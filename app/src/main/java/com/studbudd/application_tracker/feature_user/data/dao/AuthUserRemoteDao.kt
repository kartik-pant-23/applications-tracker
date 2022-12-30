package com.studbudd.application_tracker.feature_user.data.dao

import com.studbudd.application_tracker.common.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.models.UserRemote
import retrofit2.Response
import retrofit2.http.GET

interface AuthUserRemoteDao {

    @GET("user")
    suspend fun getUserData(): Response<ApiResponse<UserRemote>>

}