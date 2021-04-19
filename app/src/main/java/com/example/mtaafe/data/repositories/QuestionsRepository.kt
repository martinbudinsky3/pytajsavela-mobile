package com.example.mtaafe.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mtaafe.data.models.*
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.Exception
import java.util.ArrayList

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

    suspend fun postQuestion(apiToken: String, title : RequestBody, body : RequestBody, tags : List<Long>, images : List<MultipartBody.Part>?) : ApiResult<out Any>{
        try {
            Log.d("Post question api call", "Posting question.")

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

    suspend fun getQuestionDetails(apiToken: String, questionId: Long): ApiResult<out Any> {
        try {
            Log.d("Quest. details api call", "Getting question details")

            apiInterface?.getQuestionDetails("Bearer $apiToken", questionId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Quest. details api call", "Successful question details api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Quest. details api call", "Server returns response with error code.", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Quest. details api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getQuestionEditForm(apiToken: String, questionId: Long): ApiResult<out Any> {
        try {
            Log.d("Ques edit-form api call", "Getting question edit-form")

            apiInterface?.getQuestionEditForm("Bearer $apiToken", questionId).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Ques edit-form api call", "Successful question edit-form api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Ques edit-form api call", "Server returns response with error code.", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Ques edit-form api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

//    suspend fun editQuestion(apiToken: String, questionId: Long, newTitle: RequestBody, newBody: RequestBody,
//                             newTags: List<RequestBody>?, deletedTags: List<RequestBody>?): ApiResult<out Any> {
suspend fun editQuestion(apiToken: String, questionId: Long, questionEdit: QuestionEdit): ApiResult<out Any> {
        try {
            Log.d("Edit question api call", "Putting new question details")

//            apiInterface?.editQuestion(
//                    "Bearer $apiToken",
//                    questionId,
//                    newTitle,
//                    newBody,
//                    newTags,
//                    deletedTags
            apiInterface?.editQuestion(
                    "Bearer $apiToken",
                    questionId,
                    questionEdit
            ).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Edit question api call", "Successful question edit api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Edit question api call", "Server returns response with error code.", exception)
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
}