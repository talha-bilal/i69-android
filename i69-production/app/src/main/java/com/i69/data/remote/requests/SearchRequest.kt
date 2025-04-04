package com.i69.data.remote.requests

import androidx.annotation.Keep

@Keep
data class SearchRequest(
    val interestedIn: Int = 0,
    val id: String,
    val searchKey: String? = null,
    val minHeight: Int? = null,
    val maxHeight: Int? = null,
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val lat: Double? = 0.0,
    val long: Double? = 0.0,
    val tags: List<Int>? = null,
    val familyPlans: Int? = null,
    val gender: Int? = null,
    val politics: Int? = null,
    val religious: Int? = null,
    val zodiacSign: Int? = null,
    val maxDistance: Int? = null,
)