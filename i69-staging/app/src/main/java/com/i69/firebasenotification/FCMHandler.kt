package com.i69.firebasenotification

import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException


class FCMHandler {
    companion object {
        open  fun enableFCM() {
            // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }


        open  fun disableFCM() {
            // Disable auto init
            FirebaseMessaging.getInstance().isAutoInitEnabled = false
            Thread {
                try {
//                FirebaseMessaging.getInstance().deleteInstanceId()
                    FirebaseMessaging.getInstance().deleteToken()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }

    }
}