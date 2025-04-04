package com.i69.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.i69.R
import com.i69.data.config.Constants
import com.i69.data.enums.HttpStatusCode
import com.i69.data.models.market.FreightEstimateResponse
import com.i69.data.models.market.GetAllCategoriesResponse
import com.i69.data.models.market.GetChildrenCategoriesResponse
import com.i69.data.models.market.Products
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.api.MarketGraphqlApi
import com.i69.data.remote.requests.ReportRequest
import com.i69.data.remote.requests.SearchRequest
import com.i69.data.remote.requests.SearchRequestNew
import com.i69.data.remote.responses.ResponseBody
import com.i69.db.DBResource
import com.i69.singleton.App
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.viewModels.UserViewModel
import org.json.JSONObject


fun String.getGraphqlApiBody(): String {
    val paramObject = JSONObject()
    paramObject.put("query", this)
    return paramObject.toString()
}

fun getUserDetailsQueryResponse(): String =
    StringBuilder().append("id, username, fullName, email,userLanguageCode, subscription,")
        .append("photosQuota, ${getPhotosQueryResponse()}, ")
        .append("country, city, state, countryCode, countryFlag, purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex, canScheduleMoment, canScheduleStory,")
        .append("hasStoryQuota, hasMomentQuota, canPostMoment, canPostStory, location, familyPlans, religion, politics, isConnected, planname, ")
        .append("interestedIn, tags, sportsTeams, books, ")
        .append("followersCount, followingCount, userVisitorsCount, userVisitingCount, ")
        .append("privatePhotoRequestStatus, ")
        .append("education, zodiacSign, movies, music, ")
        .append("ethinicity, tvShows, work, ${getAvtarQueryResponse()}, ")
        .append("likes { ${getBlockedUserQueryResponse()} }, ")
        .append("userAttrTranslation { id, name}, ")
        .append("blockedUsers { ${getBlockedUserQueryResponse()} }")
        .append("userSubscription { ${getUserSubscriptionQueryResponse()} }")
        .toString()


fun getLanguageCode(): String =
    StringBuilder().append("username, userLanguageCode").toString()

fun getPhotosQueryResponse(): String = "avatarPhotos { id, url, type }"

fun getUserSubscriptionQueryResponse(): String = "isActive"

fun getBlockedUserQueryResponse(): String = "id, username, fullName, ${getPhotosQueryResponse()}"

fun getAvtarQueryResponse(): String = "avatar { url }"


//// Search
fun getSearchQueryResponse(
    randomUsersQueryName: String,
    popularUsersQueryName: String,
    searchRequest: SearchRequest,
    hasLocation: Boolean
): String =
    StringBuilder()
        .append("query {")
        .append(
            getSearchOptionQueryResponse(
                queryName = randomUsersQueryName,
                searchRequest = searchRequest,
                limit = Constants.SEARCH_RANDOM_LIMIT,
                hasLocation = hasLocation
            )
        )

        .append(
            getSearchOptionQueryResponse(
                queryName = popularUsersQueryName,
                searchRequest = searchRequest,
                limit = Constants.SEARCH_POPULAR_LIMIT,
                hasLocation = hasLocation
            )
        )
        .append("}")
        .toString()


// {"query":"mutation {updateProfile (id: \"5f6179d2-80a6-436e-b81b-a915607966c7\", location: [23.8374933, 90.367195] ) {id}}"}
//// newcode
fun getSearchQueryResponseNew(
    randomUsersQueryName: String,
    autoDeductCoin: Int = 0,
    searchRequest: SearchRequest,
    searchKey: String = "",
    hasLocation: Boolean
): String =
    StringBuilder()
        .append("query {")
        .append(
            getSearchOptionQueryResponse(
                autoDeductCoin,
                randomUsersQueryName,
                searchRequest,
                Constants.SEARCH_RANDOM_LIMIT,
                hasLocation
            )
        )
        .append("}")
        .toString()


