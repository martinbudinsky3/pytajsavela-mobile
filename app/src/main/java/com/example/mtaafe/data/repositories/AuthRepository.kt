package com.example.mtaafe.data.repositories

import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.ErrorEntity
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

    suspend fun login(credentials: Credentials) =
        try {
            apiInterface?.login(credentials).let {
                if(it?.isSuccessful == true) ApiResult.Success(it)
                else ApiResult.Error<ErrorEntity>(ErrorHandler.getError(HttpException(it)))
            }
        } catch (exception: Exception) {
            ApiResult.Error<ErrorEntity>(ErrorHandler.getError(exception))
        }
}