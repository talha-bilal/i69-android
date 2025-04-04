package com.i69.ui.screens.main.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.exception.ApolloException
import com.chaos.view.PinView
import com.google.gson.Gson
import com.i69.DeleteAccountAllowedQuery
import com.i69.R
import com.i69.UserSubscriptionQuery
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.databinding.FragmentSettingsBinding
import com.i69.firebasenotification.FCMHandler
import com.i69.languages.LanguageAdapter
import com.i69.singleton.App
import com.i69.ui.base.BaseFragment
import com.i69.ui.interfaces.AlertDialogCallback
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.messenger.chat.contact.ContactActivity
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(),
    AdapterView.OnItemSelectedListener {

    private val viewModel: UserViewModel by activityViewModels()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = SettingsFragment::class.java.simpleName
    private var userId: String = ""
    private var userToken: String = ""
    private var userEmail: String = ""
    var userChatID: Int = 0
    private var allLangs = fetchLanguages()
    lateinit var sharedPref: SharedPref
    lateinit var stringConstant: AppStringConstant

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    private fun changeLanguageDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)

        headerTitle.text =
            "${AppStringConstant(requireContext()).are_you_sure_you_want_to_change_language}"
        noButton.text = "${AppStringConstant(requireContext()).no}"
        yesButton.text = "${AppStringConstant(requireContext()).yes}"

        val builder = AlertDialog.Builder(requireContext(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            binding?.spinnerLang?.performClick()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setupTheme() {
//        updateLanguageTranslation()
        val config: Configuration = resources.configuration
        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            // in Right To Left layout
            binding?.ivBuyArrow?.rotation = 180f
            binding?.ivBlockUserArrow?.rotation = 180f
            binding?.ivPrivacyArrow?.rotation = 180f
            binding?.ivTermsArrow?.rotation = 180f
            binding?.ivSubscriptionArrow?.rotation = 180f
            // toast("View is RTL")
        }
        viewStringConstModel.data.observe(this@SettingsFragment) { data ->
            binding?.stringConstant = data
            stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
            stringConstant = it.value ?: AppStringConstant(requireContext())
        }

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            userEmail = getEmailId()!!
        }

        sharedPref = SharedPref(requireContext())
        sharedPref.setLanguage(false)
        sharedPref.setLanguageFromSettings(false)
        setLocalTempConstants()
        getLanguages()

        navController = findNavController()

        isDeleteAccountAllowed()

        binding?.languagesLayout?.setOnClickListener {
            changeLanguageDialog()
        }
        binding?.spinnerLang?.setEnabled(false)
        binding?.spinnerLang?.isClickable = false
        binding?.spinnerLang?.adapter = LanguageAdapter(requireContext(), allLangs)
        Log.e(TAG, allLangs.toString())
        binding?.spinnerLang?.onItemSelectedListener = this

        binding?.inviteFriendContainer?.setOnClickListener {
            startActivity(Intent(requireContext(), ContactActivity::class.java).apply {
                putExtra("isInviteFriendsLink", true)
            })
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val language = getMainActivity()?.pref?.getString("language", "en")
            for (i in 0 until allLangs.size) {
                if (allLangs[i].supportedLangCode.equals(language)) {
                    binding?.spinnerLang?.setSelection(i)
                }
            }
        }
    }

    private fun setLocalTempConstants() {
        TempConstants.LanguageChanged = false
        TempConstants.isFromSettings = false
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            //activity?.onBackPressed()
            findNavController().popBackStack()
            //navController.navigate(R.id.nav_search_graph)
            //activity?.finish()
            //getActivity()?.getFragmentManager()?.popBackStack();
            //activity?.finishAfterTransition()
        }

        binding?.subScriptionLayout?.setOnClickListener {
            navController?.navigate(R.id.action_settingsFragment_to_userSubScriptionDetailFragment)
        }

        binding?.buyCoinsContainer?.setOnClickListener {
            navController?.navigate(R.id.actionGoToPurchaseFragment)
        }
        binding?.blockedContainer?.setOnClickListener {
            navController?.navigate(R.id.action_settingsFragment_to_blockedUsersFragment)
        }
        binding?.privacyContainer?.setOnClickListener {
            getMainActivity()?.pref?.edit()?.putString("typeview", "privacy")?.apply()
            navController?.navigate(R.id.actionGoToPrivacyFragment)
        }
        binding?.termsContainer?.setOnClickListener {
            getMainActivity()?.pref?.edit()?.putString("typeview", "terms_and_conditions")?.apply()
            navController?.navigate(R.id.actionGoToPrivacyFragment)
        }
        binding?.logoutContainer?.setOnClickListener {
            Log.e(TAG, "Before: UserId: ${App.userPreferences.userId}")
            lifecycleScope.launch(Dispatchers.IO) {
                App.userPreferences.clear()
            }
            viewModel.logOut(userId = userId, token = userToken) {
                Log.e(TAG, "After: UserId: ${App.userPreferences.userId}")
                FCMHandler.disableFCM()
                startNewActivity()
            }
        }
        binding?.deleteContainer?.setOnClickListener {
//            showDeleteProfile()
            lifecycleScope.launch(Dispatchers.Main) {
                when (val response = viewModel.verifyUser(userEmail, token = userToken)) {
                    is Resource.Success -> {
                        var jsonObject = response.data
                        val message = jsonObject?.data?.getAsJsonObject("data")
                            ?.getAsJsonObject("userVerify")
                            ?.get("message")
                            ?.asString
                        hideProgressView()
                        if (!message.isNullOrEmpty())
                            Toast.makeText(activity, "${message}", Toast.LENGTH_SHORT)
                                .show()
                        showDialogToAddVerificationCode()
                    }

                    is Resource.Error -> {
                        hideProgressView()
                        Log.e(TAG, "${response.message}")
                        binding?.root?.snackbar("${response.message}")
                    }

                    else -> {}
                }
            }

//            showDialogToAddVerificationCode()
        }
