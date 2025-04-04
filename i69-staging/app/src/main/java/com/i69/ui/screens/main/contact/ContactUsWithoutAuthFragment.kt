package com.i69.ui.screens.main.contact

import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.i69.R
import com.i69.BuildConfig
import com.i69.databinding.FragmentContactusBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.utils.snackbar
import com.paypal.pyplcheckout.ui.feature.sca.runOnUiThread
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ContactUsWithoutAuthFragment : BaseFragment<FragmentContactusBinding>() {

    private var userToken: String? = null
    private var userId: String? = null
    private var TAG: String = ContactUsWithoutAuthFragment::class.java.simpleName

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentContactusBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            getMainActivity()?.drawerSwitchState()
        }
        binding?.toolbarLogo?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.bell?.setOnClickListener {
            val dialog = NotificationDialogFragment(
                userToken,
                binding?.counter,
                userId,
                binding?.bell
            )
            getMainActivity()?.notificationDialog(
                dialog,
                childFragmentManager,
                "${requireActivity().resources.getString(R.string.notifications)}"
            )
        }

        binding?.sentMsg?.setOnClickListener {
            val name = binding?.etName?.text.toString()
            val email = binding?.etEmail?.text.toString()
            val message = binding?.etMessage?.text.toString()

            if (name.isNullOrEmpty()) {
                binding?.etName?.error = "Name required!"
                binding?.etName?.requestFocus()
                return@setOnClickListener
            }
            if (email.isNullOrEmpty()) {
                binding?.etEmail?.error = "Email required!"
                binding?.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if (message.isNullOrEmpty()) {
                binding?.etMessage?.error = "Message required!"
                binding?.etMessage?.requestFocus()
                return@setOnClickListener
            }
            binding?.pg?.visibility = View.VISIBLE
            contactUs(name, email, message)
        }
    }

    private fun contactUs(
        name: String? = "",
        email: String,
        message: String
    ) {
        runOnUiThread {
            try {
                showProgressView()

//            val client = OkHttpClient()
                val client = OkHttpClient.Builder()
                    .connectTimeout(45, TimeUnit.SECONDS) // Time to establish the connection
                    .readTimeout(30, TimeUnit.SECONDS)    // Time to wait for server response
                    .writeTimeout(30, TimeUnit.SECONDS)   // Time to send data to the server
                    .build()
                val mediaType = "application/json".toMediaType()
                val body =
                    "{\r\n    \"name\": \"$name\",\r\n    \"email\": \"$email\",\r\n    \"message\": \"$message\"\r\n}".toRequestBody(
                        mediaType
                    )

                val request = Request.Builder()
                    .url(BuildConfig.BASE_URL + "api/contact-us/")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()
                try {
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        hideProgressView()
                        binding?.pg?.visibility = View.GONE

                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody!!)
                        val isSuccess = jsonObject.getBoolean("success")

                        if (isSuccess) {
                            binding?.root?.snackbar(getString(R.string.email_sent))
                            findNavController().popBackStack()
                        } else {
                            binding?.root?.snackbar("${requireActivity().resources.getString(R.string.somethig_went_wrong_please_try_again)}")
//                    Toast.makeText(activity, getString(R.string.somethig_went_wrong_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    Log.e(TAG, "Request timed out: ${e.message}")
                    hideProgressView()
                } catch (e: Exception) {
                    Log.e(TAG, "An error occurred: ${e.message}")
                    hideProgressView()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
}