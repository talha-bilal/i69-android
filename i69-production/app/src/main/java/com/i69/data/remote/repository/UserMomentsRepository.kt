package com.i69.data.remote.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.i69.GetAllUserMomentsQuery
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.R
import com.i69.data.models.Moment
import com.i69.data.models.OfflineStory
import com.i69.data.models.UserAvatar
import com.i69.data.models.UserWithAvatar
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.responses.MomentLikes
import com.i69.ui.screens.main.moment.db.MomentDao
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientForContore
import com.i69.utils.getGraphqlApiBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserMomentsRepository @Inject constructor(
    private val api: GraphqlApi, private val momentsDao: MomentDao
) {
    private var _coinPrice: ArrayList<MomentLikes> = ArrayList()
    private val coinPrice: MutableLiveData<ArrayList<MomentLikes>> = MutableLiveData()
    var TAG: String = UserMomentsRepository::class.java.simpleName
    private var isApiRunning = false
    suspend fun insertMomentsList(moments: List<Moment>) {
        return withContext(Dispatchers.IO) {
            momentsDao.insertMomentsList(moments)
        }
    }

    suspend fun getMomentsList(): List<Moment> {
        return withContext(Dispatchers.IO) {
            momentsDao.getMomentsList()
        }
    }

    suspend fun deleteAllOfflineMoments() {
        withContext(Dispatchers.IO) {
            momentsDao.deleteAllMoments()
        }
    }

    fun insertStoryList(moments: List<OfflineStory>) {
        return momentsDao.insertStoryList(moments)
    }

    fun getStoryList(): List<OfflineStory> {
        return momentsDao.getStoryList()
    }

    fun deleteAllStories() {
        momentsDao.deleteAllStories()
    }

    fun getMomentLikes(
        viewModelScope: CoroutineScope,
        momentPk: String,
        token: String,
        callback: (ArrayList<MomentLikes>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _coinPrice.clear()
            val queryName = "getMomentLikes"
            val query =
                StringBuilder().append("query {").append("$queryName(momentPk: $momentPk){ ")
                    .append("user{  ").append("id, ").append("username, ").append("email, ")
                    .append("fullName, ").append("avatar{ ").append("url ").append("}").append("}")
                    .append("}").append("}").toString()

            try {
                val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
                val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
                Log.e(TAG,"Query: $query")
                Log.e(TAG,"Result: ${result.body()}")

                when {
                    result.isSuccessful -> {
                        val dataJsonObject = jsonObject["data"].asJsonObject
                        Log.e(TAG,"dataJsonObject: $dataJsonObject")
                        val coinSettingsJsonArray = dataJsonObject[queryName].asJsonArray
                        Log.e(TAG,"coinSettingsJsonArray: $coinSettingsJsonArray")


                        coinSettingsJsonArray.forEach { jsonElement ->
                            Log.e(TAG,"JsonElement: $jsonElement")
                            val userJson = jsonElement.asJsonObject

                            // Extract the 'user' object
                            val userObject: JsonObject = userJson.getAsJsonObject("user")

                            // Extract individual fields from the 'user' object
                            val id: String = userObject.get("id").asString
                            val username: String = userObject.get("username").asString
                            val email: String = userObject.get("email").asString
                            val fullName: String = userObject.get("fullName").asString

                            // Extract the 'avatar' object
//                            val avatarObject: JsonObject = userObject.getAsJsonObject("avatar")
//                            val avatarUrl: String = avatarObject.get("url").asString

                            val avatarObject: JsonElement? = userObject.get("avatar")

                            val avatarUrl: String = if (avatarObject != null && avatarObject.isJsonObject) {
                                val jsonObject = avatarObject.asJsonObject
                                if (jsonObject.has("url") && !jsonObject.get("url").isJsonNull) {
                                    jsonObject.get("url").asString
                                } else {
                                    ""
                                }
                            } else {
                                ""
                            }


                            val userAvatar = UserAvatar(avatarUrl)
                            val userWithAvatar = UserWithAvatar(
                                id,
                                username,
                                fullName,
                                email,
                                userAvatar
                            )
                            val momentLikes: MomentLikes = MomentLikes(userWithAvatar)

//                            val json = Gson().fromJson(jsonElement, MomentLikes::class.java)
                            _coinPrice.add(momentLikes)
                        }

                        val error: String? = if (jsonObject.has("errors")) {
                            jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                        } else null


                        if (error.isNullOrEmpty()) coinPrice.postValue(_coinPrice)

                        callback(_coinPrice)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

//    fun getMomentLikes(
//        viewModelScope: CoroutineScope,
//        momentPk: String,
//        token: String,
//    ): MutableLiveData<ArrayList<MomentLikes>> {
//
//        _coinPrice.clear()
//        if (_coinPrice.isEmpty()) {
//            viewModelScope.launch(Dispatchers.IO) {
//                loadMomentsLikes(token, momentPk)
//
//            }
//        }
//        coinPrice.value = _coinPrice
//        return coinPrice
//    }

    private suspend fun loadMomentsLikes(token: String, momentPk: String) {
//        val queryName = "selfMomentLikes"

        val queryName = "getMomentLikes"

        val query = StringBuilder().append("query {").append("$queryName(momentPk: $momentPk){ ")
            .append("user{  ").append("id, ").append("username, ").append("email, ")
            .append("fullName, ").append("avatar{ ").append("url ").append("}").append("}")
            .append("}").append("}").toString()

        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG,"Query: $query")
            Log.e(TAG,"Result: ${result.body()}")

            when {
                result.isSuccessful -> {
                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val coinSettingsJsonArray = dataJsonObject[queryName].asJsonArray

                    coinSettingsJsonArray.forEach { jsonElement ->
                        val json = Gson().fromJson(jsonElement, MomentLikes::class.java)
                        _coinPrice.add(json)
                    }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null


                    if (error.isNullOrEmpty()) coinPrice.postValue(_coinPrice)

//                    callback(_coinPrice)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getMultiUserStories(
        context: Context,
        token: String,
        callback: (success: Int, stories: List<GetAllUserMultiStoriesQuery.AllUserMultiStory?>?, message: String) -> Unit
    ) {
        val storiesQuery = GetAllUserMultiStoriesQuery()
        Log.e(TAG, " storiesQuery - " + storiesQuery)
        val res = try {
            apolloClient(context, token).query(storiesQuery)
                .execute()
        } catch (e: ApolloException) {
            null
        }

        if (res != null) {
            if (res.hasErrors()) {
                val errorMessage = res.errors?.get(0)?.message
                callback(0, null, errorMessage.toString())
            } else {
                callback(1, res.data?.allUserMultiStories, "")
            }
        } else {
            callback(0, null, "")
        }
    }


    suspend fun getUserMoments(
        context: Context,
        viewModelScope: CoroutineScope,
        token: String,
        width: Int,
        size: Int,
        i: Int,
        endCursors: String,
        callback: (
            ArrayList<GetAllUserMomentsQuery.Edge>,
            endCursor: String?,
            hasNextPage: Boolean?
        ) -> Unit,
        onFailure: (String) -> Unit
    ) {
//        if (!isApiRunning) {
//            isApiRunning = true
       // val momentsQuery2 = GetAllSeachProductsMutations
        val momentsQuery = GetAllUserMomentsQuery(
            width = width,
            characterSize = size,
            first = i,
            after = endCursors,
            ""
        )
        Log.e(TAG,"Width: $width")
        Log.e(TAG,"Size: $size")
        Log.e(TAG,"First: $i")
        Log.e(TAG,"EndCursors: $endCursors")
        Log.e(TAG,"MomentsQuery: $momentsQuery")
        val res = try {
            apolloClientForContore(context, token).query(momentsQuery).execute()
        } catch (e: ApolloException) {
            Log.e(TAG,"apolloResponse ${e.message}")
            onFailure(e.message.toString())
            null
        }
        if (res != null) {
            if (res.hasErrors()) {
//                Log.e("rr2rrr","-->"+res.errors!!.get(0).nonStandardFields!!.get("code").toString())
                if (res.errors?.size!! > 0) {
                    onFailure(res.errors!![0].message)
                    Log.e(TAG,"$TAG error- apolloResponse;- ${res.errors}")
                    Log.e(TAG,"$TAG error- apolloResponse;- ${res.errors!![0].message}")
                } else {
                    onFailure(context.getString(R.string.something_went_wrong))
                }

//                if (res.errors!![0].nonStandardFields?.get("code")
//                        .toString() == "InvalidOrExpiredToken"
//                ) {
//                    // error("User doesn't exist")
//                    onFailure("Something wrong happens")
//                }
            } else {
                Log.e(TAG,"PageInfo: ${res.data?.allUserMoments?.pageInfo}")
                Log.e(TAG,"$TAG -- res -- moments: ${res.data}")
                val allMoments = res.data?.allUserMoments?.edges
                val endCursor: String? = res.data?.allUserMoments?.pageInfo?.endCursor
                val hasNextPage: Boolean? = res.data?.allUserMoments?.pageInfo?.hasNextPage
                if (!allMoments.isNullOrEmpty()) {
                    val allUserMomentsFirst: ArrayList<GetAllUserMomentsQuery.Edge> =
                        ArrayList()
                    allMoments.indices.forEach { i ->
                        allMoments[i]?.let { allUserMomentsFirst.add(it) }
                    }
                    val finalMoments =
                        allUserMomentsFirst.distinctBy { it.node?.pk } as ArrayList
                    callback(finalMoments, endCursor, hasNextPage)
                }
            }
//                isApiRunning = false
        }
//        } else {
//            Log.e("Api is already running")
//        }
    }


//    private suspend fun getAllMoments(
//        token: String,
//        query: String,
//        getMomentQuryName: String,
//        callback: (List<User>, List<User>, List<User>, String?) -> Unit
//    ) {
//        try {
//            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
//            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
//            Log.e("Query: $query")
//
//
//            when {
//                result.isSuccessful -> {
//                    val allMomentsList = ArrayList<User>()
//
//                    val dataJsonObject = jsonObject["data"].asJsonObject
//                    val allMometsArrays = dataJsonObject[getMomentQuryName].asJsonArray
//
//                    allMometsArrays.forEach { jsonElement ->
//                        val json = Gson().fromJson(jsonElement, User::class.java)
//                        allMomentsList.add(json)
//                    }
//
//
//                    val error: String? = if (jsonObject.has("errors")) {
//                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
//                    } else {
//                        null
//                    }
//
//                    if (error.isNullOrEmpty())
//                        callback.invoke(allMomentsList, emptyList(), emptyList(),null)
//                    else
//                        callback.invoke(emptyList(), emptyList(), emptyList(), error)
//
//                }
//                else -> callback.invoke(emptyList(), emptyList(), emptyList(), "Something went wrong! Please try again later!")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            callback.invoke(emptyList(), emptyList(), emptyList(), "Something went wrong! Please try again later!")
//        }
//    }

}