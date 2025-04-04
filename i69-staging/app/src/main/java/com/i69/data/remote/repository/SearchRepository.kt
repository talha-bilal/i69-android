package com.i69.data.remote.repository

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.MyPermission
import com.i69.data.models.User
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.requests.SearchRequest
import com.i69.data.remote.requests.SearchRequestNew
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.getSearchMostActiveUsersQueryResponse
import com.i69.utils.getSearchNewQueryResponse
import com.i69.utils.getSearchPopularUsersQueryResponse
import com.i69.utils.getSearchQueryResponseAll
import com.i69.utils.getSearchQueryResponseNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Keep
@Singleton
class SearchRepository @Inject constructor(

    private val api: GraphqlApi
) {
    private var TAG: String = SearchRepository::class.java.simpleName

    fun getSearchMostActiveUsers(
        viewModelScope: CoroutineScope,
        token: String,
        searchRequest: SearchRequest,
        autoDeductCoin: Int = 0,
        interestedIn: Int,
        context: Context,
        hasSkip: Boolean,
        callback: (List<User>, String?, MyPermission) -> Unit
    ) {
        val mostActiveUsersQueryName = "mostActiveUsers"

        var searchQuery = ""
        if (hasSkip) {
            Log.e(TAG,"getSearchMostActiveUsers: mySkippedActiveUsers : $mostActiveUsersQueryName")
            searchQuery = getSearchMostActiveUsersQueryResponse(autoDeductCoin, interestedIn)
        } else {
            var hasLocation =
                searchRequest.lat != null && searchRequest.long != null && searchRequest.maxDistance != null
            if (hasLocation) {
                hasLocation =
                    searchRequest.lat!!.roundToInt() != 0 && searchRequest.long!!.roundToInt() != 0
            }

            // val searchQuery = getSearchQueryResponse(randomUsersQueryName, popularUsersQueryName, searchRequest, hasLocation)
            searchQuery = getSearchQueryResponseNew(
                randomUsersQueryName = mostActiveUsersQueryName,
                autoDeductCoin = autoDeductCoin,
                searchRequest = searchRequest,
                hasLocation = hasLocation
            )
        }
        Log.e(TAG,"getSearchMostActiveUsers: getSearchMostActiveUsers: $searchQuery")

        viewModelScope.launch(Dispatchers.IO) {
            getMostActiveUsers(
                token, searchQuery,

                mostActiveUsersQueryName, context = context
            ) { _popularUsers, error, myPermission ->
                viewModelScope.launch(Dispatchers.Main) {
                    callback(_popularUsers, error, myPermission)
                }
            }
        }
    }


    // working method
    fun getSearchPopularUsers(
        viewModelScope: CoroutineScope,
        token: String,
        searchRequest: SearchRequest,
        autoDeductCoin: Int = 0,
        interestedIn: Int,
        context: Context,
        hasSkip: Boolean,
        callback: (List<User>, String?, MyPermission) -> Unit
    ) {

        val popularUsersQueryName = "popularUsers"

        // val searchQuery = getSearchQueryResponse(randomUsersQueryName, popularUsersQueryName, searchRequest, hasLocation)
        var searchQuery = ""
        if (hasSkip) {
            Log.e(TAG,"getSearchPopularUsers: mySkippedPopilarUsres : $popularUsersQueryName")
            searchQuery = getSearchPopularUsersQueryResponse(autoDeductCoin, interestedIn)
        } else {
            var hasLocation =
                searchRequest.lat != null && searchRequest.long != null && searchRequest.maxDistance != null

            if (hasLocation) {
                hasLocation =
                    searchRequest.lat!!.roundToInt() != 0 && searchRequest.long!!.roundToInt() != 0
            }
//         val searchQuery = getSearchQueryResponse(randomUsersQueryName, popularUsersQueryName, searchRequest, hasLocation)

            searchQuery = getSearchQueryResponseNew(
                randomUsersQueryName = popularUsersQueryName,
                autoDeductCoin = autoDeductCoin,
                searchRequest = searchRequest,
                hasLocation = hasLocation
            )

        }
        Log.e(TAG,"popularUsersQueryName: getSearchPopular: $searchQuery")
//        val searchQuery = getSearchPopularUsersQueryResponse(autoDeductCoin, interestedIn)

        viewModelScope.launch(Dispatchers.IO) {
            getPopularUsers(
                token,
                searchQuery,

                popularUsersQueryName,
                context = context,
            ) { _popularUsers, error, myPermission ->
                viewModelScope.launch(Dispatchers.Main) {
                    callback(_popularUsers, error, myPermission)
                }
            }
        }
    }


    // working method
    fun getSearchUsers(
        viewModelScope: CoroutineScope,
        token: String,
        searchRequest: SearchRequest,
        autoDeductCoin: Int = 0,
        context: Context,
        hasSkip: Boolean,
        callback: (List<User>, List<User>, List<User>, String?, MyPermission, MyPermission, MyPermission) -> Unit
    ) {
        val randomUsersQueryName = "randomUsers"
        val popularUsersQueryName = "popularUsers"
        val mostActiveUsersQueryName = "mostActiveUsers"
        var hasLocation = searchRequest.lat != null && searchRequest.long != null && searchRequest.maxDistance != null

        if (hasLocation) {
            hasLocation =
                searchRequest.lat!!.roundToInt() != 0 && searchRequest.long!!.roundToInt() != 0
        }

//         val searchQuery = getSearchQueryResponse(randomUsersQueryName, popularUsersQueryName, searchRequest, hasLocation)

//        val searchQuery = getSearchQueryResponseNew(randomUsersQueryName = randomUsersQueryName,autoDeductCoin =autoDeductCoin, searchRequest=searchRequest, hasLocation=hasLocation)
        val searchQuery = if (hasSkip) {
            getSearchQueryResponseNew(autoDeductCoin = autoDeductCoin)
        } else {
            getSearchQueryResponseAll(
                randomUsersQueryName = randomUsersQueryName,
                popularUsersQueryName = popularUsersQueryName,
                mostActiveUsersQueryName = mostActiveUsersQueryName,
                autoDeductCoin = autoDeductCoin,
                searchRequest = searchRequest,
                hasLocation = hasLocation
            )
        }

        Log.e(TAG,"SearchRepository: Query: $searchQuery")
        Log.e(TAG,"getSearchUsers: getSearchnew : $searchQuery")

        viewModelScope.launch(Dispatchers.IO) {
            getUsers(
                token,
                searchQuery,
                randomUsersQueryName,
                popularUsersQueryName,
                mostActiveUsersQueryName,
                context = context,
            ) { _randomUsers, _popularUsers, _mostActiveUsers, error, randomPermission, popularPermission, mostactivePermission ->
                viewModelScope.launch(Dispatchers.Main) {
                    callback(
                        _randomUsers,
                        _popularUsers,
                        _mostActiveUsers,
                        error,
                        randomPermission,
                        popularPermission,
                        mostactivePermission
                    )
                }
            }
        }
    }


    fun getSearchUsersNew(
        viewModelScope: CoroutineScope,
        token: String,
        searchRequest: SearchRequestNew,
        autoDeductCoin: Int = 0,
        context: Context,
        callback: (List<User>, List<User>, List<User>, String?) -> Unit
    ) {
        val randomUsersQueryName = "randomUsers"
        val popularUsersQueryName = "popularUsers"

        val searchQuery = getSearchNewQueryResponse(
            randomUsersQueryName,
            popularUsersQueryName,
            searchRequest,
            autoDeductCoin,
        )

        Log.e(TAG,"getSearchUsersNew: getSearchPopular: $searchQuery")

        viewModelScope.launch(Dispatchers.IO) {
            getSearchUsers(
                token,
                searchQuery,
                randomUsersQueryName,
                popularUsersQueryName,
                context = context,
            ) { _randomUsers, _popularUsers, _mostActiveUsers, error, mypermission ->
                viewModelScope.launch(Dispatchers.Main) {
                    callback(_randomUsers, _popularUsers, _mostActiveUsers, error)
                }
            }
        }
    }

    private suspend fun getUsers(
        token: String,
        query: String,
        randomUsersQueryName: String,
        popularUsersQueryName: String,
        mostActiveUsersQueryName: String,
        context: Context,
        callback: (List<User>, List<User>, List<User>, String?, MyPermission, MyPermission, MyPermission) -> Unit
    ) {
        try {
            Log.e(TAG,"getUsers: $query")
            Log.e(TAG,"getUsers: ${query.getGraphqlApiBody()}")
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)

            Log.e(TAG,"getUsers")

            Log.e(TAG,"Token: $token")
            Log.e(TAG,"Query: $query")
            Log.e(TAG,"Result: ${result.body()}")
            Log.e(TAG,"getUsers: ${query.getGraphqlApiBody()}")
            Log.e(TAG,"getUsers: Result: ${result.body()}")
            Log.e(TAG,"getUsers: $jsonObject")

            when {
                result.isSuccessful -> {
                    val randomUsersList = ArrayList<User>()
                    val popularUsersList = ArrayList<User>()
                    val mostActiveUsersList = ArrayList<User>()


                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else {
                        null
                    }


                    val dataJsonObject = jsonObject["data"].asJsonObject
                    var randomPermission = MyPermission(false)
                    var popularPermission = MyPermission(false)
                    var mostactivePermission = MyPermission(false)
                    dataJsonObject.isJsonNull
                    var randomUsersJsonArray: JsonArray? = null

                    if (dataJsonObject.has(randomUsersQueryName) && !dataJsonObject[randomUsersQueryName].isJsonNull) {


                        val randomJsonObject = dataJsonObject[randomUsersQueryName].asJsonObject


                        randomUsersJsonArray = randomJsonObject["user"].asJsonArray


                        if (randomJsonObject.has("myPermission")) {
                            val permissionObject = randomJsonObject["myPermission"].asJsonObject

                            randomPermission =
                                Gson().fromJson(permissionObject, MyPermission::class.java)
                        }
                    }

                    randomUsersJsonArray?.forEach { jsonElement ->
                        val json = Gson().fromJson(jsonElement, User::class.java)
                        randomUsersList.add(json)
                    }

                    var popularUsersJsonArray: JsonArray? = null

                    if (dataJsonObject.has(popularUsersQueryName) && !dataJsonObject[popularUsersQueryName].isJsonNull) {


                        val popularJsonObject = dataJsonObject[popularUsersQueryName].asJsonObject

                        popularUsersJsonArray = popularJsonObject["user"].asJsonArray


                        if (popularJsonObject.has("myPermission")) {
                            val permissionObject = popularJsonObject["myPermission"].asJsonObject

                            popularPermission =
                                Gson().fromJson(permissionObject, MyPermission::class.java)
                        }
                    }
                    if (popularUsersJsonArray != null) {
                        popularUsersJsonArray.forEach { jsonElement ->
                            val json = Gson().fromJson(jsonElement, User::class.java)
                            popularUsersList.add(json)
                        }
                    }

                    var mostActiveUsersJsonArray: JsonArray? = null

                    if (dataJsonObject.has(mostActiveUsersQueryName) && !dataJsonObject[mostActiveUsersQueryName].isJsonNull) {


                        var mostActiveJsonObject =
                            dataJsonObject[mostActiveUsersQueryName].asJsonObject


                        mostActiveUsersJsonArray = mostActiveJsonObject["user"].asJsonArray


                        if (mostActiveJsonObject.has("myPermission")) {
                            val permissionObject = mostActiveJsonObject["myPermission"].asJsonObject

                            mostactivePermission =
                                Gson().fromJson(permissionObject, MyPermission::class.java)
                        }
                    }
                    if (mostActiveUsersJsonArray != null) {
                        mostActiveUsersJsonArray.forEach { jsonElement ->
                            val json = Gson().fromJson(jsonElement, User::class.java)
                            mostActiveUsersList.add(json)
                        }
                    }


//
//                    popularUsersJsonArray.forEach { jsonElement ->
//                        val json = Gson().fromJson(jsonElement, User::class.java)
//                        popularUsersList.add(json)
//                    }

//                    val mostActiveUsersList = randomUsersList.filter { it.isOnline }

                    if (error.isNullOrEmpty()) callback.invoke(
                        randomUsersList,
                        popularUsersList,
                        mostActiveUsersList,
                        null,
                        randomPermission,
                        popularPermission,
                        mostactivePermission
                    )
                    else callback.invoke(
                        emptyList(),
                        emptyList(),
                        emptyList(),
                        error,
                        randomPermission,
                        popularPermission,
                        mostactivePermission
                    )

                }

                else -> callback.invoke(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    AppStringConstant1.something_went_wrong_please_try_again_later,
//                    context.getString(R.string.something_went_wrong_please_try_again_later),
                    MyPermission(false),
                    MyPermission(false),
                    MyPermission(false)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(
                emptyList(),
                emptyList(),
                emptyList(),
                AppStringConstant1.something_went_wrong_please_try_again_later,
//                context.getString(R.string.something_went_wrong_please_try_again_later),
                MyPermission(hasPermission = false),
                MyPermission(hasPermission = false),
                MyPermission(hasPermission = false)
            )
        }
    }


    private suspend fun getMostActiveUsers(
        token: String,
        query: String,
        mostActiveUsersQueryName: String,
        context: Context,
        callback: (List<User>, String?, MyPermission) -> Unit
    ) {
        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)

            Log.e(TAG,"getUsers")

            Log.e(TAG,"getMostActiveUsers: Token: $token")
            Log.e(TAG,"getMostActiveUsers: Query: $query")
            Log.e(TAG,"getMostActiveUsers: Result: ${result.body()}")
            Log.e(TAG,"getMostActiveUsers: MyMostActiveResponse : $jsonObject")
            when {
                result.isSuccessful -> {

                    val mostActiveUsersList = ArrayList<User>()

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else {
                        null
                    }

                    val dataJsonObject = jsonObject["data"].asJsonObject
                    var randomUsersJsonArray: JsonArray? = null
                    var myPermission = MyPermission(false)
                    dataJsonObject.isJsonNull

                    if (dataJsonObject.has(mostActiveUsersQueryName) && !dataJsonObject[mostActiveUsersQueryName].isJsonNull) {

                        val randomJsonObject = dataJsonObject[mostActiveUsersQueryName].asJsonObject

                        if (randomJsonObject.has("user") && randomJsonObject["user"].isJsonArray) {

                            randomUsersJsonArray = randomJsonObject["user"].asJsonArray
                        }

                        if (randomJsonObject.has("myPermission")) {
                            val permissionObject = randomJsonObject["myPermission"].asJsonObject

                            myPermission =
                                Gson().fromJson(permissionObject, MyPermission::class.java)
                        }
                    }

                    if (randomUsersJsonArray != null) {
                        randomUsersJsonArray.forEach { jsonElement ->
                            val json = Gson().fromJson(jsonElement, User::class.java)
                            mostActiveUsersList.add(json)
                        }
                    }

                    if (error.isNullOrEmpty()) callback.invoke(

                        mostActiveUsersList,

                        null, myPermission
                    )
                    else callback.invoke(emptyList(), error, myPermission)

                }

                else -> callback.invoke(
                    emptyList(), AppStringConstant1.something_went_wrong_please_try_again_later,
//                    context.getString(R.string.something_went_wrong_please_try_again_later),
                    MyPermission(false)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(
                emptyList(),

                AppStringConstant1.something_went_wrong_please_try_again_later,
//                context.getString(R.string.something_went_wrong_please_try_again_later),
                MyPermission(hasPermission = false)
            )
        }
    }

    private suspend fun getPopularUsers(
        token: String,
        query: String,
        popularUsersQueryName: String,
        context: Context,
        callback: (List<User>, String?, MyPermission) -> Unit
    ) {
        try {
            Log.e(TAG,"getPopularUsers: Query: $query")
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)

            Log.e(TAG,"getPopularUsers: getUsers")

            Log.e(TAG,"getPopularUsers: Token: $token")
            Log.e(TAG,"getPopularUsers: Query: $query")
            Log.e(TAG,"getPopularUsers: Result: ${result.body()}")

            when {
                result.isSuccessful -> {

                    val popularUsersList = ArrayList<User>()


                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else {
                        null
                    }


                    val dataJsonObject = jsonObject["data"].asJsonObject
                    var randomUsersJsonArray: JsonArray? = null
                    var myPermission = MyPermission(false)
                    dataJsonObject.isJsonNull

                    if (dataJsonObject.has(popularUsersQueryName) && !dataJsonObject[popularUsersQueryName].isJsonNull) {


                        val randomJsonObject = dataJsonObject[popularUsersQueryName].asJsonObject


                        randomUsersJsonArray = randomJsonObject["user"].asJsonArray


                        if (randomJsonObject.has("myPermission")) {
                            val permissionObject = randomJsonObject["myPermission"].asJsonObject

                            myPermission =
                                Gson().fromJson(permissionObject, MyPermission::class.java)
                        }
                    }


                    if (randomUsersJsonArray != null) {
                        randomUsersJsonArray.forEach { jsonElement ->
                            val json = Gson().fromJson(jsonElement, User::class.java)
                            popularUsersList.add(json)
                        }
                    }


//
//                    popularUsersJsonArray.forEach { jsonElement ->
//                        val json = Gson().fromJson(jsonElement, User::class.java)
//                        popularUsersList.add(json)
//                    }

//                    val mostActiveUsersList = randomUsersList.filter { it.isOnline }

                    if (error.isNullOrEmpty()) callback.invoke(

                        popularUsersList,

                        null, myPermission
                    )
                    else callback.invoke(emptyList(), error, myPermission)

                }

                else -> callback.invoke(
                    emptyList(), AppStringConstant1.something_went_wrong_please_try_again_later,
//                    context.getString(R.string.something_went_wrong_please_try_again_later),
                    MyPermission(false)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(
                emptyList(), AppStringConstant1.something_went_wrong_please_try_again_later,
//                context.getString(R.string.something_went_wrong_please_try_again_later),
                MyPermission(hasPermission = false)
            )
        }
    }


    private suspend fun getSearchUsers(
        token: String,
        query: String,
        randomUsersQueryName: String,
        popularUsersQueryName: String,
        autoDeductCoin: Int = 0,
        context: Context,
        callback: (List<User>, List<User>, List<User>, String?, MyPermission) -> Unit
    ) {
        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)

            Log.e(TAG,"getSearchUsers: Token $token")
            Log.e(TAG,"getSearchUsers: Result: ${result.body()}")
            Log.e(TAG,"getSearchUsers: Result: ${result.body()}")
            Log.e(TAG,"getSearchUsers: Query: $query")

            when {
                result.isSuccessful -> {
                    val randomUsersList = ArrayList<User>()
                    val popularUsersList = ArrayList<User>()

                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val randomUsersJsonArray = dataJsonObject["users"].asJsonArray
                    randomUsersJsonArray.forEach { jsonElement ->
                        val json = Gson().fromJson(jsonElement, User::class.java)
                        randomUsersList.add(json)
                    }

                    var myPermission = MyPermission(hasPermission = false)
                    if (dataJsonObject.has("myPermission")) {
                        val permissionObject = jsonObject["myPermission"].asJsonObject
                        //   val popularUsersJsonArray = dataJsonObject[popularUsersQueryName].asJsonArray


                        myPermission = Gson().fromJson(permissionObject, MyPermission::class.java)
                    }/* popularUsersJsonArray.forEach { jsonElement ->
                         val json = Gson().fromJson(jsonElement, User::class.java)
                         popularUsersList.add(json)
                     }*/

                    //  val mostActiveUsersList = randomUsersList.filter { it.isOnline }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else {
                        null
                    }

                    if (error.isNullOrEmpty()) callback.invoke(
                        randomUsersList, ArrayList(), ArrayList(), null, myPermission
                    )
                    else callback.invoke(emptyList(), emptyList(), emptyList(), error, myPermission)

                }

                else -> callback.invoke(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    AppStringConstant1.something_went_wrong_please_try_again_later,
//                    context.getString(R.string.something_went_wrong_please_try_again_later),
                    MyPermission(false)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(
                emptyList(),
                emptyList(),
                emptyList(),
                AppStringConstant1.something_went_wrong_please_try_again_later,
//                context.getString(R.string.something_went_wrong_please_try_again_later),
                MyPermission(false)
            )
        }
    }

}