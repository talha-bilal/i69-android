package com.i69.data.remote.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.i69.data.models.UserWithAvatar

@Keep
data class MomentLikes(
    @SerializedName("user") val user: UserWithAvatar?,
)
