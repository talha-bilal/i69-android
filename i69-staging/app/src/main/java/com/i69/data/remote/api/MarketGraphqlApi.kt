package com.i69.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MarketGraphqlApi {

    /// Graphql Api
//    @POST("/")
//    suspend fun callApi(
//        @Header("Authorization") token: String?,
//        @Body body: String?
//    ): Response<String>

    @POST("i69/graphql")
    suspend fun callApi(
        @Header("Authorization") token: String?,
        @Body body: String?
    ): Response<String>
}