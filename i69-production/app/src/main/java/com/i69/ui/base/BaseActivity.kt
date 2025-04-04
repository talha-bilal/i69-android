package com.i69.ui.base

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.i69.R
import com.i69.data.preferences.UserPreferences
import com.i69.singleton.App
import com.i69.utils.ContextWrapper
import com.i69.utils.SharedPref
import com.i69.utils.createLoadingDialog
import com.i69.utils.transact
import kotlinx.coroutines.flow.first
import java.util.Locale

abstract class BaseActivity<dataBinding : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var userPreferences: UserPreferences
    lateinit var binding: dataBinding
    protected lateinit var loadingDialog: Dialog
    private var TAG: String = BaseActivity::class.java.simpleName
    lateinit var sharedPrefrences: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate: BaseActivity............")
        try {
            if (resources.getBoolean(R.bool.isTablet)) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            userPreferences = App.userPreferences

            super.onCreate(savedInstanceState)

//            supportActionBar?.hide()

            binding = getActivityBinding(layoutInflater)
            setContentView(binding.root)
            binding.apply {
                lifecycleOwner = this@BaseActivity
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            try {
                loadingDialog = createLoadingDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setupTheme(savedInstanceState)
            setupClickListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase!!)
        val language = pref.getString("language", "")
        val newLocale = Locale(language)
        val context: Context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

    abstract fun getActivityBinding(inflater: LayoutInflater): dataBinding

    abstract fun setupTheme(savedInstanceState: Bundle?)

    abstract fun setupClickListeners()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showProgressView() {
        Log.e(TAG, "showProgressView: ")
        runOnUiThread { loadingDialog.show() }
    }

    protected fun hideProgressView() {
        Log.e(TAG, "hideProgressView: ")
        runOnUiThread { loadingDialog.dismiss() }
    }

    suspend fun getCurrentUserName() = userPreferences.userName.first()

    suspend fun getCurrentUserId() = userPreferences.userId.first()

    suspend fun getCurrentUserToken() = userPreferences.userToken.first()

    suspend fun getChatUserId() = userPreferences.chatUserId.first()

    fun transact(fr: Fragment, addToBackStack: Boolean = false) =
        supportFragmentManager.transact(fr, addToBackStack)
}