package com.i69.ui.screens.auth.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import com.i69.R
import com.i69.databinding.FragmentWelcomeNewBinding
import com.i69.di.modules.AppModule
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.auth.AuthActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.utils.getResponse
import com.i69.utils.startActivity
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeNewBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentWelcomeNewBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    private var TAG: String = WelcomeFragment::class.java.simpleName
    private var userId: String? = null
    private var userToken: String? = null
    override fun setupTheme() {
        (activity as AuthActivity).updateStatusBarColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorPrimary
            )
        )
    }

    override fun setupClickListeners() {
        binding?.start?.setOnClickListener {
            lifecycleScope.launch {
                Log.e(TAG, "CurrentUserId  --> ${getCurrentUserId()}")
                Log.e(TAG, "UserTokenc --> ${getCurrentUserToken()}")
                userId = getCurrentUserId()!!
                userToken = getCurrentUserToken()!!


                val queryName = "sendNotification"
                val query = StringBuilder()
                    .append("mutation {")
                    .append("$queryName (")
                    .append("userId: \"${userId}\", ")
                    .append("notificationSetting: \"WELCOME\" ")
                    .append(") {")
                    .append("sent")
                    .append("}")
                    .append("}")
                    .toString()

                val result = AppModule.provideGraphqlApi().getResponse<JSONObject>(
                    query,
                    queryName, userToken
                )
                Log.e(TAG, "ResultFirstMessage : ${Gson().toJson(result)}")
            }

            requireActivity().startActivity<MainActivity>()
            requireActivity().finish()
        }
    }

}