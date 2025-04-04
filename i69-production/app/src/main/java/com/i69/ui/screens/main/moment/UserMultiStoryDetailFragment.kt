package com.i69.ui.screens.main.moment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.i69.BuildConfig
import com.i69.CommentDeleteMutation
import com.i69.CommentOnStoryMutation
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.GetAllUserStoriesQuery
import com.i69.GetUserDataQuery
import com.i69.GiftPurchaseMutation
import com.i69.LikeOnStoryMutation
import com.i69.OnUpdateStorySubscription
import com.i69.R
import com.i69.ReportCommentMutation
import com.i69.ReportStoryMutation
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.ModelGifts
import com.i69.di.modules.AppModule
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentReceivedGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.ui.adapters.CommentReplyListAdapter
import com.i69.ui.adapters.MultiStoryCommentListAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.adapters.VideoMultiStoryCommentListAdapter
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.ui.viewModels.ReplysModel
import com.i69.utils.AnimationTypes
import com.i69.utils.ApiUtil
import com.i69.utils.CountDownTimerExt
import com.i69.utils.GlideImageLoader
import com.i69.utils.LogUtil
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.createLoadingDialog
import com.i69.utils.getResponse
import com.i69.utils.loadCircleImage
import com.i69.utils.navigate
import com.i69.utils.setViewGone
import com.i69.utils.smoothProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UserMultiStoryDetailFragment : DialogFragment(),
    MultiStoryCommentListAdapter.ClickPerformListener,
    CommentReplyListAdapter.ClickonListener {

    companion object {
        private const val TAG = "UserMultiStoryDetail"
    }

    private var TAG: String = UserMultiStoryDetailFragment::class.java.simpleName

    interface PlayerReadyListener {
        fun onPlayerReady()
    }

    interface DeleteCallback {
        fun deleteCallback(objectId: Int)
    }

    private var listener: DeleteCallback? = null

    private var playerReadyListener: PlayerReadyListener? = null

    private var showDelete: Boolean = false
    private var tickTime: Long = 3000
    var countUp: Int = 100
    private lateinit var loadingDialog: Dialog
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var realDurationMillis: Int = 0
    private var isFullscreen = false
    private var timer1: CountDownTimerExt? = null
    private lateinit var views: View
    lateinit var userurl: String
    lateinit var username: String
    var progressBar1: ProgressBar? = null
    private lateinit var exoPlayer: ExoPlayer
    var positionForStory: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var receivedGiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var glide: RequestManager
    var txtNearbyUserLikeCount: MaterialTextView? = null
    var txtNearbyUserLike: MaterialTextView? = null
    var txtMomentRecentComment: MaterialTextView? = null
    var lblItemNearbyCommentCount: MaterialTextView? = null
    var lblItemNearbyUserCommentCount: MaterialTextView? = null
    var msg_write: EditText? = null
    var rvSharedMoments: RecyclerView? = null
    var rvLikes: RecyclerView? = null
    var nodata: MaterialTextView? = null
    lateinit var userPic: ImageView
    lateinit var txtTimeAgo: MaterialTextView
    lateinit var imgUserStory: ImageView
    lateinit var progressBar: ProgressBar
    private var isPlayerPlaying = true
    var curProgressBar: ProgressBar? = null
    var curStoryType: String = ""
    var no_data: MaterialTextView? = null
    var txtheaderlike: MaterialTextView? = null
    lateinit var stories: List<GetAllUserMultiStoriesQuery.Edge?>
    var items: ArrayList<CommentsModel> = ArrayList()
    var player_view: PlayerView? = null
    var giftUserid: String? = null
    var progressBarList = mutableListOf<ProgressBar>()
    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null
    var userToken: String? = null
    var Uid: String? = null
    lateinit var pause: ImageView
    var objectID: Int? = null
    var Replymodels: CommentsModel? = null

    var adapter: MultiStoryCommentListAdapter? = null
    var adapter2: VideoMultiStoryCommentListAdapter? = null
    var adapters: StoryLikesAdapter? = null

    private var job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    fun setListener(listener: DeleteCallback?) {
        this.listener = listener
    }

    fun setPlayerReadyListner(playerReadyListener: PlayerReadyListener) {
        this.playerReadyListener = playerReadyListener
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }


    override fun onDestroy() {
        super.onDestroy()
        if (timer1 != null) {
            timer1 = null
        }
        job.cancel()
    }

    override fun onPause() {
        super.onPause()

        if (timer1 != null) {
            timer1!!.restart()
        }
        pauseTimerAndPlayer()
    }

    override fun onResume() {
        super.onResume()

        if (curStoryType != "video") {
//            player_view!!.visibility = View.GONE
//            imgUserStory.visibility = View.VISIBLE
        } else {

            if (pause.visibility == View.VISIBLE) {
                pause.visibility = View.GONE
            }
//            player_view!!.visibility = View.VISIBLE
//            imgUserStory.visibility = View.GONE
        }

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (timer1 != null) {
            timer1!!.restart()
        }
        pauseTimerAndPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        views = inflater.inflate(R.layout.fragment_user_story_detail, container, false)
        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
        return views
    }

    fun pauseTimer() {
        if (timer1 != null) {
            timer1!!.pause()
        }
    }

    fun startTimer() {
        if (timer1 != null) {
            timer1!!.start()
        }
    }

    private fun stopTimer() {
        if (timer1 != null) {
            timer1!!.pause()

        }
    }

    private fun restartTimer() {
        if (timer1 != null) {
            timer1!!.restart()
        }
    }


    override fun onStart() {
        super.onStart()
        loadingDialog = requireActivity().createLoadingDialog()
        Uid = arguments?.getString("Uid", "")
        val gson = Gson()
        val json = arguments?.getString("stories")
        Log.e(TAG, "" + json)
        stories = gson.fromJson(json, GetAllUserMultiStoriesQuery.Stories::class.java)?.edges!!
        userToken = arguments?.getString("token", "")
        showDelete = arguments?.getBoolean("showDelete") ?: false
        //  Toast.makeText(requireContext(),"Stories count ${stories?.size}",Toast.LENGTH_LONG).show()
        glide = Glide.with(requireContext())

        getStories()
//        subscribeForUpdateStory()
        userPic = views.findViewById<ImageView>(R.id.userPic)
        val lblName = views.findViewById<MaterialTextView>(R.id.lblName)
        val ctParent = views.findViewById<RelativeLayout>(R.id.ct_parent)
        txtTimeAgo = views.findViewById(R.id.txtTimeAgo)

        val typeface_semibold =
            Typeface.createFromAsset(activity?.assets, "fonts/poppins_semibold.ttf")
        lblName.typeface = typeface_semibold
        txtTimeAgo.typeface = typeface_semibold

        val sendgiftto = views.findViewById<MaterialTextView>(R.id.sendgiftto)
        progressBar1 = views.findViewById(R.id.progressBar1)
        imgUserStory = views.findViewById(R.id.imgUserStory)
        progressBar = views.findViewById(R.id.progressBar)

        val img_close = views.findViewById<ImageView>(com.i69.R.id.img_close)
        val giftsTabs = views.findViewById<TabLayout>(R.id.giftsTabs)
        val giftsPager = views.findViewById<ViewPager>(R.id.gifts_pager)
        txtNearbyUserLikeCount = views.findViewById(R.id.lblItemNearbyUserLikeCount)
        txtNearbyUserLike = views.findViewById(R.id.txtNearbyUserLike)
        txtMomentRecentComment = views.findViewById(R.id.txtMomentRecentComment)
        lblItemNearbyUserCommentCount = views.findViewById(R.id.lblItemNearbyUserCommentCount)
        lblItemNearbyCommentCount = views.findViewById(R.id.lblItemNearbyCommentCount)
        val thumbnail = views.findViewById<ImageView>(R.id.thumbnail)
        val send_btn = views.findViewById<ImageView>(R.id.send_btn)
        msg_write = views.findViewById<EditText>(R.id.msg_write)
        val imgNearbyUserLikes = views.findViewById<ImageView>(R.id.imgNearbyUserLikes)
        rvSharedMoments = views.findViewById(R.id.rvSharedMoments)
        nodata = views.findViewById(R.id.no_data)
        player_view = views.findViewById(R.id.player_view)
        val linearProgressbar = views.findViewById<LinearLayout>(R.id.linear_progressbars)
        for (i in stories.indices) {
            val newProgressBar1 =
                ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
            newProgressBar1.id = i
            val params1 = TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            params1.setMargins(32, 0, 0, 0)
            newProgressBar1.layoutParams = params1/*            val newParams = newProgressBar1.layoutParams as ViewGroup.MarginLayoutParams
                        newParams.setMargins(16,0,0,0)
              */
            newProgressBar1.max = 1500
            newProgressBar1.progress = 0
            newProgressBar1.progressBackgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.message_list_description_color_2
                )
            )
            newProgressBar1.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            progressBarList.add(newProgressBar1)
            linearProgressbar.addView(newProgressBar1)
        }
        rvLikes = views.findViewById(R.id.rvLikes)
        no_data = views.findViewById(R.id.no_datas)
        txtheaderlike = views.findViewById(R.id.txtheaderlike)
        val likes_l = views.findViewById<LinearLayout>(R.id.likes_l)
        val likes_view = views.findViewById<LinearLayout>(R.id.likes_view)

        val comment_l = views.findViewById<LinearLayout>(R.id.comment_l)
        val gift_l = views.findViewById<LinearLayout>(R.id.gift_l)
        val delete_story = views.findViewById<ImageView>(R.id.delete_story)
        val imgReport = views.findViewById<ImageView>(R.id.imgReport)

        val report_l = views.findViewById<LinearLayout>(R.id.report_l)
        val lblItemNearbyUserGiftTitle =
            views.findViewById<MaterialTextView>(R.id.lblItemNearbyUserGiftTitle)/*if(Uid.equals(giftUserid))
        {
            report_l.visibility = View.GONE

        }else{
            report_l.visibility = View.VISIBLE
        }*/
        pause = views.findViewById(R.id.iv_pause)
        if (showDelete) {
            delete_story.visibility = View.VISIBLE
            imgReport.visibility = View.GONE
            lblItemNearbyUserGiftTitle.text = getString(R.string.received_gift)
        } else {
            imgReport.visibility = View.VISIBLE
        }
        setStory("onStart:317", positionForStory)
        userPic.loadCircleImage(userurl)
        lblName.text = username
        sendgiftto.text = context?.resources?.getString(R.string.send_git_to) + " " + username
        val relativeLayoutLeft = views.findViewById<RelativeLayout>(R.id.relativeLayoutLeft)
        relativeLayoutLeft.setOnClickListener {
            LogUtil.debug("Left")
            if (positionForStory > 0) {
                if (curStoryType != "video") {
                    tickTime = 3000
                    positionForStory -= 1
                    progressBar1!!.smoothProgress(0)
                    curProgressBar!!.smoothProgress(0)
                    timer1?.pause()
                    setStory("onStart:318", positionForStory)
                } else {
                    if (this::exoPlayer.isInitialized) {

                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                            exoPlayer.stop()
                        }

                        positionForStory -= 1
                        progressBar1!!.smoothProgress(0)
                        curProgressBar!!.smoothProgress(0)
                        timer1?.pause()
                        setStory("onStart:322", positionForStory)
                    } else {
                        positionForStory -= 1
                        progressBar1!!.smoothProgress(0)
                        curProgressBar!!.smoothProgress(0)
                        timer1?.pause()
                        setStory("onStart:319", positionForStory)
                    }
                }
            }
        }
        val relativeLayoutRight = views.findViewById<RelativeLayout>(R.id.relativeLayoutRight)
        relativeLayoutRight.setOnClickListener {
            if (positionForStory < stories.size - 1) {
                if (curStoryType != "video") {
                    progressBar1!!.smoothProgress(3000)
                    curProgressBar!!.smoothProgress(3000)
                    tickTime = 3000
                    timer1?.finish()
                } else {
                    if (this::exoPlayer.isInitialized) {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                            exoPlayer.stop()
                        }

                        progressBar1!!.smoothProgress(realDurationMillis - 200)
                        curProgressBar!!.smoothProgress(realDurationMillis - 200)
                        timer1?.finish()
                    } else {
                        progressBar1!!.smoothProgress(realDurationMillis - 200)
                        curProgressBar!!.smoothProgress(realDurationMillis - 200)
                        timer1?.finish()
                    }
                }
            } else {
                dismiss()
            }
            LogUtil.debug("Right")
        }
        userPic.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            if (curStoryType != "video") {
                restartTimer()
            } else {
                pauseTimerAndPlayer()
            }
            if (showDelete) {
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

            dismiss()
        }
        lblName.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            if (curStoryType != "video") {
                restartTimer()
            } else {
                pauseTimerAndPlayer()
            }
            if (showDelete) {
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
            dismiss()
        }
