package com.i69.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserSubscription(
    val isActive: Boolean = false,
    val isCancelled: Boolean = false
) : Serializable