package com.i69.data.models

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserWithAvatar(
    @SerializedName("id") val id: String? = "",
    @SerializedName("username") val username: String? = "",
    @SerializedName("fullName") val fullName: String? = "",
    @SerializedName("email") val email: String? = "",
    @SerializedName("avatar") val avatar: UserAvatar? = null,
)