//        imgUserStory.setOnTouchListener { _, event ->
//            if (event?.action == MotionEvent.ACTION_DOWN) {
//                timer1!!.restart()
//            } else if (event?.action == MotionEvent.ACTION_UP) {
//                startDismissCountDown1()
//
//            }
//            true
//        }
        ctParent.setOnClickListener {
            if (curStoryType == "video") {
                if (exoPlayer.playWhenReady) {
                    pauseTimerAndPlayer()
                } else {
                    resumeTimerAndPlayer()
                }
            }
        }
        img_close.setOnClickListener {
            if (curStoryType != "video") {
                restartTimer()
//                stopTimer()
            } else {
                pauseTimerAndPlayer()
            }
            dismiss()
        }
        val bottomSheet = views.findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (curStoryType != "video") {
                            startTimer()
                        } else {
                            resumeTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (curStoryType != "video") {
                            pauseTimer()
                        } else {
                            pauseTimerAndPlayer()
                        }
                    }
                }
            }
        })
        val giftbottomSheet = views.findViewById<ConstraintLayout>(R.id.giftbottomSheet)
        GiftbottomSheetBehavior = BottomSheetBehavior.from(giftbottomSheet)
        GiftbottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        if (curStoryType != "video") {
                            startTimer()
                        } else {
                            resumeTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (curStoryType != "video") {
                            pauseTimer()
                        } else {
                            pauseTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }

                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        receivedGiftbottomSheetBehavior =
            BottomSheetBehavior.from(views.findViewById(R.id.receivedGiftBottomSheet))
        receivedGiftbottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (curStoryType != "video") {
                            startTimer()
                        } else {
                            resumeTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (curStoryType != "video") {
                            pauseTimer()
                        } else {
                            pauseTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }

                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        val likebottomSheet = views.findViewById<ConstraintLayout>(R.id.likebottomSheet)
        LikebottomSheetBehavior = BottomSheetBehavior.from(likebottomSheet)
//        LikebottomSheetBehavior.setBottomSheetCallback(object :

        LikebottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (curStoryType != "video") {
                            startTimer()
                        } else {
                            resumeTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (curStoryType != "video") {
                            pauseTimer()
                        } else {
                            pauseTimerAndPlayer()
                        }
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }

                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })
        sendgiftto.setOnClickListener {
            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            lifecycleScope.launchWhenCreated {
                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->
                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(
                                requireContext(), userToken!!
                            ).mutation(GiftPurchaseMutation(gift.id, giftUserid!!, Uid!!)).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                        if (res?.hasErrors() == false) {
//                                views.snackbar("You bought ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} successfully!")
                            Toast.makeText(
                                requireContext(),
                                context?.resources?.getString(R.string.you_bought) + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + context?.resources?.getString(
                                    R.string.successfully
                                ),
                                Toast.LENGTH_LONG
                            ).show()

                            //fireGiftBuyNotificationforreceiver(gift.id,giftUserid!!)
                        }
                        if (res!!.hasErrors()) {
//                                views.snackbar(""+ res.errors!![0].message)
                            Toast.makeText(
                                requireContext(), "${res.errors!![0].message}", Toast.LENGTH_LONG
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
        //added received gift fragment
        if (showDelete) {
            childFragmentManager.beginTransaction().replace(
                R.id.receivedGiftContainer, FragmentReceivedGifts()
            ).commitAllowingStateLoss()
        }
        send_btn.setOnClickListener(View.OnClickListener {
            if (msg_write?.text.toString().equals("")) {
//                binding.root.snackbar(getString(R.string.you_cant_add_empty_msg))
                Toast.makeText(
                    requireContext(), getString(R.string.you_cant_add_empty_msg), Toast.LENGTH_LONG
                ).show()
                return@OnClickListener
            }
            if (Replymodels != null && msg_write?.text.toString().startsWith("@") && msg_write?.text.toString().trim()
                    .contains(
                        Replymodels!!.username!!, true
                    )
            ) {

                showProgressView()
                lifecycleScope.launchWhenResumed {
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).mutation(

                            CommentOnStoryMutation(
                                msg_write?.text.toString()
                                    .replace("@" + Replymodels!!.username!!, "").trim(),
                                Replymodels!!.cmtID!!.toInt(),
                                "genericcomment"
                            )
                        ).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse ${e.message}")
                    }

                    hideProgressView()
//                    fireCommntStoryNotification(objectID!!.toString(),giftUserid!!)
                    msg_write?.setText("")

//                    val usermoments = res.data?.genericComment

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
                            requireContext(), userToken!!
                        ).mutation(
                            CommentOnStoryMutation(
                                msg_write?.text.toString(), objectID!!, "story"
                            )
                        ).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception ${e.message}")
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                        hideProgressView()
                        return@launchWhenResumed
                    }
                    hideProgressView()
//                fireCommntStoryNotification(objectID!!.toString(), giftUserid!!)
                    msg_write?.setText("")
//                    val usermoments = res.data?.genericComment
//                    if (items.size > 0) {
//                    dismiss()
                    RefreshStories()
//                    } else {
//                        getStories()
//                    }
                }
            }
        })
