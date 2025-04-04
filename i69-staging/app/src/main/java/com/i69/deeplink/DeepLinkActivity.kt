package com.i69.deeplink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.i69.databinding.ActivityDeepLinkBinding


class DeepLinkActivity : Activity() {

    private lateinit var binding: ActivityDeepLinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle the app link intent
        handleAppLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent!!)
        handleAppLink(intent)
    }

    private fun handleAppLink(intent: Intent?) {
        intent?.data?.let { uri ->
            // Extract data from the URI, e.g., the path and query parameters
            val path = uri.path
            val query = uri.query
            // Use the path or query parameters to navigate to specific content
            // For example, navigate to a detail page based on the URI
        }
    }

}