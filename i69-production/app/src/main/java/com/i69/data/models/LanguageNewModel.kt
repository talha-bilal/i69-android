package com.i69.data.models

import androidx.annotation.Keep

@Keep
data class LanguageNewModel(
    val defaultPickers: DefaultPickers,
    val detail: String
)

@Keep
data class DefaultPickers(
    val countries: List<Country>,
    val languages: List<Language>,
    val subscriptions: List<Subscription>
)

@Keep
data class Country(
    val code: String,
    val flag_url: String,
    val name: String
)

@Keep
data class Language(
    val country_code: String,
    val country_flag: String,
    val id: Int,
    val language: String,
    val language_code: String
)

@Keep
data class Subscription(
    val id: Int,
    val name: String
)