package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class Photo(
    val id: String,
    var url: String?,
    var type: String
)
