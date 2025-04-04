package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class BlockedUser(
    val id: String,
    val username: String,
    val avatarPhotos: List<Photo>? = emptyList(),
    val fullName:String
)
