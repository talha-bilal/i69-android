package com.i69.data.models.market

import com.google.gson.annotations.SerializedName


data class ItemInfo(

    @SerializedName("productId") var productId: String? = null,
    @SerializedName("mobileDetail") var mobileDetail: String? = null,
    @SerializedName("subject") var subject: String? = null,
    @SerializedName("categoryId") var categoryId: String? = null,
    @SerializedName("gmtCreate") var gmtCreate: String? = null,
    @SerializedName("salesCount") var salesCount: String? = null,
    @SerializedName("categorySequence") var categorySequence: String? = null,
    @SerializedName("wsOfflineDate") var wsOfflineDate: String? = null,
    @SerializedName("currencyCode") var currencyCode: String? = null,
    @SerializedName("detail") var detail: String? = null,
    @SerializedName("wsDisplay") var wsDisplay: String? = null,
    @SerializedName("avgEvaluationRating") var avgEvaluationRating: String? = null,

    )