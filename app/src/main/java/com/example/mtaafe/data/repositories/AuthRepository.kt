package com.example.mtaafe.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.FcmToken
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import com.example.mtaafe.utils.ErrorHandler
import retrofit2.HttpException
import java.lang.Exception

class AuthRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun login(credentials: Credentials): ApiResult<out Any> {
        try {
            Log.i("Login api call", "Logging in user")
            apiInterface?.login(credentials).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Login api call", "Successful login api call")
                    return ApiResult.Success(it.body()!!)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Login api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }

        // something else went wrong
        catch (exception: Exception) {
            Log.e("Login api call", "Error while making api call.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun logout(apiToken: String): ApiResult<out Any> {
        try {
            Log.i("Logout api call", "Logging out user")
            apiInterface?.logout("Bearer $apiToken").let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Logout api call", "Successful logout api call")
                    return ApiResult.Success(it)
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Logout api call", "Server returns response with error code. Response: $it", exception)
                    return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
                }
            }
        }

        // something else went wrong
        catch (exception: Exception) {
            Log.e("Logout api call", "Error while making api call.", exception)
            return ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
    }

    suspend fun postFcmToken(apiToken: String, fcmToken: FcmToken) {
        try {
            Log.i("Fcm token api call", "Posting fcm token")
            apiInterface?.postFcmToken("Bearer $apiToken", fcmToken).let {
                // server returns 200
                if (it?.isSuccessful == true) {
                    Log.i("Logout api call", "Successful logout api call")
                }

                // server returns response with error code
                else {
                    val exception = HttpException(it!!)
                    Log.e("Fcm token api call", "Server returns response with error code. Response: $it", exception)
                }
            }
        }

        // something else went wrong
        catch (exception: Exception) {
            Log.e("Fcm token api call", "Error while making api call.", exception)
        }
    }
}