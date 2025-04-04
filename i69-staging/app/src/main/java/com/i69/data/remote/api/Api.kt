package com.i69.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import com.i69.data.remote.responses.ImageResult
import okhttp3.RequestBody

interface Api {

    /// Upload Image
    @Multipart
    @POST("/api/user/{userId}/photo/")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): Response<ImageResult>


    @GET("/api")
    suspend fun getLanguages(): Response<com.i69.data.models.LanguageNewModel>

}