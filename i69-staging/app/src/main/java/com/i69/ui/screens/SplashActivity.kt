package com.i69.ui.screens

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.gson.Gson
import com.i69.AttrTranslationQuery
import com.i69.R
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.applocalization.getLoalizations
import com.i69.applocalization.getLoalizationsStringList
import com.i69.applocalization.updateLoalizationsConstString
import com.i69.databinding.ActivitySplashBinding
import com.i69.singleton.App
import com.i69.ui.base.BaseActivity
import com.i69.ui.screens.auth.AuthActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.SearchInterestedInFragment
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.Resource
import com.i69.utils.SharedPref
import com.i69.utils.TempConstants
import com.i69.utils.apolloClient
import com.i69.utils.defaultAnimate
import com.i69.utils.fetchLanguages
import com.i69.utils.snackbar
import com.i69.utils.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private var startTime: Long = 0
    private var TAG: String = SplashActivity::class.java.simpleName

    @Inject
    lateinit var sharedPref: SharedPref


    private lateinit var appUpdateManager: AppUpdateManager
    private val viewModessl: AppStringConstantViewModel by viewModels()
    private val viewModel: UserViewModel by viewModels()


    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivitySplashBinding.inflate(inflater)

    override fun onResume() {
        super.onResume()
        val endTime = System.currentTimeMillis() // Record the end time
        val loadTime = endTime - startTime // Calculate load time
        Log.e(TAG, "Load time: $loadTime ms")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        startTime = System.currentTimeMillis() // Record the start time

        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)

        if (!sharedPref.getFirstTimeLanguage()) {
            val languages = fetchLanguages()
            val language = Locale.getDefault().language
            val pref = PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)
            for (i in 0 until languages.size) {
                if (languages[i].supportedLangCode.equals(language)) {
                    pref.edit()?.putString("language", language)?.apply()
//                    sharedPref.setLanguageName(language)
                    break
                }
            }
            sharedPref.setFirtsTimeLanguage(true)
        }
        if (sharedPref.getLanguage()) {
            val background = binding.ctSplash
            if (sharedPref.getLanguageFromSettings()) {
                TempConstants.isFromSettings = true
                background.setBackgroundColor(ContextCompat.getColor(this, R.color.container_color))
            }
            TempConstants.LanguageChanged = true
            val intent = Intent(this, ProgressBarActivity::class.java)
            startActivity(intent)
        }

        setDefaultLanguage()
    }

    private fun setDefaultLanguage() {
        lifecycleScope.launch {
            var lang = ""
            val pref: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)

            val config = resources.configuration
            lang = if (getCurrentUserId() == null) Locale.getDefault().language
            else if (pref.getString("language", "").isNullOrEmpty()) Locale.getDefault().language
            else pref.getString("language", "en").toString()


            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.setLocale(locale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }


    override fun setupTheme(savedInstanceState: Bundle?) {
//        PreferenceManager.getDefaultSharedPreferences(this@SplashActivity).edit().clear().apply();
        if (intent.hasExtra("data")) {
            Log.e(TAG, "data : ${Gson().toJson(intent.extras!!.get("data").toString())}")
            val mainIntent = Intent(this, MainActivity::class.java)
            val dataString = intent.extras!!.get("data").toString()
            val dataValues = JSONObject(dataString)

            if (dataValues.has("roomID")) {
                Log.e(TAG, "room id : ${dataValues.getString("roomID")}")
                mainIntent.putExtra("isChatNotification", "yes")
                mainIntent.putExtra("roomIDs", dataValues.getString("roomID"))
            } else {
                mainIntent.putExtra("isNotification", "yes")
                mainIntent.putExtra("notificationData", dataString)
            }
            Log.e(TAG, "Going to launch Main activity")
            startActivity(mainIntent)
        } else {
            // appUpdateManager = AppUpdateManagerFactory.create(this)
            //  val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            Log.e(TAG, "splash_act : 158")
            navigate()
            /* appUpdateInfoTask
                 .addOnSuccessListener { appUpdateInfo ->
                     if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                         && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                     )
                         startUpdate(appUpdateInfo) else navigate()
                 }
                 .addOnFailureListener {
                     Log.e("splash_act", "167")
                     navigate()
                 }*/

        }


        SearchInterestedInFragment.setShowAnim(true)
        binding.splashLogo.defaultAnimate(400, 500)
        binding.splashTitle.defaultAnimate(300, 700)


//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
//        val channelId = getString(R.string.default_notification_channel_id)
//        val channelName = getString(R.string.default_notification_channel_name)
//        val channelDescription = getString(R.string.default_notification_channel_desc)
//        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
//        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val channel = NotificationChannelCompat.Builder(channelId, importance).apply {
//            setName(channelName)
//            setDescription(channelDescription)
//            setSound(alarmSound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
//        }
//        channel.setVibrationEnabled(true)
//        NotificationManagerCompat.from(this).createNotificationChannel(channel.build())


//        printKeyHash(this)
//        getEncodedApiKey(LocalStringConstants.google_maps_key)
    }

    override fun setupClickListeners() {

    }

    /* private fun startUpdate(appUpdateInfo: AppUpdateInfo) {
         appUpdateManager.startUpdateFlowForResult(
             appUpdateInfo,
             AppUpdateType.FLEXIBLE,
             this,
             MY_REQUEST_CODE
         )
     }*/

    private fun navigate() {
        lifecycleScope.launch(Dispatchers.Main) {
            val userId = getCurrentUserId()
            if (userId != null && !sharedPref.getLanguage()) {
                updateLanguage(userId, getCurrentUserToken()!!)
            } else {
                // delay(1200)
                //  updateLanguage(getCurrentUserId()!!, getCurrentUserToken()!!)
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "splash_act goToAuthActivity")
                    if (userId == null) {
                        goToAuthActivity()
                    } else {
                        Log.e(TAG, "splash_act goToMainActivity")
                        Log.e(TAG, "Going to launch main activity ... 2")
                        startActivity<MainActivity>()
                    }
                }
            }
        }
    }

    private fun goToAuthActivity() {
        val transactions = arrayOf<Pair<View, String>>(
            Pair(binding.splashLogo, "logoView"), Pair(binding.splashTitle, "logoTitle")
        )
        val options = ActivityOptions.makeSceneTransitionAnimation(this, *transactions)
        val intent = Intent(this, AuthActivity::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e(TAG, "request code : $requestCode")
        Log.e(TAG, "result code : $requestCode")
        Log.e(TAG, "data : ${Gson().toJson(data)}")


        /* if (requestCode == MY_REQUEST_CODE) {
             if (resultCode != RESULT_OK) {

                 navigate()
             } else {
                 navigate()
             }
         }*/
    }


    private fun updateLanguageChanged() {
        Log.e(TAG, "UpdatageTranslation : UpdateLanguageTranslation")
        Log.e(TAG, "splash_act : updateLanguageChanged")
        lifecycleScope.launch {

//                Log.e("u id", "-->" + getCurrentUserId())
//                Log.e("userToken", "-->" + getCurrentUserToken())
//                userId = getCurrentUserId()!!
            var userToken = App.userPreferences.userToken.first()
            Log.e(TAG, "usertokenn $userToken")

            var locaLizationString = getLoalizationsStringList()
            Log.e(TAG, "splash_act_${"locaLizationString"} $locaLizationString")
            if (userToken.isNullOrEmpty()) {
                var consted = getLoalizations(this@SplashActivity, isUpdate = true)
                viewModessl.data.postValue(consted)
            } else {
                val res = try {
                    apolloClient(this@SplashActivity, userToken).query(
                        AttrTranslationQuery(
                            locaLizationString
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    Toast.makeText(this@SplashActivity, "${e.message}", Toast.LENGTH_LONG).show()

//                hideProgressView()
                    return@launch
                }
                if (res.hasErrors()) {
                    Log.e(TAG, "responsegetted : ${Gson().toJson(res.errors)}")
                } else {

                    res.data?.attrTranslation?.forEach {
                        if (it?.nameTranslated == "") {
                            Log.e(TAG, "TransaltionNw ${it.name}  ${it.nameTranslated}")
                        }
                    }
                    Log.e(
                        TAG,
                        "splash_act_${"res.data?.attrTranslation"} ${res.data?.attrTranslation}"
                    )
                    val consted = getLoalizations(this@SplashActivity, res.data?.attrTranslation)
                    Log.e(TAG, "splash_act_${"consted"} $consted")
//                  Log.e(":responsegetted", Gson().toJson( res.data))
                    val tarnslationJson = Gson().toJson(consted)/* val veryLongString = tarnslationJson.toString()
                     val maxLogSize = 1000
                     for (i in 0..veryLongString.length / maxLogSize) {
                         val start = i * maxLogSize
                         var end = (i + 1) * maxLogSize
                         end = if (end > veryLongString.length) veryLongString.length else end
                         Log.v("TAG", veryLongString.substring(start, end))
                     }*/
                    Log.e(TAG, "responsegetted  $tarnslationJson")
//                    viewModessl.data.postValue(consted)
                    val sharedPref = SharedPref(this@SplashActivity)
                    sharedPref.setAttrTranslater(consted)
                    updateLoalizationsConstString(this@SplashActivity, consted)
                    Log.e(TAG, "WalletTranslation ${consted.wallet}")
//                    AppStringConstant1.feed = consted.feed
//                    AppStringConstant1(this@SplashActivity).feed= consted.feed


//                    lifecycleScope.launch {
//                        updateLanguage(getCurrentUserId()!!, getCurrentUserToken()!!)
//                    }

                    //delay(1200)

                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.e(TAG, "splash_act 342")
                        if (getCurrentUserId() == null) {
                            goToAuthActivity()
                        } else {
                            Log.e(TAG, "Going to launch main activity ... 3")
                            startActivity<MainActivity>()
                        }
                    }
                }
            }
        }
    }


    private fun updateLanguage(id: String, token: String) {
        val deviceLocale = Locale.getDefault().language
        Log.e(TAG, "splash_act_$deviceLocale")
        lifecycleScope.launch(Dispatchers.Main) {

            when (val response = viewModel.updateLanguage(
                languageCode = deviceLocale, userid = id, token = token
            )) {
                is Resource.Success -> {
                    Log.e(TAG, "splash_act_${"LanguageUpdate Success"} ${response.message}")
                    updateLanguageChanged()
                    Log.e(TAG, "${"LanguageUpdate Success"} ${response.message}")
                }

                is Resource.Error -> {
                    Log.e(TAG, "${"LanguageUpdate Failed"} ${response.message}")
                    binding.root.snackbar("${"LanguageUpdate Failed"} ${response.message}")
                }

                else -> {

                }
            }
        }

        val sharedPref = SharedPref(applicationContext)
        sharedPref.setLanguage(true)
        sharedPref.setLanguageFromSettings(true)

//        lifecycleScope.launch(Dispatchers.Main) {
//            if (getCurrentUserId() == null) goToAuthActivity() else startActivity<MainActivity>()
//        }

    }

    private fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES
            )
            Log.e(TAG, "Package Name= ${context.applicationContext.packageName}")
            for (signature in packageInfo.signatures!!) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e(TAG, "MyKeyHash= $key")
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Name not found: $e1")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "No such an algorithm : $e")
        } catch (e: Exception) {
            Log.e(TAG, "Exception : $e")
        }
        return key
    }
}