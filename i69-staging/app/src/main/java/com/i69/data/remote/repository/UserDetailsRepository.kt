package com.i69.data.remote.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.api.DefaultUpload
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.i69.data.models.Id
import com.i69.data.models.ProfileVisit
import com.i69.data.models.SelectedLanguageModel
import com.i69.data.models.StoryScheduledPublish
import com.i69.data.models.User
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.requests.ReportRequest
import com.i69.data.remote.responses.CoinPrice
import com.i69.data.remote.responses.GetCoinPrice
import com.i69.data.remote.responses.ResponseBody
import com.i69.db.DBResource
import com.i69.R
import com.i69.data.models.DeleteChatRoom
import com.i69.profile.db.dao.UserDao
import com.i69.singleton.App
import com.i69.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailsRepository @Inject constructor(
    private val api: GraphqlApi,
    private val userUpdateRepository: UserUpdateRepository,
    private val userDao: UserDao,
    private val context: Context
) {
    private var TAG: String = UserDetailsRepository::class.java.simpleName

    private var _currentUser: User? = null
    private var currentUser: MutableLiveData<User> = MutableLiveData()

    private var _coinPrice: ArrayList<CoinPrice> = ArrayList()
    private val coinPrice: MutableLiveData<ArrayList<CoinPrice>> = MutableLiveData()

    suspend fun deleteChatRoom(roomId: Int, token: String): Resource<ResponseBody<DeleteChatRoom>> {
        Log.e(TAG, "deleteChatRoom: RoomId: $roomId")

        val queryName = "deleteRoom"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName (")
            .append("chatId: $roomId")
            .append(") {")
            .append("message")
            .append("}")
            .append("}")
            .toString()

        Log.e(TAG, "deleteChatRoom: Query : $query")
        val response: Resource<ResponseBody<DeleteChatRoom>> =
            api.getResponse(query, queryName, token)
        Log.e(TAG, "deleteChatRoom: Response : ${response.message} ${response.data?.data}")
        return response
    }

    suspend fun addUserProfileVisit(
        userId: String,
        visitorId: String,
        token: String
    ): Resource<ResponseBody<ProfileVisit>> {
        Log.e(TAG, "addUserProfileVisit: UserId: $userId  VisitorId: $visitorId")

        val queryName = "profileVisit"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName (")
            .append("visiting: \"$visitorId\" ")
//            .append(if (visitorId.isEmpty()) "" else "visitor_uuid: \"$visitorId\"")
            .append(") {")
            .append("isVisited,")
            .append("isNotificationSent")
            .append("}")
            .append("}")
            .toString()

        Log.e(TAG, "addUserProfileVisit: Query:  $query")
        val response: Resource<ResponseBody<ProfileVisit>> =
            api.getResponse(query, queryName, token)
        Log.e(TAG, "addUserProfileVisit: Response: ${response.message} ${response.data?.data}")

        return response
    }

    suspend fun scheduleStory(
        file: DefaultUpload,
        publishAt: String,
        token: String
    ): Resource<ResponseBody<StoryScheduledPublish>> {
        Log.e(TAG, "scheduleStory: File: $file PublishedAt: $publishAt")

        val queryName = "insertStory"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName (")
            .append("file: $file")
            .append(") story {")
            .append("isPublished,")
            .append("publishAt")
            .append("}")
            .append("}")
            .toString()

        Log.e(TAG, "scheduleStory: Query: $query")
        val response: Resource<ResponseBody<StoryScheduledPublish>> =
            api.getResponse(query, queryName, token)
        Log.e(TAG, "scheduleStory: Response: ${response.message} ${response.data?.data}")
        return response
    }

    fun getCurrentUser(
        viewModelScope: CoroutineScope,
        userId: String,
        token: String,
        reload: Boolean
    ): MutableLiveData<User> {
        if (_currentUser == null || reload) viewModelScope.launch {
            getUserDetails(userId, token = token)?.let {
                _currentUser = it
                currentUser.postValue(_currentUser!!)
            }
        }
        //  currentUser.postValue(_currentUser!!)
        return currentUser
    }

    fun getCurrentUser(
        viewModelScope: CoroutineScope,
        userId: String,
        token: String,
        reload: Boolean,
        currentUserLiveData: MutableLiveData<User>
    ): MutableLiveData<User> {
        if (_currentUser == null || reload) viewModelScope.launch {
            getUserDetails(userId, token = token)?.let {
                _currentUser = it
                currentUser.postValue(_currentUser!!)
                currentUserLiveData.postValue(_currentUser)
            }
        }
        // currentUser.postValue(_currentUser!!)
        return currentUser
    }

    /*fun getCoinPrices(
        viewModelScope: CoroutineScope,
        token: String,
        coinsLiveData: MutableLiveData<List<CoinPrice>>
    ) {
        viewModelScope.launch {
            LogUtil.debug("Here 10")
            getCoinPricess(token).let {
                LogUtil.debug("Here 11 ${it.data?.data?.getCoinPrices?.size}")
//                _coinPrices = it
//                coinsLiveData.postValue(_coinPrices)
            }
        }
    }*/

    fun createPaypalOrder(
        viewModelScope: CoroutineScope,
        amount: Float,
        currency: String,
        token: String?
    ) {
        //if (_currentUser == null) {
        viewModelScope.launch {
            paypalCreateOrder(amount, currency, token)
        }
        //}
    }

    /*fun getCoinPrices(
        viewModelScope: CoroutineScope,
        token: String?
    ): Resource<ResponseBody<JsonObject>> {
        viewModelScope.launch {
            val queryName = "getCoinPrices"
            val query = StringBuilder()
                .append("query {")
                .append("$queryName { ")
                .append("coinsCount ")
                .append("originalPrice ")
                .append("discountedPrice ")
                .append("currency ")
                .append("}")
                .append("}")
                .toString()

//        val response: Resource<ResponseBody<JsonObject>> = api.getResponse(query, queryName, token)
            return@launch api.getResponse(query, queryName, token)
        }
    }*/

    private suspend fun loadCurrentUser(
        userId: String,
        token: String,
        currentUserLiveData: MutableLiveData<User>
    ) {
        getUserDetails(userId, token = token)?.let {
            _currentUser = it
            currentUser.postValue(_currentUser!!)
        }
    }

    // User
    suspend fun getUserDetails(userId: String?, token: String?): User? {
        val queryName = "user"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName (")
            .append("id: \"${userId}\" ")
            .append(") {")
            .append(getUserDetailsQueryResponse())
            .append("}")
            .append("}")
            .toString()
        return api.getResponse<User>(query, queryName, token).data?.data
    }

//    fun getCoinPrice(
//        viewModelScope: CoroutineScope,
//        token: String
//    ): MutableLiveData<ArrayList<CoinPrice>> {
//        if (_coinPrice.isEmpty()) {
//            viewModelScope.launch(Dispatchers.IO) { loadCoinPrices(token) }
//        }
//        coinPrice.value = _coinPrice
//        return coinPrice
//    }


    fun getCoinPrice(
        viewModelScope: CoroutineScope,
        token: String, callback: (ArrayList<CoinPrice>) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            _coinPrice.clear()

            val queryName = "getCoinPrices"
            val query = StringBuilder()
                .append("query {")
                .append("$queryName { ")
                .append("coinsCount, ")
                .append("originalPrice, ")
                .append("discountedPrice, ")
                .append("currency ")
                .append("}")
                .append("}")
                .toString()

            try {
                val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
                val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
                Log.e(TAG, "getCoinPrice: Query: $query")
                Log.e(TAG, "getCoinPrice: ResultBody ${result.body()}")
                when {
                    result.isSuccessful -> {
                        val dataJsonObject = jsonObject["data"].asJsonObject
                        val coinSettingsJsonArray = dataJsonObject[queryName].asJsonArray

                        coinSettingsJsonArray.forEach { jsonElement ->
                            val json = Gson().fromJson(jsonElement, CoinPrice::class.java)
                            _coinPrice.add(json)
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

//        coinPrice.value = _coinPrice

//    }


    private suspend fun loadCoinPrices(token: String) {
        val queryName = "getCoinPrices"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("coinsCount, ")
            .append("originalPrice, ")
            .append("discountedPrice, ")
            .append("currency ")
            .append("}")
            .append("}")
            .toString()

        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG, "Query: $query")
            Log.e(TAG, "Result: ${result.body()}")

            when {
                result.isSuccessful -> {
                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val coinSettingsJsonArray = dataJsonObject[queryName].asJsonArray

                    coinSettingsJsonArray.forEach { jsonElement ->
                        val json = Gson().fromJson(jsonElement, CoinPrice::class.java)
                        _coinPrice.add(json)
                    }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null


                    if (error.isNullOrEmpty()) coinPrice.postValue(_coinPrice)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // User
    suspend fun getCoinPricess(token: String?): Resource<ResponseBody<GetCoinPrice>> {
        val queryName = "getCoinPrices"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("coinsCount, ")
            .append("originalPrice, ")
            .append("discountedPrice, ")
            .append("currency ")
            .append("}")
            .append("}")
            .toString()
        return api.getResponse(
            query,
            queryName,
            token
        )
        /*val response =
        LogUtil.debug("Response : :${response.code}")
        LogUtil.debug("Response : :${response.message}")
        LogUtil.debug("Response : :${response.data}")
        return response*/
    }

    suspend fun getUserProfile(userId: String?, token: String?): DBResource<User> {
        Log.e(TAG, "getUserProfile.....")
        val queryName = "user"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName (")
            .append("id: \"${userId}\" ")
            .append(") {")
            .append(getUserDetailsQueryResponse())
            .append("}")
            .append("}")
            .toString()
        Log.e(TAG, "Query: $query")
        return api.getData(query, queryName, token)
    }

    suspend fun getLanguage(userId: String?, token: String?): DBResource<SelectedLanguageModel> {
        val queryName = "user"
        val query = StringBuilder()
            .append("query {")
            .append("user (")
            .append("id: \"${userId}\" ")
            .append(") {")
            .append(getLanguageCode())
            .append("}")
            .append("}")
            .toString()
        return api.getData<SelectedLanguageModel>(query, queryName, token)
    }

    suspend fun reportUser(
        reportRequest: ReportRequest,
        token: String?
    ): Resource<ResponseBody<Id>> {
        val queryName = "reportUser"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName (")
            .append("reportee: \"${reportRequest.reportee}\", ")
            .append("reporter: \"${reportRequest.reporter}\", ")
            .append("timestamp: \"${reportRequest.timestamp}\"")
            .append(") {")
            .append("id")
            .append("}")
            .append("}")
            .toString()

        return api.getResponse(query, queryName, token)
    }

    suspend fun paypalCreateOrder(amount: Float, currency: String, token: String?) {
        val queryName = "paypalCreateOrder"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName (")
            .append("amount: $amount, ")
            .append("currency: \"$currency\"")
            .append(") {")
            .append("id")
            .append("}")
            .append("}")
            .toString()

        val response: Resource<ResponseBody<JsonObject>> = api.getResponse(query, queryName, token)
    }

    /*suspend fun getCoinPrices(token: String?): Resource<ResponseBody<List<CoinPrice>>> {
        val queryName = "getCoinPrices"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("coinsCount ")
            .append("originalPrice ")
            .append("discountedPrice ")
            .append("currency ")
            .append("}")
            .append("}")
            .toString()

//        val response: Resource<ResponseBody<JsonObject>> = api.getResponse(query, queryName, token)
        return api.getResponse(query, queryName, token)
    }*/


    /// Log Out
    fun logOut(
        viewModelScope: CoroutineScope,
        userId: String,
        token: String,
        callback: () -> Unit
    ) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(App.getAppContext().getString(R.string.server_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(App.getAppContext(), gso)

        viewModelScope.launch {

            userUpdateRepository.updateFirebasrToken(
                userId = userId,
                firebasetoken = "log_out",
                token = token
            )

            userUpdateRepository.updateOneSignalPlayerId(
                userId = userId,
                onesignalPlayerId = "log_out",
                token = token
            )
        }

        /*QBUsers.signOut().performAsync(object : QBEntityCallback<Void> {
            override fun onSuccess(p0: Void?, p1: Bundle?) {}

            override fun onError(p0: QBResponseException?) {}
        })*/
        _currentUser = null
        // currentUser.postValue(_currentUser)

        clearChatSystem()

        googleSignInClient.signOut()
            .addOnSuccessListener {
                Log.e(TAG, "Google Sign Out Success")
                logOutFromFacebook(callback)
            }
            .addOnFailureListener {
                Log.e(TAG, "Google Sign Out Failure")
                logOutFromFacebook(callback)
            }
    }

    private fun clearChatSystem() {
        /*ChatHelper.logOut()
        ChatHelper.destroy()
        QbDialogHolder.clear()*/
    }

    private fun logOutFromFacebook(callback: () -> Unit) {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            LoginManager.getInstance().logOut()
            callback()
            return
        }
        callback()
    }

    suspend fun audioChat(
        roomId: Int,
        requestName: String,
        moderatorId: String,
        callStrType: String,
        data: String, token: String
    ): Resource<ResponseBody<JsonObject>> {
        val queryName = "sendCall"
        val query = StringBuilder()
            .append("mutation {")
            .append("$queryName(")
            .append("roomId: $roomId, ")
            .append("requestName: \"$requestName\", ")
            .append("moderatorId: \"$moderatorId\", ")
            .append("callStrType: \"$callStrType\", ")
            .append("data: $data")
            .append(") {")
            .append("call {")
            .append("id ")
            .append("callType ")
            .append("timestamp ")
            .append("requestName ")
            .append("roomId {")
            .append("id ")
            .append("} ")
            .append("userId {")
            .append("id ")
            .append("username ")
            .append("}")
            .append("}")
            .append("}")
            .append("}")
            .toString()

        return api.getResponse(query, queryName, token)
    }
}