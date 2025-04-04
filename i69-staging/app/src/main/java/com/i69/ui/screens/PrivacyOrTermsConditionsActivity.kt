package com.i69.ui.screens

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Window.FEATURE_NO_TITLE
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.i69.R
import com.i69.data.config.Constants


class PrivacyOrTermsConditionsActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private var TAG: String = PrivacyOrTermsConditionsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)
        window.requestFeature(FEATURE_NO_TITLE)
        mWebView = WebView(this)
        mWebView.loadUrl(getUrl())
        val privacyUrl =
            Constants.URL_PRIVACY_POLICY//.plus(MainActivity.getMainActivity()?.pref?.getString("language", "en")).plus("/policy")
        Log.e(TAG, "PrivacyUrl: $privacyUrl")
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                view.loadUrl(url)
//                return true
//            }
        }
        setContentView(mWebView)
    }

    private fun getUrl() =
        if (intent.hasExtra("type") && intent.getStringExtra("type") == "privacy") Constants.URL_PRIVACY_POLICY else Constants.URL_TERMS_AND_CONDITION
}