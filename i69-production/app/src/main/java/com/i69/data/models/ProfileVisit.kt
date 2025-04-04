package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class ProfileVisit(
    val isVisited: Boolean? = null,
    val isNotificationSent: Boolean? = null
)