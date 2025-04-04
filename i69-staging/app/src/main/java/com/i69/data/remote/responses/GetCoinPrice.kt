package com.i69.data.remote.responses

import androidx.annotation.Keep

@Keep
data class GetCoinPrice(
    val getCoinPrices: List<CoinPrice>,
)