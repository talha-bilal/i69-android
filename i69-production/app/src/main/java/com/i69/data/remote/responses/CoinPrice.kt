package com.i69.data.remote.responses

import androidx.annotation.Keep

@Keep
data class CoinPrice(
    val coinsCount: String,
    val originalPrice: String,
    val discountedPrice: String,
    val currency: String,
)
