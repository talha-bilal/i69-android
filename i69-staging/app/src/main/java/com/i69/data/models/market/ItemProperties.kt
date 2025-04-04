package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class ItemProperties(
    @SerializedName("attrName") var attrName: String? = null,
    @SerializedName("attrNameId") var attrNameId: String? = null,
    @SerializedName("attrValue") var attrValue: String? = null,
    @SerializedName("attrValueId") var attrValueId: String? = null,
)