package com.studbudd.application_tracker.feature_user.data.dao

import com.studbudd.application_tracker.common.data.models.ApiResponse
import com.studbudd.application_tracker.feature_user.data.entity.UserRemote
import retrofit2.Response
import retrofit2.http.GET

interface AuthUserRemoteDao {

    @GET("user")
    suspend fun getUserData(): Response<ApiResponse<UserRemote>>

}