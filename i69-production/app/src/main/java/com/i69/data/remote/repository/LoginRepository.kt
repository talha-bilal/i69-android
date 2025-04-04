package com.i69.data.remote.repository

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.i69.GetAllSocialAuthStatusQuery
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.requests.LoginRequest
import com.i69.data.remote.responses.LoginResponse
import com.i69.data.remote.responses.ResponseBody
import com.i69.utils.Resource
import com.i69.utils.apolloClient
import com.i69.utils.getResponse
import com.i69.utils.getStringOrNull
import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton


private const val AVATAR_URL =
    "https://graph.facebook.com/%s/picture?type=large&width=1024&height=1024"
private const val EXTRA_FIELDS = "fields"

private const val EXTRA_FIELDS_VALUE = "id,name,email,picture.type(large)"

//private const val EXTRA_FIELDS_VALUE = "id, first_name, last_name,email,gender,birthday"
private const val EXTRA_NAME = "name"
private const val EXTRA_ID = "id"

@Keep
@Singleton
class LoginRepository @Inject constructor(private val api: GraphqlApi) {

    private var TAG: String = LoginRepository::class.java.simpleName

    suspend fun getAvailableSocialSignInClients(context: Context): ApolloResponse<GetAllSocialAuthStatusQuery.Data>? {
        val response = try {
            apolloClient(context, "").query(
                GetAllSocialAuthStatusQuery()
            ).execute()
        } catch (e: ApolloException) {
            Log.e(TAG,"apolloResponse ${e.message}")
            null
        }
        return response
    }

    suspend fun login(loginRequest: LoginRequest): Resource<ResponseBody<LoginResponse>> {
        val queryName = "socialAuth"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("accessToken: \"${loginRequest.accessToken}\", ")
            .append("accessVerifier: \"${loginRequest.accessVerifier}\", ")
            .append("provider: \"${loginRequest.provider}\"").append(") {")
            .append("id, email, token, isNew, username").append("}").append("}").toString()
        Log.e(TAG,"Query:  $query")
        Log.e(TAG,"QueryName $queryName")
        return api.getResponse(query, queryName)
    }

    fun getUserDataFromFacebook(
        loginResult: LoginResult, callback: (String?, ArrayList<String>?) -> Unit
    ) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken) { fbRes, _ ->
            val name = fbRes?.getStringOrNull(EXTRA_NAME)

//            val photos = arrayListOf(String.format(AVATAR_URL, fbRes?.getString(EXTRA_ID)))
            callback(name, null)
        }

        val appSecretProof = generateAppSecretProof(
            AccessToken.getCurrentAccessToken()!!.token, "cc752b4e78233fe6df148dc6305fb6d0"
        )
        val params = Bundle()
        params.putString(EXTRA_FIELDS, EXTRA_FIELDS_VALUE)
        params.putString("appsecret_proof", appSecretProof)
        request.parameters = params
        request.executeAsync()
    }

    private fun generateAppSecretProof(accessToken: String, appSecret: String): String {
        val hmacSHA256 = "HmacSHA256"
        val keySpec = SecretKeySpec(appSecret.toByteArray(), hmacSHA256)
        val mac = Mac.getInstance(hmacSHA256)
        mac.init(keySpec)
        val result = mac.doFinal(accessToken.toByteArray())

        // Convert to hexadecimal string
        return BigInteger(1, result).toString(16).padStart(64, '0')
    }

}