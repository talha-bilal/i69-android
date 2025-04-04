package com.i69.billing

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BillingRepository @Inject constructor(
    private val billingDataSource: BillingDataSource,
    defaultScope: CoroutineScope
) {
    private var TAG: String = BillingRepository::class.java.simpleName
    val billingLifecycleObserver: LifecycleObserver
        get() = billingDataSource

    private val consumedPurchaseMessages: MutableSharedFlow<Int> = MutableSharedFlow()

    val consumedPurchase: Flow<Int>
        get() = consumedPurchaseMessages

    init {
        defaultScope.launch {
            billingDataSource.getConsumedPurchases().collect {
                it.forEach { sku ->
                    Log.e(TAG,"Consumed Purchases SKU : $sku")
                    consumedPurchaseMessages.emit(0)
                }
            }
        }
    }

    fun buySku(activity: Activity, sku: String) {
        billingDataSource.launchBillingFlow(activity, sku)
    }

}