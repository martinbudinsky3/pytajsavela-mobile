package com.example.mtaafe.network

import android.database.Observable
import com.example.mtaafe.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("questions")
    suspend fun postQuestion(
            @Part("title") title : RequestBody,
            @Part("body") body : RequestBody,
            @Part("tags") tags : List<RequestBody>?,
            @Part("images") images : List<MultipartBody.Part>?):
            Response<Question>

    @Multipart
    @POST("questions/{id}/answers")
    suspend fun postAnswer(
            @Part("body") body : RequestBody,
            @Part("images") images : List<MultipartBody.Part>?):
            Response<Answer>
}