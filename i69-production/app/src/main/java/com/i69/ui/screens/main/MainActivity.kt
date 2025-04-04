package com.i69.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.location.Location
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import android.text.format.DateUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.size
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import com.i69.AttrTranslationQuery
import com.i69.BuildConfig
import com.i69.ChatRoomSubscription
import com.i69.GetAllRoomsQuery
import com.i69.GetAllUserParticularMomentsQuery
import com.i69.GetAllUserStoriesQuery
import com.i69.GetBroadcastMessageQuery
import com.i69.GetFirstMessageQuery
import com.i69.GetNotificationQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.applocalization.getLoalizations
import com.i69.applocalization.getLoalizationsStringList
import com.i69.applocalization.updateLoalizationsConstString
import com.i69.data.models.User
import com.i69.data.models.market.Category
import com.i69.data.remote.repository.UserUpdateRepository
import com.i69.databinding.ActivityMainBinding
import com.i69.singleton.App
import com.i69.ui.base.BaseActivity
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.coins.PurchaseFragment
import com.i69.ui.screens.main.market.store.ProductFragment
import com.i69.ui.screens.main.market.store.StoreFragment
import com.i69.ui.screens.main.messenger.chat.MessengerNewChatFragment
import com.i69.ui.screens.main.messenger.chat.contact.ContactActivity
import com.i69.ui.screens.main.moment.PlayUserStoryDialogFragment
import com.i69.ui.screens.main.moment.UserStoryDetailFragment
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.viewModels.MarketPlacesViewModel
import com.i69.ui.viewModels.UserViewModel
import com.i69.ui.views.CircleTransform
import com.i69.ui.views.MyBottomNavigation
import com.i69.utils.AnimationTypes
import com.i69.utils.ApiUtil
import com.i69.utils.BackButtonBehaviour
import com.i69.utils.SharedPref
import com.i69.utils.TempConstants
import com.i69.utils.apolloClient
import com.i69.utils.closeNavigationDrawer
import com.i69.utils.disableNavigationDrawer
import com.i69.utils.drawerSwitchState
import com.i69.utils.enableNavigationDrawer
import com.i69.utils.findFileExtension
import com.i69.utils.hasLocationPermission
import com.i69.utils.isCurrentLanguageFrench
import com.i69.utils.isImageFile
import com.i69.utils.isLocationEnabled
import com.i69.utils.isVideoFile
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.i69.utils.promptEnableGPS
import com.i69.utils.setupWithNavController
import com.i69.utils.showDialog
import com.i69.utils.snackbar
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.PaymentMethodsActivityStarter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var TAG: String = MainActivity::class.java.simpleName
    private val mViewModel: UserViewModel by viewModels()

    private var profileMenuItem: MenuItem? = null

    lateinit var navController: NavController
    private var navController2: LiveData<NavController>? = null

    @Inject
    lateinit var userUpdateRepository: UserUpdateRepository

    @Inject
    lateinit var sharedPref: SharedPref

    private var mUser: User? = null
    private var userId: String? = null
    private var userToken: String? = null
    private lateinit var job: Job
    private val viewModel: UserViewModel by viewModels()
    private var bottomNav1: MyBottomNavigation? = null
    var userprofile: String = ""
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var drawerSelectedItemId = R.id.nav_search_graph

    lateinit var pref: SharedPreferences
    private var totoalUnreadBadge = 0

    private var locationDialog: Dialog? = null

    private var stripe: Stripe? = null

    private val viewModessl: AppStringConstantViewModel by viewModels()
    private var remoteMessage: RemoteMessage? = null
    var width = 0
    var size = 0

    private var isLocationEnableShoot = false
    private val marketPlacesViewModel: MarketPlacesViewModel by viewModels()
    private val categoryList = mutableListOf<Category>()

    val permissionReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            showLoader()
            if (hasLocationPermission(this, locPermissions)) {
                val locationService =
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                locationService.lastLocation.addOnSuccessListener { location: Location? ->
                    val lat: Double? = location?.latitude
                    val lon: Double? = location?.longitude
                    hideProgressView()
                    if (lat != null && lon != null) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val res = mViewModel.updateLocation(
                                userId = userId!!, location = arrayOf(lat, lon), token = userToken!!
                            )
                            Log.e(TAG, "UpdateLocation: ${res.message}")
                        }
                    }
                }.addOnFailureListener { hideProgressView() }

            } else hideProgressView()
        }

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(inflater)

    private fun updateLanguageTranslation() {

        lifecycleScope.launch {
            var appString: AppStringConstant? = sharedPref.attrTranslater()
            if (appString == null) {
                appString = getLoalizations(this@MainActivity, isUpdate = true)
            }
            try {
                binding.invalidateAll()
                AppStringConstant(this@MainActivity).feed = appString.feed
                Log.e(TAG, "callTranslation ==> ${appString.interested_in}")

                Log.e(TAG, "callTranslation ==> ${AppStringConstant1.interested_in}")
                viewModessl.data.postValue(appString)

                Log.e(
                    TAG,
                    "callTranslation1 ==> ${AppStringConstant(this@MainActivity).interested_in}"
                )

                updateLanguageChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setupTheme(savedInstanceState: Bundle?) {
        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            window?.decorView?.setOnApplyWindowInsetsListener { view, insets ->
                val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
                getMainActivity()?.binding?.bottomNavigation?.isGone = insetsCompat.isVisible(
                    WindowInsetsCompat.Type.ime()
                )
                view.onApplyWindowInsets(insets)
            }
        }
        updateLanguageTranslation()
        mainActivity = this
        pref = PreferenceManager.getDefaultSharedPreferences(mainActivity!!)
        val lang = Locale.getDefault().language
        if (pref.getString("language", null) == null) {
            lifecycleScope.launch(Dispatchers.Main) {
                pref.edit()?.putString("language", lang)?.apply()
                async(Dispatchers.IO) {
                    viewModel.updateLanguage(
                        languageCode = if (lang == "pt") "pt_pt" else lang,
                        userid = getCurrentUserId()!!,
                        token = getCurrentUserToken()!!
                    )
                }.await()
            }
        }

        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            this.let { marketPlacesViewModel.getCategories() }
        }
        marketPlacesViewModel.getAllCategoriesResponse.observe(this) { data ->
            data?.forEach {
                categoryList.add(Category(it.categoryId, it.categoryName, it.parentCategoryId))
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setViewModel(mViewModel, binding)
        if (savedInstanceState == null) {
            setupBottomNav()
        }
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
        setupNavigation()
        notificationOpened = true
        updateFirebaseToken(userUpdateRepository)

        lifecycleScope.launch(Dispatchers.IO) {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            Log.e(TAG, "UserId : $userId!!")
            Log.e(TAG, "UserToken : $userToken!!")
            getBroadcastMessageBadge()
            getWelcomeMessageBadge()
            updateChatBadge()
            loadUser()
            handleNotificationClick()
        }

        checkAppUpdate()
    }

    fun loadUser() {
        val user = mViewModel.getCurrentUser(userId!!, token = userToken!!, true)
        lifecycleScope.launch(Dispatchers.Main) {
            user.observe(this@MainActivity) { user ->
                Log.e(TAG, "User $user")
                user?.let {
                    try {
                        mUser = it
                        mUser?.id = "$userId"
                        Log.e(TAG, "loadUser: ")
                        updateNavItem(mUser?.avatarIndex?.let { it1 ->
                            mUser?.avatarPhotos?.get(it1)?.url?.replace(
                                "${BuildConfig.BASE_URL}media/", "${BuildConfig.BASE_URL}media/"
                            ).toString()
                        })
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }
                }
            }
        }
    }

    private fun handleNotificationClick() {
        if ((intent.hasExtra("isNotification") && intent.getStringExtra("isNotification") != null)) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home_graph
            try {
                val dataValues = JSONObject(intent.getStringExtra("notificationData"))
                navigateByType(dataValues, dataValues.getInt("pk"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if ((intent.hasExtra("isChatNotification") && intent.getStringExtra("isChatNotification") != null)) {
            if ((intent.hasExtra("roomIDs") && intent.getStringExtra("roomIDs") != null)) {
                val rID = intent.getStringExtra("roomIDs")

                pref.edit().putString("roomIDNotify", "true").putString("roomID", rID).apply()
                binding.bottomNavigation.selectedItemId = R.id.nav_chat_graph
            }
        }
    }

    private fun exitOnError() {
        lifecycleScope.launch(Dispatchers.Main) {
            App.userPreferences.clear()
            val intent = Intent(this@MainActivity, SplashActivity::class.java)
            startActivity(intent)
            finishAffinity()
            return@launch
        }
    }

    private fun navigateByType(dataValues: JSONObject, pkId: Int) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(this@MainActivity, userToken!!).query(
                        GetNotificationQuery(pkId)
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all stories${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "apolloResponse GetNotificationQuery  ${res.hasErrors()}")
                Log.e(TAG, "GetNotificationQuery --> ${Gson().toJson(res)}")
                if (res.hasErrors()) {
                    if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        return@repeatOnLifecycle exitOnError()
                    }
                }
                if (dataValues.has("notification_type")) {
                    when (dataValues.getString("notification_type")) {
                        "LIKE", "CMNT", "MMSHARED" -> {
                            val momentid = dataValues.get("momentId").toString()
                            getMoments(momentid)
                        }

                        "STSHARED", "STLIKE", "STCMNT" -> {
                            val storyId = if (dataValues.has("storyID")) {
                                dataValues.get("storyID").toString()
                            } else {
                                dataValues.get("storyId").toString()
                            }
                            getStories(storyId)
                        }

                        "PROFILEVISIT" -> {
                            val followUserId = dataValues.getString("visited_user_id")
                            if (userId == followUserId) {
                                binding.bottomNavigation.selectedItemId =
                                    R.id.nav_user_profile_graph
                            } else {
                                openOtherProfileScreen(followUserId)
                            }
                        }

                        "USERFOLLOW" -> {
                            val followUserId = dataValues.getString("followerID")
                            if (userId == followUserId) {
                                binding.bottomNavigation.selectedItemId =
                                    R.id.nav_user_profile_graph
                            } else {
                                openOtherProfileScreen(followUserId)
                            }
                        }

                        "SNDMSG" -> {
                            binding.bottomNavigation.selectedItemId = R.id.nav_user_profile_graph
                            updateChatBadge()
                        }
                    }
                } else if (dataValues.has("data")) {
                    val jsonObject = dataValues.getJSONObject("data")
                    if (jsonObject.has("notification_type")) {
                        if (jsonObject.getString("notification_type") == "SM") {
                            if (dataValues.has("user_id")) {
                                val id = dataValues.getString("user_id")
                                if (userId == id) {
                                    binding.bottomNavigation.selectedItemId =
                                        R.id.nav_user_profile_graph
                                } else {
                                    openOtherProfileScreen(id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openOtherProfileScreen(followUserId: String) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            val bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", followUserId)
            currentFragment.findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
        }
    }

    private fun getMoments(ids: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(this@MainActivity, userToken!!).query(
                        GetAllUserParticularMomentsQuery(
                            width, size, ids
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all moments ${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "getMoments--> ${Gson().toJson(res)}")
                if (res.hasErrors()) {
                    if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        // error("User doesn't exist")
                        return@repeatOnLifecycle exitOnError()
                    }
                }

                val allmoments = res.data?.allUserMoments!!.edges
                allmoments.indices.forEach { i ->
                    if (ids == allmoments[i]!!.node!!.pk.toString()) {
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        val currentFragment =
                            navHostFragment.childFragmentManager.primaryNavigationFragment
                        if (currentFragment != null) {
                            val bundle = Bundle().apply {
                                putString("momentID", allmoments[i]?.node!!.pk!!.toString())
                                putString("filesUrl", allmoments[i]?.node!!.file!!)
                                putString("Likes", allmoments[i]?.node!!.like!!.toString())
                                putString("Comments", allmoments[i]?.node!!.comment!!.toString())
                                val gson = Gson()
                                putString(
                                    "Desc",
                                    gson.toJson(allmoments[i]?.node!!.momentDescriptionPaginated)
                                )
                                putString("fullnames", allmoments[i]?.node!!.user!!.fullName)
                                if (allmoments[i]!!.node!!.user!!.gender != null) {
                                    putString("gender", allmoments[i]!!.node!!.user!!.gender!!.name)
                                } else {
                                    putString("gender", null)
                                }
                                putString(
                                    "momentuserID",
                                    allmoments[i]?.node!!.user!!.id.toString()
                                )
                            }
                            currentFragment.findNavController()
                                .navigate(R.id.momentsAddCommentFragment, bundle)
                        }
                        return@forEach
                    }
                }
            }
        }
    }

    private fun getStories(storyId: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(this@MainActivity, userToken!!).query(
                        GetAllUserStoriesQuery(
                            100, "", storyId, ""
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all stories ${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "apolloResponse allUserStories stories ${res.hasErrors()}")
                Log.e(TAG, "GetUserStories --> ${Gson().toJson(res)}")
                if (res.hasErrors()) {
                    try {
                        if (res.errors?.get(0)?.nonStandardFields!!["code"]?.toString() == "InvalidOrExpiredToken") {
                            // error("User doesn't exist")
                            return@repeatOnLifecycle exitOnError()
                        }
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
                val allUserStories = res.data?.allUserStories!!.edges
                allUserStories.indices.forEach { i ->
                    if (storyId == allUserStories[i]!!.node!!.pk?.toString()) {
                        val userStory = allUserStories[i]
                        val formatter =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                        Log.e(TAG, "filee ${userStory?.node!!.fileType} ${userStory.node.file}")
                        val url = if (!BuildConfig.USE_S3) {
                            if (userStory.node.file
                                    .startsWith(BuildConfig.BASE_URL)
                            ) userStory.node.file
                            else "${BuildConfig.BASE_URL}${userStory.node.file}"
                        } else if (userStory.node.file
                                .startsWith(ApiUtil.S3_URL)
                        ) userStory.node.file
                        else ApiUtil.S3_URL.plus(userStory.node.file)
                        var userurl = ""
                        userurl =
                            if (userStory.node.user!!.avatar != null && userStory.node.user.avatar!!.url != null) {
                                userStory.node.user.avatar.url!!
                            } else {
                                ""
                            }
                        val username = userStory.node.user.fullName
                        val UserID = userId
                        val objectId = userStory.node.pk

                        val momentTime = try {
                            var text = userStory.node.createdDate.toString()
                            text = text.replace("T", " ").substring(0, text.indexOf("."))
                            formatter.parse(text)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Date()
                        }

                        val times = DateUtils.getRelativeTimeSpanString(
                            momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                        )
                        if (userStory.node.fileType.equals("video")) {
                            val dialog = PlayUserStoryDialogFragment(object :
                                UserStoryDetailFragment.DeleteCallback {
                                override fun deleteCallback(objectId: Int) {

                                }
                            })
                            val b = Bundle()
                            b.putString("Uid", UserID)
                            b.putString("url", url)
                            b.putString("userurl", userurl)
                            b.putString("username", username)
                            b.putString("times", times.toString())
                            b.putString("token", userToken)
                            b.putInt("objectID", objectId!!)
                            dialog.arguments = b
                            dialog.show(supportFragmentManager, "${AppStringConstant1.story}")
                        } else {
                            val dialog = UserStoryDetailFragment(null)
                            val b = Bundle()
                            b.putString("Uid", UserID)
                            b.putString("url", url)
                            b.putString("userurl", userurl)
                            b.putString("username", username)
                            b.putString("times", times.toString())
                            b.putString("token", userToken)
                            b.putInt("objectID", objectId!!)
                            dialog.arguments = b
                            dialog.show(supportFragmentManager, "${AppStringConstant1.story}")
                        }
                        return@forEach
                    }
                }
            }
        }
    }

    fun updateLanguageChanged() {
        lifecycleScope.launch {
            val userToken = App.userPreferences.userToken.first()
            Log.e(TAG, "usertokenn $userToken")

            val locaLizationString = getLoalizationsStringList()

            if (userToken.isNullOrEmpty()) {
                val consted = getLoalizations(this@MainActivity, isUpdate = true)
                viewModessl.data.postValue(consted)
            } else {
                val res = try {
                    apolloClient(this@MainActivity, userToken).query(
                        AttrTranslationQuery(
                            locaLizationString
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_LONG).show()
                    return@launch
                }
                if (res.hasErrors()) {
                } else {
                    val consted = getLoalizations(this@MainActivity, res.data?.attrTranslation)

                    Log.e(TAG, "updateLanguageChanged: ${Gson().toJson(consted)}")
                    viewModessl.data.postValue(consted)
                    val sharedPref = SharedPref(this@MainActivity)
                    sharedPref.setAttrTranslater(consted)
                    updateLoalizationsConstString(this@MainActivity, consted)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {

            data?.let {
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)

                val paymentMethod = result?.paymentMethod
//                paymentMethodId = paymentMethod?.id.toString()
//                paymentSession?.handlePaymentData(requestCode, resultCode, it)
            }
        } else {
            if (stripe != null) {
                stripe?.onPaymentResult(
                    requestCode,
                    data,
                    object : ApiResultCallback<PaymentIntentResult> {
                        override fun onSuccess(result: PaymentIntentResult) {
                            val paymentIntent = result.intent

                            when (paymentIntent.status) {
                                StripeIntent.Status.Succeeded -> {
                                    Log.e(TAG, "Payment Success")

                                    val navHostFragment =
                                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                                    val currentFragment =
                                        navHostFragment.childFragmentManager.primaryNavigationFragment
                                    if (currentFragment != null && currentFragment is PurchaseFragment) {
                                        val purchaseFragment = currentFragment
                                        purchaseFragment.paymentIntentComplete(paymentIntent)
                                    }
                                }

                                StripeIntent.Status.RequiresPaymentMethod -> {
                                    hideProgressView()
                                    paymentIntent.lastPaymentError?.message?.let {
                                        binding.root.snackbar(
                                            it
                                        )
                                    }
                                    Log.e(
                                        TAG,
                                        "Payment Failed " + paymentIntent.lastPaymentError?.message
                                    )
                                }

                                else -> {
                                    hideProgressView()
                                    showDialog("${paymentIntent.status}")

                                    Log.e(TAG, "Payment status unknown " + paymentIntent.status)
                                }
                            }
                        }

                        override fun onError(e: Exception) {
                            hideProgressView()
                            showDialog("${e.localizedMessage}")
                            Log.e(TAG, "Payment Error " + e.localizedMessage)
                        }
                    })
            }
        }
    }

    fun setStripePayMentIntent(

        striped: Stripe
    ) {

        stripe = striped

//        purchaseFragment.setPaymentLauncherForStripe(paymentLauncher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun getmsgsubscriptionlistner() {
        job = lifecycleScope.launch {
            viewModel.newMessageFlow.collect { message ->
                message?.let { newMessage ->
                    if (userId != message.userId.id) {
                        if (mContextTemp != null) {
                            sendNotification(message)
                        }
                        updateChatBadge()
                    }
                }
            }
        }
    }

    private fun updateFirebaseToken(userUpdateRepository: UserUpdateRepository) {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("App", "Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e(TAG, "FirebaseToken : $token")
            token?.let {
                GlobalScope.launch {
                    val userId = App.userPreferences.userId.first()
                    val userToken = App.userPreferences.userToken.first()

                    if (userId != null && userToken != null) {
                        userUpdateRepository.updateFirebasrToken(userId, token, userToken)
                        getmsgsubscriptionlistner()
                    }
                }
            }
        })
    }

    private fun sendNotification(message: ChatRoomSubscription.Message) {
        notificationOpened = false
        val intent = Intent(App.getAppContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
        val id2 = message.roomId.id

        intent.putExtra("isChatNotification", "yes")
        intent.putExtra("roomIDs", id2)

        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val soundUri: Uri

        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //  soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.iphone_ringtone);

        Log.e(TAG, "" + BuildConfig.BASE_URL + message.userId.avatarPhotos?.get(0)!!.url)
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon_buy_chat_coins)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSound(soundUri).setAutoCancel(true)
                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.app_name)
            val importance = IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.app_name), name, importance)
            channel.description = description
            val attributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM).build()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(soundUri, attributes)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(this)

        val myContentView = RemoteViews(packageName, R.layout.layout_notification_content_expanded)
        val myExpandedContentView =
            RemoteViews(packageName, R.layout.layout_notification_content_expanded)
        myContentView.setTextViewText(R.id.content_title, message.userId.fullName)
        myExpandedContentView.setTextViewText(R.id.content_title, message.userId.fullName)
        if (message.content.contains("media/chat_files")) {
            val ext = message.content.findFileExtension()
            val stringResId = if (ext.isImageFile()) {
                R.string.photo
            } else if (ext.isVideoFile()) {
                R.string.video
            } else {
                R.string.file
            }
            val icon = if (ext.isImageFile()) {
                R.drawable.ic_photo
            } else if (ext.isVideoFile()) {
                R.drawable.ic_video
            } else {
                R.drawable.ic_baseline_attach_file_24
            }
            myContentView.setTextViewText(R.id.content_message, getString(stringResId))
            myContentView.setImageViewResource(R.id.myimage, icon)
            myContentView.setViewVisibility(R.id.myimage, VISIBLE)

            myExpandedContentView.setTextViewText(R.id.content_message, getString(stringResId))
            myExpandedContentView.setImageViewResource(R.id.myimage, icon)
            myExpandedContentView.setViewVisibility(R.id.myimage, VISIBLE)

//            mBuilder.setContentText(message.userId.fullName+" : "+message.content)
        } else {
            myContentView.setTextViewText(R.id.content_message, message.content)
            myContentView.setViewVisibility(R.id.myimage, GONE)

            myExpandedContentView.setTextViewText(R.id.content_message, message.content)
            myExpandedContentView.setViewVisibility(R.id.myimage, GONE)
//            mBuilder.setContentText(message.userId.fullName+" : "+message.content)
        }

        if (!message.userId.avatarPhotos.isNullOrEmpty()) {
            if (message.userId.avatarPhotos[0] != null && message.userId.avatarPhotos[0]!!.url != null) {
                loadImage(this,
                    BuildConfig.BASE_URL + message.userId.avatarPhotos.get(0)!!.url,
                    { bitmap ->
                        myContentView.setImageViewBitmap(R.id.iv_profile, bitmap)
                        mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        mBuilder.setCustomContentView(myContentView)
                        notificationManager.notify(Random.nextInt(), mBuilder.build())
                    },
                    { drawable ->
                        if (drawable != null) {
                            myContentView.setImageViewResource(
                                R.id.iv_profile, R.drawable.ic_default_user
                            )
                            myExpandedContentView.setImageViewResource(
                                R.id.iv_profile, R.drawable.ic_default_user
                            )
                        }
                        mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        mBuilder.setCustomContentView(myContentView)
                        notificationManager.notify(Random.nextInt(), mBuilder.build())
                    })
            } else {
                myContentView.setImageViewResource(R.id.iv_profile, R.drawable.login_logo)
                myExpandedContentView.setImageViewResource(R.id.iv_profile, R.drawable.login_logo)
                mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                myExpandedContentView.setViewVisibility(R.id.iv_profile, GONE)
                myContentView.setViewVisibility(R.id.iv_profile, GONE)
                mBuilder.setCustomContentView(myContentView)
                notificationManager.notify(Random.nextInt(), mBuilder.build())
            }
        } else {
            myContentView.setImageViewResource(R.id.iv_profile, R.drawable.ic_default_user)
            myExpandedContentView.setImageViewResource(R.id.iv_profile, R.drawable.ic_default_user)
            mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())

            mBuilder.setCustomContentView(myContentView)
            notificationManager.notify(Random.nextInt(), mBuilder.build())
        }
    }

    private fun sendNotification(title: String, message: String) {
        notificationOpened = false
        val intent = Intent(App.getAppContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
        val id2 = "fdsfsd"

        intent.putExtra("isChatNotification", "yes")
        intent.putExtra("roomIDs", id2)


        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val soundUri: Uri

        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(soundUri)

                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND).setPriority(PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.app_name)
            val importance = IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.app_name), name, importance)
            channel.description = description
            val attributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM).build()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(soundUri, attributes)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(this)

        if (message.contains("media/chat_files")) {
            val ext = message.findFileExtension()
            val stringResId = if (ext.isImageFile()) {
                R.string.photo
            } else if (ext.isVideoFile()) {
                R.string.video
            } else {
                R.string.file
            }
            val icon = if (ext.isImageFile()) {
                R.drawable.ic_photo
            } else if (ext.isVideoFile()) {
                R.drawable.ic_video
            } else {
                R.drawable.ic_baseline_attach_file_24
            }
            val myContentView =
                RemoteViews(packageName, R.layout.layout_notification_content_expanded)
            myContentView.setImageViewResource(R.id.myimage, icon)
            myContentView.setTextViewText(R.id.content_title, title)
            myContentView.setTextViewText(R.id.content_message, getString(stringResId))
//            mBuilder.setContentText(message.userId.fullName+" : "+message.content)
            mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            mBuilder.setCustomContentView(myContentView)
            mBuilder.setCustomBigContentView(myContentView)
        } else {
            mBuilder.setContentText("UserName : $message")
        }
        notificationManager.notify(Random.nextInt(), mBuilder.build())
    }

    private fun getWelcomeMessageBadge() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val resFirstMessage = try {
                    apolloClient(this@MainActivity, userToken!!).query(GetFirstMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception getFirstMessage${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    //hideProgressView()
                    return@repeatOnLifecycle
                }

                if (!resFirstMessage.hasErrors()) {
                    if (resFirstMessage.data?.firstmessage != null) {
                        //addBadge(0)
                        if (totoalUnreadBadge == 0) {
                            totoalUnreadBadge = resFirstMessage.data?.firstmessage?.unread!!.toInt()
                        } else {
                            totoalUnreadBadge += resFirstMessage.data?.firstmessage?.unread!!.toInt()
                        }
                        addUnreadBadge()
                    }
                }
            }
        }
    }

    private fun getBroadcastMessageBadge() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val resBroadcast = try {
                    apolloClient(this@MainActivity, userToken!!).query(GetBroadcastMessageQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception getBroadcastMessage${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }

                val allRoom = resBroadcast.data?.broadcast

                if (allRoom != null) {
                    if (totoalUnreadBadge == 0 && allRoom.unread!!.toInt() > 0) {
                        totoalUnreadBadge = 1//allRoom?.unread!!.toInt()
                    } else {
                        if (totoalUnreadBadge > 0 && allRoom.unread!!.toInt() > 0) {
                            totoalUnreadBadge + 1//data!!.node!!.unread!!.toInt()
                        } else totoalUnreadBadge
                    }
                    addUnreadBadge()
                }
            }
        }
    }

    fun updateChatBadge() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(this@MainActivity, userToken!!).query(GetAllRoomsQuery(20))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception all room API${e.message}")
                    binding.root.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }

                val allRoom = res.data?.rooms?.edges
                Log.e(TAG, "updateChatBadge: " + res.data?.rooms?.toString())

                if (allRoom.isNullOrEmpty()) {
                    return@repeatOnLifecycle
                }

                totoalUnreadBadge = 0
                allRoom.indices.forEach { i ->
                    val data = allRoom[i]
                    if (totoalUnreadBadge == 0 && data!!.node!!.unread!!.toInt() > 0) {
                        totoalUnreadBadge = 1//data!!.node!!.unread!!.toInt()
                    } else {
                        if (data!!.node!!.unread!!.toInt() > 0) {
                            totoalUnreadBadge + 1//data!!.node!!.unread!!.toInt()
                        }
                    }
                }
                addUnreadBadge()
            }
        }
    }

    private fun addUnreadBadge() {
        try {
            binding.bottomNavigation.addBadge(totoalUnreadBadge)
        } catch (e: Exception) {
            Log.e(TAG, "exceptionInAddBadge: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun setupClickListeners() {
        registerReceiver(
            notiBroadcastReceiver,
            IntentFilter(ACTION_NEW_NOTIFICATION),
            Context.RECEIVER_NOT_EXPORTED
        )
        binding.clNotification.setOnClickListener {
            remoteMessage?.let { messageBody ->
                try {
                    val obj = JSONObject(messageBody.data.toString())
                    if (obj.length() != 0) {
                        val dataValues =
                            JSONObject(messageBody.data.toString()).getJSONObject("data")
                        if (dataValues.length() != 0) {
                            if (dataValues.has("roomID")) {
                                val rID = dataValues.getString("roomID")
                                pref.edit().putString("roomIDNotify", "true")
                                    .putString("roomID", rID).apply()

                                binding.bottomNavigation.selectedItemId = R.id.nav_chat_graph
                            } else {
                                navigateByType(dataValues, dataValues.getInt("pk"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.clNotification.visibility = GONE
            handler.removeCallbacks(runnable)
        }

        binding.ivDismissNotification.setOnClickListener {
            binding.clNotification.visibility = GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::job.isInitialized) {
            job.cancel()
        }
        handler.removeCallbacks(runnable)
        unregisterReceiver(notiBroadcastReceiver)
    }

    private fun setupNavigation() {
        updateLocation()
        binding.mainNavView.getHeaderView(0).findViewById<View>(R.id.btnHeaderClose)
            .setOnClickListener {
                if (binding.drawerLayout.isOpen) {
                    binding.drawerLayout.close()
                } else {
                    navController.popBackStack()
                }
            }

        binding.mainNavView.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.nav_search_graph
        binding.mainNavView.setNavigationItemSelectedListener {
            val itemId = it.itemId
            val category = categoryList.find { it.categoryId == itemId.toString() }
            if (category != null) {
                openMarketPlaceWithMenu(category)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                when (it.itemId) {
                    R.id.nav_search_graph -> openSearchScreen()
                    R.id.nav_coinpurchase_graph -> openCoinScreen()
                    R.id.nav_coinplan_graph -> openPlanScreen()
                    R.id.nav_item_contact -> openContactusScreen()
                    R.id.nav_invite_friend -> openInviteFriendScreen()
                    // market place nav items
                    R.id.nav_search_women -> openMarketPlaceWithMenu("Women's clothing")
                    R.id.nav_item_watches -> openMarketPlaceWithMenu("Watches")
                    R.id.nav_item_bags -> openMarketPlaceWithMenu("Bag's")
                    R.id.nav_item_men -> openMarketPlaceWithMenu("Men's Clothing")
                    R.id.nav_item_school -> openMarketPlaceWithMenu("School and Office Suppliers")
                    R.id.nav_item_sports -> openMarketPlaceWithMenu("Sports and Outdoors")
                    R.id.nav_item_house_hold -> openMarketPlaceWithMenu("HouseHold Appliances")
                    R.id.nav_item_jewelry -> openMarketPlaceWithMenu("jwelery and Accessories")
                    R.id.nav_item_automobile -> openMarketPlaceWithMenu("Automobile")
                    R.id.nav_privacy_graph -> {
                        openPrivacyScreen()
                    }

                    1001 -> {
                        true
                    }

                    R.id.nav_setting_graph -> openSettingsScreen()
                }
            }, 200)

            binding.drawerLayout.closeNavigationDrawer()
            return@setNavigationItemSelectedListener true
        }
    }

    private fun openInviteFriendScreen() {
        startActivity(Intent(this@MainActivity, ContactActivity::class.java).apply {
            putExtra("isInviteFriendsLink", true)
        })
    }

    private fun openContactusScreen() {
        Log.e(TAG, "splasact_main\",\"h_act_main" + "openContactusScreen")
        pref.edit()?.putString("typeview", "privacy")?.apply()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        currentFragment?.findNavController()?.navigate(R.id.actionGoToContactFragment)
    }

    private fun observeNotification() {
        if ((intent.hasExtra("isNotification") && intent.getStringExtra("isNotification") != null)) {
            pref.edit().putString("ShowNotification", "true").apply()

            binding.bottomNavigation.selectedItemId = R.id.nav_home_graph
        } else if ((intent.hasExtra("isChatNotification") && intent.getStringExtra("isChatNotification") != null)) {
            Log.e(TAG, "notii" + "--> " + "22-->" + intent.getStringExtra("roomIDs"))

            if ((intent.hasExtra("roomIDs") && intent.getStringExtra("roomIDs") != null)) {
                val rID = intent.getStringExtra("roomIDs")


                pref.edit().putString("roomIDNotify", "true").putString("roomID", rID).apply()

                binding.bottomNavigation.selectedItemId = R.id.nav_chat_graph
            }
        } else if (intent.hasExtra(ARGS_SCREEN) && intent.getStringExtra(ARGS_SCREEN) != null) {
            if (intent.hasExtra(ARGS_SENDER_ID) && intent.getStringExtra(ARGS_SENDER_ID) != null) {
                val senderId = intent.getStringExtra(ARGS_SENDER_ID)
                onNotificationClick(senderId!!)
            } else {
                openMessagesScreen()
            }
        }
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private val MY_REQUEST_CODE = 101

    private fun checkAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                )
            ) startUpdate(appUpdateInfo)
        }.addOnFailureListener {
            Log.e(TAG, "splash_act : 167")
        }
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo, AppUpdateType.FLEXIBLE, this, MY_REQUEST_CODE
        )
    }

    private fun onNotificationClick(senderId: String) {
    }

    fun drawerSwitchState() {
        binding.drawerLayout.drawerSwitchState()
    }

    fun enableNavigationDrawer() {
        binding.drawerLayout.enableNavigationDrawer()
    }

    fun disableNavigationDrawer() {
        binding.drawerLayout.disableNavigationDrawer()
    }

    fun reloadNavigationMenu() {
        binding.mainNavView.menu.clear()
        binding.mainNavView.inflateMenu(R.menu.activity_main_drawer)
    }

    fun reloadNavigationMarketMenu() {
        binding.mainNavView.menu.clear()
        val menu: Menu = binding.mainNavView.menu
        if (categoryList.size > 0)
            for (category in categoryList)
                menu.add(Menu.NONE, category.categoryId!!.toInt(), Menu.NONE, category.categoryName)
                    .setIcon(R.drawable.ic_default_user) // Optional: Add an icon
    }

    fun setDrawerItemCheckedUnchecked(id: Int?) {

        if (id != null) binding.mainNavView.setCheckedItem(id)
        else {
            val size = binding.mainNavView.menu.size
            for (i in 0 until size) binding.mainNavView.menu.getItem(i).isChecked = false
        }
    }

    private fun updateNavItem(userAvatar: Any?) {
        userprofile = userAvatar.toString()

        val url = if (!BuildConfig.USE_S3) {
            if (userprofile.startsWith(BuildConfig.BASE_URL)) userprofile
            else "${BuildConfig.BASE_URL}${userprofile}"
        } else if (userprofile.startsWith(ApiUtil.S3_URL)) userprofile
        else ApiUtil.S3_URL.plus(userprofile)

        Log.e(TAG, "updateNavItem: ${userprofile}")
        binding.bottomNavigation.loadImage(
            url, R.id.nav_user_profile_graph, R.drawable.ic_default_user
        )
        if (profileMenuItem != null) {
            val primaryColor = ContextCompat.getColor(this, R.color.colorPrimary)
            Glide.with(this).load(url).apply(
                RequestOptions.bitmapTransform(
                    CircleTransform(
                        borderWidth = 2, borderColor = primaryColor
                    )
                )
            ).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable, transition: Transition<in Drawable>?
                ) {
                    profileMenuItem?.icon = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        }
    }

    private fun updateLocation() {
        if (!isLocationEnabled(this@MainActivity)) {
            locationDialog()
        }
        if (hasLocationPermission(applicationContext, locPermissions)) {
            val locationService = LocationServices.getFusedLocationProviderClient(this@MainActivity)
            locationService.lastLocation.addOnSuccessListener { location: Location? ->
                val lat: Double? = location?.latitude
                val lon: Double? = location?.longitude
                if (lat != null && lon != null) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val res = mViewModel.updateLocation(
                            userId = userId!!, location = arrayOf(lat, lon), token = userToken!!
                        )

                        if (res.message.equals("User doesn't exist")) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                userPreferences.clear()
                                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                        }
                        Log.e(TAG, "" + res.message)
                    }
                }
            }
        } else {
            permissionReqLauncher.launch(locPermissions)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private var mContextTemp: Context? = null
    override fun onPause() {
        super.onPause()
        mContextTemp = null
        isAppInFront = false

    }

    override fun onResume() {
        super.onResume()
        isAppInFront = true
        mContext = this@MainActivity
        mContextTemp = this@MainActivity
        if (!notificationOpened) {
            notificationOpened = true
            observeNotification()
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.my.app.onMessageReceived")
        intentFilter.addAction("gift_Received_1")
        intentFilter.addAction("notification_received")
        registerReceiver(
            broadCastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED
        )

        if (isLocationEnableShoot) {
            isLocationEnableShoot = false
            updateLocation()
        }
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (intent!!.action!!.equals("gift_Received_1")) {
                updateChatBadge()
            }
            if (intent.action!!.equals("notification_received")) {
                if (dialog != null) {
                    val notificationId = intent.getStringExtra("notification_id")
                    if (dialog != null && dialog!!.isVisible && notificationId != null) dialog!!.addNotification(
                        notificationId
                    )
                }
            }
        }
    }

    private fun openSearchScreen() {
        binding.bottomNavigation.selectedItemId = R.id.nav_search_graph
    }

    private fun openUserMoments() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home_graph
    }

    fun openMarketPlace() {
        binding.bottomNavigation.selectedItemId = R.id.nav_market_graph
    }

    private fun openMarketPlaceWithMenu(cat: String) {

        val bundle = Bundle()
        bundle.putString("category", cat)
    }

    private fun openMarketPlaceWithMenu(category: Category) {
        val navHostFragment =
            supportFragmentManager.findFragmentByTag("bottomNavigation#2") as? NavHostFragment
        val storeFragment =
            navHostFragment?.childFragmentManager?.fragments?.firstOrNull { it is StoreFragment } as? StoreFragment

        storeFragment?.onMenuCategoryItemClicked(category)
    }


    private fun openMessagesScreen() {
        binding.bottomNavigation.selectedItemId = R.id.nav_chat_graph
    }

    private fun openProfileScreen() {
        binding.bottomNavigation.selectedItemId = R.id.nav_user_profile_graph
    }

    private fun openPrivacyScreen() {
        pref.edit()?.putString("typeview", "privacy")?.apply()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        currentFragment?.findNavController()?.navigate(R.id.actionGoToPrivacyFragment)
    }

    private fun openPlanScreen() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        currentFragment?.findNavController()?.navigate(R.id.action_global_plan)
    }

    private fun openCoinScreen() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        currentFragment?.findNavController()?.navigate(R.id.action_global_purchase)
    }

    private fun openSettingsScreen() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            currentFragment.findNavController().navigate(R.id.action_global_setting)
        }
    }

    companion object {
        var mContext: Context? = null
        const val ARGS_SCREEN = "screen"
        const val ARGS_SENDER_ID = "sender_id"

        var notificationOpened = false
        private var viewModel: UserViewModel? = null
        private var binding: ActivityMainBinding? = null

        var mainActivity: MainActivity? = null

        var isAppInFront = false
        const val ACTION_NEW_NOTIFICATION = BuildConfig.APPLICATION_ID + ".new_notification"
        const val KEY_REMOTE_MSG = "remoteMessage"

        @JvmName("getMainActivity1")
        fun getMainActivity(): MainActivity? {
            return mainActivity
        }

        fun setViewModel(updatedViewModel: UserViewModel, updatedBinding: ActivityMainBinding) {
            viewModel = updatedViewModel
            binding = updatedBinding
        }
    }

    private val locPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        setupBottomNav()
    }

    @SuppressLint("RestrictedApi")
    private fun setupBottomNav() {

        val graphIds = listOf(
            R.navigation.nav_search_graph,
            R.navigation.nav_home_graph,
            R.navigation.nav_market_graph,
            R.navigation.nav_add_new_moment_graph,
            R.navigation.nav_chat_graph,
            R.navigation.nav_user_profile_graph
        )
        val bottomNav = binding.bottomNavigation

        bottomNav.setOnItemSelectedListener { item ->
            Log.e(TAG, "onNavigationItemSelected: ${item.itemId}")
            when (item.itemId) {
                R.id.nav_user_profile_graph -> {
                    openProfileScreen()
                    true
                }

                else -> false
            }
        }
        bottomNav1 = bottomNav

        binding.bottomNavigation.itemIconTintList = null

        val controller = bottomNav.setupWithNavController(
            fragmentManager = supportFragmentManager,
            navGraphIds = graphIds,
            backButtonBehaviour = BackButtonBehaviour.POP_HOST_FRAGMENT,
            containerId = R.id.nav_host_fragment,
            firstItemId = R.id.nav_search_graph, // Must be the same as bottomNavSelectedItemId
            intent = this@MainActivity.intent
        )
        controller.observe(this) {
//            setupActionBarWithNavController(it)
        }
        navController2 = controller

        val navigationItemView = bottomNav.getChildAt(0) as BottomNavigationMenuView
        val navigationItemView2 = navigationItemView.getChildAt(5) as BottomNavigationItemView
        val displayMetrics = resources.displayMetrics

        navigationItemView2.setIconSize(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50f, displayMetrics
            ).toInt()
        )

        if (TempConstants.LanguageChanged) {
            if (TempConstants.isFromSettings) {
                openUserMoments()
            } else {
                openSettingsScreen()
            }
        }
    }

    override fun onBackPressed() {
        try {
            exitConfirmation()
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }


    private fun locationDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_location, null)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)
        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        locationDialog = builder.create()

        yesButton.setOnClickListener {
            isLocationEnableShoot = true
            locationDialog?.dismiss()
            promptEnableGPS(this@MainActivity)
        }

        locationDialog?.show()
    }


    private fun exitConfirmation() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)

        var title = "${AppStringConstant(this@MainActivity).are_you_sure_you_want_to_exit_I69}"

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment

        if (currentFragment != null && currentFragment is MessengerNewChatFragment) {
            super.onBackPressed()
            return
            title = "${AppStringConstant(this@MainActivity).do_you_want_to_leave_this_page}"
        }
        if (currentFragment != null && currentFragment is ProductFragment) {
            super.onBackPressed()
            return
        }

        headerTitle.text = title
        noButton.text = "${AppStringConstant(this@MainActivity).no}"
        yesButton.text = "${AppStringConstant(this@MainActivity).yes}"

        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            super.onBackPressed()
        }

        dialog.show()
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController2?.value?.navigateUp()!! || super.onSupportNavigateUp()
    }

    val notiBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == ACTION_NEW_NOTIFICATION) {
                    remoteMessage = it.getParcelableExtra(KEY_REMOTE_MSG) as RemoteMessage?
                    remoteMessage?.let { messageBody ->
                        val textTitle: String = messageBody.notification!!.title!!
                        val url = if (JSONObject(messageBody.data.toString()).getJSONObject("data")
                                .has("title")
                        ) {
                            if (JSONObject(messageBody.data.toString()).getJSONObject("data")
                                    .getString("title").equals("Received Gift")
                            ) {
                                BuildConfig.BASE_URL + JSONObject(messageBody.data.toString()).getJSONObject(
                                    "data"
                                ).getString("giftUrl")
                            } else if (JSONObject(messageBody.data.toString()).getJSONObject("data")
                                    .getString("title").equals("Sent message")
                            ) {
                                BuildConfig.BASE_URL + JSONObject(messageBody.data.toString()).getJSONObject(
                                    "data"
                                ).getString("user_avatar")
                            } else {
                                null
                            }
                        } else {
                            if (messageBody.notification!!.icon != null) {
                                messageBody.notification!!.icon
                            } else {
                                null
                            }
                        }
                        loadImageAndPostNotification(messageBody, url, textTitle)
                    }
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        binding.clNotification.visibility = GONE
    }

    private fun loadImageAndPostNotification(
        messageBody: RemoteMessage, url: String?, textTitle: String
    ) {
        if (!url.isNullOrEmpty()) {
            binding.ivImage.visibility = VISIBLE
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE)
                .optionalCircleCrop().placeholder(R.drawable.ic_default_user).into(binding.ivImage)
        } else {
            binding.ivImage.visibility = GONE
        }
        if (isCurrentLanguageFrench()) {
            binding.tvTitle.text =
                JSONObject(messageBody.data.toString()).getJSONObject("data").getString("title_fr")
            binding.tvBody.text =
                JSONObject(messageBody.data.toString()).getJSONObject("data").getString("body_fr")
            binding.clNotification.visibility = VISIBLE
            handler.postDelayed(runnable, 7 * 1000)
        } else {
            binding.tvTitle.text = textTitle
            binding.tvBody.text = messageBody.notification!!.body
            binding.clNotification.visibility = VISIBLE
            handler.postDelayed(runnable, 7 * 1000)
        }
    }

    fun showLoader() {
        showProgressView()
    }

    var filePath: File? = null
    var description: String? = null
    var checked: Boolean = false
    var isShare: Boolean = false
    var isShareLater: Boolean = false
    var publishAt: String = ""

    fun isValidTime(
        year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int
    ): Boolean {
        val now = Calendar.getInstance().time
        val selectedTime = Calendar.getInstance()
        selectedTime.set(year, monthOfYear, dayOfMonth)
        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedTime.set(Calendar.MINUTE, minute)

        return now.before(selectedTime.time)
    }

    fun openUserAllMoments(file: File, description: String, checked: Boolean) {
        filePath = file
        this.description = description
        this.checked = checked
        isShare = true
        binding.bottomNavigation.selectedItemId = R.id.nav_home_graph
    }

    fun openUserAllMoments(file: File, description: String, checked: Boolean, publishAt: String) {
        filePath = file
        this.description = description
        this.checked = checked
        isShare = false
        isShareLater = true
        this.publishAt = publishAt
        binding.bottomNavigation.selectedItemId = R.id.nav_home_graph

    }

    var dialog: NotificationDialogFragment? = null

    fun notificationDialog(
        dialog: NotificationDialogFragment, childFragmentManager: FragmentManager, s: String
    ) {
        this.dialog = dialog
        this.dialog!!.show(childFragmentManager, s)
    }

    fun isUserAllowedToPostStory() = mUser?.canPostStory == true

    fun isUserAllowedToPostMoment() = mUser?.canPostMoment == true

    fun isUserHasMomentQuota(): Boolean {
        return mUser?.hasMomentQuota == true
    }

    fun isUserHasStoryQuota(): Boolean {
        return mUser?.hasStoryQuota == true
    }

    fun isUserAllowedToScheduleStory() = mUser?.canScheduleStory == true

    fun isUserAllowedToScheduleMoment() = mUser?.canScheduleMoment == true

    fun isUserHasSubscription(): Boolean {
        return mUser?.userSubscription?.isActive == true && mUser?.userSubscription?.isCancelled == false
    }

    fun getRequiredCoins(coinsNeededFor: String, coinsNeeded: (Int) -> Unit) {
        viewModel.getCoinSettingsByRegion(userToken.toString(), coinsNeededFor).observe(this) {
            Log.e(TAG, "getRequiredCoins: ${it.coinsNeeded}")
            coinsNeeded.invoke(it.coinsNeeded)
        }
    }
}