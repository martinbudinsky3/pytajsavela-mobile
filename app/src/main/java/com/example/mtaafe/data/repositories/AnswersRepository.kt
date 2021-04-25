package com.example.mtaafe.data.repositories

import android.util.Log
import com.example.mtaafe.data.models.AnswerEdit
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.lang.Exception

class AnswersRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun postAnswer(apiToken: String, questionId: Long, body : RequestBody, images : List<MultipartBody.Part>?) : ApiResult<out Any>{
        try {
            Log.i("Post answer api call", "Posting answer.")

            apiInterface?.postAnswer(
                    "Bearer $apiToken",
                    questionId,
                    body,
                    images
            ).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Post answer api call", "Successful post answer api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Post answer api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        catch (exception: Exception) {
            Log.e("Post answer api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun editAnswer(apiToken: String, answerEdit: AnswerEdit): ApiResult<out Any> {
        try {
            Log.i("Edit answer api call", "Putting new answer details")

            apiInterface?.editAnswer(
                    "Bearer $apiToken",
                    answerEdit.id,
                    answerEdit
            ).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Edit answer api call", "Successful answer edit api call")
                    return ApiResult.Success(it)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Edit answer api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Edit answer api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getAnswerEditForm(apiToken: String, answerId: Long) : ApiResult<out Any>{
        try {
            Log.i("Answer edit info api call", "Getting answers data for edit form.")

            apiInterface?.getAnswerEditForm("Bearer $apiToken", answerId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Answer edit info api call", "Successful answer edit info api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Answer edit info api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        catch (exception: Exception) {
            Log.e("Answer edit info api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun deleteAnswer(apiToken: String, answerId: Long): ApiResult<out Any> {
        try {
            Log.i("Answer delete api call", "Deleting answer")

            apiInterface?.deleteAnswer("Bearer $apiToken", answerId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Answer delete api call", "Successful answer delete api call")
                    return ApiResult.Success(it)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Answer delete api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Answer delete api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }


}