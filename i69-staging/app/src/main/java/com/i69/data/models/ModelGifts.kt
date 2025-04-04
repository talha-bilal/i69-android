package com.i69.data.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

@Keep
data class ModelGifts(
    @SerializedName("data")
    var `data`: Data
) {
    @Keep
    data class Data(
        @SerializedName("allRealGift")
        var allRealGift: MutableList<AllRealGift>) {
        @Keep
        data class AllRealGift(
            @SerializedName("cost")
            var cost: Double,
            @SerializedName("giftName")
            var giftName: String,
            @SerializedName("id")
            var id: String,
            @SerializedName("picture")
            var picture: String,
            @SerializedName("type")
            var type: String,
            var isSelected: Boolean = false
        ){
            fun getRoundedCost(): String{
                return cost.roundToInt().toString()
            }
        }
    }
}