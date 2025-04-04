package com.i69.data.models

import androidx.annotation.Keep
import com.i69.GetUserQuery
@Keep
data class BaseUserVisitingModel(val viewType : Int, val userVisiting: GetUserQuery.UserVisiting, val dateTime: CharSequence = "")
