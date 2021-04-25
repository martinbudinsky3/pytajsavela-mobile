package com.example.mtaafe.data.repositories

import android.util.Log
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import java.lang.Exception

class TagsRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getTagsList(apiToken: String, page: Int, searchQuery: String): ApiResult<out Any> {
        try {
            Log.i("Tags list api call", "Getting $page of tags list")
            apiInterface?.getTagsList("Bearer $apiToken", page, searchQuery).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Tags list api call", "Successful tags list api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Tags list api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Tags list api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun getTagQuestionsList(apiToken: String, tagId: Long, page: Int): ApiResult<out Any> {
        try {
            Log.i("Tag questions api call", "Getting $page of tag questions list")
            apiInterface?.getTagQuestionsList("Bearer $apiToken", tagId, page).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Tag questions api call", "Successful tag questions list api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Tag questions api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Tag questions api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}