fun getSearchQueryResponseAll(
    randomUsersQueryName: String,
    popularUsersQueryName: String,
    mostActiveUsersQueryName: String,
    autoDeductCoin: Int = 0,
    searchRequest: SearchRequest,
    searchKey: String = "",
    hasLocation: Boolean
): String =
    StringBuilder()
        .append("query {")
        .append(
            getSearchOptionQueryResponse(
                autoDeductCoin,
                randomUsersQueryName,
                searchRequest,
                Constants.SEARCH_RANDOM_LIMIT,
                hasLocation
            )
        )
        .append(",")
        .append(
            getSearchOptionQueryResponse(
                autoDeductCoin,
                popularUsersQueryName,
                searchRequest,
                Constants.SEARCH_RANDOM_LIMIT,
                hasLocation
            )
        ).append(",")
        .append(
            getSearchOptionQueryResponse(
                autoDeductCoin,
                mostActiveUsersQueryName,
                searchRequest,
                Constants.SEARCH_RANDOM_LIMIT,
                hasLocation
            )
        )
        .append("}")
        .toString()


//StringBuilder()
//.append("query {")
//.append("randomUsers(")
//.append("searchKey:")
//.append("\" \",")
////.append("\"admin\",")
//.append("autoDeductCoin : $autoDeductCoin")
//.append(")")
//.append("{")
//.append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
//.append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
//.append("}")
//.toString()


fun getSearchQueryResponseNew(
    autoDeductCoin: Int = 0,
    searchKey: String = "",
): String =
    StringBuilder()
        .append("query {")
        .append("randomUsers(")
        .append("searchKey:")
        .append("\" \",")
//               .append("\"admin\",")
        .append("autoDeductCoin : $autoDeductCoin")
        .append(")")
        .append("{")
        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
        .append("}")
        .toString()


fun getSearchMostActiveUsersQueryResponse(
    autoDeductCoin: Int = 0, interestedIn: Int
): String =
//    StringBuilder()
//        .append("query {")
//        .append("mostActiveUsers(")
//        .append("interestedIn : $interestedIn")
//        .append(",")
//        .append("autoDeductCoin : $autoDeductCoin")
//        .append(")")
//        .append("{")
//        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
//        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
//        .append("}")
//        .toString()

//    StringBuilder()
//        .append("query {")
//        .append(
//            getSearchOptionQueryResponse(
//                autoDeductCoin,
//                randomUsersQueryName,
//                searchRequest,
//                Constants.SEARCH_RANDOM_LIMIT,
//                hasLocation
//            )
//        )
//        .append("}")
//        .toString()

    StringBuilder()
        .append("query {")
        .append("mostActiveUsers(")
        .append("searchKey:")
        .append("\" \",")
//               .append("\"admin\",")
//        .append("interestedIn : $interestedIn")
//        .append(",")
        .append("autoDeductCoin : $autoDeductCoin")
        .append(")")
        .append("{")
        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
        .append("}")
        .toString()

fun getSearchPopularUsersQueryResponse(
    autoDeductCoin: Int = 0, interestedIn: Int
): String =

//    StringBuilder()
//        .append("query {")
//        .append("popularUsers(")
////        .append("searchKey:")
////        .append("\" \",")
////               .append("\"admin\",")
//        .append("interestedIn : $interestedIn")
//        .append(",")
//        .append("autoDeductCoin : $autoDeductCoin")
//        .append(")")
//        .append("{")
//        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
//        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
//        .append("}")
//        .toString()

//    StringBuilder()
//        .append("query {")
//        .append(
//            getSearchOptionQueryResponse(
//                autoDeductCoin,
//                randomUsersQueryName,
//                searchRequest,
//                Constants.SEARCH_RANDOM_LIMIT,
//                hasLocation
//            )
//        )
//        .append("}")
//        .toString()

    StringBuilder()
        .append("query {")
        .append("popularUsers(")
        .append("searchKey:")
        .append("\" \",")
//               .append("\"admin\",")
//        .append("interestedIn : $interestedIn")
//        .append(",")
        .append("autoDeductCoin : $autoDeductCoin")
        .append(")")
        .append("{")
        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")
        .append("}")
        .toString()

