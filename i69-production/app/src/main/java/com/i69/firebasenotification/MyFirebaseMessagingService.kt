package com.i69.firebasenotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.i69.data.config.Constants
import com.i69.data.remote.repository.UserUpdateRepository
import com.i69.BuildConfig
import com.i69.R
import com.i69.singleton.App
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.MainActivity.Companion.ACTION_NEW_NOTIFICATION
import com.i69.ui.screens.main.MainActivity.Companion.KEY_REMOTE_MSG
import com.i69.utils.ApiUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.random.Random


@DelicateCoroutinesApi
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit  var userUpdateRepository: UserUpdateRepository

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        if(this::userUpdateRepository.isInitialized) {
            sendRegistrationToServer(s)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) sendNotification(remoteMessage)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {

        Log.e(TAG, "sendRegistrationTokenToServer($token)")
        Log.e("sendTokenToServer", "($token)")
        token?.let {
            GlobalScope.launch {
                val userId = App.userPreferences.userId.first()
                val userToken = App.userPreferences.userToken.first()

                if (userId != null && userToken != null) {
                    userUpdateRepository.updateFirebasrToken(userId, token, userToken)
                }
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
//    private fun sendNotification(messageBody: RemoteMessage) {
//        /**
//         * Added by AM COIN DEDUCTION AND ADDED*/
//        Log.e("TAG_notification_data", "sendNotification: " + messageBody.data)
//        Log.e("TAG_notification_title", "sendNotification: " + messageBody.notification!!.title)
//        Log.e("TAG_notification_title", "sendNotification: " + messageBody)
//        if (messageBody.notification!!.title != null) {
//            val textTitle: String = messageBody.notification!!.title!!
//            MainActivity.notificationOpened = false
//            val intent = Intent(App.getAppContext(), MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//            if (JSONObject(messageBody.data.toString()).getJSONObject("data").has("title")) {
//                if (JSONObject(messageBody.data.toString()).getJSONObject("data").getString("title")
//                        .equals("Received Gift") ||
//                    JSONObject(messageBody.data.toString()).getJSONObject("data").getString("title")
//                        .equals("Sent message")
//                ) {
//                    intent.putExtra("isNotification", "yes")
//                    val intentsend = Intent()
//                    intentsend.putExtra("extra", textTitle)
//                    intentsend.action = "gift_Received"
//                    sendBroadcast(intentsend, "com.my.app.onMessageReceived")
//
////                    intent.putExtra("isNotification", "yes")
//                    val intentsend1 = Intent()
//                    intentsend1.putExtra("extra", textTitle)
//                    intentsend1.action = "gift_Received_1"
//                    sendBroadcast(intentsend1, "com.my.app.onMessageReceived")
//
//                }
//            }
//
//            if (textTitle.contains("Gifted coins deduction") || textTitle.contains("Gifted coins added")) {
//                intent.putExtra("isNotification", "yes")
//                val intentsend2 = Intent()
//                intentsend2.putExtra("extra", textTitle)
//                intentsend2.putExtra("coins",""+messageBody.data.toString())
//                intentsend2.action = "gift_Received"
//                sendBroadcast(intentsend2, "com.my.app.onMessageReceived")
//                Log.e("TAG", "sendNotification: " )
//
////                val intentsend1 = Intent()
////                intentsend1.putExtra("coins",""+messageBody.data.toString())
////                intentsend1.action = "gift_Received_2"
////                sendBroadcast(intentsend1, "com.my.app.onMessageReceived")
//            }
//            if (textTitle.contains("New Moment added")) {
//                intent.putExtra("isNotification", "yes")
//                val intentsend2 = Intent()
//                intentsend2.putExtra("extra", textTitle)
//                intentsend2.action = "moment_added"
//                sendBroadcast(intentsend2, "com.my.app.onMessageReceived")
//            }
//            if (textTitle == "Moment Liked" || textTitle == "Comment in moment" ||
//                textTitle == "Story liked" || textTitle == "Story Commented" ||
//                textTitle == "Gift received" || textTitle == "Welcome to the i69" ||
//                messageBody.notification!!.body!!.contains("offered you")
//            ) {
//                intent.putExtra("isNotification", "yes")
//            } else if (textTitle.contains("Gifted coins deduction") || textTitle.contains("Gifted coins added")) {
//                intent.putExtra("isNotification", "yes")
//                val intent = Intent()
//                intent.putExtra("extra", textTitle)
//                intent.putExtra("coins",""+messageBody.data.toString())
//                intent.action = "com.my.app.onMessageReceived"
//                intent.action = "gift_Received"
//                sendBroadcast(intent, "com.my.app.onMessageReceived")
//
////                val intentsend1 = Intent()
////                intentsend1.putExtra("coins",""+messageBody.data.toString())
////                intentsend1.action = "gift_Received_3"
////                sendBroadcast(intentsend1, "com.my.app.onMessageReceived")
//
//            } else {
//                try {
//                    val obj = JSONObject(messageBody.data.toString())
//                    intent.putExtra("isChatNotification", "yes")
//                    if (obj.length() != 0) {
//                        val obj1 = obj.getJSONObject("data")
//                        if (obj1.length() != 0 && obj1.has("roomID")) {
//                            intent.putExtra("roomIDs", obj1.getString("roomID"))
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            val intentsend1 = Intent()
//            intentsend1.action = "notification_received"
//            intentsend1.putExtra("notification_id",""+messageBody.data.toString())
//            sendBroadcast(intentsend1, "com.my.app.onMessageReceived")
//
//            Log.e("Sent_message", "onReceive 1 : ${Constants.INTENTACTION}" )
//            val notify_id = Random.nextInt()
//            intent.action = Constants.INTENTACTION
//            intent.putExtra("notify_id", notify_id)
//            intent.putExtra("extra", textTitle)
//            intent.putExtra("coins",""+messageBody.data.toString())
//
//            //Second Time Tune
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//
//            if (MainActivity.isAppInFront) {
//                val notiIntent = Intent(ACTION_NEW_NOTIFICATION)
//                notiIntent.setPackage(BuildConfig.APPLICATION_ID)
//                notiIntent.putExtra(KEY_REMOTE_MSG,messageBody)
//                sendBroadcast(notiIntent)
//                return
//            }
//            var pendingIntent: PendingIntent? = null
//            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//            } else {
//                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//            }
//            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            var mBuilder: NotificationCompat.Builder? = null
//            val title = try {
//                JSONObject(messageBody.data.toString()).getJSONObject("data")
//                    .getString("title")
//            } catch (e: Exception) {
//                "No title"
//            }
//            Log.e(TAG,"FirebaseService"+ "data ${messageBody.data} \n title $title")
//            if (JSONObject(messageBody.data.toString()).getJSONObject("data").has("title")) {
//                if (JSONObject(messageBody.data.toString()).getJSONObject("data").getString("title").equals("Received Gift")) {
//                    val url = BuildConfig.BASE_URL + JSONObject(messageBody.data.toString()).getJSONObject("data").getString("giftUrl")
//                    loadImageAndPostNotification(this,messageBody,url,textTitle,soundUri,pendingIntent)
//                } else if (JSONObject(messageBody.data.toString()).getJSONObject("data").getString("title").equals("Sent message")) {
//                    val url = BuildConfig.BASE_URL + JSONObject(messageBody.data.toString()).getJSONObject("data").getString("user_avatar")
//                    loadImageAndPostNotification(this,messageBody,url,textTitle,soundUri,pendingIntent)
//                }
//            } else {
//                if (messageBody.notification!!.icon != null) {
//                    Log.e("iconn", "" + messageBody.notification!!.icon)
//                    val url = messageBody.notification!!.icon!!
//                    loadImageAndPostNotification(this,messageBody,url,textTitle,soundUri,pendingIntent)
//                } else {
//                    mBuilder = NotificationCompat.Builder(this, getString(R.string.app_name))
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(textTitle)
//                        .setContentText(messageBody.notification!!.body)
//                        .setSound(soundUri)
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
//                        .setDefaults(Notification.DEFAULT_SOUND)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    notifyNotification(mBuilder, soundUri)
//                }
//            }
//        }
//    }

    private fun sendNotification(messageBody: RemoteMessage) {
        Log.e("TAG_notification_data", "sendNotification: " + messageBody.data)
        Log.e("TAG_notification_title", "sendNotification: " + messageBody.notification?.title)

        val textTitle: String = messageBody.notification?.title ?: return
        MainActivity.notificationOpened = false
        val intent = Intent(App.getAppContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val dataMap = messageBody.data

        if (dataMap.containsKey("title")) {
            val titleFromData = dataMap["title"]
            if (titleFromData == "Received Gift" || titleFromData == "Sent message") {
                intent.putExtra("isNotification", "yes")
                sendCustomBroadcast("gift_Received", textTitle)
                sendCustomBroadcast("gift_Received_1", textTitle)
            }
        }

        if (textTitle.contains("Gifted coins deduction") || textTitle.contains("Gifted coins added")) {
            intent.putExtra("isNotification", "yes")
            sendCustomBroadcast("gift_Received", textTitle, dataMap["coins"])
        }

        if (textTitle.contains("New Moment added")) {
            intent.putExtra("isNotification", "yes")
            sendCustomBroadcast("moment_added", textTitle)
        }

        if (textTitle in listOf("Moment Liked", "Comment in moment", "Story liked", "Story Commented", "Gift received", "Welcome to the i69")
            || messageBody.notification?.body?.contains("offered you") == true
        ) {
            intent.putExtra("isNotification", "yes")
        } else {
            dataMap["roomID"]?.let { roomId ->
                intent.putExtra("isChatNotification", "yes")
                intent.putExtra("roomIDs", roomId)
            }
        }

        sendCustomBroadcast("notification_received", textTitle, dataMap["notification_id"])

        Log.e("Sent_message", "onReceive 1 : ${Constants.INTENTACTION}")

        val notifyId = Random.nextInt()
        intent.apply {
            action = Constants.INTENTACTION
            putExtra("notify_id", notifyId)
            putExtra("extra", textTitle)
            putExtra("coins", dataMap.toString())
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        if (MainActivity.isAppInFront) {
            sendCustomBroadcast(ACTION_NEW_NOTIFICATION, messageBody.toString())
            return
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

//        val imageUrl = dataMap["giftUrl"]?.let { BuildConfig.BASE_URL + it }
//            ?: dataMap["user_avatar"]?.let { BuildConfig.BASE_URL + it }
//            ?: messageBody.notification?.icon

        val imageUrl = when {
            dataMap["giftUrl"]?.isNotEmpty() == true -> formatUrl(dataMap["giftUrl"]!!)
            dataMap["user_avatar"]?.isNotEmpty() == true -> formatUrl(dataMap["user_avatar"]!!)
            else -> messageBody.notification?.icon
        }

        if (imageUrl != null) {
            loadImageAndPostNotification(this, messageBody, imageUrl, textTitle, soundUri, pendingIntent)
        } else {
            val mBuilder = NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(textTitle)
                .setContentText(messageBody.notification?.body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notifyNotification(mBuilder, soundUri)
        }
    }

    /**
     * Utility function to send broadcasts
     */
    private fun sendCustomBroadcast(action: String, extra: String?, coins: String? = null) {
        val intent = Intent(action).apply {
            putExtra("extra", extra)
            coins?.let { putExtra("coins", it) }
        }
        sendBroadcast(intent, "com.my.app.onMessageReceived")
    }

    /**
     * Helper function to format the URL correctly based on S3 usage.
     */
    private fun formatUrl(url: String): String {
        return if (!BuildConfig.USE_S3) {
            if (url.startsWith(BuildConfig.BASE_URL)) url else "${BuildConfig.BASE_URL}${url}"
        } else {
            if (url.startsWith(ApiUtil.S3_URL)) url else ApiUtil.S3_URL + url
        }
    }

    private fun notifyNotification(mBuilder:NotificationCompat.Builder?,soundUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.app_name), name, importance)
            channel.description = description
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(soundUri, attributes)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
            // notificationId is a unique int for each notification that you must define
            if (mBuilder != null) {
                Log.e(TAG,"FirebaseService"+"mBuilder not null")
                notificationManager.notify(Random.nextInt(), mBuilder.build())
            }
        } else {
            val notificationManager = NotificationManagerCompat.from(this)
            // notificationId is a unique int for each notification that you must define
            if (mBuilder != null) {
                Log.e(TAG,"FirebaseService"+ "mBuilder not null")
                notificationManager.notify(Random.nextInt(), mBuilder.build())
            }
        }
    }

    private fun loadImageAndPostNotification(context: Context, messageBody:RemoteMessage, url:String, textTitle:String, soundUri:Uri, pendingIntent:PendingIntent) {
        GlobalScope.launch(Dispatchers.IO) {
            var image : Bitmap? = null
            val inputStream: InputStream
            try {
                val urlNew = URL(url)
                val connection: HttpURLConnection = urlNew.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                inputStream = connection.inputStream
                image = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                ContextCompat.getDrawable(context,R.drawable.ic_launcher)?.let {
                    image = drawableToBitmap(it)
                }
            }
            val mBuilder : NotificationCompat.Builder = NotificationCompat.Builder(context, getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(textTitle)
                .setContentText(messageBody.notification!!.body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            if (image != null) {
                mBuilder.setLargeIcon(image)
            }

            notifyNotification(mBuilder, soundUri)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}
