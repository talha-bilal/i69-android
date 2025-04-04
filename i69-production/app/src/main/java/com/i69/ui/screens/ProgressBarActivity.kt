package com.i69.ui.screens

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.AttrTranslationQuery
import com.i69.R
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.applocalization.getLoalizations
import com.i69.applocalization.getLoalizationsStringList
import com.i69.applocalization.updateLoalizationsConstString
import com.i69.singleton.App.Companion.userPreferences
import com.i69.ui.screens.main.MainActivity
import com.i69.utils.SharedPref
import com.i69.utils.TempConstants
import com.i69.utils.apolloClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProgressBarActivity : AppCompatActivity() {

    private var TAG: String = ProgressBarActivity::class.java.simpleName
    private val viewModessl: AppStringConstantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progressbar)

        val background = findViewById<ConstraintLayout>(R.id.ctProgress)
        if (TempConstants.isFromSettings) {
            background.setBackgroundColor(ContextCompat.getColor(this, R.color.container_color))
        }
        Log.e("CommingInProgressBar", "CommingInProgressBar")

        updateLanguageChanged()

//        CoroutineScope(Dispatchers.Main).launch {
//            delay(500)
//            startActivity(Intent(this@ProgressBarActivity, MainActivity::class.java))
//            finish()
//        }

    }


    fun updateLanguageChanged() {


        lifecycleScope.launch() {

//                Log.e("u id", "-->" + getCurrentUserId())
//                Log.e("userToken", "-->" + getCurrentUserToken())
//                userId = getCurrentUserId()!!
            var userToken = userPreferences.userToken.first()
            Log.e(TAG,"usertokenn $userToken")

            Log.e("callProgressBarActivity", "callProgressBarActivity")
            var locaLizationString = getLoalizationsStringList()

            if (userToken.isNullOrEmpty()) {
                var consted =
                    getLoalizations(this@ProgressBarActivity, isUpdate = true)
                viewModessl.data.postValue(consted)
            } else {
                val res = try {
                    apolloClient(this@ProgressBarActivity, userToken!!).query(
                        AttrTranslationQuery(
                            locaLizationString
                        )
                    )
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG,"apolloResponse ${e.message}")
                    Toast.makeText(this@ProgressBarActivity, " ${e.message}", Toast.LENGTH_LONG)
                        .show()

//                hideProgressView()
                    return@launch
                }
                if (res.hasErrors()) {

                    Log.e("responsegetted", Gson().toJson(res.errors))
                } else {


                    var consted =
                        getLoalizations(this@ProgressBarActivity, res.data?.attrTranslation)
                    updateLoalizationsConstString(this@ProgressBarActivity, consted)
//                  Log.e(":responsegetted", Gson().toJson( res.data))
                    Log.e("responsegetted", Gson().toJson(consted))
//                    viewModessl.data.postValue(consted)
                    val sharedPref = SharedPref(this@ProgressBarActivity)
                    sharedPref.setAttrTranslater(consted)


                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        startActivity(Intent(this@ProgressBarActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }


//            if (res?.hasErrors() == false) {
////                                views.snackbar("You bought ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} successfully!")
//                Toast.makeText(
//                    requireContext(),
//                    context?.resources?.getString(R.string.you_bought) + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + context?.resources?.getString(
//                        R.string.successfully
//                    ),
//                    Toast.LENGTH_LONG
//                ).show()
//
//                //fireGiftBuyNotificationforreceiver(gift.id,giftUserid!!)
//
//            }
//            if (res!!.hasErrors()) {
////                                views.snackbar(""+ res.errors!![0].message)
//                Toast.makeText(
//                    requireContext(),
//                    "" + res.errors!![0].message,
//                    Toast.LENGTH_LONG
//                ).show()
//
//            }
//            Log.e("apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}")
        }

    }

}