//// Search New
fun getSearchNewQueryResponse(
    randomUsersQueryName: String,
    popularUsersQueryName: String,
    searchRequest: SearchRequestNew, autoDeductCoin: Int = 0
): String =
    StringBuilder()
        .append("query {")
        //  .append(getSearchNewOptionQueryResponse(randomUsersQueryName, searchRequest, Constants.SEARCH_RANDOM_LIMIT, true))
        .append(
            getSearchNewOptionQueryResponse(
                popularUsersQueryName,
                searchRequest,
                Constants.SEARCH_POPULAR_LIMIT,
                true
            )
        )
        .append("}")
        .toString()

fun getSearchOptionQueryResponse(
    autoDeductCoin: Int = 0,
    queryName: String,
    searchRequest: SearchRequest,
    limit: Int,
    hasLocation: Boolean
) =
    StringBuilder()
        .append("$queryName (")
        .append("interestedIn: ${searchRequest.interestedIn}, ")
        .append("autoDeductCoin : ${autoDeductCoin},")
//        .append("limit: ${limit}, ")
//        .append("id: \"${searchRequest.id}\", ")
        .append(if (searchRequest.minHeight == null) "" else "minHeight: ${searchRequest.minHeight}, ")
        .append(if (searchRequest.maxHeight == null) "" else "maxHeight: ${searchRequest.maxHeight}, ")
        .append(if (searchRequest.minAge == null) "" else "minAge: ${searchRequest.minAge}, ")
        .append(if (searchRequest.maxAge == null) "" else "maxAge: ${searchRequest.maxAge}, ")
        .append(if (!hasLocation) "" else if (searchRequest.maxDistance == 0) "" else "latitude: ${searchRequest.lat}, ")
        .append(if (!hasLocation) "" else if (searchRequest.maxDistance == 0) "" else "longitude: ${searchRequest.long}, ")
        .append(if (!hasLocation) "" else if (searchRequest.maxDistance == 0) "" else "maxDistance: ${searchRequest.maxDistance}, ")
        .append(if (searchRequest.gender == null) "" else "gender: ${searchRequest.gender}, ")
        .append(if (searchRequest.familyPlans == null) "" else "familyPlan: ${searchRequest.familyPlans}, ")
        .append(if (searchRequest.politics == null) "" else "politics: ${searchRequest.politics}, ")
        .append(if (searchRequest.religious == null) "" else "religious: ${searchRequest.religious}, ")
        .append(if (searchRequest.zodiacSign == null) "" else "zodiacSign: ${searchRequest.zodiacSign}, ")
        .append(if (searchRequest.searchKey.isNullOrEmpty()) "" else "searchKey: \"${searchRequest.searchKey}\" ")
        .append(") {")
        .append("user { id, username, fullName, email,userLanguageCode,purchaseCoins, giftCoins,isOnline, gender, age, height, about, avatarIndex,location, familyPlans, religion, politics, photosQuota, ${getPhotosQueryResponse()} }")
        .append("myPermission { hasPermission, coinsToUnlock, freeUserLimit }}")

//        .append(getUserDetailsQueryResponse())
//        .append("}, ")
        .toString()

fun getSearchNewOptionQueryResponse(
    queryName: String,
    searchRequest: SearchRequestNew,
    limit: Int,
    hasLocation: Boolean
): String =
    StringBuilder()
        .append("users(")
        .append(if (searchRequest.name.isNullOrEmpty()) "" else "name: \"${searchRequest.name}\" ")
        .append(") {")
        .append(getUserDetailsQueryResponse())
        .append("}, ")
        .toString()


fun List<String>?.getStringFromList(): String {
    var stringValue = ""
    this?.forEachIndexed { index, string ->
        if (index == 0) stringValue = "["
        stringValue += "\"${string}\","
        if (index == this.size - 1) stringValue =
            stringValue.substring(0, stringValue.length - 1) + "]"
    }
    return stringValue
}


