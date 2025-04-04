package com.i69.data.models.market

import com.google.gson.annotations.SerializedName


data class SearchProducts (

  @SerializedName("products" ) var products : ArrayList<Products> = arrayListOf()

)