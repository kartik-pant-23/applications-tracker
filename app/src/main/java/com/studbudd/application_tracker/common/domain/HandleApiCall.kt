package com.studbudd.application_tracker.common.domain

import android.util.Log
import com.studbudd.application_tracker.common.data.models.ApiResponse
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class HandleApiCall(
    private val userDao: UserDao
) {
    suspend operator fun<T> invoke(
        apiCall: suspend () -> Response<ApiResponse<T>>,
        TAG: String
    ): Resource<T> {
        return try {
            val res = apiCall()
            if (res.code() == 401 || res.code() == 403) {
                userDao.deleteLocalUser()
                Resource.LoggedOut()
            } else if (res.isSuccessful && res.body() != null) {
                Resource.Success(res.body()!!.data!!)
            } else {
                Resource.Failure(res.message() ?: "Unknown error occurred")
            }
        } catch (e: HttpException) {
            Log.e(TAG, "$e")
            Resource.Failure("Oops! Something went wrong.")
        } catch (e: IOException) {
            Log.e(TAG, "$e")
            Resource.Failure("Couldn't reach server, check your internet connection")
        }
    }
}