package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class MultimediaInfo(
    @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf(),
)