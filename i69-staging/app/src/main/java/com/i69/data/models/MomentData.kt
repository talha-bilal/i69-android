package com.i69.data.models

import com.i69.GetAllUserMomentsQuery

data class MomentData(
    val moments: ArrayList<GetAllUserMomentsQuery.Edge>,
    val endCursor: String? = null,
    val hasNextPage: Boolean? = false
)