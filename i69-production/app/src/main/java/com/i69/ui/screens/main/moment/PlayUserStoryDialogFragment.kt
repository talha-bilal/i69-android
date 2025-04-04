package com.i69.ui.screens.main.moment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import com.i69.BuildConfig
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.i69.*
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.ModelGifts
import com.i69.di.modules.AppModule
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.ui.adapters.*
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


const val STATE_RESUME_WINDOW = "resumeWindow"
const val STATE_RESUME_POSITION = "resumePosition"
const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
const val STATE_PLAYER_PLAYING = "playerOnPlay"

class PlayUserStoryDialogFragment(val listener: UserStoryDetailFragment.DeleteCallback?) :
    DialogFragment(), StoryCommentListAdapter.ClickPerformListener,
    CommentReplyListAdapter.ClickonListener {

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true

    private var countUp: Int = 100
    private lateinit var loadingDialog: Dialog

    private lateinit var views: View
    var progressBar1: ProgressBar? = null
    private lateinit var exoPlayer: ExoPlayer
    var player_view: PlayerView? = null
    var adapters: StoryLikesAdapter? = null
    private var timer1: CountDownTimerExt? = null
    lateinit var pause: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    var txtNearbyUserLikeCount: MaterialTextView? = null
    var txtMomentRecentComment: MaterialTextView? = null
    var lblItemNearbyUserCommentCount: MaterialTextView? = null
    var msg_write: EditText? = null
    var rvSharedMoments: RecyclerView? = null
    var nodata: MaterialTextView? = null
    var items: ArrayList<CommentsModel> = ArrayList()
    lateinit var glide: RequestManager
    var rvLikes: RecyclerView? = null
    var no_data: MaterialTextView? = null
    var txtheaderlike: MaterialTextView? = null
    var giftUserid: String? = null
    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null
    var userToken: String? = null
    var Uid: String? = null
    var objectID: Int? = null
    var Replymodels: CommentsModel? = null
    var adapter: VideoStoryCommentListAdapter? = null
    private var TAG: String = PlayUserStoryDialogFragment::class.java.simpleName
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(R.layout.fragment_user_story_detail, container, false)
        if (savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            playbackPosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            isFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
            isPlayerPlaying = savedInstanceState.getBoolean(STATE_PLAYER_PLAYING)
        }
        return views
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        pausetimerandplayer()
    }

    fun pausetimerandplayer() {
        if (timer1 != null) {
            timer1!!.pause()

        }
        if (exoPlayer != null) {
            exoPlayer.playWhenReady = false
            pause.visibility = View.VISIBLE
        }
    }

    fun resumetimerandplayer() {

        if (timer1 != null) {
            timer1!!.start()
        }
        if (exoPlayer != null) {
            exoPlayer.playWhenReady = true
            pause.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        loadingDialog = requireActivity().createLoadingDialog()
        val userPic = views.findViewById<ImageView>(R.id.userPic)
        val lblName = views.findViewById<MaterialTextView>(R.id.lblName)
        val ctParent = views.findViewById<RelativeLayout>(R.id.ct_parent)
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
        val thumbnail = views.findViewById<ImageView>(R.id.thumbnail)
        val send_btn = views.findViewById<ImageView>(R.id.send_btn)
        msg_write = views.findViewById(R.id.msg_write)
        rvSharedMoments = views.findViewById(R.id.rvSharedMoments)
        nodata = views.findViewById(R.id.no_data)
        player_view = views.findViewById(R.id.player_view)
        rvLikes = views.findViewById(R.id.rvLikes)
        no_data = views.findViewById(R.id.no_datas)
        txtheaderlike = views.findViewById(R.id.txtheaderlike)
        val likes_l = views.findViewById<LinearLayout>(R.id.likes_l)
        val likes_view = views.findViewById<LinearLayout>(R.id.likes_view)
        val comment_l = views.findViewById<LinearLayout>(R.id.comment_l)
        val gift_l = views.findViewById<LinearLayout>(R.id.gift_l)
        val delete_story = views.findViewById<ImageView>(R.id.delete_story)
        pause = views.findViewById<ImageView>(R.id.iv_pause)
        val showDelete = arguments?.getBoolean("showDelete") ?: false
        val report_l = views.findViewById<LinearLayout>(R.id.report_l)

        if (showDelete) {
            delete_story.visibility = View.VISIBLE
            report_l.visibility = View.GONE
        } else {
            report_l.visibility = View.VISIBLE
        }
        glide = Glide.with(requireContext())
        Uid = arguments?.getString("Uid", "")
        val url = arguments?.getString("url", "")
        Log.e(TAG,"playview ${arguments?.getString("url")}")
        val uri: Uri = Uri.parse(url)
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(MimeTypes.VIDEO_MP4)
            .build()
        playView(mediaItem)
        val userurl = arguments?.getString("userurl", "")
        val username = arguments?.getString("username", "")
        val times = arguments?.getString("times", "")
        userToken = arguments?.getString("token", "")
        objectID = arguments?.getInt("objectID", 0)
        getStories()
        ctParent.setOnClickListener {
            if (exoPlayer.playWhenReady) {
                pausetimerandplayer()
            } else {
                resumetimerandplayer()
            }
        }
        player_view!!.visibility = View.VISIBLE
        imgUserStory.visibility = View.GONE
        userPic.loadCircleImage(userurl!!)
        lblName.text = username
        txtTimeAgo.text = times
        sendgiftto.text = context?.resources?.getString(R.string.send_git_to) + " " + username!!
        userPic.setOnClickListener(View.OnClickListener {
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            pausetimerandplayer()
            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
            dismiss()
        })

        lblName.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", giftUserid)
            pausetimerandplayer()
            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
            dismiss()
        })
        img_close.setOnClickListener {
            pausetimerandplayer()
            dismiss()
        }
        comment_l.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                pausetimerandplayer()

            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                resumetimerandplayer()
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
                    pausetimerandplayer()
                } else {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                    resumetimerandplayer()
                }
            }
        }

        likes_view.setOnClickListener {
            if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                pausetimerandplayer()
            } else {
                LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                resumetimerandplayer()
            }
        }

        likes_l.setOnClickListener(View.OnClickListener {
            if (Uid.equals(giftUserid)) {
                if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    pausetimerandplayer()
                } else {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                    resumetimerandplayer()
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
                        Log.e(TAG,"apolloResponseException ${e.message}")
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                        return@launchWhenResumed
                    }
                    val usermoments = res.data?.genericLike
                    RefreshStories()
                }
            }
        })
        val bottomSheet = views.findViewById<ConstraintLayout>(com.i69.R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })

        val giftbottomSheet = views.findViewById<ConstraintLayout>(R.id.giftbottomSheet)
        GiftbottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(giftbottomSheet)
        GiftbottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        val likebottomSheet = views.findViewById<ConstraintLayout>(R.id.likebottomSheet)
        LikebottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(likebottomSheet)
        LikebottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        resumetimerandplayer()
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        pausetimerandplayer()
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }

                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })
        sendgiftto.setOnClickListener(View.OnClickListener {

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
                            Log.e(TAG,"apolloResponseException ${e.message}")
                            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG)
                                .show()
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
                                "${res.errors!![0].message}",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                        Log.e(TAG,"apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}")
                    }
                    hideProgressView()
                }
            }

        })


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
                        Log.e(TAG,"apolloResponse ${e.message}")
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
                        Log.e(TAG,"apolloResponse ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "Exception ${e.message}",
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
                apolloClient(requireContext(), userToken!!).query(GetUserDataQuery(Uid!!)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "Exception ${e.message}", Toast.LENGTH_LONG).show()

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
                        Log.e(TAG,
                            "URL " + UserData.avatar!!.url!!.replace(
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

        delete_story.setOnClickListener {
            dismiss()
            listener?.deleteCallback(objectID ?: 0)
        }

        report_l.setOnClickListener {
            pausetimerandplayer()
            reportDialog()
        }
    }


    private fun reportDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportMessage = dialogLayout.findViewById<EditText>(R.id.report_message)
        val okButton = dialogLayout.findViewById<TextView>(R.id.ok_button)
        val cancleButton = dialogLayout.findViewById<TextView>(R.id.cancel_button)
        okButton.text = "${AppStringConstant(requireActivity()).ok}"
        cancleButton.text = "${AppStringConstant(requireActivity()).cancel}"

        val builder = AlertDialog.Builder(activity, R.style.DeleteDialogTheme)
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
                    Log.e(TAG,"apolloResponse Exception ${e.message}")
                    Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()

                    hideProgressView()
                    dialog.dismiss()
                    return@launchWhenResumed
                }

                if (res.hasErrors()) {

                } else {
                    Log.e(TAG,
                        "rsponceSuccess"+
                        res.data!!.reportStory!!.storyReport!!.story.pk.toString()
                    )
                }
                resumetimerandplayer()
                hideProgressView()
                dialog.dismiss()
            }
        }

        cancleButton.setOnClickListener {
            resumetimerandplayer()
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun subscribeForUpdateStory(stories: List<GetAllUserStoriesQuery.Edge?>) {
        var storiesPkList = java.util.ArrayList<Int>()
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
                    Log.e(TAG,"reealltime exception= ${it.message}")
                }
                    .retryWhen { cause, attempt ->
                        Log.e(TAG,"reealltime retry $attempt ${cause.message}")
                        delay(attempt * 1000)
                        true
                    }.collect { newStory ->
                        if (newStory.hasErrors()) {
                            Log.e(TAG,"reealltime response error = ${newStory.errors?.get(0)?.message}")
                        } else {
                            Log.e(TAG,"reealltime NewStoryData ${newStory.data?.onUpdateStory}")
                            if (items.size > 0) {
                                RefreshStories()

                            } else {

                                getStories()
                            }
                        }
                    }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG,"reealltime exception= ${e2.message}")
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
                Log.e(TAG,"apolloResponse Exception${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            Log.e(TAG,"apolloResponse allUserStories stories ${res.hasErrors()}")
            val allUserStories = res.data?.allUserStories!!.edges

            if (allUserStories.size != 0) {
                subscribeForUpdateStory(allUserStories)
                val likeCount = allUserStories.get(0)!!.node!!.likesCount.toString()
                val commentCount = allUserStories.get(0)!!.node!!.commentsCount.toString()
                txtNearbyUserLikeCount!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                lblItemNearbyUserCommentCount!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
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
                    Log.e(TAG,"apolloResponse: ${Likedata[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE

                    val items1: java.util.ArrayList<CommentsModel> = java.util.ArrayList()

                    Likedata.indices.forEach { i ->

                        val models = CommentsModel()

                        val fullName = Likedata[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.commenttext = name
                        if (Likedata[i]!!.node!!.user.avatar != null && !Likedata[i]!!.node!!.user.avatar!!.url.isNullOrEmpty()) {
                            models.userurl = Likedata[i]!!.node!!.user.avatar!!.url
                        } else {
                            models.userurl = ""
                        }

                        items1.add(models)
                    }

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
                if (Commentdata!!.size > 0) {
                    Log.e(TAG,"apolloResponse: ${Commentdata?.get(0)?.node!!.commentDescription}")
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
                        val fullName = Commentdata[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.username = name
                        models.timeago = Commentdata[i]!!.node!!.createdDate.toString()
                        models.cmtID = Commentdata[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.uid = Commentdata[i]!!.node!!.user.id.toString()
                        models.cmtlikes = Commentdata[i]!!.node!!.likesCount.toString()
                        for (f in 0 until Commentdata[i]!!.node!!.replys!!.edges.size) {
                            val md = ReplysModel()
                            md.replytext =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url
                            val fullName =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            val name = if (fullName != null && fullName.length > 15) {
                                fullName.substring(0, minOf(fullName.length, 15))
                            } else {
                                fullName
                            }
                            md.usernames = name
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
                        VideoStoryCommentListAdapter(
                            requireActivity(),
                            this@PlayUserStoryDialogFragment,
                            items,
                            this@PlayUserStoryDialogFragment
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
                Log.e(TAG,"apolloResponse ${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            Log.e(TAG,"apolloResponse allUserStories stories ${res.hasErrors()}")
            val allUserStories = res.data?.allUserStories!!.edges
            if (allUserStories != null && allUserStories.size != 0) {

                val likeCount = allUserStories.get(0)!!.node!!.likesCount.toString()
                val commentCount = allUserStories.get(0)!!.node!!.commentsCount.toString()
                txtNearbyUserLikeCount!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"
                txtMomentRecentComment!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                lblItemNearbyUserCommentCount!!.text =
                    commentCount + " ${requireActivity().resources.getString(R.string.comments)}"
                txtheaderlike!!.text =
                    likeCount + " ${requireActivity().resources.getString(R.string.like)}"
                giftUserid = allUserStories.get(0)!!.node!!.user!!.id.toString()
                val Likedata = allUserStories.get(0)!!.node!!.likes!!.edges
                if (Likedata.size > 0) {
                    Log.e(TAG,"apolloResponse: ${Likedata[0]!!.node!!.id}")
                    no_data!!.visibility = View.GONE
                    rvLikes!!.visibility = View.VISIBLE
                    val items1: java.util.ArrayList<CommentsModel> = java.util.ArrayList()
                    Likedata.indices.forEach { i ->
                        val models = CommentsModel()
                        val fullName = Likedata[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.commenttext = name
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
                if (Commentdata!!.size > 0) {
                    Log.e(TAG,"apolloResponse: ${Commentdata?.get(0)?.node!!.commentDescription}")
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
                        val fullName = Commentdata[i]!!.node!!.user.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        models.username = name
                        models.timeago = Commentdata[i]!!.node!!.createdDate.toString()
                        models.cmtID = Commentdata[i]!!.node!!.pk.toString()
                        models.momentID = objectID?.toString()
                        models.uid = Commentdata[i]!!.node!!.user.id.toString()
                        models.cmtlikes = Commentdata[i]!!.node!!.likesCount.toString()
                        for (f in 0 until Commentdata[i]!!.node!!.replys!!.edges.size) {
                            val md = ReplysModel()
                            md.replytext =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.commentDescription
                            md.userurl =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.avatarPhotos?.get(
                                    0
                                )?.url
                            val fullName =
                                Commentdata[i]!!.node!!.replys!!.edges[f]!!.node!!.user.fullName
                            val name = if (fullName != null && fullName.length > 15) {
                                fullName.substring(0, minOf(fullName.length, 15))
                            } else {
                                fullName
                            }
                            md.usernames = name
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
                                }
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

    fun fireLikeStoryNotification(pkids: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder()
                .append("mutation {")
                .append("$queryName (")
                .append("userId: \"${userid}\", ")
                .append("notificationSetting: \"STLIKE\", ")
                .append("data: {pk:${pkids}}")
                .append(") {")
                .append("sent")
                .append("}")
                .append("}")
                .toString()
            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query,
                queryName, userToken
            )
            Log.e(TAG,"RSLT"+ "" + result.message)
        }
    }

    fun fireCommntStoryNotification(pkids: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder()
                .append("mutation {")
                .append("$queryName (")
                .append("userId: \"${userid}\", ")
                .append("notificationSetting: \"STCMNT\", ")
                .append("data: {pk:${pkids}}")
                .append(") {")
                .append("sent")
                .append("}")
                .append("}")
                .toString()

            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query,
                queryName, userToken
            )
            Log.e(TAG,"RSLT"+ "" + result.message)
        }
    }

    fun fireGiftBuyNotificationforreceiver(gid: String, userid: String?) {
        lifecycleScope.launchWhenResumed {
            val queryName = "sendNotification"
            val query = StringBuilder()
                .append("mutation {")
                .append("$queryName (")
                .append("userId: \"${userid}\", ")
                .append("notificationSetting: \"GIFT RLVRTL\", ")
                .append("data: {giftId:${gid}}")
                .append(") {")
                .append("sent")
                .append("}")
                .append("}")
                .toString()

            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query,
                queryName, userToken
            )
            Log.e(TAG,"RSLT"+ "" + result.message)
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

    private fun initPlayer() {

        val mediaItem = MediaItem.Builder()
            .setUri("")
            .setMimeType(MimeTypes.VIDEO_MP4)
            .build()
        exoPlayer = ExoPlayer.Builder(requireActivity()).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem, false)
            prepare()
        }
        player_view!!.player = exoPlayer
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentWindowIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    private fun releasePlayer() {
        isPlayerPlaying = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentWindowIndex
        exoPlayer.release()
    }

    @OptIn(UnstableApi::class)
    private fun playView(mediaItem: MediaItem) {
        showProgressView()
        exoPlayer = ExoPlayer.Builder(requireActivity()).build().apply {
            playWhenReady = isPlayerPlaying
            seekTo(currentWindow, playbackPosition)
            setMediaItem(mediaItem, false)
            prepare()
        }
        player_view!!.player = exoPlayer
        var durationSet = false
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                    val realDurationMillis: Long = exoPlayer.getDuration()
                    durationSet = true
                    val duration = realDurationMillis
                    Log.e(TAG,"filee ${duration}")
                    progressBar1!!.max = realDurationMillis.toInt()
                    val millisInFuture = duration.toLong()
                    timer1 = object : CountDownTimerExt(millisInFuture, 100) {
                        override fun onTimerTick(millisUntilFinished: Long) {
                            Log.e(TAG, "onTimerTick $millisUntilFinished")
                            onTickProgressUpdate()
                        }

                        override fun onTimerFinish() {
                            dismiss()
                        }
                    }
                    hideProgressView()
                    timer1.run { this!!.start() }
                }
            }

            fun onPlayWhenReadyCommitted() {
                // No op.
            }

            fun onPlayerError(error: ExoPlaybackException) {
                // No op.
            }
        })
    }

    private fun onTickProgressUpdate() {
        countUp += 100
        val progress = countUp
        Log.e(TAG,"prggress $progress")
        progressBar1!!.smoothProgress(progress)
    }

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
                Log.e(TAG,"apolloResponse ${e.message}")
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
}
