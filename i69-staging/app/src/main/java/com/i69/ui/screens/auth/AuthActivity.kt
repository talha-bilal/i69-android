package com.i69.ui.screens.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.AttrTranslationQuery
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.applocalization.getLoalizations
import com.i69.applocalization.getLoalizationsStringList
import com.i69.applocalization.updateLoalizationsConstString
import com.i69.databinding.ActivitySignInBinding
import com.i69.singleton.App
import com.i69.ui.base.BaseActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.utils.SharedPref
import com.i69.utils.apolloClient
import com.i69.utils.getGraphqlApiBody
import com.i69.utils.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivitySignInBinding>() {
    private var TAG: String = AuthActivity::class.java.simpleName
    private val appStringConstantViewModel: AppStringConstantViewModel by viewModels()

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivitySignInBinding.inflate(inflater)

    override fun setupTheme(savedInstanceState: Bundle?) {}

    override fun setupClickListeners() {}

    fun updateStatusBarColor(color: Int) {// Color must be in hexadecimal format
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun updateLanguageChanged() {
        showProgressView()
        lifecycleScope.launch {
            val userToken = App.userPreferences.userToken.first()
            Log.e(TAG, "UserToken : $userToken")
            val localizationString = getLoalizationsStringList()
            Log.e(TAG, "localizationString: $localizationString")
            if (userToken.isNullOrEmpty()) {
                val stringConstant = getLoalizations(this@AuthActivity, isUpdate = true)
                appStringConstantViewModel.data.postValue(stringConstant)
            } else {
                Log.e(TAG, "User token is not null")
                val res = try {
                    val query = AttrTranslationQuery(localizationString)
                    Log.e(TAG, "LocalizationString: $localizationString")
                    Log.e(TAG, "Query: ${query.toString().getGraphqlApiBody()}")
                    apolloClient(this@AuthActivity, userToken).query(query).execute()
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    Log.e(TAG, "apolloResponse ${e.message}")
                    Toast.makeText(this@AuthActivity, " ${e.message}", Toast.LENGTH_LONG).show()
                    return@launch
                }
                Log.e(TAG,"Response: $res")
                if (!res.hasErrors()) {
                    Log.e(TAG,"Response doesnt had error!")
                    val stringConstant =
                        getLoalizations(this@AuthActivity, res.data?.attrTranslation)
                    Log.e(TAG, Gson().toJson(stringConstant))
                    appStringConstantViewModel.data.postValue(stringConstant)
                    val sharedPref = SharedPref(this@AuthActivity)
                    sharedPref.setAttrTranslater(stringConstant)
                    updateLoalizationsConstString(this@AuthActivity, stringConstant)
                } else {
                    Log.e(TAG,"Response has error!")
                }
                delay(1200)
                lifecycleScope.launch(Dispatchers.Main) {
                    hideProgressView()
                    Log.e(TAG,"Launching main activity")
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }
    }
}
