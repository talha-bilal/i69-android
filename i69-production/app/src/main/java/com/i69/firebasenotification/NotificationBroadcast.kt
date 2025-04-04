package com.i69.firebasenotification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.messenger.chat.MessengerNewChatFragment
import com.i69.ui.screens.main.messenger.list.MessengerListFragment
import com.i69.utils.LogUtil
import org.json.JSONObject

class NotificationBroadcast(var activity: Fragment?) : BroadcastReceiver() {
    private var TAG: String = NotificationBroadcast::class.java.simpleName

    companion object {
        var isBroadcastReceived = false
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (!isBroadcastReceived) {
            try {
                val bundle = intent.extras
                if (bundle != null) {
                    if (bundle.containsKey("data")) {
                        val dataObject = bundle.getString("data")?.let { JSONObject(it) }
                        Log.e(TAG, "onReceive: Message Received $dataObject")
                    }
                    if (bundle.containsKey("type")) {
                        val typeString = bundle.getString("type")
                        Log.e(TAG, "onReceive: Type Received: $typeString")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "onReceive: Error : $e")
            }

            getAction(intent)

        }
    }

    private fun getAction(intent: Intent) {
        val chatBundle = intent.extras
        if (chatBundle != null) {
            for (key in chatBundle.keySet()) {
                val value = chatBundle[key]
                Log.e(TAG, "Key: $key, Value: $value")
            }
            LogUtil.debug("NotificationData : : : ${intent.extras}")
            if ((intent.hasExtra("isChatNotification") && intent.getStringExtra("isChatNotification") != null)
                && intent.getStringExtra("isChatNotification") == "yes"
            ) {
                LogUtil.debug("Here 1")
                if ((intent.hasExtra("roomIDs") && intent.getStringExtra("roomIDs") != null)) {
                    LogUtil.debug("Here 2")
                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            LogUtil.debug("Here 3")
                            val rID = intent.getStringExtra("roomIDs")
                            val bundle = Bundle()
                            bundle.putString("roomIDNotify", rID)
                            LogUtil.debug("Activity : : : $activity")
                            when (activity) {
                                is MessengerListFragment -> {
                                    LogUtil.debug("Here 4")
                                    val messageListFragment = activity as MessengerListFragment
                                    soundPlay(messageListFragment)
                                    clearNotification(messageListFragment)
                                    isBroadcastReceived = true
                                    messageListFragment.onNewMessage(
                                        intent.getStringExtra("roomIDs").toString()
                                    )

                                }

                                is MessengerNewChatFragment -> {
                                    LogUtil.debug("Here 5")
                                    soundPlay(activity as MessengerNewChatFragment)
                                    clearNotification(activity as MessengerNewChatFragment)
                                    //(activity as MessengerNewChatFragment).setupData(false)
                                }

                                is BaseFragment<*> -> {
                                    LogUtil.debug("Here 6")
                                    soundPlay(activity as BaseFragment<*>)
                                    getMainActivity()?.pref?.edit()?.putString("newChat", "true")
                                        ?.putString("roomIDS", rID)?.apply()
                                }
                            }
                        }
                    }, 500)
                }
            } else if ((intent.hasExtra("isNotification") && intent.getStringExtra("isNotification") != null)) {
                LogUtil.debug("Here isNotification")
                Handler(Looper.getMainLooper()).postDelayed({
                    kotlin.run {
                        val bundle = Bundle()
                        bundle.putString("ShowNotification", "true")
                    }
                }, 500)
            } else if (intent.hasExtra(MainActivity.ARGS_SCREEN) && intent.getStringExtra(
                    MainActivity.ARGS_SCREEN
                ) != null
            ) {
                if (intent.hasExtra(MainActivity.ARGS_SENDER_ID) && intent.getStringExtra(
                        MainActivity.ARGS_SENDER_ID
                    ) != null
                ) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            val senderId = intent.getStringExtra(MainActivity.ARGS_SENDER_ID)
                            onNotificationClick(senderId!!)
                        }
                    }, 500);
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            if (activity is MessengerListFragment) {
                                soundPlay(activity as MessengerListFragment)
                                clearNotification(activity as MessengerListFragment)
                                //(activity as MessengerListFragment).updateList(false)
                            } else if (activity is MessengerNewChatFragment) {
                                soundPlay(activity as MessengerNewChatFragment)
                                clearNotification(activity as MessengerNewChatFragment)
                            }
                        }
                    }, 500)
                }
            }
        }
    }

    private fun soundPlay(fragment: Fragment) {
        try {
            if (fragment.isAdded) {
                val notification: Uri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(fragment.requireActivity(), notification)
                r.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onNotificationClick(senderId: String) {
//        val msgPreviewModel: MessagePreviewModel? = QbDialogHolder.getChatDialogById(senderId)
//        msgPreviewModel?.let {
//            viewModel?.setSelectedMessagePreview(it)
//            navController.navigate(R.id.globalUserToChatAction)
//        }
    }

    private fun clearNotification(fragment: Fragment) {
        if (fragment.isAdded) {
            val notificationManager = fragment.requireActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(NOTIFY_ID)
            notificationManager.cancelAll()
        }
    }
}
