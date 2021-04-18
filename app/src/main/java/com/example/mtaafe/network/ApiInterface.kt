package com.example.mtaafe.network

import com.example.mtaafe.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @Headers("Accept: application/json")
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<LoggedInUser>

    @Headers("Accept: application/json")
    @GET("questions")
    suspend fun getQuestionsList(
            @Header("Authorization") bearerToken: String,
            @Query("page") page: Int?
    ): Response<QuestionsList>

    @Multipart
    @POST("questions")
    suspend fun postQuestion(
            @Part("title") title : RequestBody,
            @Part("body") body : RequestBody,
            @Part("tags") tags : List<MultipartBody.Part>,
            @Part("images") images : List<MultipartBody.Part>):
            Response<Question>

    @Headers("Accept: application/json")
    @GET("tags")
    suspend fun getTagsList(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int?,
        @Query("search") search: String?
    ): Response<TagsList>

    @Headers("Accept: application/json")
    @GET("tags/{id}/questions")
    suspend fun getTagQuestionsList(
        @Header("Authorization") bearerToken: String,
        @Path("id") tagId: Long,
        @Query("page") page: Int?
    ): Response<TagQuestionsList>

    @Headers("Accept: application/json")
    @GET("users/{id}")
    suspend fun getUserInfo(
        @Header("Authorization") bearerToken: String,
        @Path("id") userId: Long
    ): Response<User>

    @Headers("Accept: application/json")
    @GET("users/{id}/questions")
    suspend fun getUserQuestionsList(
        @Header("Authorization") bearerToken: String,
        @Path("id") userId: Long
    ): Response<UserQuestionsList>

    @Headers("Accept: application/json")
    @GET("users/{id}/answers")
    suspend fun getUserAnswersList(
        @Header("Authorization") bearerToken: String,
        @Path("id") userId: Long
    ): Response<UserAnswersList>
}