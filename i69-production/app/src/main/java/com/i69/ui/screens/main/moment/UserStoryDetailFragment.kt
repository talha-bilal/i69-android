package com.i69.ui.screens.main.moment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.i69.*
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.ModelGifts
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.BuildConfig
import com.i69.R
import com.i69.ui.adapters.CommentReplyListAdapter
import com.i69.ui.adapters.StoryCommentListAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.ui.viewModels.ReplysModel
import com.i69.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.util.ArrayList

class UserStoryDetailFragment(val listener: DeleteCallback?) : DialogFragment(),
    StoryCommentListAdapter.ClickPerformListener, CommentReplyListAdapter.ClickonListener {

    interface DeleteCallback {
        fun deleteCallback(objectId: Int)
    }

    private var TAG: String = UserStoryDetailFragment::class.java.simpleName
    private var tickTime: Long = 3000
    var countUp: Int = 100

    private lateinit var loadingDialog: Dialog

    private var timer1: CountDownTimerExt? = null

    private lateinit var views: View

    var progressBar1: ProgressBar? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var glide: RequestManager

    var txtNearbyUserLikeCount: MaterialTextView? = null
    var txtMomentRecentComment: MaterialTextView? = null
    var lblItemNearbyUserCommentCount: MaterialTextView? = null
    var lblItemNearbyCommentCount: MaterialTextView? = null
    var msg_write: EditText? = null
    var rvSharedMoments: RecyclerView? = null

    var rvLikes: RecyclerView? = null
    var nodata: MaterialTextView? = null

    var no_data: MaterialTextView? = null
    var txtheaderlike: MaterialTextView? = null

    var items: ArrayList<CommentsModel> = ArrayList()

    var giftUserid: String? = null

    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null

    var userToken: String? = null
    var Uid: String? = null

    var objectID: Int? = null
    var Replymodels: CommentsModel? = null

    var adapter: StoryCommentListAdapter? = null
    var adapters: StoryLikesAdapter? = null

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (timer1 != null) {
            timer1!!.restart()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        views = inflater.inflate(R.layout.fragment_user_story_detail, container, false)
        return views
    }

    fun pausetimer() {
        if (timer1 != null) {
            timer1!!.pause()
        }
    }

    fun starttimer() {
        if (timer1 != null) {
            timer1!!.start()
        }
    }

    fun restarttimer() {
        if (timer1 != null) {
            timer1!!.restart()
        }
    }

    override fun onStart() {
        super.onStart()
        loadingDialog = requireActivity().createLoadingDialog()

        Uid = arguments?.getString("Uid", "")
        val url = arguments?.getString("url", "")
        val userurl = arguments?.getString("userurl", "")
        val username = arguments?.getString("username", "")
        val times = arguments?.getString("times", "")
        userToken = arguments?.getString("token", "")
        objectID = arguments?.getInt("objectID", 0)
        val showDelete = arguments?.getBoolean("showDelete") ?: false

        glide = Glide.with(requireContext())

        val userPic = views.findViewById<ImageView>(R.id.userPic)
        val lblName = views.findViewById<MaterialTextView>(R.id.lblName)
        val txtTimeAgo = views.findViewById<MaterialTextView>(R.id.txtTimeAgo)
        val sendgiftto = views.findViewById<MaterialTextView>(R.id.sendgiftto)
        progressBar1 = views.findViewById(R.id.progressBar1)
        val imgUserStory = views.findViewById<ImageView>(R.id.imgUserStory)
        val img_close = views.findViewById<ImageView>(com.i69.R.id.img_close)
        val giftsTabs = views.findViewById<TabLayout>(R.id.giftsTabs)
        val giftsPager = views.findViewById<ViewPager>(R.id.gifts_pager)
        txtNearbyUserLikeCount = views.findViewById(R.id.txtNearbyUserLikeCount)
        txtMomentRecentComment = views.findViewById(R.id.txtMomentRecentComment)
        lblItemNearbyUserCommentCount = views.findViewById(R.id.lblItemNearbyUserCommentCount)
        lblItemNearbyCommentCount = views.findViewById(R.id.lblItemNearbyCommentCount)
        val thumbnail = views.findViewById<ImageView>(R.id.thumbnail)
        val send_btn = views.findViewById<ImageView>(R.id.send_btn)
        msg_write = views.findViewById(R.id.msg_write)
        rvSharedMoments = views.findViewById(R.id.rvSharedMoments)
        nodata = views.findViewById(R.id.no_data)

        rvLikes = views.findViewById(R.id.rvLikes)
        no_data = views.findViewById(R.id.no_datas)
        txtheaderlike = views.findViewById(R.id.txtheaderlike)


        val likes_l = views.findViewById<LinearLayout>(R.id.likes_l)
        val likes_view = views.findViewById<LinearLayout>(R.id.likes_view)
        val comment_l = views.findViewById<LinearLayout>(R.id.comment_l)
        val gift_l = views.findViewById<LinearLayout>(R.id.gift_l)
        val delete_story = views.findViewById<ImageView>(R.id.delete_story)

        val report_l = views.findViewById<LinearLayout>(R.id.report_l)

        if (showDelete) {
            delete_story.visibility = View.VISIBLE
            report_l.visibility = View.GONE
        } else {
            report_l.visibility = View.VISIBLE
        }

        getStories()

        userPic.loadCircleImage(userurl!!)
        lblName.text = username
        txtTimeAgo.text = times
        sendgiftto.text = context?.resources?.getString(R.string.send_git_to) + " " + username!!

        userPic.setOnClickListener {
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            restarttimer()

            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
            dismiss()
        }

        lblName.setOnClickListener {
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            restarttimer()

            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
            dismiss()
        }

        imgUserStory.loadImage(url ?: "", {
            startDismissCountDown1()

        }, {
            restarttimer()

            dismiss()
        })

        img_close.setOnClickListener {
            restarttimer()
            dismiss()
        }

        val bottomSheet = views.findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        starttimer()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        pausetimer()
                    }
                }
            }
        })


        val giftbottomSheet = views.findViewById<ConstraintLayout>(R.id.giftbottomSheet)
        GiftbottomSheetBehavior = BottomSheetBehavior.from(giftbottomSheet)
        GiftbottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        starttimer()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        pausetimer()
                    }
                }
            }
        })
        val likebottomSheet = views.findViewById<ConstraintLayout>(R.id.likebottomSheet)
        LikebottomSheetBehavior = BottomSheetBehavior.from(likebottomSheet)
        LikebottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        starttimer()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        pausetimer()
                    }
                }
            }
        })


        sendgiftto.setOnClickListener {

            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }

            lifecycleScope.launchWhenCreated() {
                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->

                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(
                                requireContext(),
                                userToken!!
                            ).mutation(GiftPurchaseMutation(gift.id, giftUserid!!, Uid!!)).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse ${e.message}")
                            Toast.makeText(
                                requireContext(),
                                "UserStory1: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (res?.hasErrors() == false) {
                            Toast.makeText(
                                requireContext(),
                                context?.resources?.getString(R.string.you_bought) + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + context?.resources?.getString(
                                    R.string.successfully
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (res!!.hasErrors()) {
                            Toast.makeText(
                                requireContext(),
                                "UserStory2: ${res.errors!![0].message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e(
                            TAG,
                            "apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}"
                        )
                    }
                    hideProgressView()
                }
            }
        }


        giftsTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                giftsPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        giftsTabs.setupWithViewPager(giftsPager)
        setupViewPager(giftsPager)


        send_btn.setOnClickListener(View.OnClickListener {
            if (msg_write?.text.toString().equals("")) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.you_cant_add_empty_msg),
                    Toast.LENGTH_LONG
                ).show()

                return@OnClickListener
            }

            if (msg_write?.text.toString().startsWith("@") && msg_write?.text.toString()
                    .trim().contains(
                        Replymodels!!.username!!, true
                    )
            ) {

                showProgressView()
                lifecycleScope.launchWhenResumed {
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(
                            CommentOnStoryMutation(
                                msg_write?.text.toString()
                                    .replace("@" + Replymodels!!.username!!, "").trim(),
                                Replymodels!!.cmtID!!.toInt(), "genericcomment"
                            )
                        )
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse ${e.message}")
                    }

                    hideProgressView()
                    msg_write?.setText("")

                    if (items.size > 0) {
                        RefreshStories()
                    } else {
                        getStories()
                    }
                }

            } else {
                showProgressView()
                lifecycleScope.launchWhenResumed {
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(
                            CommentOnStoryMutation(
                                msg_write?.text.toString(),
                                objectID!!, "story"
                            )
                        )
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "UserStory3 ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        hideProgressView()
                        return@launchWhenResumed
                    }

                    hideProgressView()
                    msg_write?.setText("")

                    val usermoments = res.data?.genericComment

                    if (items.size > 0) {
                        RefreshStories()
                    } else {
                        getStories()
                    }
                }
            }
        })


        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(GetUserDataQuery(Uid!!))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "UserStory4: ${e.message}", Toast.LENGTH_LONG)
                    .show()

                hideProgressView()
                return@launchWhenResumed
            }

            hideProgressView()
            val UserData = res.data?.user

            try {
                if (UserData!!.avatar!! != null) {
                    try {
                        thumbnail.loadCircleImage(
                            UserData.avatar!!.url!!.replace(
                                "http://95.216.208.1:8000/media/",
                                "${BuildConfig.BASE_URL}media/"
                            )
                        )
                        Log.e(
                            TAG,
                            "URL " + UserData.avatar.url!!.replace(
                                "http://95.216.208.1:8000/media/",
                                "${BuildConfig.BASE_URL}media/"
                            )
                        )
                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
            } catch (e: Exception) {
            }
        }


        likes_view.setOnClickListener {
            if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                pausetimer()

            } else {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                starttimer()
            }
        }
        likes_l.setOnClickListener {
            if (Uid.equals(giftUserid)) {
                if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    pausetimer()
                } else {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                    starttimer()
                }
            } else {
                lifecycleScope.launchWhenResumed {
                    val res = try {
                        apolloClient(
                            requireContext(),
                            userToken!!
                        ).mutation(LikeOnStoryMutation(objectID!!, "story"))
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "UserStory5: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launchWhenResumed
                    }
                    val usermoments = res.data?.genericLike

                    RefreshStories()
                }
            }

        }

        comment_l.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                pausetimer()

            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                starttimer()
            }
        }

        gift_l.setOnClickListener {
            if (Uid.equals(giftUserid)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_cant_bought_gift_yourseld),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {

                if (GiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    pausetimer()

                } else {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                    starttimer()
                }
            }
        }

        delete_story.setOnClickListener {
            deleteConfirmation()
        }

        report_l.setOnClickListener {
            pausetimer()
            reportDialog()
        }

    }

    private fun reportDialog() {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportMessage = dialogLayout.findViewById<EditText>(R.id.report_message)
        val okButton = dialogLayout.findViewById<TextView>(R.id.ok_button)
        val cancleButton = dialogLayout.findViewById<TextView>(R.id.cancel_button)

        okButton.text = "${AppStringConstant(getMainActivity()).ok}"
        cancleButton.text = "${AppStringConstant(getMainActivity()).cancel}"

        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()


        okButton.setOnClickListener {
            val message = reportMessage.text.toString()
            showProgressView()
            lifecycleScope.launchWhenResumed {
                val res = try {
                    apolloClient(
                        requireContext(),
                        userToken!!
                    ).mutation(ReportStoryMutation(objectID.toString(), message))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception ${e.message}")
                    Toast.makeText(requireContext(), "UserStory6: ${e.message}", Toast.LENGTH_LONG)
                        .show()

                    hideProgressView()
                    dialog.dismiss()
                    return@launchWhenResumed
                }

                if (res.hasErrors()) {

                } else {
                    Log.e(
                        TAG,
                        "rsponceSuccess" + res.data!!.reportStory!!.storyReport!!.story.pk.toString()
                    )
                }

                starttimer()

                hideProgressView()
                dialog.dismiss()
            }
        }

        cancleButton.setOnClickListener {
            starttimer()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun subscribeForUpdateStory(stories: List<GetAllUserStoriesQuery.Edge?>) {

        val storiesPkList = ArrayList<Int>()

        stories.indices.forEach {
            var pkss = stories[it]?.node!!.pk
            if (pkss != null) {
                storiesPkList.add(pkss)
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnUpdateStorySubscription(storiesPkList)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    Log.e(
                        TAG,
                        "storySubscription" +
                                "realtime retry  $attempt ${cause.message}"
                    )
                    delay(attempt * 1000)
                    true
                }.collect { newStory ->
                    if (newStory.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newStory.errors?.get(0)?.message}"
                        )
                    } else {

                        if (items.size > 0) {
                            RefreshStories()
                        } else {
                            getStories()
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }

    private fun getStories() {
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserStoriesQuery(
                        100, "",
                        objectID.toString(), ""
                    )
                )
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "UserStory7: ${e.message}", Toast.LENGTH_LONG)
                    .show()

                return@launchWhenResumed
            }
            Log.e(TAG, "apolloResponse allUserStories stories ${res.hasErrors()}")

            val allUserStories = res.data?.allUserStories!!.edges

            subscribeForUpdateStory(allUserStories)
            if (allUserStories.size != 0) {
                val likeCount = allUserStories.get(0)!!.node!!.likesCount.toString()
                val commentCount = allUserStories.get(0)!!.node!!.commentsCount.toString()
                txtNearbyUserLikeCount!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                lblItemNearbyCommentCount!!.text = commentCount
                lblItemNearbyUserCommentCount!!.text =
                    "${requireActivity().resources.getString(R.string.comments)}"
                txtheaderlike!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"

                giftUserid = allUserStories.get(0)!!.node!!.user!!.id.toString()

                val Likedata = allUserStories.get(0)!!.node!!.likes!!.edges

                if (rvLikes!!.itemDecorationCount == 0) {
                    rvLikes!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            outRect.top = 25
                        }
                    })
                }
                if (Likedata.size > 0) {
                    Log.e(TAG, "apolloResponse: ${Likedata[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE

                    val items1: ArrayList<CommentsModel> = ArrayList()

                    Likedata.indices.forEach { i ->
                        val models = CommentsModel()

                        models.commenttext = Likedata[i]!!.node!!.user.fullName

                        if (Likedata[i]!!.node!!.user.avatar != null && !Likedata[i]!!.node!!.user.avatar!!.url.isNullOrEmpty()) {
                            models.userurl = Likedata[i]!!.node!!.user.avatar!!.url
                        } else {
                            models.userurl = ""
                        }

                        models.uid = Likedata[i]!!.node!!.user.id

                        items1.add(models)
                    }

                    adapters =
                        StoryLikesAdapter(
                            requireActivity(),
                            items1,
                            glide
                        )

                    adapters?.userProfileClicked {
                        Log.e(TAG, "storyDetailsFragment" + "$it")
                        val bundle = Bundle()
                        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
                        bundle.putString("userId", it.uid)
                        if (Uid == it.uid) {
                            MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
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
                    }

                    rvLikes!!.adapter = adapters
                    rvLikes!!.layoutManager = LinearLayoutManager(activity)
                } else {
                    no_data!!.visibility = View.VISIBLE
                    rvLikes!!.visibility = View.GONE
                }

                val Commentdata = allUserStories.get(0)!!.node!!.comments!!.edges

                if (rvSharedMoments!!.itemDecorationCount == 0) {
                    rvSharedMoments!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            outRect.top = 25
                        }
                    })
                }
                if (Commentdata.size > 0) {
                    Log.e(TAG, "apolloResponse: ${Commentdata?.get(0)?.node!!.commentDescription}")
                    nodata!!.visibility = View.GONE
                    rvSharedMoments!!.visibility = View.VISIBLE

                    items = ArrayList()

                    Commentdata.indices.forEach { i ->
                        val hm: MutableList<ReplysModel> = ArrayList()

                        val models = CommentsModel()

                        models.commenttext = Commentdata[i]!!.node!!.commentDescription

                        if (Commentdata[i]!!.node!!.user.avatar != null && Commentdata[i]!!.node!!.user.avatar!!.url!!.isNotEmpty()) {

                            models.userurl = Commentdata[i]!!.node!!.user.avatar!!.url
                        } else {
                            models.userurl = ""
                        }
                        models.username = Commentdata[i]!!.node!!.user.fullName
                        models.timeago = Commentdata[i]!!.node!!.createdDate.toString()
                        models.cmtID = Commentdata[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.cmtlikes = Commentdata[i]!!.node!!.likesCount.toString()

                        models.uid = Commentdata[i]!!.node!!.user.id.toString()

                        for (f in 0 until Commentdata[i]!!.node!!.replys!!.edges.size) {

                            val md = ReplysModel()

                            md.replytext =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url
                            md.usernames =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            md.timeago =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.createdDate.toString()
                            md.uid =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.id.toString()

                            hm.add(f, md)
                        }

                        models.replylist = hm
                        models.isExpanded = true

                        items.add(models)
                    }

                    adapter =
                        StoryCommentListAdapter(
                            requireActivity(),
                            this@UserStoryDetailFragment,
                            items,
                            this@UserStoryDetailFragment
                        )
                    rvSharedMoments!!.adapter = adapter
                    rvSharedMoments!!.layoutManager = LinearLayoutManager(activity)
                } else {
                    nodata!!.visibility = View.VISIBLE
                    rvSharedMoments!!.visibility = View.GONE
                }
            }
        }
    }

    private fun RefreshStories() {

        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserStoriesQuery(
                        100, "",
                        objectID.toString(), ""
                    )
                )
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "UserStory8: ${e.message}", Toast.LENGTH_LONG)
                    .show()

                return@launchWhenResumed
            }
            Log.e(TAG, "apolloResponse allUserStories stories ${res.hasErrors()}")

            val allUserStories = res.data?.allUserStories!!.edges
            if (allUserStories != null && allUserStories.size != 0) {
                val likeCount = allUserStories.get(0)!!.node!!.likesCount.toString()
                val commentCount = allUserStories.get(0)!!.node!!.commentsCount.toString()
                txtNearbyUserLikeCount!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                lblItemNearbyCommentCount!!.text = commentCount
                lblItemNearbyUserCommentCount!!.text =
                    "${requireActivity().resources.getString(R.string.comments)}"
                txtheaderlike!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"

                giftUserid = allUserStories.get(0)!!.node!!.user!!.id.toString()

                val Likedata = allUserStories.get(0)!!.node!!.likes!!.edges
                if (Likedata.size > 0) {
                    Log.e(TAG, "apolloResponse: ${Likedata[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE

                    val items1: ArrayList<CommentsModel> = ArrayList()

                    Likedata.indices.forEach { i ->

                        val models = CommentsModel()

                        models.commenttext = Likedata[i]!!.node!!.user.fullName

                        if (Likedata[i]!!.node!!.user.avatar != null && !Likedata[i]!!.node!!.user.avatar!!.url.isNullOrEmpty()) {
                            models.userurl = Likedata[i]!!.node!!.user.avatar!!.url
                        } else {
                            models.userurl = ""
                        }
                        items1.add(models)
                    }

                    if (adapters == null) {
                        adapters = StoryLikesAdapter(requireActivity(), items1, glide)

                        adapters?.userProfileClicked {
                            var bundle = Bundle()
                            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
                            bundle.putString("userId", it.uid)
                            if (Uid == it.uid) {
                                MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
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
                        }


                        rvLikes!!.adapter = adapters
                        rvLikes!!.layoutManager = LinearLayoutManager(activity)
                    } else {
                        adapters!!.addAll(items1)
                        adapters!!.notifyDataSetChanged()
                    }
                } else {
                    no_data!!.visibility = View.VISIBLE
                    rvLikes!!.visibility = View.GONE
                }

                val Commentdata = allUserStories.get(0)!!.node!!.comments!!.edges

                if (rvSharedMoments!!.itemDecorationCount == 0) {
                    rvSharedMoments!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            outRect.top = 25
                        }
                    })
                }
                if (Commentdata.size > 0) {
                    Log.e(TAG, "apolloResponse: ${Commentdata?.get(0)?.node!!.commentDescription}")
                    nodata!!.visibility = View.GONE
                    rvSharedMoments!!.visibility = View.VISIBLE

                    val items1: ArrayList<CommentsModel> = ArrayList()

                    Commentdata.indices.forEach { i ->
                        val hm: MutableList<ReplysModel> = ArrayList()


                        val models = CommentsModel()

                        models.commenttext = Commentdata[i]!!.node!!.commentDescription

                        if (Commentdata[i]!!.node!!.user.avatar != null && Commentdata[i]!!.node!!.user.avatar!!.url!!.isNotEmpty()) {
                            models.userurl = Commentdata[i]!!.node!!.user.avatar!!.url
                        } else {
                            models.userurl = ""
                        }
                        models.username = Commentdata[i]!!.node!!.user.fullName
                        models.timeago = Commentdata[i]!!.node!!.createdDate.toString()
                        models.cmtID = Commentdata[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.cmtlikes = Commentdata[i]!!.node!!.likesCount.toString()

                        models.uid = Commentdata[i]!!.node!!.user.id.toString()
                        for (f in 0 until Commentdata[i]!!.node!!.replys!!.edges.size) {

                            val md = ReplysModel()

                            md.replytext =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url
                            md.usernames =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            md.timeago =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.createdDate.toString()
                            md.uid =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.id.toString()

                            hm.add(f, md)
                        }

                        models.replylist = hm
                        models.isExpanded = true

                        items1.add(models)

                        if (items1.size - 1 == i) {
                            if (items.size != 0) {
                                if (adapter != null) {
                                    adapter!!.updateList(items1)
                                } else {
                                    Log.e(TAG, "myadapterarenull")
                                }
                            }
                        } else {
                            Log.e(TAG, "ItemSizeNteMatch")
                        }
                    }
                } else {
                    nodata!!.visibility = View.VISIBLE
                    rvSharedMoments!!.visibility = View.GONE
                }
            }
        }
    }

    protected fun showProgressView() {
        loadingDialog.show()
    }

    protected fun hideProgressView() {
        loadingDialog.dismiss()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()

        adapter.addFragItem(fragRealGifts!!, getString(R.string.real_gifts))
        adapter.addFragItem(fragVirtualGifts!!, getString(R.string.virtual_gifts))
        viewPager.adapter = adapter
    }

    private fun startDismissCountDown1() {

        timer1 = object : CountDownTimerExt(tickTime, 100) {
            override fun onTimerTick(millisUntilFinished: Long) {
                Log.e(TAG, "onTimerTick $millisUntilFinished")
                onTickProgressUpdate(millisUntilFinished)
            }

            override fun onTimerFinish() {
                dismiss()
            }
        }
        timer1.run { this!!.start() }
    }

    fun onTickProgressUpdate(milliSec: Long) {
        tickTime = milliSec
        countUp += 100
        val progress = countUp
        Log.e(TAG, "prggress $progress")
        progressBar1!!.smoothProgress(progress)
    }


    fun getMainActivity() = activity as MainActivity
    override fun onreply(position: Int, models: CommentsModel) {
        Replymodels = models
        msg_write?.setText("")
        msg_write?.setText("")
        msg_write?.requestFocus()
        msg_write?.postDelayed(Runnable {

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

            inputMethodManager!!.showSoftInput(
                msg_write,
                InputMethodManager.SHOW_IMPLICIT
            )
            msg_write?.append("@" + models.username + " ")

        }, 150)
    }

    override fun oncommentLike(position: Int, models: CommentsModel) {

        showProgressView()
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(
                    requireContext(),
                    userToken!!
                ).mutation(LikeOnStoryMutation(models.cmtID!!.toInt(), "genericcomment"))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "UserStory9: ${e.message}", Toast.LENGTH_LONG)
                    .show()

                hideProgressView()
                return@launchWhenResumed
            }

            RefreshStories()
            hideProgressView()
        }
    }

    override fun onUsernameClick(position: Int, models: CommentsModel) {

        var bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", models.uid)
        if (Uid == models.uid) {
            MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
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
    }

    override fun onUsernameClick(position: Int, models: ReplysModel) {
        var bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", models.uid)
        if (Uid == models.uid) {
            MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
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
    }

    private fun deleteConfirmation() {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)

        headerTitle.text =
            "${AppStringConstant(getMainActivity()).are_you_sure_you_want_to_delete_story}"
        noButton.text = "${AppStringConstant(getMainActivity()).no}"
        yesButton.text = "${AppStringConstant(getMainActivity()).yes}"

        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss();
        }

        yesButton.setOnClickListener {
            dialog.dismiss();
            dismiss()
            listener?.deleteCallback(objectID ?: 0)
        }

        dialog.show()
    }

}
