package com.i69.data.models.market

import com.google.gson.annotations.SerializedName


data class ProductDetails(

    @SerializedName("itemInfo") var itemInfo: ItemInfo? = ItemInfo(),
    @SerializedName("skuInfo") var skuInfo: ArrayList<SkuInfo> = arrayListOf(),
    @SerializedName("itemProperties") var itemProperties: ArrayList<ItemProperties> = arrayListOf(),
    @SerializedName("logisticsInfo") var logisticsInfo: LogisticsInfo? = LogisticsInfo(),
    @SerializedName("storeInfo") var storeInfo: StoreInfo? = StoreInfo(),
    @SerializedName("packageInfo") var packageInfo: PackageInfo? = PackageInfo(),
    @SerializedName("multimediaInfo") var multimediaInfo: MultimediaInfo? = MultimediaInfo()
)