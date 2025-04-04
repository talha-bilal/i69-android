package com.i69.data.models.market

data class FilterColorSkus(
    val color: String,
    val skuPropertyId: String,
    val skuImage: String,
    val resellPrice: String,
    val currencySymbol: String,
    val availableStock: String,
    val skuId: String,
    val filterSizeSkus: List<FilterSizeSkus>
)