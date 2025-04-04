package com.i69.ui.screens.main.privacy

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.i69.R
import com.i69.data.config.Constants
import com.i69.databinding.FragmentPrivacyBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import kotlinx.coroutines.launch

class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>() {
    private var userToken: String? = null
    private var userId: String? = null
    private var url: String? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPrivacyBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupTheme() {
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
        }
        url = if (getMainActivity()?.pref?.getString("typeview", "privacy").equals("privacy")) {
            Constants.URL_PRIVACY_POLICY
        } else {
            Constants.URL_TERMS_AND_CONDITION
        }

        val webView: WebView? = binding?.privacyWebView

        webView?.settings?.javaScriptEnabled = true

        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.useWideViewPort = true
        webSettings?.loadWithOverviewMode = true
        webSettings?.domStorageEnabled = true

        webView?.webViewClient = WebViewController()
        webView?.loadUrl(url.toString())
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            getMainActivity()?.drawerSwitchState()
        }
        binding?.toolbarLogo?.setOnClickListener {
            findNavController().popBackStack()
        }


        binding?.bell?.setOnClickListener {
            val dialog = NotificationDialogFragment(userToken, binding?.counter, userId, binding?.bell)
            getMainActivity()?.notificationDialog(dialog, childFragmentManager, "${requireActivity().resources.getString(R.string.notificatins)}")
        }
    }

    class WebViewController : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        }
    }
}