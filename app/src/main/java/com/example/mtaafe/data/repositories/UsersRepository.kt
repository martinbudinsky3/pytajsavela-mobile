package com.example.mtaafe.data.repositories

import android.util.Log
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import java.lang.Exception

class UsersRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getUserInfo(apiToken: String, userId: Long): ApiResult<out Any> {
        try {
            Log.i("User info api call", "Getting user info")
            apiInterface?.getUserInfo("Bearer $apiToken", userId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("User info api call", "Successful user info api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("User info api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("UUser info api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getUserQuestionsList(apiToken: String, userId: Long): ApiResult<out Any> {
        try {
            Log.i("User questions api call", "Getting user questions")
            apiInterface?.getUserQuestionsList("Bearer $apiToken", userId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("User questions api call", "Successful user questions api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("User questions api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("User questions api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getUserAnswersList(apiToken: String, userId: Long): ApiResult<out Any> {
        try {
            Log.i("User answers api call", "Getting user answers")
            apiInterface?.getUserAnswersList("Bearer $apiToken", userId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("User answers api call", "Successful user answers api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("User answers api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("User answers api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}