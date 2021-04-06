package com.example.mtaafe.data.repositories

import androidx.lifecycle.LiveData
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import retrofit2.Response

class AuthRepository {
    private var apiInterface: ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    suspend fun login(credentials: Credentials): Response<LoggedInUser>? = apiInterface?.login(credentials)
}