//        countrySelectionDialog()
    }

//    private fun countrySelectionDialog() {
//        val language =getMainActivity()?.pref?.getString("language","en")
//        binding.ccp.setDefaultCountryUsingNameCode(language)
//        binding.ccp.resetToDefaultCountry()
//        binding.ccp.setOnCountryChangeListener(object : CountryCodePicker.OnCountryChangeListener {
//            override fun onCountrySelected() {
//                Toast.makeText(
//                    requireContext(),
//                    "Updated " + binding.ccp.selectedCountryNameCode,
//                    Toast.LENGTH_SHORT
//                ).show();
//                getMainActivity()?.pref?.edit()?.putString("language", binding.ccp.selectedCountryNameCode)?.apply()
//
//                requireActivity().recreate()
////                val config = resources.configuration
////                val lang = binding.ccp.selectedCountryNameCode // your language code
////                val locale = Locale(lang)
////                Locale.setDefault(locale)
////                config.setLocale(locale)
////
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
////                    requireActivity().createConfigurationContext(config)
////                resources.updateConfiguration(config, resources.displayMetrics)
//
//            }
//        })
//    }

    private fun showDeleteProfile(code: String) {
        requireContext().showAlertDialog(getString(R.string.yes),
            getString(R.string.delete_account),
            getString(R.string.are_you_sure),
            object : AlertDialogCallback {
                override fun onNegativeButtonClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }

                override fun onPositiveButtonClick(dialog: DialogInterface) {
                    showProgressView()
                    deleteAccount(code)
                }
            })
    }

    private fun showDialogToAddVerificationCode() {
        val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.enter_verification_code_dialog, null)
        dialog.setContentView(view)
        val topMargin = requireActivity().resources.getDimension(com.intuit.sdp.R.dimen._1sdp)
                .toInt() // Top margin in pixels
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)

            attributes = attributes.apply {
                gravity = Gravity.CENTER
                y = topMargin
            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.show()

        val pinView = view.findViewById<PinView>(R.id.pinView)
        val txtOtpMsg = view.findViewById<TextView>(R.id.txtOtpMsg)
        val senOtp = view.findViewById<TextView>(R.id.senOtp)
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        txtOtpMsg.text = "Enter otp which sent on " + userEmail
        senOtp.setOnClickListener {
            if (pinView.text?.trim()?.length!! < 6) {
                Toast.makeText(activity, "Enter verification code", Toast.LENGTH_SHORT).show()
            } else {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                showDeleteProfile(pinView.text!!.trim().toString())
            }
        }
        imgClose.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    private fun deleteAccount(code: String) {
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.logOut(userId = userId, token = userToken) {

                lifecycleScope.launch(Dispatchers.Main) {
                    when (val response = viewModel.deleteProfile(userId, token = userToken, code.toInt())) {
                        is Resource.Success -> {
                            hideProgressView()
                            lifecycleScope.launch(Dispatchers.IO) {
                                App.userPreferences.clear()
                            }
                            viewModel.logOut(userId = userId, token = userToken) {
                                Log.e(TAG, "After: UserId: ${App.userPreferences.userId}")
                                FCMHandler.disableFCM()
                                startNewActivity()
                            }
                        }

                        is Resource.Error -> {
                            hideProgressView()
                            Log.e(TAG, "${response.message}")
                            binding?.root?.snackbar("${response.message}")
                        }

                        else -> {}
                    }
                }
//            }
//        }
    }

    private fun getLanguages() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getLanguages()
            val response = viewModel.getLanguages()
            if (response.body()?.defaultPickers?.languages?.isNotEmpty() == true) {
                Log.e(TAG, response.body()?.defaultPickers?.languages?.size.toString())
            }
        }
    }

    private fun startNewActivity() {
        lifecycleScope.launch(Dispatchers.Main) {
            userPreferences?.clear()
            //App.userPreferences.saveUserIdToken("","","")
            val intent = Intent(requireActivity(), SplashActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        //if (position > 0) {
        val selectedlanguage = fetchLanguages()[position].supportedLangCode
        val selectedlanguageid = fetchLanguages()[position].id
        if (getMainActivity()?.pref?.getString("language", "en").equals(selectedlanguage)) {

//                lifecycleScope.launch(Dispatchers.Main) {
//                    viewModel.updateLanguage(languageCode = selectedlanguage, userid = userId, token = userToken)
//                }

        } else {

            sharedPref.setLanguage(true)
            sharedPref.setLanguageFromSettings(true)

            lifecycleScope.launch(Dispatchers.Main) {
                getMainActivity()?.pref?.edit()?.putString("language", selectedlanguage)?.apply()
                async(Dispatchers.IO) {
//                            result?.list?.let { userRepository.insertDropDownData(it) }
                    viewModel.updateLanguage(
                        languageCode = if (selectedlanguage == "pt") "pt_pt" else selectedlanguage,
                        userid = userId,
                        token = userToken
                    )
                    viewModel.updateLanguageId(
                        languageCode = selectedlanguageid, userid = userId, token = userToken
                    )
                }.await()

                triggerRebirth(requireContext())
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    fun userSubscription() {
//        showProgressView()
        lifecycleScope.launchWhenResumed {

            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).query(
                    UserSubscriptionQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")
                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                Log.e("userCurentSubScription", Gson().toJson(response.data))
            }
        }
    }

    private fun isDeleteAccountAllowed() {
        lifecycleScope.launchWhenResumed {
            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).query(
                    DeleteAccountAllowedQuery()

                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                binding.root.snackbar("Exception Stripe Publish key${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }
            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG, "isDeleteAccountVi : $errorMessage")
                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                Log.e(TAG, "isDeleteAccountVi : ${Gson().toJson(response)}")
                lifecycleScope.launch(Dispatchers.Main) {
                    if (!response.data!!.deleteAccountAllowed.isNullOrEmpty() && response.data!!.deleteAccountAllowed!!.get(
                            0
                        )?.isDeleteAccountAllowed == true
                    ) {
                        binding?.logoutDivider?.visibility = View.VISIBLE
                        binding?.deleteContainer?.visibility = View.VISIBLE
                    } else {
                        binding?.logoutDivider?.visibility = View.GONE
                        binding?.deleteContainer?.visibility = View.GONE
                    }
                }
            }
        }
    }
}