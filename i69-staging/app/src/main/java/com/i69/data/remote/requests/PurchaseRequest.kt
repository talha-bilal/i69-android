package com.i69.data.remote.requests

import androidx.annotation.Keep

@Keep
data class PurchaseRequest(
    val id: String = "",
    val currency :String = "",
    val coins: Int = 0,
    val money: Double = 0.0,
    val method: String = "",
    val paymentMethod: String = "",
    val payment_id : String = ""
)