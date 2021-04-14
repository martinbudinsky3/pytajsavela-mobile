package com.example.mtaafe.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.Question
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
class QuestionsRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getQuestionsList(apiToken: String, page: Int): ApiResult<out Any> {
        try {
            Log.d("Questions list api call", "Getting `${page}`")

            apiInterface?.getQuestionsList("Bearer $apiToken", page).let {
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

    suspend fun postQuestion(question : Question) : ApiResult<out Any>{
        try {
            Log.d("Post question api call", "Posting question.")

            apiInterface?.postQuestion(question).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Post question api call", "Successful post question api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Post question api call", "Server returns response with error code.", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        catch (exception: Exception) {
            Log.e("Questions api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}