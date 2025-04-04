package com.i69.ui.screens.main.contact

import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.i69.BuildConfig
import com.i69.R
import com.i69.databinding.FragmentContactusBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.viewModels.AuthViewModel
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class ContactUsFragment:BaseFragment<FragmentContactusBinding>() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var userToken: String? = null
    private var userId: String? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentContactusBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
        }
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
            getMainActivity()?.notificationDialog(dialog,childFragmentManager,"${requireActivity().resources.getString(R.string.notifications)}")
        }

        binding?.sentMsg?.setOnClickListener {
            val name = binding?.etName?.text.toString()
            val email = binding?.etEmail?.text.toString()
            val message = binding?.etMessage?.text.toString()

            if(name.isNullOrEmpty()){
                binding?.etName?.setError("Name required!")
                binding?.etName?.requestFocus()
                return@setOnClickListener
            }
            if(email.isNullOrEmpty()){
                binding?.etEmail?.setError("Email required!")
                binding?.etEmail?.requestFocus()
                return@setOnClickListener
            }
            if(message.isNullOrEmpty()){
                binding?.etMessage?.setError("Message required!")
                binding?.etMessage?.requestFocus()
                return@setOnClickListener
            }
            binding?.pg?.visibility = View.VISIBLE
            contactUs(name,email,message)
        }
    }

    private fun contactUs(
        name: String,
        email: String,
        message: String
    ) {
        lifecycleScope.launch(Dispatchers.Main) {

            val userToken = getCurrentUserToken()!!
            loadingDialog?.show()
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val body = "{\r\n    \"name\": \"$name\",\r\n    \"email\": \"$email\",\r\n    \"message\": \"$message\"\r\n}".toRequestBody(mediaType)
            val request = Request.Builder()
                .url(BuildConfig.BASE_URL+"api/contact-us/")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()
            val response = client.newCall(request).execute()

            if(response.isSuccessful){
                loadingDialog?.dismiss()
                binding?.pg?.visibility = View.GONE

                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody)
                val isSuccess = jsonObject.getBoolean("success")

                if (isSuccess) {
                    binding?.root?.snackbar(getString(R.string.email_sent))
                    findNavController().popBackStack()
                }
                else {
                    binding?.root?.snackbar(getString(R.string.somethig_went_wrong_please_try_again))
                }
            }
        }
    }
}