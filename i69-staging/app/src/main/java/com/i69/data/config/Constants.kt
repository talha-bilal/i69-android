package com.i69.data.config

import android.app.Activity
import com.google.android.gms.wallet.WalletConstants
import com.i69.BuildConfig


object Constants {
    /*//const val BASE_URL = "https://api.i69app.com/"
    const val BASE_URL = "https://admin.chatadmin-mod.click/admin/"*/
    const val URL_TERMS_AND_CONDITION = "https://sites.google.com/view/i69-tos/"

    //    const val URL_PRIVACY_POLICY = "https://i69app.com/policy"
   // const val URL_PRIVACY_POLICY = "https://i69app.com/"

    //const val URL_PRIVACY_POLICY = "https://sites.google.com/view/i69app/accueil"
    const val URL_PRIVACY_POLICY = "https://sites.google.com/view/i69app2/privacy-policy"

    const val ADMIN_EMAIL = "isixtynine.adm@gmail.com"
    const val SEARCH_RANDOM_LIMIT = 30
    const val SEARCH_POPULAR_LIMIT = 30

    const val INTEREST_MUSIC = 0
    const val INTEREST_MOVIE = 1
    const val INTEREST_TV_SHOW = 2
    const val INTEREST_SPORT_TEAM = 3
    const val EXTRA_INTEREST_TYPE = "EXTRA_INTEREST_TYPE"
    const val EXTRA_INTEREST_VALUE = "EXTRA_INTEREST_VALUE"
    const val EXTRA_IMG_SLIDER = "EXTRA_IMG_SLIDER"
    const val SLIDER_POSITION = "SLIDER_POSITION"
    const val PRIVATE_IMAGES_FOUND = "PRIVATE_IMAGES_FOUND"
    const val PRIVATE_IMAGES_REQUEST_STATUS = "PRIVATE_IMAGES_REQUEST_STATUS"
    const val MY_ID = "MY_ID"
    const val OTHER_ID = "OTHER_ID"
    const val CURRENT_USER_ID = "CURRENT_USER_ID"


    /// Twitter
    var TWITTER_SIGN_IN_URL = "${BuildConfig.BASE_URL}auth/twitter/redirect/"
    var TWITTER_CALLBACK_URL = "${BuildConfig.BASE_URL}signin-twitter"
    var TWITTER_CALLBACK_TEMP_URL = "https://admin.chatadmin-mod.click/signin-twitter"


    /// IN APP BILLING
//    const val IN_APP_FIRST_TYPE = "android.test.purchased"
    const val IN_APP_FIRST_TYPE = "1_type"
    const val IN_APP_SECOND_TYPE = "2_types"
    const val IN_APP_THIRD_TYPE = "3_types"
    const val IN_APP_FOURTH_TYPE = "4_type"
    const val IN_APP_FIFTH_TYPE = "5_type"
    const val IN_APP_SIXTH_TYPE = "6_types"
    const val IN_APP_SEVENTH_TYPE = "7_type"
    const val IN_APP_EIGHTH_TYPE = "8_type"
    const val IN_APP_NINETH_TYPE = "9_type"
    const val IN_APP_TENTH_TYPE = "10_types"

    // One Signal
    const val ONESIGNAL_APP_ID = "0be9bbe8-af94-4498-8c96-36cc96604f41"

    /// Google In-App Purchase
    val IN_APP_SKUS = arrayOf(
        IN_APP_FIRST_TYPE,
        IN_APP_SECOND_TYPE,
        IN_APP_THIRD_TYPE,
        IN_APP_FOURTH_TYPE,
        IN_APP_FIFTH_TYPE,
        IN_APP_SIXTH_TYPE,
        IN_APP_SEVENTH_TYPE,
        IN_APP_EIGHTH_TYPE,
        IN_APP_NINETH_TYPE,
        IN_APP_TENTH_TYPE
    )

    val IN_APP_SKUS_COINS = arrayOf(
        100,
        500,
        1150,
        250, /// Arrangement Wrong
        2550,
        5600
    )

