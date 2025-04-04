package com.i69.ui.screens.auth.login

import android.content.Intent
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.i69.R
import com.i69.databinding.FragmentLoginBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.auth.AuthActivity
import com.i69.ui.viewModels.AuthViewModel
import com.i69.utils.defaultAnimate
import com.i69.utils.showDialogWithBackground
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: AuthViewModel by activityViewModels()
    private var TAG: String = LoginFragment::class.java.simpleName

    private val googleLoginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            showProgressView()
            viewModel.handleGoogleIntentResponse(requireContext(), userPreferences, result)
        }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun initObservers() {
        viewModel.whitelistSignInClient.observe(viewLifecycleOwner) { socialClients ->
            if (socialClients != null) {
                for (socialAuth in socialClients) {
                    Log.e(TAG, "SocialAuth: $socialAuth")
                    if (socialAuth!!.provider!!.contains("google", ignoreCase = true)) {
                        binding?.signInButtonWithGoogle?.visibility =
                            if (socialAuth.status.name.contains("DISABLED", ignoreCase = true)) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    } else if (socialAuth.provider!!.contains("facebook", ignoreCase = true)) {
                        binding?.signInButtonWithFb?.visibility =
                            if (socialAuth.status.name.contains("DISABLED", ignoreCase = true)) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    }
                }
            }
        }

        viewModel.nextScreenId.observe(viewLifecycleOwner) { navigateId ->
            hideProgressView()
            moveTo(navigateId)
        }

        viewModel.updateLanguage.observe(viewLifecycleOwner) {
            hideProgressView()
            (requireActivity() as AuthActivity).updateLanguageChanged()
        }

        viewModel.contactErrorDialog.observe(viewLifecycleOwner) {
            hideProgressView()
            requireActivity().showDialogWithBackground(getString(R.string.account_deleted_error)) {
                moveTo(LoginFragmentDirections.actionLoginFragmentToContactFragment())
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            hideProgressView()
            if (error.isNotEmpty()) {
                binding?.root?.snackbar(error)
            }
        }
    }

    override fun setupTheme() {
        viewModel.initializeSocialLogins(requireActivity(), userPreferences)
        Log.e(TAG, "setupThemes: " + Locale.getDefault().language)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.availableSocialSignInClients(requireContext())
        }
        binding?.signInLogo?.defaultAnimate(300, 800)
        binding?.signInAppTitle?.defaultAnimate(300, 800)
        binding?.signInButtonWithFb?.defaultAnimate(300, 800)
        binding?.signInButtonWithGoogle?.defaultAnimate(300, 800)
    }

    override fun setupClickListeners() {
        binding?.signInButtonWithFb?.setOnClickListener { loginToFacebook() }
        binding?.signInButtonWithGoogle?.setOnClickListener { loginToGoogle() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginToGoogle() {
        val signInIntent = viewModel.getGoogleSignInClient().signInIntent
        googleLoginLauncher.launch(signInIntent)
    }

    private fun loginToFacebook() {
        showProgressView()
        val loginManager = viewModel.getFacebookLoginManager()
        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile", "user_friends")
        )
    }
}