suspend inline fun <reified T> MarketGraphqlApi.getResponse(
    query: String?,
    queryName: String?,
    token: String? = ""
): Resource<ResponseBody<T>> {

    return try {
        Log.e("dddd123", "Query: ${query?.getGraphqlApiBody()}")

        val result = this.callApi(token = "Token $token", body = query?.getGraphqlApiBody())
        val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
        Log.e("ApiUtils","Token: $token")
        Log.e("ApiUtils","Query: $query")
        Log.e("ApiUtils","Response: ${result.body().toString()}")
        Log.e("dddd123", "Response: ${result.code()}")
        Log.e("dddd123", "Response: ${result.body().toString()}")
        when {
            result.isSuccessful -> {
                Log.e("dddd", "Response: is successful")
                val error: String? = if (jsonObject.has("errors")) {
                    jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                } else {
                    null
                }
                Log.e("dddd", "Response: 222  444444")
                var json: T? = null
                if (jsonObject.has("data")) {
                    Log.e("dddd", "Response: 222  ${jsonObject["data"]}")
                    var data = jsonObject["data"]
//                    if(data.asJsonObject.has("sendMessage") && data.asJsonObject["sendMessage"] == null){
                    if (!error.isNullOrEmpty() && data.asJsonObject.has("sendMessage") || !error.isNullOrEmpty()) {
                        Log.e("exceptionErrorRespnse", error!!)
                    } else {
//                        json = if (jsonObject["data"] is JsonObject) {
//                            Log.e("dddd", "Response: 222  JsonObject")
//                            Gson().fromJson(
//                                jsonObject["data"].asJsonObject[queryName],
//                                T::class.java
//                            )
//                        } else {
//                            Log.e("dddd", "Response: 222  JsonArray")
//                            Gson().fromJson(
//                                jsonObject["data"].asJsonArray,
//                                T::class.java
//                            )
//                        }
//                        if (jsonObject["data"].asJsonObject[queryName].isJsonObject) {
//                            // Parse as JsonObject
//                            Log.e("dddd", "Response: 222  JsonObject")
//                            json = Gson().fromJson(
//                                jsonObject["data"].asJsonObject[queryName].asJsonObject,
//                                T::class.java
//                            )
//                        } else if (jsonObject["data"].asJsonObject[queryName].isJsonArray) {
//                            // Parse as JsonArray
//                            Log.e("dddd", "Response: 222  JsonArray")
//                            json = Gson().fromJson(
//                                jsonObject["data"].asJsonObject[queryName].asJsonArray,
//                                T::class.java
//                            )
//                        }

                        data = jsonObject["data"].asJsonObject
                        val queryResult = data[queryName]

                        json = when {
                            queryResult.isJsonArray -> {
                                if (queryName.equals("feedItems")) {
                                    val listType =
                                        object : TypeToken<ArrayList<Products>>() {}.type
                                    Gson().fromJson(queryResult.asJsonArray, listType)
                                } else if (queryName.equals("getAllCategories")) {
                                    val listType = object :
                                        TypeToken<ArrayList<GetAllCategoriesResponse>>() {}.type
                                    Gson().fromJson(queryResult.asJsonArray, listType)
                                } else if (queryName.equals("getChildrenCategories")) {
                                    val listType = object :
                                        TypeToken<ArrayList<GetChildrenCategoriesResponse>>() {}.type
                                    Gson().fromJson(queryResult.asJsonArray, listType)
                                } else if (queryName.equals("imageSearch")) {
                                    val listType = object : TypeToken<ArrayList<Products>>() {}.type
                                    Gson().fromJson(queryResult.asJsonArray, listType)
                                } else if (queryName.equals("freightEstimate")) {
                                    val listType = object :
                                        TypeToken<ArrayList<FreightEstimateResponse>>() {}.type
                                    Gson().fromJson(queryResult.asJsonArray, listType)
                                } else null
                            }

                            queryResult.isJsonObject -> Gson().fromJson(
                                queryResult.asJsonObject,
                                T::class.java
                            )

                            else -> null
                        }
                    }
                }
                Log.e("dddd", "Response: 222  7777")
                val response = ResponseBody(data = json, errorMessage = error)
                Log.e("ApiUtils","Response: $response")
                Log.e("dddd", "Response: 222  55555")
                if (response.errorMessage.isNullOrEmpty() && response.data != null) {
                    Log.e("dddd", "Response: 333")
                    Resource.Success(HttpStatusCode.OK, response)
                } else {
                    Log.e("dddd", "Response: 444")
                    Log.e("dddd", response.errorMessage!!.toString())
                    if (response.errorMessage.contains("User does't exist")) {
                        App.userPreferences.clear()
                        //App.userPreferences.saveUserIdToken("","","")
                        val intent = Intent(MainActivity.mContext, SplashActivity::class.java)
                        MainActivity.mContext!!.startActivity(intent)
                        (MainActivity.mContext as Activity).finishAffinity()
                    }
                    Toast.makeText(
                        MainActivity.mContext,
                        "User doesn't exist" + response.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    copyToClipboard(
                        MainActivity.mContext!!,
                        "User doesn't exist" + response.errorMessage,
                        " User doesn't exist "
                    )
                    Resource.Error(
                        code = HttpStatusCode.BAD_REQUEST,
                        message = response.errorMessage
                    )
                }
            }

            else -> {
                Log.e("VisitorMutation", "else: ${result}")
                Toast.makeText(
                    MainActivity.mContext,
                    "else--${result.code()}: ${result.message()}",
                    Toast.LENGTH_SHORT
                ).show()
                copyToClipboard(
                    MainActivity.mContext!!,
                    "else--${result.code()}: ${result.message()}",
                    " Else Error "
                )
                Resource.Error(
                    code = HttpStatusCode.INTERNAL_SERVER_ERROR,
                    message = App.getAppContext().getString(R.string.something_went_wrong)
                )
            }
        }
    } catch (e: Exception) {
        Toast.makeText(MainActivity.mContext, "Exception--${e.message}", Toast.LENGTH_SHORT).show()
        copyToClipboard(MainActivity.mContext!!, "Exception--${e.message}", " Exception Error ")
//        Toast.makeText(MainActivity.mContext, App.getAppContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        e.printStackTrace()
        Resource.Error(
            code = HttpStatusCode.INTERNAL_SERVER_ERROR,
            message = "${
                App.getAppContext().getString(R.string.something_went_wrong)
            } ${e.localizedMessage}"
        )
    }
}

