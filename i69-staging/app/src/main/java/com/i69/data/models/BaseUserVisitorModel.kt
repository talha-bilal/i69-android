package com.i69.data.models

import androidx.annotation.Keep
import com.i69.GetUserQuery
@Keep
data class BaseUserVisitorModel(val viewType : Int,val userVisitor: GetUserQuery.UserVisitor,val dateTime: CharSequence = "")
