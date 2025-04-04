package com.i69.data.models

import androidx.annotation.Keep
import com.i69.GetAllPackagesQuery

@Keep
data class BaseAllPackageModel(var isExpanded : Boolean, val allPackage: GetAllPackagesQuery.AllPackage?)
