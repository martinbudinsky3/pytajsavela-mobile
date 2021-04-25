package com.example.mtaafe.data.repositories

import android.R.attr.bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.mtaafe.data.models.*
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import com.example.mtaafe.views.adapters.QuestionAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.InputStream


class QuestionsRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getQuestionsList(apiToken: String, page: Int): ApiResult<out Any> {
        try {
            Log.i("Questions list api call", "Getting $page of questions list")

            apiInterface?.getQuestionsList("Bearer $apiToken", page).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Questions list api call", "Successful questions list api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Questions list api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
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

    suspend fun postQuestion(
        apiToken: String,
        title: RequestBody,
        body: RequestBody,
        tags: List<Long>,
        images: List<MultipartBody.Part>?
    ) : ApiResult<out Any>{
        try {
            Log.i("Post question api call", "Posting question.")

            apiInterface!!.postQuestion(
                "Bearer $apiToken",
                title,
                body,
                tags,
                images
            ).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Post question api call", "Successful post question api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Post question api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        catch (exception: Exception) {
            Log.e("Questions api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getQuestionDetails(apiToken: String, questionId: Long): ApiResult<out Any> {
        try {
            Log.i("Question detail api call", "Getting question details")

            apiInterface?.getQuestionDetails("Bearer $apiToken", questionId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Question detail api call", "Successful question detail api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Question detail api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Question detail api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getQuestionEditForm(apiToken: String, questionId: Long): ApiResult<out Any> {
        try {
            Log.i("Question edit info api call", "Getting question edit-form info")

            apiInterface?.getQuestionEditForm("Bearer $apiToken", questionId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Question edit info api call", "Successful question edit info api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Question edit info api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Question edit info api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun editQuestion(apiToken: String, questionId: Long, questionEdit: QuestionEdit): ApiResult<out Any> {
        try {
            Log.i("Edit question api call", "Putting new question details")

            apiInterface?.editQuestion(
                "Bearer $apiToken",
                questionId,
                questionEdit
            ).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Edit question api call", "Successful question edit api call")
                    return ApiResult.Success(it)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Edit question api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Edit question api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun deleteQuestion(apiToken: String, questionId: Long): ApiResult<out Any> {
        try {
            Log.i("Question delete api call", "Deleting question")

            apiInterface?.deleteQuestion("Bearer $apiToken", questionId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Question delete api call", "Successful question delete api call")
                    return ApiResult.Success(it)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Question delete api call",
                        "Server returns response with error code. Response: $it",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Question delete api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}