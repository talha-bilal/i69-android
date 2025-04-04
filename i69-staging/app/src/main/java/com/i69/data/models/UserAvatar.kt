package com.i69.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserAvatar(
    @SerializedName("url") val url: String?
)