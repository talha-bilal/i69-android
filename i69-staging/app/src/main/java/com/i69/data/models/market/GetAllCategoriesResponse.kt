package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class GetAllCategoriesResponse(
    @SerializedName("categoryId") var categoryId: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("parentCategoryId") var parentCategoryId: String? = null,
)