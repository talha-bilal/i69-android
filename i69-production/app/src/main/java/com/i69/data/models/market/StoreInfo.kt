package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class StoreInfo(
    @SerializedName("communicationRating") var communicationRating: String? = null,
    @SerializedName("itemAsDescribedRating") var itemAsDescribedRating: String? = null,
    @SerializedName("shippingSpeedRating") var shippingSpeedRating: String? = null,
    @SerializedName("storeCountryCode") var storeCountryCode: String? = null,
    @SerializedName("storeId") var storeId: String? = null,
    @SerializedName("storeName") var storeName: String? = null,
)