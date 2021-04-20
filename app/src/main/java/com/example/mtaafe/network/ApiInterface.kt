package com.example.mtaafe.network

import android.database.Observable
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.models.Tag
import com.example.mtaafe.views.AnswerEdit
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

    @Headers("Accept: application/json")
    @GET("questions/{id}")
    suspend fun getQuestionDetails(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?
    ): Response<Question>

    @Headers("Accept: application/json")
    @Multipart
    @JvmSuppressWildcards
    @POST("questions")
    suspend fun postQuestion(
            @Header("Authorization") apiToken: String,
            @Part("title") title : RequestBody,
            @Part("body") body : RequestBody,
            @Part("tags") tags : List<Long>,
            @Part images : List<MultipartBody.Part>?
    ): Response<Any>

    @Headers("Accept: application/json")
    @Multipart
    @JvmSuppressWildcards
    @POST("questions/{id}/answers")
    suspend fun postAnswer(
            @Header("Authorization") apiToken: String,
            @Path("id") question_id: Long?,
            @Part("body") body : RequestBody,
            @Part images : List<MultipartBody.Part>?):
            Response<Any>

    @Headers("Accept: application/json")
    @GET("questions/{id}/edit-form")
    suspend fun getQuestionEditForm(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?
    ): Response<Question>

    @Headers("Accept: application/json")
    @GET("answers/{id}/edit-form")
    suspend fun getAnswerEditForm(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?
    ): Response<Answer>

    @Headers("Accept: application/json")
    @JvmSuppressWildcards
    @PUT("questions/{id}")
    suspend fun editQuestion(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?,
            @Body questionEdit: QuestionEdit):
            Response<Any>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @JvmSuppressWildcards
    @PUT("answers/{id}")
    suspend fun editAnswer(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?,
            @Body answerEdit : AnswerEdit):
            Response<Any>

    @Headers("Accept: application/json")
    @JvmSuppressWildcards
    @DELETE("questions/{id}")
    suspend fun deleteQuestion(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?):
            Response<Any>

    @Headers("Accept: application/json")
    @JvmSuppressWildcards
    @DELETE("answers/{id}")
    suspend fun deleteAnswer(
            @Header("Authorization") apiToken: String,
            @Path("id") id: Long?):
            Response<Any>
}