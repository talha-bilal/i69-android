package com.i69.ui.screens.main.moment.momentcomment


import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.i69.BuildConfig
import com.i69.CommentDeleteMutation
import com.i69.CommentReplyOnMomentMutation
import com.i69.CommentonmomentMutation
import com.i69.GetAllCommentOfMomentQuery
import com.i69.GetUserDataQuery
import com.i69.GiftPurchaseMutation
import com.i69.LikeOnCommentMutation
import com.i69.LikeOnMomentMutation
import com.i69.R
import com.i69.ReportCommentMutation
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.ModelGifts
import com.i69.data.remote.responses.MomentLikes
import com.i69.databinding.FragmentMomentsAddcommentsBinding
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentReceivedGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.ui.adapters.CommentListAdapter
import com.i69.ui.adapters.CommentReplyListAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.ui.viewModels.ReplysModel
import com.i69.ui.viewModels.UserMomentsModelView
import com.i69.utils.AnimationTypes
import com.i69.utils.LogUtil
import com.i69.utils.apolloClient
import com.i69.utils.hideKeyboard
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Arrays


class MomentAddCommentFragment : BaseFragment<FragmentMomentsAddcommentsBinding>(),
    CommentListAdapter.ClickPerformListener,
    CommentReplyListAdapter.ClickonListener {

    private lateinit var giftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var receivedGiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var momentLikesAdapters: StoryLikesAdapter
    private var TAG: String = MomentAddCommentFragment::class.java.simpleName
    private var userToken: String? = null
    private var userName: String? = null
    private var userId: String? = null
    private lateinit var fragVirtualGifts: FragmentVirtualGifts
    private lateinit var fragRealGifts: FragmentRealGifts
    var giftUserid: String? = null
    var itemPosition: Int? = 0

    private var muserID: String? = ""
    private var mID: String? = ""
    private var filesUrl: String? = ""
    private var likess: String? = ""
    private var Comments: String? = ""
    private var Desc_: String? = ""
    private var Desc1_: List<String?>? = ArrayList()
    private var fullnames: String? = ""
    private var gender: String? = ""
    var builder: SpannableStringBuilder? = null
    var adapter: CommentListAdapter? = null
    var items: ArrayList<CommentsModel> = ArrayList()
    var Replymodels: CommentsModel? = null
    private val mViewModel: UserMomentsModelView by activityViewModels()
    private var exoPlayer: ExoPlayer? = null


    var momentLikesUsers: ArrayList<MomentLikes> = ArrayList()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMomentsAddcommentsBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()
        viewStringConstModel.data.observe(this@MomentAddCommentFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }
        binding?.model = mViewModel
        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            userName = getCurrentUserName()!!
            userId = getCurrentUserId()
            Log.e(TAG, "usertoken $userToken")

            getAllCommentsofMoments()

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).query(GetUserDataQuery(userId!!)).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }

                    hideProgressView()
                    val UserData = res.data?.user

                    try {
                        if (UserData!!.avatar != null) {

                            try {
                                binding?.thumbnail?.loadCircleImage(
                                    UserData.avatar!!.url!!.replace(
                                        "${BuildConfig.BASE_URL_REP}media/",
                                        "${BuildConfig.BASE_URL}media/"
                                    )
                                )
                                Log.e(
                                    TAG,
                                    "URL " + UserData.avatar?.url!!.replace(
                                        "${BuildConfig.BASE_URL_REP}media/",
                                        "${BuildConfig.BASE_URL}media/"
                                    )
                                )
                            } catch (e: Exception) {
                                e.stackTrace
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        Log.e(TAG, "usertoken 2 $userToken")

        binding?.imgNearbyUserComment?.setOnClickListener {
            binding?.momentDetailScrollView?.post {
                binding?.momentDetailScrollView?.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding?.lblItemNearbyUserCommentCount?.setOnClickListener {
            binding?.momentDetailScrollView?.post {
                binding?.momentDetailScrollView?.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding?.imgback?.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })

        setSendGiftLayout()
        showLikeBottomSheet()

        binding?.imgNearbyUserGift?.setOnClickListener(View.OnClickListener {

            if (userId!! != muserID) {
                if (giftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    giftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    giftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            } else {
                childFragmentManager.beginTransaction()
                    .replace(R.id.receivedGiftContainer, FragmentReceivedGifts())
                    .commitAllowingStateLoss()

                receivedGiftbottomSheetBehavior =
                    BottomSheetBehavior.from(binding?.receivedGiftBottomSheet!!)
                receivedGiftbottomSheetBehavior.setBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {

                    }
                })

                if (receivedGiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })
        binding?.sendBtn?.setOnClickListener(View.OnClickListener {
            if (binding?.msgWrite?.text?.toString().equals("")) {
                binding?.root?.snackbar(getString(R.string.you_cant_add_empty_msg))
                return@OnClickListener
            }
            val msgWriteString: String = binding?.msgWrite!!.text.toString()
            if (Replymodels != null && msgWriteString.startsWith("@") && msgWriteString.trim()
                    .contains(Replymodels!!.username!!, true)
            ) {
                showProgressView()
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        val message =
                            msgWriteString.replace("@" + Replymodels!!.username!!, "").trim()
                        val res = try {
                            apolloClient(
                                requireContext(), userToken!!
                            ).mutation(
                                CommentReplyOnMomentMutation(
                                    message, Replymodels!!.momentID!!, Replymodels!!.cmtID!!
                                )
                            ).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            binding?.root?.snackbar("${e.message}")
                            hideProgressView()
                            return@repeatOnLifecycle
                        }
                        if (res.hasErrors()) {
                            val error = res.errors?.get(0)?.message
                            Log.e(TAG, "Exception momentUpdateDesc $error")
                            binding?.root?.snackbar(" $error")
                            hideProgressView()
                            return@repeatOnLifecycle
                        } else {
                            Log.e(TAG, "CommentReplyOnMomentMutation: " + res.data.toString())
                            if (res.data!!.commentMoment != null && res.data!!.commentMoment!!.comment != null) {
                                val usermoments = res.data?.commentMoment!!.comment!!.momemt
                                binding?.lblItemNearbyUserCommentCount?.text =
                                    "" + usermoments.comment!!
                            }
                            binding?.msgWrite?.setText("")
                            itemPosition?.let { it1 ->
                                getMainActivity()?.pref?.edit()
                                    ?.putString("checkUserMomentUpdate", "true")
                                    ?.putString("mID", mID)
                                    ?.putInt(
                                        "itemPosition", it1
                                    )?.apply()
                            }
                            getAllCommentsofMomentsRefresh()
                            hideProgressView()
                        }
                    }
                }
            } else {
                showProgressView()
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        val mutation = binding?.msgWrite?.text?.toString()
                            ?.let { it1 -> CommentonmomentMutation(mID.toString(), it1) }
                        val res = try {
                            apolloClient(requireContext(), userToken!!).mutation(mutation!!)
                                .execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception${e.message}")
                            binding?.root?.snackbar("${e.message}")
                            hideProgressView()
                            return@repeatOnLifecycle
                        }
                        if (res.hasErrors()) {
                            val error = res.errors?.get(0)?.message
                            Log.e(TAG, "Exception momentUpdateDesc $error")
                            binding?.root?.snackbar(" $error")
                            hideProgressView()
                            return@repeatOnLifecycle
                        } else {
                            hideProgressView()
                            Log.e(TAG, "setupTheme: " + res.data)
                            binding?.msgWrite?.setText("")
                            if (res.data?.commentMoment != null) {
                                if (res.data?.commentMoment!!.comment != null) {
                                    val usermoments = res.data?.commentMoment!!.comment!!.momemt
                                    binding?.lblItemNearbyUserCommentCount?.text =
                                        "" + usermoments.comment!!
                                }
                            }
                            itemPosition?.let { it1 ->
                                getMainActivity()?.pref?.edit()
                                    ?.putString("checkUserMomentUpdate", "true")
                                    ?.putString("mID", mID)
                                    ?.putInt(
                                        "itemPosition", it1
                                    )?.apply()
                            }

                            if (items.size > 0) {
                                getAllCommentsofMomentsRefresh()
                            } else {
                                getAllCommentsofMoments()
                            }
                        }
                    }
                }
            }
        })

        binding?.txtMomentViewLike?.visibility = View.VISIBLE
        binding?.txtMomentViewLike?.setOnClickListener {
            getMomentLikes()
        }

        binding?.imgNearbyUserLikes?.setOnClickListener {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userId = getCurrentUserId()!!
                    Log.e(TAG, "onLikeofMomentClick: UserId: $userId   Username: $userName")
                    val selectedUserId = muserID
                    if (selectedUserId == userId) {
                        getMomentLikes()
                    } else {
                        showProgressView()
                        Log.e(TAG, "mID = $mID")
                        Log.e(TAG, "userToken = $userToken")
                        val res = try {
                            val queryString = LikeOnMomentMutation(mID!!)
                            Log.e(TAG, "QuertString: $queryString")
                            apolloClient(requireContext(), userToken!!).mutation(queryString)
                                .execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            binding?.root?.snackbar("${e.message}")
                            hideProgressView()
                            return@repeatOnLifecycle
                        }
                        hideProgressView()
                        Log.e(TAG, "Response: ${Gson().toJson(res)}")
                        getMomentLikes(true)
                        itemPosition?.let { it1 ->
                            val editor = getMainActivity()?.pref?.edit()
                            editor?.putString("checkUserMomentUpdate", "true")
                            editor?.putString("mID", mID)
                            editor?.putInt("itemPosition", it1)
                            editor?.apply()
                        }
                    }
                }
            }
        }

        binding?.lblItemNearbyName?.setOnClickListener {
            binding?.lblItemNearbyName?.hideKeyboard()
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", muserID)
            if (userId == muserID) {
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
        }

        val url = if (!BuildConfig.USE_S3) {
            if (filesUrl.toString().startsWith(BuildConfig.BASE_URL)) filesUrl.toString()
            else "${BuildConfig.BASE_URL}${filesUrl.toString()}"
        } else filesUrl.toString()

        Log.e(TAG, "setupTheme: $url")
        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".webp")) {
            binding?.playerView?.visibility = View.INVISIBLE
            binding?.imgSharedMoment?.setViewVisible()
            binding?.playerView?.setViewGone()

//            val params =
//                binding?.layoutSharedMomentInfo?.layoutParams as RelativeLayout.LayoutParams
//            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgSharedMoment)
//            binding?.layoutSharedMomentInfo?.layoutParams = params

            binding?.imgSharedMoment?.loadImage(url)
        } else {
            binding?.playerView?.setViewVisible()
            binding?.imgSharedMoment?.setViewGone()
            binding?.imgSharedMoment?.visibility = View.INVISIBLE

//            val params =
//                binding?.layoutSharedMomentInfo?.layoutParams as RelativeLayout.LayoutParams
//            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.playerView)
//            binding?.layoutSharedMomentInfo?.layoutParams = params

            binding?.playerView?.setOnClickListener {
                if (exoPlayer?.isPlaying == true) {
                    exoPlayer?.pause()
                    binding?.ivPlay?.setViewVisible()
                } else {
                    binding?.ivPlay?.setViewGone()
                    if (exoPlayer != null && exoPlayer!!.currentPosition > 0L) {
                        exoPlayer?.seekTo(exoPlayer!!.currentPosition)
                        exoPlayer?.play()
                    } else {
                        val uri: Uri = Uri.parse(url)
                        val mediaItem =
                            MediaItem.Builder().setUri(uri).setMimeType(MimeTypes.VIDEO_MP4).build()
                        playView(mediaItem)
                    }
                }
            }

            val uri: Uri = Uri.parse(url)
            val mediaItem = MediaItem.Builder().setUri(uri).setMimeType(MimeTypes.VIDEO_MP4).build()
            playView(mediaItem)
        }

        binding?.txtNearbyUserLikeCount?.text = likess

        binding?.lblItemNearbyUserCommentCount?.text = Comments


        val name = if (fullnames != null && fullnames!!.length > 15) {
            fullnames!!.substring(0, minOf(fullnames!!.length, 15))
        } else {
            fullnames
        }

        binding?.lblItemNearbyName?.text = name?.uppercase()


        binding?.txtMomentRecentComment?.text = builder
        binding?.txtMomentRecentComment?.movementMethod = LinkMovementMethod.getInstance()

        if (gender != null) {
            if (gender.equals("A_0")) {
                binding?.imgNearbyUserGift?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources, R.drawable.yellow_gift_male, null
                    )
                )

            } else if (gender.equals("A_1")) {
                binding?.imgNearbyUserGift?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources, R.drawable.red_gift_female, null
                    )
                )

            } else if (gender.equals("A_2")) {
                binding?.imgNearbyUserGift?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireActivity().resources, R.drawable.purple_gift_nosay, null
                    )
                )

            }
        }