    const val BASE_64_ENCODED_PUBLIC_KEY =
//        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgEuKZHjXCRHqSvfWDn4WUve5DGE9847jCdG/foBN/WvAhOaAQpun6Row7TKnIO0M77mfQksoprYljGjDwngXf3kSoNFhzU+kHv1syfB7dzV7WcIR3phHyY8wZVjDZjrfYzbhfUjA91vfZSQl78eEzF5TGWArkoKp7jNUP0vKOL1aGCfdn6ypsdh39S+sQI8XGRyiWSkSwkcoPeOqJRkgCLbKKgjT1fF4Cc0as0Rj5/03durHfbeN76KsYqd74ZhWJwt3UuEX7NQNLwmwDdMv3leNMQddxAobyumpiEX+7mzbwHOHwz2a9S5zKEiqUkfxmw0eUMK9Vt4sxrte2TergwIDAQAB"
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjKdfFoFYwmidg5Gl5h5xDZX8J6Kn4j9rb5fbsIo1mqNZjFPpZAdlhu0dH0k5iQaerQbloEUXsTEWjKdERrQrn8UrIhntqwa/ySZDViT5AiyqtABH2oi8yLVP1ctx54jymH7AeKOTWyEe4X3FQhUY06o3RfUaunSfdLV2qizpRnMVZxLJTkviGnUzGXAyvF5FpzQh0kYWp0aDjbRO15RDysK7B4XYrNuRaXb74dI+uQACbDPp3VzjewTg1wNAdX3oBdl84lFc8a+uW53/MBfKmCVK3dl76xHqABLiZ0hwDF4Qe5Yuy8FNl5OxBx0REuDX2ZwhNXIkFkfpOwbqoAllmQIDAQAB"

    //   const val PAYPAL_CLIENT_ID = "AXG6q4YxIzaHkhHu0vlEk1QjYp-tHvMP_JhUjsCehtqZeIZsCs-dNvWxMzFF-KvLesBXYNk4-5gp0_ib" /* Sandbox */
//    const val PAYPAL_CLIENT_ID = "AQCdEBLfbKiVI0JrFrJDubigxC4O5YD78R1WU5xxkgBl69pmNoZs5NY2zIQf_SKTFWQJb_AAEJLF5j2Q" /* Production */
//   const val PAYPAL_CLIENT_ID = "AcJmodfzEPKg7AUc0e-ImRuQ5yji7zssG4x2f4d5wb3qJZtXYej5loUXQyyB9ZAghY8wOp4ocNFO5fgb" /* Production */
    const val PAYPAL_CLIENT_ID =
        "ATPgTf-B12X0nkUbvJLo729SiSRE5c1sBWcuSKxBJ075usgZCZMaSjt0Mrkw42CEw0zpk-l7pwSjOW2T" /* Production */
//
//    val PAYPAL_USER_ACTION = UserAction.PAY_NOW
//    val PAYPAL_CURRENCY = CurrencyCode.USD
//
//   val PAYPAL_ENVIRONMENT = Environment.SANDBOX /* Sandbox */
//    val PAYPAL_ENVIRONMENT = Environment.LIVE /* Production */

    fun hideKeyboard(activity: Activity) {
        /*val view: View = activity.findViewById(R.id.content)
        if (view != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }*/
    }

    var INTENTACTION = "IntentFilter"


    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

    /**
     * The allowed networks to be requested from the API. If the user has cards from networks not
     * specified here in their account, these will not be offered for them to choose in the popup.
     *
     * @value #SUPPORTED_NETWORKS
     */
    val SUPPORTED_NETWORKS = listOf(
        "AMEX",
        "DISCOVER",
        "INTERAC",
        "JCB",
        "MASTERCARD",
        "VISA"
    )

    /**
     * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
     * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
     *
     * @value #SUPPORTED_METHODS
     */
    val SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"
    )

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #COUNTRY_CODE Your local country
     */
    const val COUNTRY_CODE = "US"

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #CURRENCY_CODE Your local currency
     */
    const val CURRENCY_CODE = "USD"

    /**
     * Supported countries for shipping (use ISO 3166-1 alpha-2 country codes). Relevant only when
     * requesting a shipping address.
     *
     * @value #SHIPPING_SUPPORTED_COUNTRIES
     */
    val SHIPPING_SUPPORTED_COUNTRIES = listOf("US", "GB")

    /**
     * The name of your payment processor/gateway. Please refer to their documentation for more
     * information.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_NAME
     */
    private const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"

    /**
     * Custom parameters required by the processor/gateway.
     * In many cases, your processor / gateway will only require a gatewayMerchantId.
     * Please refer to your processor's documentation for more information. The number of parameters
     * required and their names vary depending on the processor.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
     */
    val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
        "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
        "gatewayMerchantId" to "exampleGatewayMerchantId"
    )

    /**
     * Only used for `DIRECT` tokenization. Can be removed when using `PAYMENT_GATEWAY`
     * tokenization.
     *
     * @value #DIRECT_TOKENIZATION_PUBLIC_KEY
     */
    const val DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME"

    /**
     * Parameters required for `DIRECT` tokenization.
     * Only used for `DIRECT` tokenization. Can be removed when using `PAYMENT_GATEWAY`
     * tokenization.
     *
     * @value #DIRECT_TOKENIZATION_PARAMETERS
     */
    val DIRECT_TOKENIZATION_PARAMETERS = mapOf(
        "protocolVersion" to "ECv1",
        "publicKey" to DIRECT_TOKENIZATION_PUBLIC_KEY
    )

}