package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class LogisticsInfo(
    @SerializedName("deliveryTime"        ) var deliveryTime        : String? = null,
    @SerializedName("shipToCountry"        ) var shipToCountry        : String? = null,
)