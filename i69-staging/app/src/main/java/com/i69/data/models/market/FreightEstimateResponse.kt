package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

class FreightEstimateResponse(
    @SerializedName("code") var code: String? = null,
    @SerializedName("freeShipping") var freeShipping: String? = null,
    @SerializedName("shipFromCountry") var shipFromCountry: String? = null,
    @SerializedName("company") var company: String? = null,
    @SerializedName("mayHavePfs") var mayHavePfs: String? = null,
    @SerializedName("shippingFeeCent") var shippingFeeCent: String? = null,
    @SerializedName("tracking") var tracking: String? = null,
    @SerializedName("estimatedDeliveryTime") var estimatedDeliveryTime: String? = null,
    @SerializedName("shippingFeeFormat") var shippingFeeFormat: String? = null,
    @SerializedName("maxDeliveryDays") var maxDeliveryDays: String? = null,
    @SerializedName("deliveryDateDesc") var deliveryDateDesc: String? = null,
    @SerializedName("minDeliveryDays") var minDeliveryDays: String? = null,
    @SerializedName("guaranteedDeliveryDays") var guaranteedDeliveryDays: String? = null,
    @SerializedName("ddpIncludeVatTax") var ddpIncludeVatTax: String? = null,
    @SerializedName("shippingFeeCurrency") var shippingFeeCurrency: String? = null,
)