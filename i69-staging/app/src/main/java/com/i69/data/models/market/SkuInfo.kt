package com.i69.data.models.market

import com.google.gson.annotations.SerializedName


data class SkuInfo(

    @SerializedName("skuId") var skuId: String? = null,
    @SerializedName("skuAttr") var skuAttr: String? = null,
    @SerializedName("skuPrice") var skuPrice: String? = null,
    @SerializedName("markupPrice") var markupPrice: String? = null,
    @SerializedName("discountedPrice") var discountedPrice: String? = null,
    @SerializedName("discountPercentage") var discountPercentage: String? = null,
    @SerializedName("resellPrice") var resellPrice: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("currencySymbol") var currencySymbol: String? = null,
    @SerializedName("availableStock") var availableStock: String? = null,
    @SerializedName("completeDetails") var completeDetails: String? = null,
    @SerializedName("aeSkuPropertyDtos" ) var aeSkuPropertyDtos : ArrayList<SkuPropertyDtos> = arrayListOf()
)