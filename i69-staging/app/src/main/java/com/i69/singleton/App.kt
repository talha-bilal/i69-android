package com.i69.singleton

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import com.i69.BuildConfig
import com.i69.data.config.Constants.PAYPAL_CLIENT_ID
import com.i69.data.preferences.UserPreferences
import com.i69.data.remote.repository.UserUpdateRepository
import com.i69.ui.screens.main.coins.PurchaseFragment
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    private var TAG: String = App::class.java.simpleName
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")

    companion object {
        lateinit var userPreferences: UserPreferences
        private lateinit var mInstance: App
        fun getAppContext(): Context = mInstance.applicationContext
        private var TAG: String = App::class.java.simpleName

        fun updateFirebaseToken(userUpdateRepository: UserUpdateRepository) {
            Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG,"Fetching FCM registration token failed")
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Log.e(TAG,"FirebaseToken: $token")
                token?.let {
                    GlobalScope.launch {
                        val userId = userPreferences.userId.first()
                        val userToken = userPreferences.userToken.first()
                        if (userId != null && userToken != null) {
                            val response =
                                userUpdateRepository.updateFirebasrToken(userId, token, userToken)
                            Log.e(TAG,"Response: ${Gson().toJson(response)}")
                        }
                    }
                }
            })
        }

        fun initStripe(stripePublickey: String, purchaseFragment: PurchaseFragment) {
            Log.e(TAG,"StripePublicKey : $stripePublickey")
            PaymentConfiguration.init(mInstance.applicationContext, stripePublickey)
        }
    }

    @Inject
    lateinit var userUpdateRepository: UserUpdateRepository


    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        mInstance = this
        userPreferences = UserPreferences(this.applicationContext.dataStore)
        FacebookSdk.fullyInitialize()


        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .build()
        )

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        initFirebase()
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            AppLifecycleListener(
                userPreferences,
                userUpdateRepository
            )
        )
        initPayPal()
    }

    private fun initFirebase() {
        updateFirebaseToken(userUpdateRepository)

    }

    private fun initPayPal() {
        val config = CheckoutConfig(
            application = this,
            clientId = PAYPAL_CLIENT_ID,
            environment = Environment.LIVE,
//            environment = Environment.SANDBOX,
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay",
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PayPalCheckout.setConfig(config)
        }
    }
}