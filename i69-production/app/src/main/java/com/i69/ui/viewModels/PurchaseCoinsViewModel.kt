package com.i69.ui.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient

import com.i69.billing.BillingRepository
import com.i69.data.remote.repository.CoinRepository
import com.i69.data.remote.repository.UserDetailsRepository
import com.i69.data.remote.requests.PurchaseRequest
import com.i69.data.remote.responses.CoinPrice
import com.i69.data.remote.responses.CoinsResponse
import com.i69.data.remote.responses.ResponseBody
import com.i69.utils.PaymentsUtil
import com.i69.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PurchaseCoinsViewModel @Inject constructor(
    @ApplicationContext application: Context,
    private val coinRepository: CoinRepository,
    private val userDetailsRepository: UserDetailsRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _coinsLiveData: MutableLiveData<List<CoinPrice>> = MutableLiveData()
    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(application)

    val consumedPurchase: LiveData<Int>
        get() = billingRepository.consumedPurchase.asLiveData()
    val coinsLiveData: MutableLiveData<List<CoinPrice>>
        get() = _coinsLiveData

    val purchaseSuccess = MutableLiveData<Boolean>()
    var TAG: String = PurchaseCoinsViewModel::class.java.simpleName

    var coinPrice: ArrayList<CoinPrice> = ArrayList()

    fun getCoinPrices(token: String, callback: (String?) -> Unit) {
        userDetailsRepository.getCoinPrice(viewModelScope, token = token) { coinPrices ->
            Log.e(TAG, "getCoinPrices: $coinPrices")

            this.coinPrice.clear()
            this.coinPrice.addAll(coinPrices)
            callback.invoke(null)
        }
    }

    fun buySku(activity: Activity, sku: String) {
        Log.e(TAG, "InAppPurchaseSku"+ sku)
        billingRepository.buySku(activity, sku)
    }

    fun createPaypalOrder(amount: Float, currency: String, token: String) =
        userDetailsRepository.createPaypalOrder(viewModelScope, amount, currency, token)

    fun loadCurrentUser(userId: String, token: String, reload: Boolean) =
        userDetailsRepository.getCurrentUser(viewModelScope, userId, token = token, reload)

    fun getLoadPaymentDataTask(priceCents: Long): Task<PaymentData> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    suspend fun purchaseCoin(
        purchaseRequest: PurchaseRequest,
        token: String
    ): Resource<ResponseBody<CoinsResponse>> = coinRepository.purchaseCoin(purchaseRequest, token)

    suspend fun googleCreateOrder(
        purchaseRequest: PurchaseRequest,
        token: String,
    ): Resource<ResponseBody<CoinsResponse>> =
        coinRepository.googleCreateOrder(purchaseRequest, token)

}