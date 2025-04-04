package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class PackageInfo(
    @SerializedName("grossWeight") var grossWeight: String? = null,
    @SerializedName("packageHeight") var packageHeight: String? = null,
    @SerializedName("packageLength") var packageLength: String? = null,
    @SerializedName("packageType") var packageType: String? = null,
    @SerializedName("packageWidth") var packageWidth: String? = null,
    @SerializedName("productUnit") var productUnit: String? = null,
)