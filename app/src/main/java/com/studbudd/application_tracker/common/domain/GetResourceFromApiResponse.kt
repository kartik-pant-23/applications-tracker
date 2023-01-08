package com.studbudd.application_tracker.common.domain

import android.util.Log
import com.studbudd.application_tracker.common.models.ApiResponse
import com.studbudd.application_tracker.common.models.Resource
import retrofit2.Response

class GetResourceFromApiResponse<T> {
    operator fun invoke(response: Response<ApiResponse<T>>, TAG: String): Resource<T> {
        return if (response.code() == 401 || response.code() == 403) {
            // TODO - Refresh the token from here itself
            Log.e(TAG, "response: $response")
            Resource.LoggedOut()
        }
        else if (response.code() == 200 && response.body() != null) {
            Resource.Success(response.body()!!.data!!, response.body()!!.message)
        } else {
            Log.d(TAG, "{ response: $response }")
            Resource.Failure(response.message())
        }
    }
}