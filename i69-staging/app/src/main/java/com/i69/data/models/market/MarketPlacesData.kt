package com.i69.data.models.market

import com.i69.GetAllUserMomentsQuery

data class MarketPlacesData(
    val marketplaces: ArrayList<GetAllUserMomentsQuery.Edge>,
    val endCursor: String? = null,
    val hasNextPage: Boolean? = false
)