//        imgNearbyUserLikes.setOnClickListener(View.OnClickListener {
//        })
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(GetUserDataQuery(Uid!!)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponseException ${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                hideProgressView()
                return@launchWhenResumed
            }
            hideProgressView()
            val userData = res.data?.user
            try {
                try {
                    val avatarUrl = if (!BuildConfig.USE_S3) {
                        if (userData?.avatar?.url.toString()
                                .startsWith(BuildConfig.BASE_URL)
                        ) userData?.avatar?.url.toString()
                        else "${BuildConfig.BASE_URL}${userData?.avatar?.url}"
                    } else if (userData?.avatar?.url.toString()
                            .startsWith(ApiUtil.S3_URL)
                    ) userData?.avatar?.url.toString()
                    else ApiUtil.S3_URL.plus(userData?.avatar?.url.toString())

                    thumbnail.loadCircleImage(avatarUrl)
                } catch (e: Exception) {
                    e.stackTrace
                }
            } catch (e: Exception) {
            }
        }


        likes_view.setOnClickListener {
            if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            buttonSlideUp.text = "Slide Down";
                if (curStoryType != "video") {
                    pauseTimer()
                } else {
                    pauseTimerAndPlayer()
                }
            } else {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            buttonSlideUp.text = "Slide Up"
                if (curStoryType != "video") {
                    startTimer()
                } else {
                    resumeTimerAndPlayer()
                }
            }
        }
        likes_l.setOnClickListener {
            if (Uid.equals(giftUserid)) {
                if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            buttonSlideUp.text = "Slide Down";
                    if (curStoryType != "video") {
                        pauseTimer()
                    } else {
                        pauseTimerAndPlayer()
                    }
                } else {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            buttonSlideUp.text = "Slide Up"
                    if (curStoryType != "video") {
                        startTimer()
                    } else {
                        resumeTimerAndPlayer()
                    }
                }
            } else {
                //            showProgressView()
                lifecycleScope.launchWhenResumed {
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).mutation(LikeOnStoryMutation(objectID!!, "story")).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception${e.message}")
//                    binding.root.snackbar("Exception ${e.message}")
                        Toast.makeText(
                            requireContext(), "${e.message}", Toast.LENGTH_LONG
                        ).show()
//                    hideProgressView()
                        return@launchWhenResumed
                    }
                    if (res.hasErrors()) {
                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.something_went_wrong)} ${res.errors?.get(0)?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
//                        fireLikeStoryNotification(objectID.toString(), giftUserid)
                        RefreshStories()
                        Log.e(
                            TAG,
                            "LikeStory response data ${res.data?.genericLike}"
                        )
                    }
