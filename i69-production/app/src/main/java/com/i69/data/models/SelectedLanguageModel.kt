package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class SelectedLanguageModel(
    val username: String,
    var userLanguageCode: String
)
