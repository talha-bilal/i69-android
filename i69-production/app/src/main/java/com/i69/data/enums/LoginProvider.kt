package com.i69.data.enums

enum class LoginProvider(val provider: String) {
    GOOGLE("google-oauth2"),
    FACEBOOK("facebook-oauth2"),
    TWITTER("twitter-oauth")
}