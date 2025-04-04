package com.i69.data.remote.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginResponse(
    val id: String,
    val email: String?,
    @SerializedName("isNew")
    val isNew: Boolean,
    val token: String,
    val username: String,

)