//                hideProgressView()
                    /*       val usermoments = res.data?.res.data?.genericLike
                           Log.e("LIKE")
       */
                }
            }
        }
        comment_l.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            buttonSlideUp.text = "Slide Down";
                if (adapter != null) {
                    adapter?.notifyDataSetChanged()
                }
                if (curStoryType != "video") {
                    pauseTimer()
                } else {
                    pauseTimerAndPlayer()
                }
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            buttonSlideUp.text = "Slide Up"
                if (curStoryType != "video") {
                    startTimer()
                } else {
                    resumeTimerAndPlayer()
                }
            }
        }
        gift_l.setOnClickListener {
            if (Uid.equals(giftUserid)) {
                ///Toast.makeText(requireContext(), getString(R.string.user_cant_bought_gift_yourseld), Toast.LENGTH_LONG).show()
                if (receivedGiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    if (curStoryType != "video") {
                        pauseTimer()
                    } else {
                        pauseTimerAndPlayer()
                    }
                } else {
                    receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    if (curStoryType != "video") {
                        startTimer()
                    } else {
                        resumeTimerAndPlayer()
                    }
                }
            } else {
                if (GiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            buttonSlideUp.text = "Slide Down";
                    if (curStoryType != "video") {
                        pauseTimer()
                    } else {
                        pauseTimerAndPlayer()
                    }

                } else {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            buttonSlideUp.text = "Slide Up"
                    if (curStoryType != "video") {
                        startTimer()
                    } else {
                        resumeTimerAndPlayer()
                    }
                }

            }
        }


        delete_story.setOnClickListener {
            if (curStoryType != "video") {
                pauseTimer()
            } else {
                pauseTimerAndPlayer()
            }
            deleteConfirmation()
        }

        imgReport.setOnClickListener {
            if (curStoryType != "video") {
                pauseTimer()
            } else {
                pauseTimerAndPlayer()
            }

            reportDialog()

        }
    }

    private fun reportDialog() {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportView = dialogLayout.findViewById<TextView>(R.id.report_view)
        val reportMessage = dialogLayout.findViewById<EditText>(R.id.report_message)
        val okButton = dialogLayout.findViewById<TextView>(R.id.ok_button)
        val cancleButton = dialogLayout.findViewById<TextView>(R.id.cancel_button)

//        reportView.text = "${AppStringConstant(getMainActivity()).are_you_sure_you_want_to_delete_story}"
        okButton.text = "${AppStringConstant(getMainActivity()).ok}"
        cancleButton.text = "${AppStringConstant(getMainActivity()).cancel}"

        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        dialog.setOnDismissListener {
            if (curStoryType != "video") {
                startTimer()
            } else {
                resumeTimerAndPlayer()
            }
        }

        okButton.setOnClickListener {
            val message = reportMessage.text.toString()
            showProgressView()
            lifecycleScope.launchWhenResumed {
                val res = try {
                    apolloClient(
                        requireContext(), userToken!!
                    ).mutation(ReportStoryMutation(objectID.toString(), message)).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception ${e.message}")
                    Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()

                    hideProgressView()
                    dialog.dismiss()
                    return@launchWhenResumed
                }

                if (res.hasErrors()) {

                } else {
                    Log.e(
                        TAG,
                        "rsponceSuccess : ${res.data!!.reportStory!!.storyReport!!.story.pk.toString()}"
                    )
                }

                if (curStoryType != "video") {
                    startTimer()
                } else {
                    resumeTimerAndPlayer()
                }

                hideProgressView()
                dialog.dismiss()
            }
        }

        cancleButton.setOnClickListener {
            if (curStoryType != "video") {
                startTimer()
            } else {
                resumeTimerAndPlayer()
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()

        if (this::exoPlayer.isInitialized) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                exoPlayer.stop()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (this::exoPlayer.isInitialized) exoPlayer.release()
    }


    private fun setStory(tag: String, pos: Int) {
        LogUtil.debug("Position : $tag : $pos")
        Log.e(TAG, "Position : $tag :  $pos")

        val node = stories[pos]?.node
        val url = if (!BuildConfig.USE_S3) {
            if (node?.file.toString().startsWith(BuildConfig.BASE_URL)) node?.file.toString()
            else "${BuildConfig.BASE_URL}${node?.file.toString()}"
        } else if (node?.file.toString().startsWith(ApiUtil.S3_URL)) node?.file.toString()
        else ApiUtil.S3_URL.plus(node?.file.toString())
        Log.e(TAG, "setStory: $url")
        controlProgressBar(pos)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        curStoryType = node?.fileType.toString()

        val relativeLayoutCenter = views.findViewById<RelativeLayout>(R.id.relativeLayoutCenter)
        if (node?.fileType != "video") {
            player_view!!.visibility = View.GONE
            pause.setViewGone()
            imgUserStory.visibility = View.VISIBLE

            loadGlideImage(url)

//            imgUserStory.loadImage(url, {
//
//            }, {
//                if (context != null) {
//                    Toast.makeText(context, "Failed to load story", Toast.LENGTH_LONG).show()
//                    restartTimer()
//                }
//                //dismiss()
//            })


            startDismissCountDown1()
            userurl = ""
            progressBar1!!.progress = 0
            Log.e(TAG, "Position $pos Progreessbar ${progressBar1!!.progress} $ node $node")
            userurl = if (node?.user?.avatar?.url != null) {
                if (!BuildConfig.USE_S3) {
                    if (node.user.avatar.url.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) node.user.avatar.url.toString()
                    else "${BuildConfig.BASE_URL}${node.user.avatar.url}"
                } else if (node.user.avatar.url.toString()
                        .startsWith(ApiUtil.S3_URL)
                ) node.user.avatar.url.toString()
                else ApiUtil.S3_URL.plus(node.user.avatar.url.toString())
            } else {
                ""
            }

            val fullName = node?.user?.fullName.toString()
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            username = name

            setOtherData(node, node?.user)

//            var text = node?.createdDate.toString()
//            text = text.replace("T", " ").substring(0, text.indexOf("."))
//            val momentTime = formatter.parse(text)

            val momentTime = try {
                var text = node?.createdDate.toString()
                text = text.replace("T", " ").substring(0, text.indexOf("."))
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }

            val times = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )

            var publishAt = node?.publishAt.toString()
            Log.e(TAG, "setStory: $publishAt")
            var publishTimeInMillis = ""
            if (publishAt.isNotEmpty() && publishAt != "null") {
                publishAt = publishAt.replace("T", " ").substring(0, publishAt.indexOf("+"))
                val momentPublishTime = formatter.parse(publishAt)
                publishTimeInMillis = DateUtils.getRelativeTimeSpanString(
                    momentPublishTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }

            Log.e(TAG, "setStory: $times $publishTimeInMillis")

            objectID = node?.pk
//            RefreshStories()
//            getStories()
            txtTimeAgo.text = publishTimeInMillis.ifEmpty { times }

        } else {
            progressBar.setVisibility(View.VISIBLE)
            pause.setViewGone()
            player_view!!.visibility = View.VISIBLE
            relativeLayoutCenter.setOnClickListener {
                if (exoPlayer.isPlaying) pauseTimerAndPlayer()
                else resumeTimerAndPlayer()
            }
            imgUserStory.visibility = View.GONE
            userurl = ""
            progressBar1!!.progress = 0
            Log.e(TAG, "Position $pos Progreessbar ${progressBar1!!.progress} $ node $node")
            userurl = if (node.user?.avatar?.url != null) {
                if (!BuildConfig.USE_S3) {
                    if (node.user.avatar.url.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) node.user.avatar.url.toString()
                    else "${BuildConfig.BASE_URL}${node.user.avatar.url}"
                } else if (node.user.avatar.url.toString()
                        .startsWith(ApiUtil.S3_URL)
                ) node.user.avatar.url.toString()
                else ApiUtil.S3_URL.plus(node.user.avatar.url.toString())
            } else {
                ""
            }
            val fullName = node.user?.fullName.toString()
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            username = name
            node.user?.let { setOtherDataForVideo(node, it) }
            var text = node.createdDate.toString()
            text = text.replace("T", " ").substring(0, text.indexOf("."))
            val momentTime = formatter.parse(text)
            val times = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )

            var publishAt = node.publishAt.toString()
            var publishTimeInMillis = ""
            if (publishAt.isNotEmpty() && publishAt != "null") {
                publishAt = publishAt.replace("T", " ").substring(0, publishAt.indexOf("+"))
                val momentPublishTime = formatter.parse(publishAt)
                publishTimeInMillis = DateUtils.getRelativeTimeSpanString(
                    momentPublishTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }


            objectID = node.pk
//            RefreshStories()
//            getStories()

            txtTimeAgo.text = if (publishTimeInMillis.isNotEmpty()) publishTimeInMillis else times
            val uri: Uri = Uri.parse(url)
//            val uri: Uri = Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4")
            //val uri: Uri = Uri.fromFile(File(arguments?.getString("path")))
            val mediaItem = MediaItem.Builder().setUri(uri).setMimeType(MimeTypes.VIDEO_MP4).build()
            playView(mediaItem)
        }

    }


    private fun controlProgressBar(position: Int) {
        LogUtil.debug("Here 1 $position")
        if (position != 0) {
            LogUtil.debug("Here 2 $position")
            for (i in 0 until position) {
                LogUtil.debug("Here 3 $i")
                progressBarList[i].max = 1500
                progressBarList[i].progress = 1500
//                progressBarList[i].setProgress(3000, false)
            }
        }
        if (position != stories.size - 1) {
            LogUtil.debug("Here 4 $position")
            for (i in position + 1 until stories.size) {
                LogUtil.debug("Here 5 $position")
                progressBarList[i].progress = 0
//                progressBarList[i].setProgress(0, false)
            }
        }
        progressBarList[position].progress = 0
        curProgressBar = progressBarList[position]
    }


    //    private fun subscribeForUpdateStory() {
    private fun subscribeForUpdateStory(storiesdd: List<GetAllUserStoriesQuery.Edge?>) {


        var storiesPkList = ArrayList<Int>()

        storiesdd.indices.forEach {
            var pkss = storiesdd[it]?.node!!.pk
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
                    delay(attempt * 1000)
                    true
                }.collect { newStory ->
                    if (newStory.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newStory.errors?.get(0)?.message}"
                        )
                    } else {
                        //   Log.e("reealltime onNewMessage ${newMessage.data?.onNewMessage?.message?.timestamp}")
                        Log.e(
                            TAG,
                            " story realtime NewStoryData content ${newStory.data?.onUpdateStory}"
                        )
                        Log.e(TAG, "story realtime NewStoryData ${newStory.data}")
                        Log.e(
                            TAG,
                            "story realtime Batchnumber ${newStory.data?.onUpdateStory?.storyId}"
                        )
//                            val user =
//                                stories.filter { it?.user?.id == newStory.data?.onNewStory?.user?.id }
//                                    .filter { it?.batchNumber == newStory.data?.onNewStory?.batchNumber }

                        Log.e(TAG, "story realtime NewStoryData ${newStory.data}")

//                            val replaceStory =
//                                stories.filter { it!!.node?.pk.toString() == newStory.data?.onUpdateStory?.storyId.toString() }

                        if (items.size > 0) {
                            RefreshStories()

                        } else {

                            getStories()
                        }

//                            val position = stories.indexOf(replaceStory[0])
//                          var storiesUpdate =  stories.get(position)

//                            storiesUpdate?.node?.comments=newStory.data.onUpdateStory.comments
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "story realtime exception= ${e2.message}")
            }
        }
    }


    private fun getStories() {

//        showProgressView()
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserStoriesQuery(
                        100, "", objectID.toString(), ""
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponseException ${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
//                hideProgressView()
                return@launchWhenResumed
            }
            Log.e(TAG, "apolloResponse allUserStories stories ${res.hasErrors()}")

//            hideProgressView()
            val allUserStories = res.data?.allUserStories!!.edges
            if (allUserStories.isNotEmpty()) {
                subscribeForUpdateStory(allUserStories)

                val likeCount = allUserStories.get(0)!!.node!!.likesCount.toString()
                val commentCount = allUserStories.get(0)!!.node!!.commentsCount.toString()
                txtNearbyUserLikeCount!!.text = likeCount
                txtNearbyUserLike!!.text = "${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                lblItemNearbyCommentCount!!.text = commentCount
                lblItemNearbyUserCommentCount!!.text =
                    "${requireActivity().resources.getString(R.string.comments)}"
                txtheaderlike!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"

                /*txtNearbyUserLikeCount!!.text = allUserStories.get(0)!!.node!!.likesCount.toString()
                txtMomentRecentComment!!.text =
                    allUserStories.get(0)!!.node!!.commentsCount.toString() + " ${ requireActivity().resources.getString(R.string.comments) }"
                lblItemNearbyUserCommentCount!!.text =
                    allUserStories.get(0)!!.node!!.commentsCount.toString() + " ${ requireActivity().resources.getString(R.string.comments) }"
                txtheaderlike!!.text =
                    allUserStories.get(0)!!.node!!.likesCount.toString() + " ${ requireActivity().resources.getString(R.string.like) }"*/
                giftUserid = allUserStories.get(0)!!.node!!.user!!.id.toString()


                val likeData = allUserStories.get(0)!!.node!!.likes!!.edges


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
                if (likeData.size > 0) {
                    Log.e(TAG, "apolloResponse: ${likeData[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE


                    val items1: ArrayList<CommentsModel> = ArrayList()


                    likeData.indices.forEach { i ->
                        val models = CommentsModel()
                        val fullName = likeData[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.commenttext = name
                        models.userurl =
                            if (likeData[i]?.node?.user?.avatar != null && !likeData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                                if (!BuildConfig.USE_S3) {
                                    if (likeData[i]?.node?.user?.avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) likeData[i]?.node?.user?.avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${likeData[i]?.node?.user?.avatar?.url}"
                                } else if (likeData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) likeData[i]?.node?.user?.avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(likeData[i]?.node?.user?.avatar?.url.toString())
                            } else {
                                ""
                            }

                        models.uid = likeData[i]!!.node!!.user.id

                        items1.add(models)

                    }


                    adapters = StoryLikesAdapter(
                        requireActivity(), items1, glide
                    )

                    adapters?.userProfileClicked {
                        Log.e(TAG, "UserStoryDetailFragmt : $it")
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
                    no_data!!.visibility = View.VISIBLE
                    rvLikes!!.visibility = View.GONE

                }

                val commentData = allUserStories.get(0)!!.node!!.comments!!.edges

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
                if (commentData.size > 0) {
                    Log.e(TAG, "apolloResponse: ${commentData.get(0)?.node!!.commentDescription}")
                    nodata!!.visibility = View.GONE
                    rvSharedMoments!!.visibility = View.VISIBLE


                    items = ArrayList()


                    commentData.indices.forEach { i ->
                        val hm: MutableList<ReplysModel> = ArrayList()


                        val models = CommentsModel()

                        models.commenttext = commentData[i]!!.node!!.commentDescription

//                        if (Commentdata[i]!!.node!!.user.avatarPhotos!!.size > 0) {
                        models.userurl =
                            if (commentData[i]?.node?.user?.avatar != null && !commentData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                                if (!BuildConfig.USE_S3) {
                                    if (commentData[i]?.node?.user?.avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) commentData[i]?.node?.user?.avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${commentData[i]?.node?.user?.avatar?.url}"
                                } else if (commentData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) commentData[i]?.node?.user?.avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(commentData[i]?.node?.user?.avatar?.url.toString())
                            } else {
                                ""
                            }

                        val fullName = commentData[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.username = name
                        models.timeago = commentData[i]!!.node!!.createdDate.toString()
                        models.cmtID = commentData[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.uid = commentData[i]!!.node!!.user.id.toString()
                        models.cmtlikes = commentData[i]!!.node!!.likesCount.toString()

                        for (f in 0 until commentData[i]!!.node!!.replys!!.edges.size) {


                            val md = ReplysModel()

                            md.replytext =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url

                            val fullName =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            val name = if (fullName != null && fullName.length > 15) {
                                fullName.substring(0, minOf(fullName.length, 15))
                            } else {
                                fullName
                            }
                            md.usernames = name
                            md.timeago =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.createdDate.toString()
                            md.uid =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.id.toString()


                            hm.add(f, md)


                        }

                        models.replylist = hm
                        models.isExpanded = true



                        items.add(models)
                    }



                    rvSharedMoments!!.layoutManager = LinearLayoutManager(activity)
                    adapter = MultiStoryCommentListAdapter(
                        requireActivity(),
                        this@UserMultiStoryDetailFragment,
                        items,
                        this@UserMultiStoryDetailFragment,
                        showDelete,
                        Uid!!
                    )
                    rvSharedMoments!!.adapter = adapter
                    adapter?.notifyDataSetChanged()

                } else {
                    nodata!!.visibility = View.VISIBLE
                    rvSharedMoments!!.visibility = View.GONE

                }

            }


        }
    }


    private fun RefreshStories() {
        Log.e(TAG, "UserStoryDetailsFragmt : ${objectID.toString()}")
//        showProgressView()
        lifecycleScope.launchWhenResumed {
            val res = try {

                apolloClient(requireContext(), userToken!!).query(
                    GetAllUserStoriesQuery(
                        100, "", objectID.toString(), ""
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponseException ${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()

//                hideProgressView()
                return@launchWhenResumed
            }

            Log.e(TAG, "apolloResponse allUserStories stories ${res.hasErrors()}")

            if (res.hasErrors()) {

                Toast.makeText(
                    requireContext(),
                    "${requireActivity().resources.getString(R.string.something_went_wrong)} ${
                        res.errors?.get(
                            0
                        )?.message
                    }",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Log.e(TAG, "LikeStory Refresh data ${res.data?.allUserStories}")
            }

//            hideProgressView()
            val allUserStories = res.data?.allUserStories!!.edges
            if (allUserStories != null && allUserStories.size != 0) {
                txtNearbyUserLikeCount!!.text = "" + allUserStories.get(0)!!.node!!.likesCount
                txtNearbyUserLike!!.text =
                    "" + "${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    "" + allUserStories.get(0)!!.node!!.commentsCount + " ${
                        requireActivity().resources.getString(R.string.comments)
                    }"
                lblItemNearbyCommentCount!!.text = "" + allUserStories.get(0)!!.node!!.commentsCount
                lblItemNearbyUserCommentCount!!.text =
                    "" + "${requireActivity().resources.getString(R.string.comments)}"
                txtheaderlike!!.text = allUserStories.get(0)!!.node!!.likesCount.toString() + " ${
                    requireActivity().resources.getString(R.string.like)
                }"

                giftUserid = allUserStories.get(0)!!.node!!.user!!.id.toString()


                val Likes = allUserStories.get(0)!!.node!!.likes!!.edges
                Likes.forEach {
                    Log.e(TAG, "RefreshStories: Likes:  $Likes")
                }
                val likeData = allUserStories.get(0)!!.node!!.likes!!.edges
                if (likeData.size > 0) {
                    Log.e(TAG, "apolloResponse: ${likeData[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE

                    val items1: java.util.ArrayList<CommentsModel> = java.util.ArrayList()

                    likeData.indices.forEach { i ->

                        val models = CommentsModel()
                        val fullName = likeData[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.commenttext = name

//                        models.userurl = Likedata[i]!!.node!!.user.avatar!!.url
                        models.userurl =
                            if (likeData[i]?.node?.user?.avatar != null && !likeData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                                if (!BuildConfig.USE_S3) {
                                    if (likeData[i]?.node?.user?.avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) likeData[i]?.node?.user?.avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${likeData[i]?.node?.user?.avatar?.url}"
                                } else if (likeData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) likeData[i]?.node?.user?.avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(likeData[i]?.node?.user?.avatar?.url.toString())
                            } else {
                                ""
                            }


                        items1.add(models)

                    }

                    if (adapters == null) {
                        adapters = StoryLikesAdapter(requireActivity(), items1, glide)
                        adapters?.userProfileClicked {
                            Log.e(TAG, "userProfileClick :$it")
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

                val commentData = allUserStories.get(0)!!.node!!.comments!!.edges

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

                if (commentData.size > 0) {
                    Log.e(TAG, "apolloResponse: ${commentData.get(0)?.node!!.commentDescription}")
                    nodata!!.visibility = View.GONE
                    rvSharedMoments!!.visibility = View.VISIBLE
                    val items1: ArrayList<CommentsModel> = ArrayList()
                    commentData.indices.forEach { i ->
                        val hm: MutableList<ReplysModel> = ArrayList()
                        val models = CommentsModel()
                        models.commenttext = commentData[i]!!.node!!.commentDescription
                        if (commentData[i]!!.node!!.user.avatarPhotos!!.isNotEmpty()) {
                            try {
                                models.userurl =
                                    if (commentData[i]?.node?.user?.avatar != null && !commentData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                                        if (!BuildConfig.USE_S3) {
                                            if (commentData[i]?.node?.user?.avatar?.url.toString()
                                                    .startsWith(BuildConfig.BASE_URL)
                                            ) commentData[i]?.node?.user?.avatar?.url.toString()
                                            else "${BuildConfig.BASE_URL}${commentData[i]?.node?.user?.avatar?.url}"
                                        } else if (commentData[i]?.node?.user?.avatar?.url.toString()
                                                .startsWith(ApiUtil.S3_URL)
                                        ) commentData[i]?.node?.user?.avatar?.url.toString()
                                        else ApiUtil.S3_URL.plus(commentData[i]?.node?.user?.avatar?.url.toString())
                                    } else {
                                        ""
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                models.userurl = ""
                            }
                        } else {
                            models.userurl = ""
                        }
                        val fullName = commentData[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.username = name
                        models.timeago = commentData[i]!!.node!!.createdDate.toString()
                        models.cmtID = commentData[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.cmtlikes = commentData[i]!!.node!!.likesCount.toString()

                        models.uid = commentData[i]!!.node!!.user.id.toString()

                        for (f in 0 until commentData[i]!!.node!!.replys!!.edges.size) {


                            val md = ReplysModel()

                            md.replytext =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url
                            val fullName =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            val name = if (fullName != null && fullName.length > 15) {
                                fullName.substring(0, minOf(fullName.length, 15))
                            } else {
                                fullName
                            }
                            md.usernames = name

                            md.timeago =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.createdDate.toString()
                            md.uid =
                                commentData[i]!!.node!!.replys!!.edges[f]!!.node!!.user.id.toString()

                            hm.add(f, md)


                        }

                        models.replylist = hm
                        models.isExpanded = true
                        items1.add(models)

                    }

                    if (items1.size != 0) {
                        if (items.size != 0) {
                            if (adapter != null) {
                                adapter!!.updateList(items1)
                            } else {
                                rvSharedMoments!!.layoutManager = LinearLayoutManager(activity)
                                adapter = MultiStoryCommentListAdapter(
                                    requireActivity(),
                                    this@UserMultiStoryDetailFragment,
                                    items1,
                                    this@UserMultiStoryDetailFragment,
                                    showDelete,
                                    Uid!!
                                )
                                rvSharedMoments!!.adapter = adapter
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    nodata!!.visibility = View.VISIBLE
                    rvSharedMoments!!.visibility = View.GONE
                }
            }
        }
    }

    fun getMyContext(): Context {
        return context ?: throw IllegalStateException("Fragment $this not attached to a context.")
    }

    private fun setOtherDataForVideo(
        node: GetAllUserMultiStoriesQuery.Node, user: GetAllUserMultiStoriesQuery.User3
    ) {
        if (context != null && isAdded) {
            txtNearbyUserLikeCount?.text = node.likesCount.toString()
            txtNearbyUserLike?.text = getMyContext().resources.getString(R.string.like)
            txtMomentRecentComment!!.text =
                node.commentsCount.toString() + " ${getMyContext().resources.getString(R.string.comments)}"
            lblItemNearbyCommentCount!!.text = node.commentsCount.toString()
            lblItemNearbyUserCommentCount!!.text =
                "${getMyContext().resources.getString(R.string.comments)}"
            txtheaderlike!!.text =
                node.likesCount.toString() + " ${getMyContext().resources.getString(R.string.like)}"
            giftUserid = node.user!!.id.toString()
            val likeData = node.likes!!.edges
            if (rvLikes!!.itemDecorationCount == 0) {
                rvLikes!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.top = 25
                    }
                })
            }
            if (likeData.isNotEmpty()) {
                Log.e(TAG, "apolloResponse: ${likeData[0]!!.node!!.id}")
                no_data!!.visibility = View.GONE
                rvLikes!!.visibility = View.VISIBLE
                val items1: ArrayList<CommentsModel> = ArrayList()
                likeData.indices.forEach { i ->
                    val models = CommentsModel()
                    val fullName = likeData[i]!!.node!!.user.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    models.commenttext = name
                    models.userurl =
                        if (likeData[i]?.node?.user?.avatar != null && !likeData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                            if (!BuildConfig.USE_S3) {
                                if (likeData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(BuildConfig.BASE_URL)
                                ) likeData[i]?.node?.user?.avatar?.url.toString()
                                else "${BuildConfig.BASE_URL}${likeData[i]?.node?.user?.avatar?.url}"
                            } else if (likeData[i]?.node?.user?.avatar?.url.toString()
                                    .startsWith(ApiUtil.S3_URL)
                            ) likeData[i]?.node?.user?.avatar?.url.toString()
                            else ApiUtil.S3_URL.plus(likeData[i]?.node?.user?.avatar?.url.toString())
                        } else {
                            ""
                        }
                    items1.add(models)

                }
                adapters = StoryLikesAdapter(requireActivity(), items1, glide)
                adapters?.userProfileClicked {
                    Log.e(TAG, "UserStoryDetailsFragment : $it")
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
                no_data!!.visibility = View.VISIBLE
                rvLikes!!.visibility = View.GONE
            }
            val commentData = node.comments!!.edges
            if (rvSharedMoments!!.itemDecorationCount == 0) {
                rvSharedMoments!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.top = 25
                    }
                })
            }
            if (commentData.isNotEmpty()) {
                Log.e(TAG, "apolloResponse: ${commentData[0]?.node!!.commentDescription}")
                nodata!!.visibility = View.GONE
                rvSharedMoments!!.visibility = View.VISIBLE
                items = ArrayList()
                commentData.indices.forEach { i ->
                    val hm: MutableList<ReplysModel> = ArrayList()
                    val models = CommentsModel()
                    models.commenttext = commentData[i]!!.node!!.commentDescription
//                if (Commentdata[i]!!.node!!.user.avatarPhotos!!.isNotEmpty()) {
                    models.userurl =
                        if (commentData[i]?.node?.user?.avatar != null && !commentData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                            if (!BuildConfig.USE_S3) {
                                if (commentData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(BuildConfig.BASE_URL)
                                ) commentData[i]?.node?.user?.avatar?.url.toString()
                                else "${BuildConfig.BASE_URL}${commentData[i]?.node?.user?.avatar?.url}"
                            } else if (commentData[i]?.node?.user?.avatar?.url.toString()
                                    .startsWith(ApiUtil.S3_URL)
                            ) commentData[i]?.node?.user?.avatar?.url.toString()
                            else ApiUtil.S3_URL.plus(commentData[i]?.node?.user?.avatar?.url.toString())
                        } else {
                            ""
                        }
                    val fullName = commentData[i]!!.node!!.user.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    models.username = name
                    models.timeago = commentData[i]!!.node!!.createdDate.toString()
                    models.cmtID = commentData[i]!!.node!!.pk.toString()
                    models.momentID = objectID?.toString()

                    models.uid = commentData[i]!!.node!!.user.id.toString()

                    models.replylist = hm
                    models.isExpanded = true
                    items.add(models)
                }
                adapter2 = VideoMultiStoryCommentListAdapter(
                    requireActivity(), this, items, this
                )
                rvSharedMoments!!.adapter = adapter2
                rvSharedMoments!!.layoutManager = LinearLayoutManager(activity)
            } else {
                nodata!!.visibility = View.VISIBLE
                rvSharedMoments!!.visibility = View.GONE
            }
        }
    }

    private fun setOtherData(
        node: GetAllUserMultiStoriesQuery.Node?, user: GetAllUserMultiStoriesQuery.User3?
    ) {
        if (node != null && user != null) {
            if (activity == null || !isAdded) return

            txtNearbyUserLikeCount!!.text = node.likesCount.toString()
            txtNearbyUserLike!!.text = "${requireActivity().resources.getString(R.string.like)}"
            txtMomentRecentComment!!.text =
                node.commentsCount.toString() + " ${requireActivity().resources.getString(R.string.comments)}"
            lblItemNearbyCommentCount!!.text = node.commentsCount.toString()
            lblItemNearbyUserCommentCount!!.text =
                "${requireActivity().resources.getString(R.string.comments)}"
            txtheaderlike!!.text =
                node.likesCount.toString() + " ${requireActivity().resources.getString(R.string.like)}"
            giftUserid = user.id.toString()

            if (rvLikes!!.itemDecorationCount == 0) {
                rvLikes!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.top = 25
                    }
                })
            }

            val likeData = node.likes!!.edges

            if (likeData.isNotEmpty()) {
                Log.e(TAG, "apolloResponse: ${likeData[0]!!.node!!.id}")
                no_data!!.visibility = View.GONE
                rvLikes!!.visibility = View.VISIBLE
                val items1: ArrayList<CommentsModel> = ArrayList()
                likeData.indices.forEach { i ->
                    val models = CommentsModel()
                    val fullName = likeData[i]!!.node!!.user.fullName
                    val name = if (fullName != null && fullName.length > 15) {
                        fullName.substring(0, minOf(fullName.length, 15))
                    } else {
                        fullName
                    }
                    models.commenttext = name
                    models.userurl =
                        if (likeData[i]?.node?.user?.avatar != null && !likeData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                            if (!BuildConfig.USE_S3) {
                                if (likeData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(BuildConfig.BASE_URL)
                                ) likeData[i]?.node?.user?.avatar?.url.toString()
                                else "${BuildConfig.BASE_URL}${likeData[i]?.node?.user?.avatar?.url}"
                            } else if (likeData[i]?.node?.user?.avatar?.url.toString()
                                    .startsWith(ApiUtil.S3_URL)
                            ) likeData[i]?.node?.user?.avatar?.url.toString()
                            else ApiUtil.S3_URL.plus(likeData[i]?.node?.user?.avatar?.url.toString())
                        } else {
                            ""
                        }
                    models.uid = likeData[i]!!.node!!.user.id
                    items1.add(models)
                }

                if (requireActivity() != null) {
                    adapters = StoryLikesAdapter(
                        requireActivity(), items1, glide
                    )
                }
                adapters?.userProfileClicked {
                    Log.e(TAG, "userProfileClicked : $it")
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
                no_data!!.visibility = View.VISIBLE
                rvLikes!!.visibility = View.GONE
            }

            val commentData = node.comments!!.edges

            if (rvSharedMoments!!.itemDecorationCount == 0) {
                rvSharedMoments!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.top = 25
                    }
                })
            }
            if (commentData.isNotEmpty()) {
                Log.e(TAG, "apolloResponse: ${commentData[0]?.node!!.commentDescription}")
                nodata!!.visibility = View.GONE
                rvSharedMoments!!.visibility = View.VISIBLE
                items = ArrayList()
                commentData.indices.forEach { i ->
                    try {
                        Log.e(TAG, "setOtherData: apolloResponse: ${commentData[i]}}")
                        val hm: MutableList<ReplysModel> = ArrayList()
                        val models = CommentsModel()
                        models.commenttext = commentData[i]!!.node!!.commentDescription
                        models.userurl =
                            if (commentData[i]?.node?.user?.avatar != null && !commentData[i]?.node?.user?.avatar?.url.isNullOrEmpty()) {
                                if (!BuildConfig.USE_S3) {
                                    if (commentData[i]?.node?.user?.avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) commentData[i]?.node?.user?.avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${commentData[i]?.node?.user?.avatar?.url}"
                                } else if (commentData[i]?.node?.user?.avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) commentData[i]?.node?.user?.avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(commentData[i]?.node?.user?.avatar?.url.toString())
                            } else {
                                ""
                            }
                        val fullName = commentData[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.username = name
                        models.timeago = commentData[i]!!.node!!.createdDate.toString()
                        models.cmtID = commentData[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.uid = commentData[i]!!.node!!.user.id.toString()


                        models.replylist = hm
                        models.isExpanded = true

                        items.add(models)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
//            Activity activity = Main getActivity();
                var activities = MainActivity.getMainActivity()
//            if(activity != null && isAdded())

                if (activities != null) {
                    adapter = MultiStoryCommentListAdapter(
                        activities,
                        this@UserMultiStoryDetailFragment,
                        items,
                        this@UserMultiStoryDetailFragment,
                        showDelete,
                        Uid!!
                    )

                    rvSharedMoments!!.adapter = adapter
                    rvSharedMoments!!.layoutManager = LinearLayoutManager(activity)
                }
            } else {
                nodata!!.visibility = View.VISIBLE
                rvSharedMoments!!.visibility = View.GONE
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun playView(mediaItem: MediaItem) {
        showProgressView()
        if (MainActivity.getMainActivity() != null) {
            exoPlayer = ExoPlayer.Builder(MainActivity.getMainActivity()!!).build().apply {
                playWhenReady = isPlayerPlaying
                seekTo(currentWindow, playbackPosition)
                setMediaItem(mediaItem, false)
                prepare()
            }


            player_view?.player = exoPlayer
            var durationSet = false
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                        progressBar.visibility = View.GONE
                        playerReadyListener?.onPlayerReady()
                        val realDurationMillis: Long = exoPlayer.duration
                        durationSet = true
                        val duration = realDurationMillis
                        Log.e(TAG, "duration ${duration}")
                        Log.e(TAG, "filee $duration")
                        progressBar1!!.max = realDurationMillis.toInt()
                        curProgressBar!!.max = realDurationMillis.toInt()
                        this@UserMultiStoryDetailFragment.realDurationMillis =
                            realDurationMillis.toInt()
                        val millisInFuture = duration
                        LogUtil.debug("Millies : : : $millisInFuture")
                        timer1 = object : CountDownTimerExt(millisInFuture, 100) {
                            override fun onTimerTick(millisUntilFinished: Long) {
                                Log.e(TAG, "onTimerTick $millisUntilFinished")
//                            LogUtil.debug("onTimerTick $millisUntilFinished")
                                onTickProgressUpdate2()
                            }

                            override fun onTimerFinish() {
                                Log.e(TAG, "onTimerFinish")
                                LogUtil.debug("onTimeFinish")
                                if (positionForStory < stories.size - 1) {
                                    positionForStory += 1
                                    tickTime = 3000
                                    countUp = 0
                                    setStory("onStart:320", positionForStory)
                                } else {
                                    dismiss()
                                }
                            }
                        }
                        hideProgressView()
                        timer1.run {
                            this!!.start()
                        }
                    }
                }

                fun onPlayWhenReadyCommitted() {

                }

                fun onPlayerError(error: ExoPlaybackException) {

                }
            })
        }
    }

    fun pauseTimerAndPlayer() {
        if (timer1 != null) {
            timer1!!.pause()
        }
        if (this::exoPlayer.isInitialized) {
            exoPlayer.playWhenReady = false
            pause.visibility = View.VISIBLE
        }
    }

    fun resumeTimerAndPlayer() {
        if (timer1 != null) {
            timer1!!.start()
        }
        if (this::exoPlayer.isInitialized) {
            exoPlayer.playWhenReady = true
            pause.visibility = View.GONE
        }
    }

    fun fireLikeStoryNotification(pkids: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder().append("mutation {").append("$queryName (")
                .append("userId: \"${userid}\", ").append("notificationSetting: \"STLIKE\", ")
                .append("data: {pk:${pkids}}").append(") {").append("sent").append("}").append("}")
                .toString()
            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query, queryName, userToken
            )
            Log.e(TAG, "RSLT" + "" + result.message)
        }
    }

    fun fireCommntStoryNotification(pkids: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder().append("mutation {").append("$queryName (")
                .append("userId: \"${userid}\", ").append("notificationSetting: \"STCMNT\", ")
                .append("data: {pk:${pkids}}").append(") {").append("sent").append("}").append("}")
                .toString()

            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query, queryName, userToken
            )
            Log.e(TAG, "RSLT" + "" + result.message)

        }
    }

    fun fireGiftBuyNotificationforreceiver(gid: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder().append("mutation {").append("$queryName (")
                .append("userId: \"${userid}\", ").append("notificationSetting: \"GIFT RLVRTL\", ")
                .append("data: {giftId:${gid}}").append(") {").append("sent").append("}")
                .append("}").toString()
            val result =
                AppModule.provideGraphqlApi().getResponse<Boolean>(query, queryName, userToken)


            Log.e(TAG, "RSLT" + "" + result.message)
        }
    }

    protected fun showProgressView() {
//        loadingDialog.show()
    }

    protected fun hideProgressView() {
//        loadingDialog.dismiss()
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
        LogUtil.debug("Millies : : : $tickTime")
        timer1 = object : CountDownTimerExt(tickTime, 100) {
            override fun onTimerTick(millisUntilFinished: Long) {
                Log.e(TAG, "onTimerTick $millisUntilFinished")
//                LogUtil.debug("onTimerTick1 $millisUntilFinished")
                onTickProgressUpdate(millisUntilFinished)
            }

            override fun onTimerFinish() {
                LogUtil.debug("onTimeFinish1")
                if (positionForStory < stories.size - 1) {
                    positionForStory += 1
                    tickTime = 3000
                    countUp = 0
                    Log.e(TAG, "set story 2197")
                    setStory("onStart:321", positionForStory)
                } else {
                    if (showsDialog) dismissAllowingStateLoss()
                }
            }
        }
        timer1.run { this!!.start() }
    }

    fun onTickProgressUpdate(milliSec: Long) {
        tickTime = milliSec
        countUp += 100
        val progress = countUp
        Log.e(TAG, "Progress : $progress")
        progressBar1!!.smoothProgress(progress)
        curProgressBar!!.smoothProgress(progress)
    }

    private fun onTickProgressUpdate2() {
        countUp += 100
        val progress = countUp
        Log.e(TAG, "Progress : $progress")
        progressBar1!!.smoothProgress(progress)
        curProgressBar!!.smoothProgress(progress)
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
                msg_write, InputMethodManager.SHOW_IMPLICIT
            )
            msg_write?.append("@" + models.username + " ")

        }, 150)


    }

    override fun oncommentLike(position: Int, models: CommentsModel) {
        Log.e(TAG, "OnCommentLikeVideo : ${models.commenttext}")

        showProgressView()
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(
                    requireContext(), userToken!!
                ).mutation(LikeOnStoryMutation(models.cmtID!!.toInt(), "genericcomment")).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                    binding.root.snackbar("Exception ${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()

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

    //    fun oncommentReport(position: Int, models: CommentsModel)
    override fun oncommentReport(position: Int, models: CommentsModel) {
        reportDialog(position, models)
    }

    private fun reportDialog(position: Int, models: CommentsModel) {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportView = dialogLayout.findViewById<TextView>(R.id.report_view)
        val reportMessage = dialogLayout.findViewById<EditText>(R.id.report_message)
        val okButton = dialogLayout.findViewById<TextView>(R.id.ok_button)
        val cancleButton = dialogLayout.findViewById<TextView>(R.id.cancel_button)

//        reportView.text = "${AppStringConstant(getMainActivity()).are_you_sure_you_want_to_delete_story}"
        okButton.text = "${AppStringConstant(requireActivity()).ok}"
        cancleButton.text = "${AppStringConstant(requireActivity()).cancel}"

        val builder = AlertDialog.Builder(activity, R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val dialog = builder.create()

        okButton.setOnClickListener {
            val message = reportMessage.text.toString()
            showProgressView()
            Log.e(TAG, "" + models.cmtID!!)
            lifecycleScope.launchWhenResumed {
                val res = try {
                    apolloClient(
                        requireContext(), userToken!!
                    ).mutation(ReportCommentMutation(message, models.cmtID!!.toInt()))
//                    ).mutation(ReportCommentMutation(models?.cmtID!!,message))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception ${e.message}")
//                    binding.root.snackbar(" ${e.message}")
                    hideProgressView()
                    dialog.dismiss()
                    return@launchWhenResumed
                }

                if (res.hasErrors()) {
                    val error = res.errors?.get(0)?.message
                    Log.e(TAG, "Exception momentCommentDelete $error")
//                    binding.root.snackbar(" $error")
                    hideProgressView()
                    dialog.dismiss()
                    return@launchWhenResumed
                } else {
                    RefreshStories()
                    hideProgressView()
                }

                dialog.dismiss()

            }
        }

        cancleButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun oncommentDelete(position: Int, models: CommentsModel) {
        showProgressView()
        lifecycleScope.launchWhenResumed {
            val res = try {
                apolloClient(
                    requireContext(), userToken!!
                ).mutation(
                    CommentDeleteMutation(
                        models.cmtID!!
                    )
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse Exception${e.message}")
//                binding.root.snackbar(" ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }

            if (res.hasErrors()) {
                val error = res.errors?.get(0)?.message
                Log.e(TAG, "Exception momentCommentDelete $error")
//                binding.root.snackbar(" $error")
                hideProgressView()
                return@launchWhenResumed
            }

            RefreshStories()
            hideProgressView()

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

        dialog.setOnDismissListener {
            if (curStoryType != "video") {
                startTimer()
            } else {
                resumeTimerAndPlayer()
            }
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            dismiss()
            listener?.deleteCallback(objectID ?: 0)
        }

        dialog.show()
    }


    private fun loadGlideImage(imageUrl: String, requestOptions: RequestOptions? = null) {
        coroutineScope.launch {
            GlideImageLoader.loadImageWithProgress(
                context = requireContext(),
                imageUrl = imageUrl,
                progressBar = progressBar,
                requestOptions,
                onBitmapLoaded = { bitmap ->
                    bitmap?.let {
                        imgUserStory.setImageBitmap(bitmap)
                    }
                },
                onError = {
                    imgUserStory.setImageResource(R.drawable.ic_default_user)
                }
            )
        }
    }
}
