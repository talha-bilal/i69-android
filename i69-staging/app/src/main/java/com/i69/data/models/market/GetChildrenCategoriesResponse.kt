package com.i69.data.models.market

import com.google.gson.annotations.SerializedName

data class GetChildrenCategoriesResponse(

    @SerializedName("categoryId") var categoryId: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("parentCategoryId") var parentCategoryId: String? = null,
)

data class ChildCategory(
    val categoryId: String,
    val categoryName: String,
    val parentCategoryId: String?,
    val level: Int,
    val isLeaf: Boolean
)