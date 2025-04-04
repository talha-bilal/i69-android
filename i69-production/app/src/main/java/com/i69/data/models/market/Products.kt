package com.i69.data.models.market

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(

    @SerializedName("productId") var productId: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("currencySymbol") var currencySymbol: String? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("markupPrice") var markupPrice: Double? = null,
    @SerializedName("discountedPrice") var discountedPrice: Double? = null,
    @SerializedName("discount") var discount: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("detailUrl") var detailUrl: String? = null,
    @SerializedName("shopUrl") var shopUrl: String? = null,
    @SerializedName("firstLevelCategoryName") var firstLevelCategoryName: String? = null,
    @SerializedName("score") var score: String? = null,
    @SerializedName("resellPrice") var resellPrice: String? = null

) : Parcelable