//        binding?.toplayouts?.setViewVisible()
//        binding?.imageNVideoRV?.setViewVisible()
//        binding?.imgSharedMoment?.setViewVisible()
//        binding?.layoutSharedMomentInfo?.setViewVisible()
//        binding?.txtMomentViewLike?.setViewVisible()
//        binding?.txtMomentRecentComment?.setViewVisible()
//        binding?.rvSharedMoments?.setViewVisible()

    }

    @OptIn(UnstableApi::class)
    private fun playView(mediaItem: MediaItem) {
        exoPlayer = ExoPlayer.Builder(getMainActivity()!!).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ALL
            setMediaItem(mediaItem, false)
            prepare()
        }
        binding?.playerView?.player = exoPlayer
        binding?.playerView?.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
//        var durationSet = false
//        exoPlayer?.addListener(object : Player.Listener {
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
//                }
//            }
//        })
    }

    override fun onStop() {
        super.onStop()
        if (exoPlayer != null) {
            exoPlayer?.stop()
            exoPlayer?.release()
        }
    }

    private fun getMomentLikes(flag: Boolean = false) {
        momentLikesUsers.clear()
        lifecycleScope.launch(Dispatchers.Main) {
            userToken?.let {
                Log.e(TAG, "UserMomentNextPage Calling")
                mViewModel.getMomentLikes(it, ((mID ?: "").toString())) { error ->
                    if (error == null) {
                        activity?.runOnUiThread {
                            if (flag) {
                                val size = mViewModel.coinPrice.size
                                binding?.txtNearbyUserLikeCount?.text = "$size"
                            } else {
                                loadLikesDialog(mViewModel.coinPrice)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLikeBottomSheet() {
        binding?.rvLikes?.layoutManager = LinearLayoutManager(activity)
        if (binding?.rvLikes?.itemDecorationCount == 0) {
            binding?.rvLikes?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    outRect.top = 25
                }
            })
        }


        val items1: ArrayList<CommentsModel> = ArrayList()
        momentLikesAdapters =
            StoryLikesAdapter(requireActivity(), items1, Glide.with(requireContext()))

        momentLikesAdapters.userProfileClicked {
            LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            LogUtil.debug("UserStoryDetailsFragment : : : $it")
            val bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", it.uid)
            if (userId == it.uid) {
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
        }
        binding?.rvLikes?.adapter = momentLikesAdapters

        val likebottomSheet = binding?.likebottomSheet
        LikebottomSheetBehavior = BottomSheetBehavior.from(likebottomSheet!!)
        LikebottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    private fun loadLikesDialog(momentLikes: ArrayList<MomentLikes>) {
        momentLikesUsers.clear()
        momentLikesUsers.addAll(momentLikes)
        binding?.txtheaderlike?.text = momentLikes.size.toString() + " Likes"

        if (momentLikesUsers.isNotEmpty()) {
//            binding?.noDatas?.visibility = View.GONE
            binding?.rvLikes?.visibility = View.VISIBLE

            val items1: ArrayList<CommentsModel> = ArrayList()
            momentLikesUsers.forEach { i ->
                val models = CommentsModel()
                val fullName = i.user?.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                models.commenttext = name
                models.uid = i.user?.id
                models.userurl = i.user?.avatar?.url
                items1.add(models)
            }

            momentLikesAdapters.addAll(items1)
            momentLikesAdapters.notifyDataSetChanged()
        } else {
//            binding?.noDatas?.visibility = View.VISIBLE
            binding?.rvLikes?.visibility = View.VISIBLE
        }
        if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding?.rvSharedMoments?.visibility = View.VISIBLE
        } else {
            LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding?.rvSharedMoments?.visibility = View.VISIBLE
        }
    }


    private fun setSendGiftLayout() {
        val giftBottomSheet = binding?.root?.findViewById<ConstraintLayout>(R.id.giftbottomSheet)
        val sendgiftto = giftBottomSheet?.findViewById<MaterialTextView>(R.id.sendgiftto)
        val giftsTabs = giftBottomSheet?.findViewById<TabLayout>(R.id.giftsTabs)
        val giftsPager = giftBottomSheet?.findViewById<ViewPager>(R.id.gifts_pager)
        giftsTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                giftsPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        giftsTabs?.setupWithViewPager(giftsPager)
        setupViewPager(giftsPager)
        sendgiftto?.text = context?.resources?.getString(R.string.send_git_to) + " " + fullnames
        sendgiftto?.setOnClickListener {

            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }

            lifecycleScope.launchWhenCreated {
                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->
                        Log.e(TAG, "gift.id" + gift.id)
                        Log.e(TAG, "giftUserid" + muserID.toString())
                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(
                                requireContext(), userToken!!
                            ).mutation(GiftPurchaseMutation(gift.id, muserID!!, userId!!)).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            Toast.makeText(
                                requireContext(), "MomentAdd1: ${e.message}", Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e(TAG, "resee" + Gson().toJson(res))

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
                                "MomentAdd2: ${res.errors!![0].message}",
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
        giftbottomSheetBehavior = BottomSheetBehavior.from(giftBottomSheet!!)
        giftbottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()

        adapter.addFragItem(fragRealGifts, getString(R.string.real_gifts))
        adapter.addFragItem(fragVirtualGifts, getString(R.string.virtual_gifts))
        viewPager?.adapter = adapter
    }

    private fun getAllCommentsofMoments() {
        items = ArrayList()

        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(
                        requireContext(), userToken!!
                    ).query(GetAllCommentOfMomentQuery(mID!!)).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("getAllComments ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
                }

                hideProgressView()
                val allusermoments = res.data?.allComments

                if (binding?.rvSharedMoments?.itemDecorationCount == 0) {
                    binding?.rvSharedMoments?.addItemDecoration(object :
                        RecyclerView.ItemDecoration() {
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
                if (allusermoments?.size!! > 0) {
                    Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.commentDescription}")
                    binding?.noData?.visibility = View.GONE
                    binding?.rvSharedMoments?.visibility = View.VISIBLE
                    allusermoments.indices.forEach { i ->
                        val hm: MutableList<ReplysModel> = ArrayList()
                        val models = CommentsModel()
                        models.commenttext = allusermoments[i]!!.commentDescription
                        if (allusermoments[i]!!.user.avatarPhotos?.size!! > 0) {
                            models.userurl = allusermoments[i]!!.user.avatarPhotos?.get(0)?.url
                        } else {
                            models.userurl = ""
                        }
                        models.username = allusermoments[i]!!.user.fullName
                        models.timeago = allusermoments[i]!!.createdDate.toString()
                        models.cmtID = allusermoments[i]!!.pk.toString()
                        models.momentID = mID
                        models.cmtlikes = allusermoments[i]!!.like.toString()
                        models.uid = allusermoments[i]!!.user.id.toString()
                        for (f in 0 until allusermoments[i]!!.replys!!.size) {
                            val md = ReplysModel()
                            md.replytext = allusermoments[i]!!.replys!![f]!!.commentDescription
                            md.userurl =
                                allusermoments[i]!!.replys!![f]!!.user.avatarPhotos?.get(0)?.url
                            md.usernames = allusermoments[i]!!.replys!![f]!!.user.fullName
                            md.timeago = allusermoments[i]!!.createdDate.toString()
                            md.uid = allusermoments[i]!!.user.id.toString()
                            hm.add(f, md)
                        }
                        models.replylist = hm
                        models.isExpanded = true
                        items.add(models)
                    }
                    adapter = CommentListAdapter(
                        requireActivity(),
                        this@MomentAddCommentFragment,
                        items,
                        this@MomentAddCommentFragment,
                        userId == muserID,
                        userId!!
                    )
                    binding?.rvSharedMoments?.adapter = adapter
                    binding?.rvSharedMoments?.layoutManager = LinearLayoutManager(activity)
                } else {
                    binding?.noData?.visibility = View.VISIBLE
                    binding?.rvSharedMoments?.visibility = View.GONE
                }
            }
        }
    }


    private fun getAllCommentsofMomentsRefresh() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(
                        requireContext(), userToken!!
                    ).query(GetAllCommentOfMomentQuery(mID!!)).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse getAllComments ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                if (res.hasErrors()) {
                    val error = res.errors?.get(0)?.message
                    Log.e(TAG, "Exception momentUpdateDesc $error")
                    binding?.root?.snackbar(" $error")
                    hideProgressView()
                    return@repeatOnLifecycle
                } else {
                    val allusermoments = res.data?.allComments
                    binding?.lblItemNearbyUserCommentCount?.text = "" + res.data?.allComments?.size
                    if (binding?.rvSharedMoments?.itemDecorationCount == 0) {
                        binding?.rvSharedMoments?.addItemDecoration(object :
                            RecyclerView.ItemDecoration() {
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
                    if (allusermoments?.size!! > 0) {
                        Log.e(TAG, "apolloResponse: ${allusermoments.get(0)?.commentDescription}")
                        binding?.noData?.visibility = View.GONE
                        binding?.rvSharedMoments?.visibility = View.VISIBLE
                        val items1: ArrayList<CommentsModel> = ArrayList()
                        allusermoments.indices.forEach { i ->
                            val hm: MutableList<ReplysModel> = ArrayList()
                            val models = CommentsModel()

                            models.commenttext = allusermoments[i]!!.commentDescription
                            if (allusermoments[i]!!.user.avatarPhotos?.size!! > 0) {
                                models.userurl = allusermoments[i]!!.user.avatarPhotos?.get(0)?.url

                            } else {
                                models.userurl = ""
                            }
                            models.username = allusermoments[i]!!.user.fullName
                            models.timeago = allusermoments[i]!!.createdDate.toString()
                            models.cmtID = allusermoments[i]!!.pk.toString()
                            models.momentID = mID
                            models.cmtlikes = allusermoments[i]!!.like.toString()
                            models.uid = allusermoments[i]!!.user.id.toString()
                            for (f in 0 until allusermoments[i]!!.replys!!.size) {
                                val md = ReplysModel()
                                md.replytext = allusermoments[i]!!.replys!![f]!!.commentDescription
                                md.userurl =
                                    allusermoments[i]!!.replys!![f]!!.user.avatarPhotos?.get(0)?.url
                                md.usernames = allusermoments[i]!!.replys!![f]!!.user.fullName
                                md.timeago = allusermoments[i]!!.createdDate.toString()
                                md.uid = allusermoments[i]!!.user.id.toString()
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
                        binding?.noData?.visibility = View.VISIBLE
                        binding?.rvSharedMoments?.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val arguments = arguments
        if (arguments != null) {
            mID = arguments.getString("momentID")
            filesUrl = arguments.getString("filesUrl")
            likess = arguments.getString("Likes")
            Comments = arguments.getString("Comments")
            Desc_ = arguments.getString("Desc")
            fullnames = arguments.getString("fullnames")
            muserID = arguments.getString("momentuserID")
            gender = arguments.getString("gender")
            giftUserid = arguments.getString("userId")
            itemPosition = arguments.getInt("itemPosition")
            Desc1_ = Arrays.asList(Desc_)
            if (Desc_!!.split(",").size > 1) {
                val s1 = SpannableString(
                    Desc_!!.split(",")[0].replace("[\"", "").replace("\"]", "").replace("\"", "")
                )
                val s2 = SpannableString(resources.getString(R.string.read_more))

                s1.setSpan(
                    ForegroundColorSpan(Color.WHITE), 0, s1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                s2.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireActivity(), R.color.colorPrimary
                        )
                    ),
                    0,
                    s2.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                s2.setSpan(
                    StyleSpan(Typeface.BOLD), 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                s2.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        builder = SpannableStringBuilder()
                        Desc1_!!.forEach { builder!!.append(it).toString() }

                        binding?.txtMomentRecentComment?.text =
                            builder.toString().replace("[\"", "").replace("\"]", "")
                                .replace("\",\"", "")

                    }
                }, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder = SpannableStringBuilder()
                builder!!.append(s1)
                builder!!.append(s2)
            } else {
                builder = SpannableStringBuilder()
                builder!!.append(Desc_!!.replace("[\"", "").replace("\"]", ""))
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setupClickListeners() {

    }

    override fun onreply(position: Int, models: CommentsModel) {
        Replymodels = models
        binding?.msgWrite?.setText("")
        binding?.msgWrite?.requestFocus()
        binding?.msgWrite?.postDelayed(Runnable {
            val inputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

            inputMethodManager!!.showSoftInput(binding?.msgWrite, InputMethodManager.SHOW_IMPLICIT)
            binding?.msgWrite?.append("@" + models.username + " ")

        }, 150)
    }

    override fun oncommentLike(position: Int, models: CommentsModel) {
        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    apolloClient(
                        requireContext(), userToken!!
                    ).mutation(
                        LikeOnCommentMutation(
                            models.cmtID!!
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
                }

                hideProgressView()
                getAllCommentsofMomentsRefresh()
            }
        }
    }

    private fun reportDialog(position: Int, models: CommentsModel) {

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
            Log.e(TAG, "MyCommmentPk" + models.cmtID!!)
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userToken = getCurrentUserToken()!!
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).mutation(ReportCommentMutation(message, models.cmtID!!.toInt()))
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "reportsOnComens11" + "${e}")
                        Log.e(TAG, "reportsOnComens" + "Exception : ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        dialog.dismiss()
                        return@repeatOnLifecycle
                    }

                    if (res.hasErrors()) {
                        val error = res.errors?.get(0)?.message
                        Log.e(TAG, "Exception momentCommentDelete $error")
                        Log.e(TAG, "reportsOnComens111" + "${res.errors}")
                        binding?.root?.snackbar(" $error")
                        hideProgressView()
                        dialog.dismiss()
                        return@repeatOnLifecycle
                    } else {
                        hideProgressView()
                        getAllCommentsofMomentsRefresh()
                    }
                    dialog.dismiss()
                }
            }
        }

        cancleButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun oncommentReport(position: Int, models: CommentsModel) {
        Log.e(TAG, "myCommentReportClick" + models.commenttext!!)
        reportDialog(position, models)
    }

    override fun oncommentDelete(position: Int, models: CommentsModel) {
        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
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
                    binding?.root?.snackbar(" ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
                }

                if (res.hasErrors()) {
                    val error = res.errors?.get(0)?.message
                    Log.e(TAG, "Exception momentCommentDelete $error")
                    binding?.root?.snackbar(" $error")
                    hideProgressView()
                    return@repeatOnLifecycle
                }
                hideProgressView()
                getAllCommentsofMomentsRefresh()
            }
        }
    }

    override fun onUsernameClick(position: Int, models: CommentsModel) {
        var bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", models.uid)
        if (userId == models.uid) {
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
    }

    override fun onUsernameClick(position: Int, models: ReplysModel) {
        var bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", models.uid)
        if (userId == models.uid) {
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
    }
}