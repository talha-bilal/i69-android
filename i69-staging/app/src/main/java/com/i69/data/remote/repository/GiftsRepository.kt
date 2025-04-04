package com.i69.data.remote.repository

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.i69.data.models.Id
import com.i69.data.models.ModelGifts
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.responses.ResponseBody
import com.i69.singleton.App
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.utils.Resource
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.getResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Keep
@Singleton
class GiftsRepository @Inject constructor(
    private val api: GraphqlApi
) {
    private var _gifts: ArrayList<ModelGifts.Data.AllRealGift> = ArrayList()
    private val gifts: MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> = MutableLiveData()

    private var _realGifts: ArrayList<ModelGifts.Data.AllRealGift> = ArrayList()
    private val realGifts: MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> =
        MutableLiveData()

    private var _virtualGifts: ArrayList<ModelGifts.Data.AllRealGift> = ArrayList()
    private val virtualGifts: MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> =
        MutableLiveData()
    private var TAG: String = GiftsRepository::class.java.simpleName

    fun getRealGifts(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> {
        _realGifts.clear()
        viewModelScope.launch(Dispatchers.IO) {
            loadRealGifts(token, "allRealGift")
        }
        realGifts.value = _realGifts
        return realGifts
    }

    fun getVirtualGifts(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> {
        _virtualGifts.clear()
        viewModelScope.launch(Dispatchers.IO) {
            loadRealGifts(token, "allVirtualGift")
        }
        virtualGifts.value = _virtualGifts
        return virtualGifts
    }

    fun getAllGifts(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> {
        _gifts.clear()
        viewModelScope.launch(Dispatchers.IO) { loadAllGifts(token) }
        gifts.value = _gifts
        return gifts
    }

    private suspend fun loadRealGifts(token: String, queryName: String) {
        val queryName = "allRealGift"
        val query = StringBuilder().append("query {").append("$queryName { ")
            .append("giftName,cost,id,type,picture").append("}").append("}").toString()
        try {
            val body = query.getGraphqlApiBody()
            Log.e(TAG,"loadRealGifts: GiftsList: $token -- $body")
            val result = api.callApi(token = "Token $token", body = body)
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG,"loadRealGifts: GiftsList : $jsonObject")

            when {
                result.isSuccessful -> {
                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null

                    if (error.isNullOrEmpty()) {
                        val dataJsonObject = jsonObject["data"].asJsonObject
                        val dataArray = dataJsonObject[queryName].asJsonArray
                        _realGifts.clear()
                        _virtualGifts.clear()
                        dataArray.forEach { jsonElement ->
                            val json = Gson().fromJson(
                                jsonElement,
                                ModelGifts.Data.AllRealGift::class.java
                            )
                            when (queryName) {
                                "allRealGift" -> _realGifts.add(json)
                                "allVirtualGift" -> _virtualGifts.add(json)
                            }
                        }
                        when (queryName) {
                            "allRealGift" -> realGifts.postValue(_realGifts)
                            "allVirtualGift" -> virtualGifts.postValue(_virtualGifts)
                        }
                    } else {
                        App.userPreferences.clear()
                        //App.userPreferences.saveUserIdToken("","","")
                        val intent = Intent(MainActivity.mContext, SplashActivity::class.java)
                        MainActivity.mContext!!.startActivity(intent)
                        (MainActivity.mContext as Activity).finishAffinity()
                        Log.e(TAG,"loadRealGifts: err : $error")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private suspend fun loadAllGifts(token: String) {
        val query = StringBuilder().append("query {").append("allRealGift { ")
            .append("giftName,cost,id,type,picture").append("}").append("allVirtualGift { ")
            .append("giftName,cost,id,type,picture").append("}").append("}").toString()
        try {
            val body = query.getGraphqlApiBody()
            Log.e(TAG, "loadAllGifts: GiftsList : $token -- $body")
            val result = api.callApi(token = "Token $token", body = body)
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG,"loadAllGifts: GiftsList : ${jsonObject}")

            when {
                result.isSuccessful -> {
                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val realGiftArray = dataJsonObject["allRealGift"].asJsonArray
                    val virtualGiftArray = dataJsonObject["allVirtualGift"].asJsonArray

                    realGiftArray.forEach { jsonElement ->
                        val json =
                            Gson().fromJson(jsonElement, ModelGifts.Data.AllRealGift::class.java)
                        _gifts.add(json)
                    }
                    virtualGiftArray.forEach { jsonElement ->
                        val json =
                            Gson().fromJson(jsonElement, ModelGifts.Data.AllRealGift::class.java)
                        _gifts.add(json)
                    }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null


                    if (error.isNullOrEmpty()) {
                        gifts.postValue(_gifts)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun purchaseGift(
        token: String?,
        userId: String?,
        giftId: Int?
    ): Resource<ResponseBody<Id>> {
        val queryName = "giftpurchase"
        val query = StringBuilder().append("mutation $queryName{").append("$queryName (")
            .append("giftId: $giftId ").append(") {").append(
                "giftPurchase{" + "id," + "user{" + "id," + "username," + "email," + "coins" + "}," + "gift" + "{" + "giftName," + "cost" + "}" + "}"
            ).append("}").append("}").toString()

        Log.e(TAG,"purchaseGift: GiftsList ${query} $userId")

        return api.getResponse(query, queryName, token)
    }
}