package com.i69.data.remote.responses

import androidx.annotation.Keep

@Keep
data class CoinsResponse(
    val id: String,
    val coins: Int,
    val success: Boolean
)
