package com.i69.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.i69.data.models.CoinSettings
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.requests.PurchaseRequest
import com.i69.data.remote.responses.CoinsResponse
import com.i69.data.remote.responses.ResponseBody
import com.i69.utils.Resource
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.getResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val api: GraphqlApi
) {
    private var _coinSettings: ArrayList<CoinSettings> = ArrayList()
    private val coinSettings: MutableLiveData<ArrayList<CoinSettings>> = MutableLiveData()

    private var _coinSettingsM: CoinSettings? = null
    private val coinSettingsM: MutableLiveData<CoinSettings> = MutableLiveData()
    private var TAG: String = CoinRepository::class.java.simpleName

    suspend fun purchaseCoin(
        purchaseRequest: PurchaseRequest,
        token: String
    ): Resource<ResponseBody<CoinsResponse>> {
        val queryName = "purchaseCoin"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${purchaseRequest.id}\", ")
            .append("currency: \"${purchaseRequest.currency}\", ")
            .append("coins: ${purchaseRequest.coins}, ").append("money: ${purchaseRequest.money}, ")
            .append("paymentId: \"${purchaseRequest.payment_id}\", ")
            .append("paymentMethod: \"${purchaseRequest.paymentMethod}\", ")
            .append("method: \"${purchaseRequest.method}\"").append(") {")
            .append("id, coins, success ").append("}").append("}").toString()
        Log.e(TAG, "purchaseCoinMutation : $query")
        return api.getResponse(query, queryName, token)
    }

    suspend fun googleCreateOrder(
        purchaseRequest: PurchaseRequest,
        token: String
    ): Resource<ResponseBody<CoinsResponse>> {
        val queryName = "googleCreateOrder"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("orderId: \"${purchaseRequest.payment_id}\", ")
            .append("amount: ${purchaseRequest.money.toFloat()}, ")
            .append("status: \"${"success"}\" ")
//            .append("money: ${purchaseRequest.money}, ")
//            .append("paymentId: \"${purchaseRequest.payment_id}\", ")
//            .append("paymentMethod: \"${purchaseRequest.paymentMethod}\", ")

//            .append("method: \"${purchaseRequest.method}\"")
            .append(") {").append("statusCode, status ").append("}").append("}").toString()

        Log.e(TAG, "googleCreateOrder : $query")
        return api.getResponse(query, queryName, token)
    }

    suspend fun deductCoin(
        userId: String,
        token: String,
        deductCoinMethod: com.i69.data.enums.DeductCoinMethod
    ): Resource<ResponseBody<CoinsResponse>> {
        val queryName = "deductCoin"
        val query = StringBuilder().append("mutation {").append("$queryName (")
            .append("id: \"${userId}\", ").append("method: \"${deductCoinMethod.name}\"")
            .append(") {").append("id, coins, success ").append("}").append("}").toString()

        return api.getResponse(query, queryName, token)
    }

    fun getCoinSettings(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<ArrayList<CoinSettings>> {
        if (_coinSettings.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) { loadCoinSettings(token) }
        }
        //coinSettings.value = _coinSettings
        return coinSettings
    }

    fun getCoinSettingByRegion(
        viewModelScope: CoroutineScope,
        token: String,
        method: String
    ): MutableLiveData<CoinSettings> {
        if (_coinSettingsM == null) {
            viewModelScope.launch(Dispatchers.IO) {
                loadCoinSettingsByMethod(token, method)
                //coinSettingsM.value = _coinSettingsM!!
            }
        }

        return coinSettingsM
    }

    private suspend fun loadCoinSettings(token: String) {
        val queryName = "coinSettings"
        val query =
            StringBuilder().append("query {").append("$queryName { ").append("method, coinsNeeded")
                .append("}").append("}").toString()

        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG, "Query: $query")
            Log.e(TAG,"Result: ${result.body()}")

            when {
                result.isSuccessful -> {
                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val coinSettingsJsonArray = dataJsonObject[queryName].asJsonArray

                    coinSettingsJsonArray.forEach { jsonElement ->
                        val json = Gson().fromJson(jsonElement, CoinSettings::class.java)
                        _coinSettings.add(json)
                    }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null


                    if (error.isNullOrEmpty()) coinSettings.postValue(_coinSettings)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadCoinSettingsByMethod(token: String, method: String) {
        val queryName = "coinSettingByMethod"
        val query =
            StringBuilder().append("query {").append("$queryName (").append("method: \"${method}\"")
                .append(") {").append("region, coinsNeeded").append("}").append("}").toString()

        try {
            val result = api.callApi(token = "Token $token", body = query.getGraphqlApiBody())
            val jsonObject = Gson().fromJson(result.body(), JsonObject::class.java)
            Log.e(TAG, "Query: $query")
            Log.e(TAG,"Result: ${result.body()}")

            when {
                result.isSuccessful -> {
                    val dataJsonObject = jsonObject["data"].asJsonObject
                    val coinSettingsJsonArray = dataJsonObject[queryName].asJsonObject

//                    coinSettingsJsonArray.forEach { jsonElement ->
                    val json = Gson().fromJson(coinSettingsJsonArray, CoinSettings::class.java)
                    _coinSettingsM = json
//                    }

                    val error: String? = if (jsonObject.has("errors")) {
                        jsonObject["errors"].asJsonArray[0].asJsonObject["message"].asString
                    } else null


                    if (error.isNullOrEmpty()) coinSettingsM.postValue(_coinSettingsM!!)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}