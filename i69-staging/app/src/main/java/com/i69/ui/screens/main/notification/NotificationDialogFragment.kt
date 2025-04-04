package com.i69.ui.screens.main.notification

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.i69.BuildConfig
import com.i69.GetAllNotificationQuery
import com.i69.GetAllUserParticularMomentsQuery
import com.i69.GetAllUserStoriesQuery
import com.i69.GetNotificationCountQuery
import com.i69.GetNotificationQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.DialogNotificationsBinding
import com.i69.singleton.App
import com.i69.ui.adapters.NotificationsAdapter
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.moment.PlayUserStoryDialogFragment
import com.i69.ui.screens.main.moment.UserStoryDetailFragment
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.utils.AnimationTypes
import com.i69.utils.ApiUtil
import com.i69.utils.apolloClient
import com.i69.utils.navigate
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class NotificationDialogFragment(
    var userToken: String?,
    var Counters: MaterialTextView?,
    var userId: String?,
    var bell: ImageView?
) : DialogFragment(), NotificationsAdapter.NotificationListener {

    private lateinit var binding: DialogNotificationsBinding
    private lateinit var adapter: NotificationsAdapter
    var endCursor: String = ""
    var hasNextPage: Boolean = false
    var navController: NavController? = null
    var allnotification: MutableList<GetAllNotificationQuery.Edge?> = mutableListOf()
    var layoutManager: LinearLayoutManager? = null
    var width = 0
    var size = 0
    var isLoadingNotifications = false
    private var TAG: String = NotificationDialogFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogNotificationsBinding.inflate(inflater, container, false)
        binding.btnCloseNotifications.setOnClickListener { dismiss() }
        binding.txtNotificationTitle.text = AppStringConstant1.notificatins
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        navController = findNavController()
        allnotification = mutableListOf()
        isLoadingNotifications = false
        adapter = NotificationsAdapter(
            requireActivity(),
            this@NotificationDialogFragment,
            allnotification
        )
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvNotifications.layoutManager = layoutManager
        getAllNotifications()
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (hasNextPage) {
                binding.rvNotifications.let {
                    if (it.bottom - (binding.scrollView.height + binding.scrollView.scrollY) == 0 && !isLoadingNotifications) {
                        isLoadingNotifications = true
                        getAllNotificationsNext(10, endCursor)
                    }
                }
            }
        })
    }

    private fun getAllNotifications() {
        lifecycleScope.launchWhenResumed {
            /* val query = GetAllNotificationQuery(10, "")
             val res2 = apolloClient(
                 requireContext(),
                 userToken!!
             ).query(query).execute() */
            try {
                val query = GetAllNotificationQuery(10, "")
                val res = apolloClient(
                    requireContext(), userToken!!
                ).query(query).execute()
                if (res.hasErrors() && res.errors != null && res.errors?.isNotEmpty() == true) {
                    Log.e(TAG,"goinghasError --> " + res.errors)
                    if (res.errors!![0].nonStandardFields != null && res.errors!![0].nonStandardFields!!["code"] != null && res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        // error("User doesn't exist")
                        if (activity != null) {
                            App.userPreferences.clear()
                            //App.userPreferences.saveUserIdToken("","","")
                            if (MainActivity.mContext != null) {
                                val intent =
                                    Intent(MainActivity.mContext, SplashActivity::class.java)
                                startActivity(intent)
                                Log.e(TAG,"ddd1dddwwds -->" + res.errors!![0].nonStandardFields!!["code"])
                                requireActivity().finishAffinity()
                            }
                        }
                    }
                } else {
//                    val allNotification = res.data?.notifications!!.edges
                    allnotification.clear()
                    allnotification.addAll(res.data?.notifications!!.edges)
                    if (allnotification.isNotEmpty()) {
                        endCursor = res.data?.notifications!!.pageInfo.endCursor!!
                        hasNextPage = res.data?.notifications!!.pageInfo.hasNextPage

                        binding.noData.visibility = View.GONE
                        binding.rvNotifications.visibility = View.VISIBLE/*val allUserMomentsFirst: ArrayList<GetAllNotificationQuery.Edge> = ArrayList()
                        allnotification.indices.forEach { i ->
                            allUserMomentsFirst.add(allnotification[i]!!)
                        }*/

                        //Log.e("addDataInAdapter", "-->" + allUserMomentsFirst.size)
//                            adapter.addAll(allUserMomentsFirst)
                        //adapter.updateList(allUserMomentsFirst)

                        binding.rvNotifications.adapter = adapter
                        if (binding.rvNotifications.itemDecorationCount == 0) {
                            binding.rvNotifications.addItemDecoration(object :
                                RecyclerView.ItemDecoration() {
                                override fun getItemOffsets(
                                    outRect: Rect,
                                    view: View,
                                    parent: RecyclerView,
                                    state: RecyclerView.State
                                ) {
                                    outRect.top = 20
                                    outRect.bottom = 10
                                    outRect.left = 20
                                    outRect.right = 20
                                }
                            })
                        }
                        getNotificationIndex()
                    } else {
                        Log.e(TAG,"NotificationSize --> ${allnotification.size}")
//
                        binding.noData.visibility = View.VISIBLE
                        binding.rvNotifications.visibility = View.GONE
                    }
                }
            } catch (e: ApolloException) {
                FirebaseCrashlytics.getInstance().log(e.message.toString())
                Log.e(TAG,"apolloResponse ${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            } catch (e: Exception) {
                Log.e(TAG,"runTimeException --> ${e.message}")
                FirebaseCrashlytics.getInstance().log(e.message.toString())
            }
        }
    }

    private fun getAllNotificationsNext(i: Int, endCursors: String) {
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllNotificationQuery(
                        i, endCursors
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            if (res.hasErrors() && res.errors?.isNotEmpty() == true) {
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    // error("User doesn't exist")
                    lifecycleScope.launch(Dispatchers.Main) {
                        App.userPreferences.clear()
                        //App.userPreferences.saveUserIdToken("","","")
                        if (activity != null) {
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
//            val allNotification = res.data?.notifications!!.edges
            //allnotification.clear()
            allnotification.addAll(res.data?.notifications!!.edges)
            adapter.notifyDataSetChanged()
            endCursor = res.data?.notifications!!.pageInfo.endCursor!!
            hasNextPage = res.data?.notifications!!.pageInfo.hasNextPage
            if (binding.rvNotifications.itemDecorationCount == 0) {
                binding.rvNotifications.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.top = 25
                    }
                })
            }
            if (allnotification.isNotEmpty()) {
                Log.e(TAG,"apolloResponse: ${allnotification[0]?.node!!.id}")
                Log.e(TAG,"apolloResponse: ${allnotification[0]?.node!!.createdDate}")
            }
            isLoadingNotifications = false
        }
    }

    private fun getNotificationIndex() {
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(GetNotificationCountQuery())
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse Exception NotificationIndex${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            Log.e(TAG,"apolloResponse NotificationIndex ${res.hasErrors()}")
            if (res.hasErrors()) {
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    // error("User doesn't exist")
                    lifecycleScope.launch(Dispatchers.Main) {
                        App.userPreferences.clear()
                        //App.userPreferences.saveUserIdToken("","","")
                        if (activity != null) {
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
            val notificationCount = res.data?.unseenCount
            if (notificationCount == null || notificationCount == 0) {
                Counters?.visibility = View.GONE
                bell?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_bell
                    )
                )
            } else {
                Counters?.visibility = View.VISIBLE
//                Counters.text = "" + notificationCount
                if (notificationCount > 10) {
                    Counters?.text = "9+"
                } else {
                    Counters?.text = "$notificationCount"
                }
                bell?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_bell
                    )
                )
            }
        }
    }

    private fun getMoments(ids: String, position: Int) {
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserParticularMomentsQuery(
                        width, size, ids
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse Exception all moments ${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            if (res.hasErrors()) {
                Log.e(TAG,"getMoments: Error --> ${res.errors!![0].nonStandardFields!!["code"]}")
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    // error("User doesn't exist")
                    lifecycleScope.launch(Dispatchers.Main) {
                        App.userPreferences.clear()
                        if (activity != null) {
                            //App.userPreferences.saveUserIdToken("","","")
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
            val allmoments = res.data?.allUserMoments!!.edges
            allmoments.indices.forEach { i ->
                if (ids == allmoments[i]!!.node!!.pk.toString()) {
                    allnotification.removeAt(position)
                    adapter.notifyDataSetChanged()
                    val bundle = Bundle().apply {
                        putString("momentID", allmoments[i]?.node!!.pk!!.toString())
                        putString("filesUrl", allmoments[i]?.node!!.file!!)
                        putString("Likes", allmoments[i]?.node!!.like!!.toString())
                        putString("Comments", allmoments[i]?.node!!.comment!!.toString())
                        val gson = Gson()
                        putString(
                            "Desc", gson.toJson(allmoments[i]?.node!!.momentDescriptionPaginated)
                        )
                        putString("fullnames", allmoments[i]?.node!!.user!!.fullName)
                        if (allmoments[i]!!.node!!.user!!.gender != null) {
                            putString("gender", allmoments[i]!!.node!!.user!!.gender!!.name)
                        } else {
                            putString("gender", null)
                        }
                        putString("momentuserID", allmoments[i]?.node!!.user!!.id.toString())
                    }
                    navController!!.navigate(R.id.momentsAddCommentFragment, bundle)
                    return@forEach
                }
            }
        }
    }

    private fun getStories(pkid: String, storyId: String, position: Int) {
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserStoriesQuery(
                        100, "", pkid, storyId
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse Exception all stories ${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            Log.e(TAG,"apolloResponse allUserStories stories ${res.hasErrors()}")
            if (res.hasErrors()) {
//                Log.e("ddddddww", "-->" + res.errors!!.get(0).nonStandardFields!!["code"])
                try {
                    if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                        // error("User doesn't exist")
                        lifecycleScope.launch(Dispatchers.Main) {
                            App.userPreferences.clear()
                            if (activity != null) {
                                //App.userPreferences.saveUserIdToken("","","")
                                val intent = Intent(activity, SplashActivity::class.java)
                                startActivity(intent)
                                requireActivity().finishAffinity()
                            }
                        }
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
            val allUserStories = res.data?.allUserStories!!.edges
            allUserStories.indices.forEach { i ->
                if (pkid == allUserStories[i]!!.node!!.pk.toString()) {
                    allnotification.removeAt(position)
                    adapter.notifyDataSetChanged()
                    val userStory = allUserStories[i]
                    val formatter =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                    Log.e(TAG,"filee ${userStory?.node!!.fileType} ${userStory.node.file}")
                    val url = if (!BuildConfig.USE_S3) {
                        if (userStory.node.file.toString()
                                .startsWith(BuildConfig.BASE_URL)
                        ) userStory.node.file.toString()
                        else "${BuildConfig.BASE_URL}${userStory.node.file}"
                    } else if (userStory.node.file.toString()
                            .startsWith(ApiUtil.S3_URL)
                    ) userStory.node.file.toString()
                    else ApiUtil.S3_URL.plus(userStory.node.file.toString())
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
                    var text = userStory.node.createdDate.toString()
                    text = text.replace("T", " ").substring(0, text.indexOf("."))
                    val momentTime = formatter.parse(text)
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
                        dialog.show(
                            childFragmentManager, "${AppStringConstant1.story}"
                        )
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
                        dialog.show(
                            childFragmentManager, "${AppStringConstant1.story}"
                        )
                    }

                    /*val listComment = arrayListOf<GetAllUserMultiStoriesQuery.Edge1?>()
                    userStory.node.comments?.edges?.forEach {
                        it.node.
                        listComment.add()
                    }
                    GetAllUserMultiStoriesQuery.Comments()

                    val node = GetAllUserMultiStoriesQuery.Node(userStory.node.createdDate,userStory.node.file,userStory.node.fileType,userStory.node.id
                    ,userStory.node.pk,userStory.node.thumbnail,userStory.node.commentsCount,userStory.node.comments,userStory.node.likesCount,userStory.node.likes,userStory.node.user)
                    val pageInfo = GetAllUserMultiStoriesQuery.PageInfo2(res.data?.allUserStories!!.pageInfo.endCursor
                        ,res.data?.allUserStories!!.pageInfo.hasNextPage,,res.data?.allUserStories!!.pageInfo.hasPreviousPage,res.data?.allUserStories!!.pageInfo.startCursor)
                    val stories = GetAllUserMultiStoriesQuery.Stories(arrayListOf(GetAllUserMultiStoriesQuery.Edge(userStory.cursor,node)),pageInfo)*/
                    return@forEach
                }
            }
        }
    }

    private fun exitOnError() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (activity != null) {
                App.userPreferences.clear()
                val intent = Intent(activity, SplashActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
                return@launch
            }
        }
    }

    override fun onNotificationClick(
        position: Int, notificationdata: GetAllNotificationQuery.Edge?
    ) {

        getNotificationIndex()

        val titles = notificationdata!!.node!!.notificationSetting!!.title
//        if (titles != null) {
//            Log.e("titles", titles)
//        }

        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetNotificationQuery(
                        notificationdata.node!!.pk!!,
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse Exception all stories${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            Log.e(TAG,"apolloResponse GetNotificationQuery  ${res.hasErrors()}")
            if (res.hasErrors()) {
                Log.e(TAG,"onNotificationClick: Error --> ${res.errors!!.get(0).nonStandardFields!!["code"]}")
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    return@launchWhenResumed exitOnError()
                }
            }

            if (notificationdata.node.data != null) {
                val dataValues: JsonObject =
                    JsonParser.parseString(notificationdata.node.data.toString()).asJsonObject
                Log.e(TAG,"gettingResponse: ${notificationdata.node.data}")
                if (dataValues.has("notification_type")) {
                    when (dataValues.get("notification_type").asString) {
                        "LIKE", "CMNT", "MMSHARED" -> {
                            //Moment Liked
                            //Comment on Moment
                            //Moment shared
                            if (dataValues.has("momentId")) {
                                val momentid = dataValues.get("momentId").toString()
                                getMoments(momentid, position)
                            } else {
                                handleOldNotification(titles, notificationdata, position, res)
                            }

                        }

                        "STSHARED", "STLIKE", "STCMNT" -> {
                            //story shared
                            //Story Like
                            //Story Commented
                            if (dataValues.has("storyID")) {
                                val storyId = dataValues.get("storyID").toString()
                                getStories(storyId, "", position)
                            } else if (dataValues.has("storyId")) {
                                val storyId = dataValues.get("storyId").toString()
                                getStories(storyId, "", position)
                            } else {
                                handleOldNotification(titles, notificationdata, position, res)
                            }
                        }

                        "PROFILEVISIT" -> {
                            if (dataValues.has("visited_user_id")) {
                                allnotification.removeAt(position)
                                adapter.notifyDataSetChanged()
                                val followUserId = dataValues.get("visited_user_id").asString
                                val bundle = Bundle()
                                bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
                                bundle.putString("userId", followUserId)
                                if (userId == followUserId) {
                                    getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                                        R.id.nav_user_profile_graph
                                } else {
                                    navController?.navigate(
                                        destinationId = R.id.action_global_otherUserProfileFragment,
                                        popUpFragId = null,
                                        animType = AnimationTypes.SLIDE_ANIM,
                                        inclusive = true,
                                        args = bundle
                                    )
                                }
                            } else {
                                handleOldNotification(titles, notificationdata, position, res)
                            }
                        }

                        "USERFOLLOW" -> {
                            if (dataValues.has("followerID")) {
                                allnotification.removeAt(position)
                                adapter.notifyDataSetChanged()
                                val followUserId = dataValues.get("followerID").asString
                                if (userId == followUserId) {
                                    getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                                        R.id.nav_user_profile_graph
                                } else {
                                    val bundle = Bundle()
                                    bundle.putBoolean(
                                        SearchUserProfileFragment.ARGS_FROM_CHAT, false
                                    )
                                    bundle.putString("userId", followUserId)
                                    navController?.navigate(
                                        destinationId = R.id.action_global_otherUserProfileFragment,
                                        popUpFragId = null,
                                        animType = AnimationTypes.SLIDE_ANIM,
                                        inclusive = true,
                                        args = bundle
                                    )
                                }
                            } else {
                                handleOldNotification(titles, notificationdata, position, res)
                            }
                        }

                        "SNDMSG" -> {
                            //Gift received
                            allnotification.removeAt(position)
                            adapter.notifyDataSetChanged()
                            if (dataValues.has("roomID")) {
                                getMainActivity()?.pref?.edit()?.putString("roomIDNotify", "true")
                                    ?.putString("roomID", dataValues.get("roomID").toString())
                                    ?.apply()
                                getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                                    R.id.nav_chat_graph
                            } else {
                                getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                                    R.id.nav_user_profile_graph
                            }
                        }

                        else -> {
                            handleOldNotification(titles, notificationdata, position, res)
                        }
                    }
                } else {
                    handleOldNotification(titles, notificationdata, position, res)
                }
            } else {
                handleOldNotification(titles, notificationdata, position, res)
            }
        }
    }

    private fun handleOldNotification(
        titles: String?,
        notificationdata: GetAllNotificationQuery.Edge?,
        position: Int,
        res: ApolloResponse<GetNotificationQuery.Data>
    ) {
        if (titles == getString(R.string.moment_liked) || titles == getString(R.string.comment_in_moment)) {
            if (notificationdata?.node!!.data != null) {
                val resp: JsonObject =
                    JsonParser().parse(notificationdata.node.data.toString()).asJsonObject
                val momentid = resp.get("momentId").toString()
                getMoments(momentid, position)
            }
        } else if (titles == getString(R.string.moment_shared)) {
            if (notificationdata?.node!!.data != null) {
                val resp: JsonObject =
                    JsonParser().parse(notificationdata.node.data.toString()).asJsonObject
                if (resp.has("momentId")) {
                    val momentid = resp.get("momentId").toString()
                    getMoments(momentid, position)
                }
            }
        } else if (titles == getString(R.string.story_liked)) {
            if (notificationdata?.node!!.data != null) {
                val resp: JsonObject =
                    JsonParser().parse(notificationdata.node.data.toString()).asJsonObject
                if (resp.has("pk") && resp.get("pk") != null) {
                    val pkid = resp.get("pk").toString()
                    getStories(pkid, "", position)
                } else {
                    val responceFor: JsonObject =
                        JsonParser().parse(res.data!!.notification!!.data.toString()).asJsonObject

                    if (responceFor.has("pk") && responceFor.get("pk") != null) {
                        val pkid = responceFor.get("pk").toString()
                        getStories(pkid, "", position)
                    } else {
                        val pkid = res.data!!.notification!!.id.toString()
                        getStories("", pkid, position)
                    }
                }
            }
        } else if (titles == getString(R.string.story_shared)) {
            val responceFor: JsonObject =
                JsonParser().parse(res.data!!.notification!!.data.toString()).asJsonObject
            if (responceFor.has("storyId") && responceFor.get("storyId") != null) {
                val pkid = responceFor.get("storyId").toString()
                getStories(pkid, "", position)
            } else {
                val pkid = res.data!!.notification!!.id.toString()
                getStories("", pkid, position)
            }
        } else if (titles == getString(R.string.story_commented)) {
            if (notificationdata?.node!!.data != null) {
                val resp: JsonObject =
                    JsonParser().parse(notificationdata.node.data.toString()).asJsonObject
                if (resp.has("pk") && resp.get("pk") != null) {
                    val pkid = resp.get("pk").toString()
                    getStories(pkid, "", position)
                } else {
                    val responseFor: JsonObject =
                        JsonParser().parse(res.data!!.notification!!.data.toString()).asJsonObject

                    Log.e(TAG,Gson().toJson(resp))

                    if (responseFor.has("pk") && responseFor.get("pk") != null) {
                        val pkId = responseFor.get("pk").toString()
                        getStories(pkId, "", position)
                    } else {
                        val pkId = res.data!!.notification!!.id.toString()
                        getStories("", pkId, position)
                    }
                }
            }
        } else if (titles == getString(R.string.gift_received)) {
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
            getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                R.id.nav_user_profile_graph
        } else if (titles?.lowercase(Locale.getDefault())
                .contentEquals(getString(R.string.message_received).lowercase(Locale.getDefault()))
        ) {
            val resp: JsonObject =
                JsonParser().parse(notificationdata?.node!!.data.toString()).asJsonObject
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
            getMainActivity()?.pref?.edit()?.putString("roomIDNotify", "true")
                ?.putString("roomID", resp.get("roomID").toString())?.apply()
            getMainActivity()?.binding?.bottomNavigation?.selectedItemId = R.id.nav_chat_graph
        } else if (titles == getString(R.string.congratulations)) {
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
            getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                R.id.nav_user_profile_graph
        } else if (titles?.lowercase(Locale.getDefault())
                .contentEquals(getString(R.string.user_followed).lowercase(Locale.getDefault()))
        ) {
            var followUserId = res.data!!.notification!!.user.id
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", followUserId)
            if (userId == followUserId) {
                getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                    R.id.nav_user_profile_graph
            } else {
                findNavController().navigate(
                    destinationId = R.id.action_global_otherUserProfileFragment,
                    popUpFragId = null,
                    animType = AnimationTypes.SLIDE_ANIM,
                    inclusive = true,
                    args = bundle
                )
            }
        } else if (titles?.lowercase(Locale.getDefault())
                .contentEquals(getString(R.string.profile_visit).lowercase(Locale.getDefault()))
        ) {
            //var followUserId = res.data!!.notification!!.user.id
            val followUserId =
                res.data!!.notification!!.sender?.id ?: res.data!!.notification!!.user.id
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", followUserId)
            if (userId == followUserId) {
                getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                    R.id.nav_user_profile_graph
            } else {
                findNavController().navigate(
                    destinationId = R.id.action_global_otherUserProfileFragment,
                    popUpFragId = null,
                    animType = AnimationTypes.SLIDE_ANIM,
                    inclusive = true,
                    args = bundle
                )
            }
        } else {
            allnotification.removeAt(position)
            adapter.notifyDataSetChanged()
        }
    }

    fun addNotification(notificationId: String) {

        Log.e(TAG,"addNotification: notificationId: $notificationId")
        val obj = JSONObject(notificationId)

        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetNotificationQuery(
                        obj.getJSONObject("data").getInt("pk")
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse Exception all stories${e.message}")
                binding.root.snackbar(" ${e.message}")
                return@launchWhenResumed
            }
            Log.e(TAG,"addNotification: GetNotificationQuery  ${res.hasErrors()}")
            if (res.hasErrors()) {
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    return@launchWhenResumed exitOnError()
                }
            }

            Log.e(TAG,"addNotification: gettingPkResponce"+ "" + Gson().toJson(res.data))
            try {
                val dataValues: JsonObject = JsonParser.parseString(Gson().toJson(res.data)).asJsonObject
                val notification: JsonObject? = dataValues.getAsJsonObject("notification")
                if (notification != null) {
                    val nodes: GetAllNotificationQuery.Node = GetAllNotificationQuery.Node(
                        notification.get("id").asString,
                        notification.get("pk").asInt,
                        GetAllNotificationQuery.NotificationData(
                            notification.get("notificationData").asJsonObject.get("title").asString,
                            notification.get("notificationData").asJsonObject.get("body").asString
                        ),
                        GetAllNotificationQuery.NotificationSetting(
                            notification.get("notificationSetting").asJsonObject.get("title").asString
                        ),
                        notification.get("createdDate").asString,
                        notification.get("data").asString,
                        notification.get("notificationBody").asString,
                        0,
                    )
                    val edge: GetAllNotificationQuery.Edge = GetAllNotificationQuery.Edge(nodes, "")

                    allnotification.add(0, edge)
                    adapter.notifyItemInserted(0)
                } else {
                    binding.noData.visibility = View.VISIBLE
                    binding.rvNotifications.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}