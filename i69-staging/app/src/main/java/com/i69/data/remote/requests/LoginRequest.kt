package com.i69.data.remote.requests

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    val accessToken: String,
    val accessVerifier: String = "",
    val provider: String,
)