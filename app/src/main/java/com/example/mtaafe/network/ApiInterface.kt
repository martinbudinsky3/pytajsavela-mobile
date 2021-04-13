package com.example.mtaafe.network

import android.database.Observable
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.data.models.QuestionItem
import com.example.mtaafe.data.models.QuestionsList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface ApiInterface {
    @Headers("Accept: application/json")
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<LoggedInUser>

    @Headers("Accept: application/json")
    @GET("questions")
    suspend fun getQuestionsList(
            @Header("Authorization") apiToken: String,
            @Query("page") page: Int?
    ): Response<QuestionsList>
}