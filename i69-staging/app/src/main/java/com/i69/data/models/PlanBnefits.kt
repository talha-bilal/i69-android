package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class PlanBnefits(
    val name: String,
    var planId : String,
    var isPlatnium: Boolean,
    var isSilver: Boolean,
    var isGold : Boolean,
    var selectedPackageId : String
)


