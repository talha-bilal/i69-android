package com.i69.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

private const val TAG = "Billing"
private const val RECONNECT_TIMER_START_MILLISECONDS = 1L * 1000L
private const val RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L // 15 minutes
private const val SKU_DETAILS_REQUERY_TIME = 1000L * 60L * 60L * 4L // 4 hours

@Singleton
class BillingDataSource @Inject constructor(
    application: Application,
    private val defaultScope: CoroutineScope,
    knownInAppSkus: Array<String>
) : LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener {
    private var TAG: String = BillingDataSource::class.java.simpleName
    private val billingClient: BillingClient
    private val knownInAppSKUs: List<String> = listOf(*knownInAppSkus)

    private var reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
    private var skuDetailsResponseTime = -SKU_DETAILS_REQUERY_TIME

    // Flows that are mostly maintained so they can be transformed into observables.
    private val skuStateMap: MutableMap<String, MutableStateFlow<SkuState>> = HashMap()
    private val skuDetailsMap: MutableMap<String, MutableStateFlow<SkuDetails?>> = HashMap()

    // Observables that are used to communicate state.
    private val purchaseConsumptionInProcess: MutableSet<Purchase> = HashSet()
    private val newPurchaseFlow = MutableSharedFlow<List<String>>(extraBufferCapacity = 1)
    private val purchaseConsumedFlow = MutableSharedFlow<List<String>>()
    private val billingFlowInProcess = MutableStateFlow(false)

    private enum class SkuState {
        SKU_STATE_UNPURCHASED,
        SKU_STATE_PENDING,
        SKU_STATE_PURCHASED,
        SKU_STATE_PURCHASED_AND_ACKNOWLEDGED
    }

    companion object {
        @Volatile
        private var mInstance: BillingDataSource? = null
        private val handler = Handler(Looper.getMainLooper())
        var IS_Payment_Done = false
        var purchasesUpdated = false
        var paypalCaptureId = ""

        // Standard boilerplate double check locking pattern for thread-safe singletons.
        @JvmStatic
        fun getInstance(
            application: Application,
            defaultScope: CoroutineScope,
            knownInAppSkus: Array<String>
        ) = mInstance ?: synchronized(this) {
            mInstance ?: BillingDataSource(
                application,
                defaultScope,
                knownInAppSkus
            )
                .also { mInstance = it }
        }
    }

    init {
        initializeFlows()
        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(this)
    }

    private fun initializeFlows() {
        addSkuFlows(knownInAppSKUs)
    }

    private fun addSkuFlows(skuList: List<String>) {
        skuList.forEach { sku ->
            val skuState = MutableStateFlow(SkuState.SKU_STATE_UNPURCHASED)
            val details = MutableStateFlow<SkuDetails?>(null)

            details.subscriptionCount.map { count -> count > 0 }
                .distinctUntilChanged() // Only react to  true<->false  changes
                .onEach { isActive -> // Configure an action
                    if (isActive && SystemClock.elapsedRealtime() - skuDetailsResponseTime > SKU_DETAILS_REQUERY_TIME) {
                        skuDetailsResponseTime = SystemClock.elapsedRealtime()
                        Log.e(TAG, "Skus not fresh, re-querying")
                        querySkuDetailsAsync()
                    }
                }
                .launchIn(defaultScope)

            skuStateMap[sku] = skuState
            skuDetailsMap[sku] = details
        }
    }

    fun getNewPurchases() = newPurchaseFlow.asSharedFlow()


    //// Billing Setup
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.e(TAG, "Billing Setup Finished: Response: $responseCode  Msg: $debugMessage")

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // The BillingClient is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
                defaultScope.launch {
                    querySkuDetailsAsync()
                    refreshPurchases()
                }
            }

            else -> retryBillingServiceConnectionWithExponentialBackoff()
        }
    }

    override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to Google Play by calling the startConnection() method.
        Log.e(TAG, "Billing Service Disconnected")
        retryBillingServiceConnectionWithExponentialBackoff()
    }

    private fun retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed(
            { billingClient.startConnection(this) }, reconnectMilliseconds
        )
        reconnectMilliseconds =
            min(reconnectMilliseconds * 2, RECONNECT_TIMER_MAX_TIME_MILLISECONDS)
    }


    /// Query
    private fun querySkuDetailsAsync() {
        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(knownInAppSKUs)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            Log.e(TAG, "Query Sku Details: $skuDetailsList")
            onSkuDetailsResponse(billingResult, skuDetailsList)
        }
    }

    private suspend fun refreshPurchases() {
        Log.e(TAG, "Refreshing Purchases.")
        var billingResult: BillingResult? = null
        var purchasesList: MutableList<Purchase>? = null
        val purchasesResult = billingClient.queryPurchasesAsync(
            BillingClient.SkuType.INAPP,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    mBillingResult: BillingResult,
                    mPurchasesList: MutableList<Purchase>
                ) {
                    billingResult = mBillingResult
                    purchasesList = mPurchasesList

                    if (billingResult!!.responseCode != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Problem getting purchases: ${billingResult!!.debugMessage}")
                    } else {
                        defaultScope.launch {
                            processPurchaseList(purchasesList, knownInAppSKUs)
                        }
                    }
                }
            })
    }

    private fun onSkuDetailsResponse(
        billingResult: BillingResult,
        skuDetailsList: List<SkuDetails>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.e(TAG, "on Sku Details Response: $responseCode  Msg: $debugMessage")

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (skuDetailsList.isNullOrEmpty()) {
                    Log.e(
                        TAG,
                        "on Sku Details Response: Found null or empty SkuDetails. Check to see if the SKUs you requested are correctly published in the Google Play Console."
                    )
                } else {
                    skuDetailsList.forEach { skuDetails ->
                        val sku = skuDetails.sku
                        val detailsMutableFlow = skuDetailsMap[sku]
                        detailsMutableFlow?.tryEmit(skuDetails)
                            ?: Log.e(TAG, "Unknown sku: $sku")
                    }
                }
            }

            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> Log.e(
                TAG,
                "on Sku Details Response: $responseCode  Msg: $debugMessage"
            )

            BillingClient.BillingResponseCode.USER_CANCELED -> Log.e(
                TAG,
                "on Sku Details Response: $responseCode  Msg: $debugMessage"
            )

            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Log.e(
                TAG,
                "on Sku Details Response: $responseCode  Msg: $debugMessage"
            )

            else -> Log.e(TAG, "on Sku Details Response: $responseCode  Msg: $debugMessage")
        }
        skuDetailsResponseTime =
            if (responseCode == BillingClient.BillingResponseCode.OK) SystemClock.elapsedRealtime() else -SKU_DETAILS_REQUERY_TIME
    }


    private fun setSkuState(sku: String, newSkuState: SkuState) {
        val skuStateFlow = skuStateMap[sku]
        skuStateFlow?.tryEmit(newSkuState)
            ?: Log.e(
                TAG,
                "Unknown SKU $sku. Check to make sure SKU matches SKUS in the Play developer console."
            )
    }

    private suspend fun setSkuStateFromPurchase(purchase: Purchase) {
        purchase.skus.forEach { purchaseSku ->
            val skuStateFlow = skuStateMap[purchaseSku]
            if (skuStateFlow == null) {
                Log.e(
                    TAG,
                    "Unknown SKU $purchaseSku. Check to make sure SKU matches SKUS in the Play developer console."
                )

            } else {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams.build(),
                        object : AcknowledgePurchaseResponseListener {
                            override fun onAcknowledgePurchaseResponse(p0: BillingResult) {

                            }
                        })
                }
                when (purchase.purchaseState) {
                    Purchase.PurchaseState.PENDING -> skuStateFlow.tryEmit(SkuState.SKU_STATE_PENDING)
                    Purchase.PurchaseState.UNSPECIFIED_STATE -> skuStateFlow.tryEmit(SkuState.SKU_STATE_UNPURCHASED)
                    Purchase.PurchaseState.PURCHASED -> {
                        if (purchase.isAcknowledged) {
                            Log.e(TAG, "gfhgfgh" + "isAcknowledged")
                            skuStateFlow.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                        } else {
                            Log.e(TAG, "gfhgfgh" + "isdecjkvjkvheAcknowledged")
                            skuStateFlow.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                        }
                    }

                    else -> Log.e(TAG, "Purchase in unknown state: ${purchase.purchaseState}")
                }
            }
        }
    }

    fun getSkuDetails(sku: String): Flow<SkuDetails> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        Log.e(TAG, "Sku Details:  ${skuDetailsFlow.value}")
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails
        }
    }

    fun getConsumedPurchases() = purchaseConsumedFlow.asSharedFlow()


    private suspend fun consumePurchase(purchase: Purchase) {
        // weak check to make sure we're not already consuming the sku
        if (purchaseConsumptionInProcess.contains(purchase)) {
            // already consuming
            return
        }
        purchaseConsumptionInProcess.add(purchase)

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val consumePurchaseResult = billingClient.consumePurchase(consumeParams)
        purchaseConsumptionInProcess.remove(purchase)

        if (consumePurchaseResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Consumption successful. Emitting sku.")
            Log.e(TAG, "vfdbd" + "Consumption successful. Emitting sku.")
            defaultScope.launch {
                purchaseConsumedFlow.emit(purchase.skus)
            }
            // Since we've consumed the purchase
            for (sku in purchase.skus) {
                setSkuState(sku, SkuState.SKU_STATE_UNPURCHASED)
            }
        } else {
            Log.e(TAG, "Error while consuming: ${consumePurchaseResult.billingResult.debugMessage}")
        }
    }


    /// Launch
    fun launchBillingFlow(activity: Activity, sku: String) {
        //    getSkuDetails(sku)
        val skuDetails = skuDetailsMap[sku]?.value
        Log.e(TAG, "Sku Details $skuDetails")
        Log.e(
            TAG,
            "BDS" +
                    "launchBillingFlow: $skuDetails, ${skuDetails?.description}, ${skuDetails?.originalPrice}, ${skuDetails?.price}, ${skuDetails?.originalJson}"
        )

        skuDetails?.let {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()

            defaultScope.launch {
                val br = billingClient.launchBillingFlow(activity, flowParams)

                when (br.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        billingFlowInProcess.emit(true)

                    }

                    else -> Log.e(TAG, "Billing failed: ${br.debugMessage}")
                }
            }
        }
    }

    fun getBillingFlowInProcess(): Flow<Boolean> = billingFlowInProcess.asStateFlow()


    @SuppressLint("BinaryOperationInTimber")
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (val responseCode = billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.e(TAG, "Billing Result OK: $responseCode  Msg: ${billingResult.debugMessage}")
                if (purchases != null) {
                    Log.e(TAG, "time_0" + "onPurchasesUpdated")
                    // IS_Payment_Done=true
                    purchasesUpdated = true
                    paypalCaptureId = purchases.first().purchaseToken
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchases.first().purchaseToken)
                    val consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchases.first().purchaseToken)
                        .build()

                    defaultScope.launch {
                        billingClient.acknowledgePurchase(
                            acknowledgePurchaseParams.build(),
                            AcknowledgePurchaseResponseListener {

                            })
                        billingClient.consumePurchase(consumeParams)
                        processPurchaseList(purchases, knownInAppSKUs)
                    }
                    return
                } else Log.e(TAG, "Null Purchase List Returned from OK response!")
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> Log.e(
                TAG,
                "onPurchasesUpdated: User canceled the purchase"
            )

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Log.e(
                TAG,
                "onPurchasesUpdated: The user already owns this item"
            )

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(
                TAG,
                "onPurchasesUpdated: Developer error means that Google Play does not recognize the configuration. " +
                        "If you are just getting started, make sure you have configured the application correctly in the Google Play Console. " +
                        "The SKU product ID must match and the APK you are using must be signed with release keys."
            )

            else -> Log.e(TAG,"Billing Result: $responseCode  Msg: ${billingResult.debugMessage}")
        }
        defaultScope.launch {
            billingFlowInProcess.emit(false)
        }
    }

    private suspend fun processPurchaseList(
        purchases: MutableList<Purchase>?,
        skusToUpdate: List<String>?
    ) {
        val updatedSkus = HashSet<String>()

        purchases?.let {
            for (purchase in purchases) {

                for (sku in purchase.skus) {
                    val skuStateFlow = skuStateMap[sku]
                    if (skuStateFlow == null) {
                        Log.e(TAG,"Unknown SKU $skuStateFlow. Check to make sure SKU matches SKUS in the Play developer console.")
                        continue
                    }
                    updatedSkus.add(sku)
                }

                // Global check to make sure all purchases are signed correctly. This check is best performed on your server.
                when (purchase.purchaseState) {
                    Purchase.PurchaseState.PURCHASED -> {
                        if (!isSignatureValid(purchase)) {
                            Log.e(TAG,"Invalid signature. Check to make sure your public key is correct.")
                            // continue
                        }

                        // only set the purchased state after we've validated the signature.
                        setSkuStateFromPurchase(purchase)

                        defaultScope.launch {
                            consumePurchase(purchase)
                            newPurchaseFlow.tryEmit(purchase.skus)
                        }
                    }

                    else -> {
                        // make sure the state is set
                        setSkuStateFromPurchase(purchase)
                    }
                }
            }
        }

        skusToUpdate?.let {
            it.forEach { sku ->
                if (!updatedSkus.contains(sku)) setSkuState(sku, SkuState.SKU_STATE_UNPURCHASED)
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        Log.e(TAG,"ON RESUME")
        // this just avoids an extra purchase refresh after we finish a billing flow
        if (!billingFlowInProcess.value) {
            if (billingClient.isReady) {
                defaultScope.launch {
                    refreshPurchases()
                }
            }
        }
    }

    private fun isSignatureValid(purchase: Purchase): Boolean =
        Security.verifyPurchase(purchase.originalJson, purchase.signature)

}
