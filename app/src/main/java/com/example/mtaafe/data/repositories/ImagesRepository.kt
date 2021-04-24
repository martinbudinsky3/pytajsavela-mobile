package com.example.mtaafe.data.repositories

import android.graphics.BitmapFactory
import android.util.Log
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import java.io.InputStream

class ImagesRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun getImage(apiToken: String, imageId: Long): ApiResult<out Any> {
        try {
            Log.d("Get Image api call", "Getting image")

            apiInterface?.getImage("Bearer $apiToken", imageId).let {
                if (it?.isSuccessful == true) {
                    val inputStream: InputStream = it?.body()!!.byteStream()

                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    return ApiResult.Success(bitmap)
                }

                else {
                    val exception = HttpException(it!!)
                    Log.e(
                        "Get image api call",
                        "Server returns response with error code.",
                        exception
                    )
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        // something else went wrong
        catch (exception: Exception) {
            Log.e("Get Image api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}