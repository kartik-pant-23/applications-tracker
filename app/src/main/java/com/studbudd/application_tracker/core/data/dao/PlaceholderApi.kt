package com.studbudd.application_tracker.core.data.dao

import com.studbudd.application_tracker.core.data.models.ApiResponse
import com.studbudd.application_tracker.core.data.models.UpdatePlaceholderRequest
import com.studbudd.application_tracker.feature_user.data.models.remote.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface PlaceholderApi {

    @PATCH("users/updatePlaceholders")
    suspend fun updatePlaceholders(@Body body: UpdatePlaceholderRequest): Response<ApiResponse<UserDto>>

}