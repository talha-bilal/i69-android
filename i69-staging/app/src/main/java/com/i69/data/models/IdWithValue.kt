package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class IdWithValue(
    val id: Int,
    val value: String,
    val valueFr: String
)