fun copyToClipboard(context: Context, text: String, label: String = "copiedText") {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clipData)
}


suspend inline fun <reified T> GraphqlApi.getResponse(
    query: String?,
    queryName: String?,
    token: String? = ""
): Resource<ResponseBody<T>> {

    return try {
        Log.e("dddd123", "Query: ${query?.getGraphqlApiBody()}")

        val result = this.callApi(token = "Token $token", body = query?.getGraphqlApiBody())
        val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
        Log.e("ApiUtils","Token: $token")
        Log.e("ApiUtils","Query: $query")
        Log.e("ApiUtils","Response: ${result.body().toString()}")
        Log.e("dddd123", "Response: ${result.code()}")
        when {
            result.isSuccessful -> {
                Log.e("dddd", "Response: is successful")
                val error: String? = if (jsonObject.has("errors")) {
                    jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                } else {
                    null
                }
                Log.e("dddd", "Response: 222  444444")
                var json: T? = null
                if (jsonObject.has("data")) {
                    Log.e("dddd", "Response: 222  ${jsonObject["data"]}")
                    var data = jsonObject["data"]
//                    if(data.asJsonObject.has("sendMessage") && data.asJsonObject["sendMessage"] == null){
                    if (!error.isNullOrEmpty() && data.asJsonObject.has("sendMessage") || !error.isNullOrEmpty()) {
                        Log.e("exceptionErrorRespnse", error!!)
                    } else {
                        json = if (jsonObject["data"] is JsonObject) {
                            Log.e("dddd", "Response: 222  JsonObject")
                            Gson().fromJson(
                                jsonObject["data"].asJsonObject[queryName],
                                T::class.java
                            )
                        } else {
                            Log.e("dddd", "Response: 222  JsonArray")
                            Gson().fromJson(
                                jsonObject["data"].asJsonArray,
                                T::class.java
                            )
                        }
                    }
                }
                Log.e("dddd", "Response: 222  7777")
                var response = ResponseBody(data = json, errorMessage = error)
                if (json == null && error.isNullOrEmpty()) {
                    val data: T = Gson().fromJson(jsonObject, T::class.java)
                    response = ResponseBody(data = data, errorMessage = null)
                }
                Log.e("ApiUtils","Response: $response")
                Log.e("dddd", "Response: 222  55555")
                if (response.errorMessage.isNullOrEmpty() && response.data != null) {
                    Log.e("dddd", "Response: 333")
                    Resource.Success(HttpStatusCode.OK, response)
                } else {
                    Log.e("dddd", "Response: 444")
                    Log.e("dddd", response.errorMessage!!.toString())
                    if (response.errorMessage!!.contains("User does't exist")) {
                        App.userPreferences.clear()
                        //App.userPreferences.saveUserIdToken("","","")
                        val intent = Intent(MainActivity.mContext, SplashActivity::class.java)
                        MainActivity.mContext!!.startActivity(intent)
                        (MainActivity.mContext as Activity).finishAffinity()
                    }
                    Resource.Error(
                        code = HttpStatusCode.BAD_REQUEST,
                        message = response.errorMessage!!
                    )
                }
            }

            else -> {
                Log.e("ApiUtils", "VisitorMutation" + "else: ${result}")
                Resource.Error(
                    code = HttpStatusCode.INTERNAL_SERVER_ERROR,
                    message = App.getAppContext().getString(R.string.something_went_wrong)
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error(
            code = HttpStatusCode.INTERNAL_SERVER_ERROR,
            message = "${
                App.getAppContext().getString(R.string.something_went_wrong)
            } ${e.localizedMessage}"
        )
    }
}

suspend inline fun <reified T> GraphqlApi.getData(
    query: String?,
    queryName: String?,
    token: String?
): DBResource<T> {
    return try {
        val result = this.callApi(token = "Token $token", body = query?.getGraphqlApiBody())
        val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
        Log.e("ApiUtils","Query: $query")
        Log.e("ApiUtils","Response: ${result.body().toString()}")

        Log.e("ResponseProfile: ", "${result.body().toString()}")
        Log.e("TAG", "ResponseProfile: ${result.body().toString()}")


        when {
            result.isSuccessful -> {
                val json =
                    Gson().fromJson(jsonObject["data"].asJsonObject[queryName], T::class.java)
                val error: String? = if (jsonObject.has("errors")) {
                    jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                } else {
                    null
                }

                if (error.isNullOrEmpty() && json != null) {
                    DBResource.success(json, result.code())
                } else {
                    DBResource.error(message = error, result.code())
                }
            }

            else -> DBResource.error(
                message = App.getAppContext().getString(R.string.something_went_wrong),
                HttpStatusCode.INTERNAL_SERVER_ERROR.statusCode
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        DBResource.error(
            "${
                App.getAppContext().getString(R.string.something_went_wrong)
            } ${e.localizedMessage}", HttpStatusCode.INTERNAL_SERVER_ERROR.statusCode
        )
    }
}

suspend fun reportUserAccount(
    token: String?,
    currentUserId: String?,
    otherUserId: String?,
    reasonMsg: String?,
    mViewModel: UserViewModel,
    callback: (String) -> Unit
) {
    val reportRequest = ReportRequest(
        reportee = otherUserId,
        reporter = currentUserId,
        timestamp = reasonMsg
    )
    when (val response = mViewModel.reportUser(reportRequest, token = token)) {
        is Resource.Success -> callback(App.getAppContext().getString(R.string.report_accepted))
        is Resource.Error -> {
            Log.e("ApiUtils",
                "${
                    App.getAppContext().getString(R.string.something_went_wrong)
                } ${response.message}"
            )
            callback(
                "${
                    App.getAppContext().getString(R.string.something_went_wrong)
                } ${response.message}"
            )
        }

        else -> {}
    }
}
