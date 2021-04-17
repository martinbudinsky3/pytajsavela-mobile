package com.example.mtaafe.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
class AnswersRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun postAnswer(body : RequestBody, images : List<MultipartBody.Part>?) : ApiResult<out Any>{
        try {
            Log.d("Post answer api call", "Posting answer.")

            apiInterface?.postAnswer(
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
                    Log.e("Post answer api call", "Server returns response with error code.", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }
        catch (exception: Exception) {
            Log.e("Answers api call", "Error while connecting to server.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }
}