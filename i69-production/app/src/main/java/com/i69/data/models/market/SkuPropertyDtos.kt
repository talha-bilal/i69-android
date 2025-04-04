package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class SkuPropertyDtos(
    @SerializedName("skuPropertyValue") var skuPropertyValue: String? = null,
    @SerializedName("skuImage") var skuImage: String? = null,
    @SerializedName("skuPropertyName") var skuPropertyName: String? = null,
    @SerializedName("propertyValueDefinitionName") var propertyValueDefinitionName: String? = null,
    @SerializedName("propertyValueId") var propertyValueId: String? = null,
    @SerializedName("skuPropertyId") var skuPropertyId: String? = null,
)