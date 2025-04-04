package com.i69.data.remote.responses

import androidx.annotation.Keep

@Keep
data class ResponseBody<T>(
    val data: T?,
    val errorMessage: String? = null
)