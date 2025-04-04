package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class CoinSettings(
    val method: String,
    val coinsNeeded: Int
)