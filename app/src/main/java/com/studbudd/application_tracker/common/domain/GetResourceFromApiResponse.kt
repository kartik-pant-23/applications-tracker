package com.studbudd.application_tracker.common.domain

import android.util.Log
import com.studbudd.application_tracker.common.models.ApiResponse
import com.studbudd.application_tracker.common.models.Resource
import retrofit2.Response

class GetResourceFromApiResponse<T> {
    operator fun invoke(response: Response<ApiResponse<T>>, TAG: String): Resource<T> {
        return if (response.code() == 200 && response.body() != null) {
            Resource.Success(response.body()!!.message, response.body()!!.data!!)
        } else {
            Log.d(TAG, "{ response: $response }")
            Resource.Failure(response.message())
        }
    }
}