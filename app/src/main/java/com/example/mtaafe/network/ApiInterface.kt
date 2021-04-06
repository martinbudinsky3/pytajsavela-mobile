package com.example.mtaafe.network

import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.data.models.QuestionItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<LoggedInUser>

    @GET("questions")
    suspend fun getListOfQuestions(): Response<List<QuestionItem>>
}