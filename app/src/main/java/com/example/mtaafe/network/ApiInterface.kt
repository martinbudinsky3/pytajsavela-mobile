package com.example.mtaafe.network

import android.database.Observable
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.data.models.QuestionItem
import com.example.mtaafe.data.models.QuestionsList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*

interface ApiInterface {
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<LoggedInUser>

    @GET("questions")
    suspend fun getQuestionsList(@Header("Authorization") apiToken: String): Response<QuestionsList>
}