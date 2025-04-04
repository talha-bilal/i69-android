package com.i69.ui.screens.main.coins

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.webkit.*
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.PaypalCapturePaymentMutation
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.ActivityWebLoginBinding
import com.i69.ui.base.BaseActivity
import com.i69.utils.apolloClient
import kotlinx.coroutines.launch

class WebPaymentActivity : BaseActivity<ActivityWebLoginBinding>() {

    companion object {
        const val ARGS_ACCESS_TOKEN = "access_token"
        const val ARGS_ACCESS_VERIFIER = "access_verifier"
        var IS_Done = false
        var paypalCapturePayment = ""
    }

    private var TAG: String = WebPaymentActivity::class.java.simpleName

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityWebLoginBinding.inflate(inflater)

    override fun setupTheme(savedInstanceState: Bundle?) {
        loadingDialog.show()

        CookieManager.getInstance().removeAllCookies {
            CookieManager.getInstance().flush()
        }
        WebStorage.getInstance().deleteAllData()

        with(binding.webView) {
            clearHistory()
            clearFormData()
            clearMatches()
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            webViewClient = mWebViewClient
            webChromeClient = mWebChromeClient
            loadUrl(intent.getStringExtra("url").toString())
        }
    }

    override fun setupClickListeners() {

    }

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e(TAG,"RequestUrl"+ request?.url.toString())
            paypalCapturePayment(intent.getStringExtra("id").toString())
            return false
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) loadingDialog.dismiss()
        }
    }

    private fun paypalCapturePayment(orderId: String) {
        Log.e("FromcraetedOrderId", orderId)
        lifecycleScope.launch {
            val userToken = getCurrentUserToken()
            try {
                val response = apolloClient(applicationContext, userToken!!).mutation(
                    PaypalCapturePaymentMutation(orderId)
                ).execute()

                if (response.hasErrors()) {

                    var message = response.errors?.get(0)?.message
                        ?: AppStringConstant1.something_went_wrong_please_try_again_later

                    Log.e("MyPaymentIdWrong", Gson().toJson(response.errors))
                } else {

                    Log.e(
                        "CapturePaypalOrderId",
                        Gson().toJson(response.data?.paypalCapturePayment)
                    )
                    IS_Done = true
                    paypalCapturePayment = response.data?.paypalCapturePayment?.id!!
                    finish()
                }
            } catch (e: ApolloException) {
                Log.e(TAG,"PurchaseFragment"+ "Operators Exception ${e.message}")

            } catch (e: Exception) {
                Log.e(TAG,"PurchaseFragment"+ "Operators Exception ${e.message}")

            }
        }
    }
}