package com.i69.data.models

import androidx.annotation.Keep
import com.i69.GetAllRoomsQuery

@Keep
data class MessageQuery(val edge: GetAllRoomsQuery.Edge?, val isBroadcast: Boolean = false)
