package com.i69.data.remote.repository

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.i69.BuildConfig
import com.i69.R
import com.i69.data.models.Id
import com.i69.data.models.User
import com.i69.data.remote.api.Api
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.requests.ReportRequest
import com.i69.data.remote.responses.ResponseBody
import com.i69.singleton.App
import com.i69.utils.Resource
import com.i69.utils.getResponse
import com.i69.utils.getStringFromList
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Keep
@Singleton
class UserUpdateRepository @Inject constructor(
    private val graphqlApi: GraphqlApi,
    private val api: Api,
) {
    private var TAG: String = UserUpdateRepository::class.java.simpleName

    /// Update Profile
    suspend fun updateProfile(user: User, token: String): Resource<ResponseBody<Id>> {
        val books = user.books?.getStringFromList()
        val movies = user.movies?.getStringFromList()
        val music = user.music?.getStringFromList()
        val sportsTeams = user.sportsTeams?.getStringFromList()
        val tvShows = user.tvShows?.getStringFromList()

        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${user.id}\", ").append("fullName: \"${user.fullName}\", ")
            .append("email: \"${user.email}\", ")
            .append(if (user.about.isNullOrEmpty()) "" else "about: \"${user.about}\", ")
            .append(if (user.age == null) "" else "age: ${user.age}, ")
            .append(if (user.avatarIndex == null) "" else "avatarIndex: ${user.avatarIndex}, ")
            .append(if (user.gender == null) "" else "gender: ${user.gender}, ")
            .append(if (user.height == null) "" else "height: ${user.height}, ")
            .append(if (books.isNullOrEmpty()) "" else "book: $books, ")
            .append(if (user.education.isNullOrEmpty()) "" else "education: \"${user.education}\", ")
            .append(if (user.ethnicity == null) "" else "ethinicity: ${user.ethnicity}, ")
            .append(if (user.familyPlans == null) "" else "familyPlans: ${user.familyPlans}, ")
            .append(if (user.interestedIn.isNullOrEmpty()) "" else "interestedIn: ${user.interestedIn.toList()}, ")
            .append(if (movies.isNullOrEmpty()) "" else "movies: $movies, ")
            .append(if (music.isNullOrEmpty()) "" else "music: $music, ")
            .append(if (user.religion == null) "" else "religion: ${user.religion}, ")
            .append(if (user.politics == null) "" else "politics: ${user.politics}, ")
            .append(if (sportsTeams.isNullOrEmpty()) "" else "sportsTeams : $sportsTeams, ")
            .append(if (user.tags.isNullOrEmpty()) "" else "tagIds: ${user.tags.toList()}, ")
            .append(if (tvShows.isNullOrEmpty()) "" else "tvShows: ${tvShows}, ")
            .append(if (user.work.isNullOrEmpty()) "" else "work: \"${user.work}\", ")
            .append(if (user.zodiacSign == null) "" else "zodiacSign: ${user.zodiacSign} ,")
            .append(if (user.userLanguageCode == null) "" else "userLanguageCode: \"${user.userLanguageCode}\"")
            .append(") {").append("id").append("}").append("}").toString()
        Log.e(TAG, "updateProfile:  familyPlansQuery : ${user.familyPlans.toString()}")
        Log.e(TAG, "updateProfile:  updateQuery : $query")
        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun uploadImage(
        userId: String, token: String, filePath: String, type: String
    ): Resource<ResponseBody<Id>> {

        try {
            Log.e(TAG, "UserId: $userId")
            Log.e(TAG, "Token: $token")
            Log.e(TAG, "FilePath: $filePath")
            Log.e(TAG, "Type: $type")
            val file = File(filePath)

            val client = OkHttpClient()

            var serverURL: String = "${BuildConfig.BASE_URL}api/user/$userId/photo/"
            Log.e(TAG, "ServerUrl: $serverURL")
            val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())


            val requestBody3: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("type", type)
                    .addFormDataPart(
                        "photo", file.name ?: "i69", requestBody
                    ).build()

            val request: Request =
                Request.Builder().addHeader("Authorization", "Token $token").url(serverURL)
                    .post(requestBody3).build()


            val response: Response = client.newCall(request).execute()


            return Resource.Success(
                code = com.i69.data.enums.HttpStatusCode.OK, ResponseBody(data = Id(""))
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(
                code = com.i69.data.enums.HttpStatusCode.BAD_REQUEST,
                message = e.localizedMessage ?: App.getAppContext()
                    .getString(R.string.something_went_wrong)
            )
        }
    }

    suspend fun uploadImageRetrofit(
        userId: String, token: String, filePath: String, type: String
    ): Resource<ResponseBody<Id>> {
        try {
            val file = File(filePath)

            val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

            val image = MultipartBody.Part.createFormData("photo", file.name ?: "i69", requestBody)


            val imageType = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), type)

            val result = api.uploadImage(
                token = "Token $token", userId = userId, image = image, imageType
            )

            Log.e(TAG, "Result $result")
            Log.e(TAG, "Result ${result.body()}")
            Log.e(TAG, "Result ${result.raw()}")

            return Resource.Success(
                code = com.i69.data.enums.HttpStatusCode.OK,
                ResponseBody(data = Id(result.body()!!.id.toString()))
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(
                code = com.i69.data.enums.HttpStatusCode.BAD_REQUEST,
                message = e.localizedMessage ?: App.getAppContext()
                    .getString(R.string.something_went_wrong)
            )
        }
    }

    suspend fun uploadImage2Retrofit(
        userId: String, token: String, file: File, type: String
    ): Resource<ResponseBody<Id>> {

        try {

            val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

            val image = MultipartBody.Part.createFormData("photo", file.name ?: "i69", requestBody)


            val imageType = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), type)

            val result = api.uploadImage(
                token = "Token $token", userId = userId, image = image, imageType
            )

            Log.e(TAG, "Result $result")
            Log.e(TAG, "Result ${result.body()}")
            Log.e(TAG, "Result ${result.raw()}")

            return Resource.Success(
                code = com.i69.data.enums.HttpStatusCode.OK,
                ResponseBody(data = Id(result.body()!!.id.toString()))
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(
                code = com.i69.data.enums.HttpStatusCode.BAD_REQUEST,
                message = e.localizedMessage ?: App.getAppContext()
                    .getString(R.string.something_went_wrong)
            )
        }
    }

    suspend fun uploadImage2(
        userId: String, token: String, file: File, type: String
    ): Resource<ResponseBody<Id>> {
        try {
            Log.e(TAG, "UserId: $userId")
            Log.e(TAG, "Token: $token")
            Log.e(TAG, "FilePath: ${file.absoluteFile}")
            Log.e(TAG, "Type: $type")
            val client = OkHttpClient()

            var serverURL: String = "${BuildConfig.BASE_URL}api/user/$userId/photo/"

            val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())


            val requestBody3: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("type", type)
                    .addFormDataPart(
                        "photo", file.name ?: "i69", requestBody
                    ).build()

            val request: Request =
                Request.Builder().addHeader("Authorization", "Token $token").url(serverURL)
                    .post(requestBody3).build()

            Log.e(TAG, "$request $requestBody")
            val response: Response = client.newCall(request).execute()


            Log.e(TAG, "responsecode ${response.code} $response")
            return Resource.Success(
                code = com.i69.data.enums.HttpStatusCode.OK, ResponseBody(data = Id(""))
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(
                code = com.i69.data.enums.HttpStatusCode.BAD_REQUEST,
                message = e.localizedMessage
                    ?: "${e.message}" // App.getAppContext().getString(R.string.something_went_wrong)
            )
        }
    }


    suspend fun deleteUserPhotos(token: String, photoId: String): Resource<ResponseBody<Id>> {
        val queryName = "deletePrivatePhoto"

        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${photoId}\" ").append(") {").append("message").append("}").append("}")
            .toString()

        return graphqlApi.getResponse(query, queryName, token)
    }


    suspend fun deleteUserPublicPhotos(token: String, photoId: String): Resource<ResponseBody<Id>> {
        val queryName = "deleteAvatarPhoto"

        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${photoId}\" ").append(") {").append("message").append("}").append("}")
            .toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun updateLanguage(
        languageCode: String, userid: String, token: String
    ): Resource<ResponseBody<Id>> {

        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userid}\", ").append("userLanguageCode: \"${languageCode}\", ")
            .append(") {").append("id,username").append("}").append("}").toString()
        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun updateLanguageId(
        languageCode: Int, userid: String, token: String
    ): Resource<ResponseBody<Id>> {

        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userid}\", ").append("language: ${languageCode} ").append(") {")
            .append("id,username").append("}").append("}").toString()
        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Update User Likes
    suspend fun updateUserLikes(
        userId: String, userLikes: ArrayList<String>, token: String
    ): Resource<ResponseBody<Id>> {
        val likes = userLikes.getStringFromList()

        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("likes: $likes ").append(") {").append("id")
            .append("}").append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Update One Signal Player Id
    suspend fun updateOneSignalPlayerId(
        userId: String, onesignalPlayerId: String, token: String
    ): Resource<ResponseBody<Id>> {
        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("onesignalPlayerId: \"${onesignalPlayerId}\" ")
            .append(") {").append("id").append("}").append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Update Firebase Token
    suspend fun updateFirebasrToken(
        userId: String, firebasetoken: String, token: String
    ): Resource<ResponseBody<Id>> {
        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("fcmRegistrationId: \"${firebasetoken}\" ")
            .append(") {").append("id").append("}").append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Update Location
    suspend fun updateLocation(
        userId: String, token: String, location: Array<Double>
    ): Resource<ResponseBody<Id>> {
        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("location: ${location.toList()} ").append(") {")
            .append("id").append("}").append("}").toString()
        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun updateOnlineStatus(
        userId: String, token: String, isUserOnline: Boolean
    ): Resource<ResponseBody<Id>> {
        val queryName = "updateProfile"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("isOnline: $isUserOnline ").append(") {")
            .append("id").append("}").append("}").toString()
        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Report
    suspend fun reportUser(
        reportRequest: ReportRequest, token: String?
    ): Resource<ResponseBody<Id>> {
        val queryName = "reportUser"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("reportee: \"${reportRequest.reportee}\", ")
            .append("reporter: \"${reportRequest.reporter}\", ")
            .append("reason: \"${reportRequest.timestamp}\"").append(") {").append("id").append("}")
            .append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    /// Block
    suspend fun blockUser(
        token: String?, userId: String?, blockedId: String?
    ): Resource<ResponseBody<Id>> {
        val queryName = "blockUser"
        val query =
            StringBuilder().append("mutation {").append("$queryName (").append("id: \"$userId\", ")
                .append("blockedId: \"$blockedId\"").append(") {").append("id").append("}")
                .append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun unblockUser(
        token: String, userId: String, blockedId: String
    ): Resource<ResponseBody<Id>> {
        val queryName = "unblockUser"
        val query =
            StringBuilder().append("mutation {").append("$queryName (").append("id: \"$userId\", ")
                .append("blockedId: \"$blockedId\"").append(") {").append("id").append("}")
                .append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }


    /// Delete
    suspend fun deleteProfile(
        userId: String,
        token: String,
        code: Int
    ): Resource<ResponseBody<JsonObject>> {
        val queryName = "deleteProfile"
        val query =
            StringBuilder().append("mutation {").append("$queryName (").append("id: \"${userId}\"")
                .append(", verificationCode:${code}")
                .append(") {").append("result").append("}").append("}").toString()

        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun verifyUser(email: String, token: String): Resource<ResponseBody<JsonObject>> {
        val queryName = "VerifyUser"
        var query = ""

        query = buildString {
            append("mutation $queryName {")
            append("  userVerify(email: \"$email\") {")
            append("    message")
            append("  }")
            append("}")
        }

        return graphqlApi.getResponse(query, queryName, token)
    }

    suspend fun getLanguages(): retrofit2.Response<com.i69.data.models.LanguageNewModel> {

        return api.getLanguages()
    }

    //Share Location
    suspend fun shareLocation(
        messageStr: String, roomId: Int, moderatorId: String, token: String
    ): Resource<ResponseBody<JsonObject>> {
        val queryName = "sendMessage"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            //.append("messageStrType:\"GL\", messageStr: \"${messageStr}\", roomId:$roomId, moderatorId: \"$moderatorId\"")
            .append("messageStrType:\"GL\", messageStr: \"${messageStr}\", roomId:$roomId")
//            .append("messageStrType:\"location\", messageStr: \"${messageStr}\", roomId:$roomId")
            .append(") {").append("message{").append("content\n").append("roomId{").append("name")
            .append("}").append("}").append("}").append("}").toString()
        return graphqlApi.getResponse(query, queryName, token)
    }

}