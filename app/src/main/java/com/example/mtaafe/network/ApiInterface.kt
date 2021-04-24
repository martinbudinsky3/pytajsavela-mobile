package com.example.mtaafe.network

import com.example.mtaafe.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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

//    @Headers("Accept: application/octet-stream")
    @GET("images/{id}")
    @Streaming
    suspend fun getImage(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?
    ): Response<ResponseBody>

    @Headers("Accept: application/json")
    @GET("questions/{id}")
    suspend fun getQuestionDetails(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?
    ): Response<Question>

    @Headers("Accept: application/json")
    @Multipart
    @POST("questions")
    suspend fun postQuestion(
            @Header("Authorization") bearerToken: String,
            @Part("title") title : RequestBody,
            @Part("body") body : RequestBody,
            @Part("tags[]") tags : List<Long>,
            @Part images : List<MultipartBody.Part>?
    ): Response<Any>

    @Headers("Accept: application/json")
    @Multipart
    @POST("questions/{id}/answers")
    suspend fun postAnswer(
            @Header("Authorization") bearerToken: String,
            @Path("id") question_id: Long?,
            @Part("body") body : RequestBody,
            @Part images : List<MultipartBody.Part>?):
            Response<Any>

    @Headers("Accept: application/json")
    @GET("questions/{id}/edit-form")
    suspend fun getQuestionEditForm(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?
    ): Response<Question>

    @Headers("Accept: application/json")
    @GET("answers/{id}/edit-form")
    suspend fun getAnswerEditForm(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?
    ): Response<AnswerEdit>

    @Headers("Accept: application/json")
    @PUT("questions/{id}")
    suspend fun editQuestion(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?,
            @Body questionEdit: QuestionEdit):
            Response<Any>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PUT("answers/{id}")
    suspend fun editAnswer(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?,
            @Body answerEdit : AnswerEdit
    ): Response<Any>

    @Headers("Accept: application/json")
    @DELETE("questions/{id}")
    suspend fun deleteQuestion(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?):
            Response<Any>

    @Headers("Accept: application/json")
    @DELETE("answers/{id}")
    suspend fun deleteAnswer(
            @Header("Authorization") bearerToken: String,
            @Path("id") id: Long?):
            Response<Any>


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

    @Headers("Accept: application/json")
    @POST("logout")
    suspend fun logout(
        @Header("Authorization") bearerToken: String
    ): Response<Any>
}