package com.example.mtaafe.data.repositories

import android.util.Log
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import java.lang.Exception

class QuestionsRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getQuestionsList(apiToken: String): ApiResult<out Any> {
        try {
            apiInterface?.getQuestionsList("Bearer $apiToken").let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Questions list api call", "Successful questions list api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Questions list api call", "Server returns response with error code.", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }

        // something else went wrong
        catch (exception: Exception) {
            Log.e("Questions list api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}