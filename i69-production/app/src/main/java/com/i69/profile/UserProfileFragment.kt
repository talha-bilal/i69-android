package com.i69.profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.i69.BuildConfig
import com.i69.GetNotificationCountQuery
import com.i69.GetUserMomentsQuery
import com.i69.R
import com.i69.UserSubscriptionQuery
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.config.Constants
import com.i69.data.models.BlockedUser
import com.i69.data.models.Photo
import com.i69.data.models.User
import com.i69.databinding.FragmentUserProfileBinding
import com.i69.gifts.FragmentReceivedGifts
import com.i69.gifts.FragmentSentGifts
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.ImageSliderAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.messenger.chat.contact.ContactActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.screens.main.search.userProfile.getImageSliderIntent
import com.i69.ui.viewModels.CommentsModel
import com.i69.utils.AnimationTypes
import com.i69.utils.ApiUtil
import com.i69.utils.LogUtil
import com.i69.utils.apolloClient
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(), OnPageChangeListener {
    private var userToken: String? = null
    private var userId: String? = null
    private var userFulNAme: String? = ""
    private var userData: User? = null
    private var scrollPos = 0
    private var userAvatarSize = 0
    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var WalletGiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var momentLikesAdapters: StoryLikesAdapter
    private var fragReceivedGifts: FragmentReceivedGifts? = null
    private var fragSentGifts: FragmentSentGifts? = null
    lateinit var adapter: ImageSliderAdapter
    private var TAG: String = UserProfileFragment::class.java.simpleName

    private var currentUserLikes: ArrayList<BlockedUser> = ArrayList()

    private val tabIcons = intArrayOf(
        R.drawable.pink_gift_noavb, R.drawable.pink_gift_noavb
    )
    private val viewModel: VMProfile by activityViewModels()

    var width = 0
    var size = 0

    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserProfileBinding.inflate(inflater, container, false).apply {
            viewModel.isMyUser = true
            this.vm = viewModel
            this.stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    private val addSliderImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(TAG, "RESULT $result")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val indicator: View? =
            binding?.userImgHeader?.findViewById(com.synnapps.carouselview.R.id.indicator)
        indicator?.setViewGone()

        val config: Configuration = resources.configuration
        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            //in Right To Left layout
            binding?.ownProfileLayout?.llName?.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        val displayMetrics = DisplayMetrics()
        width = displayMetrics.widthPixels
        val densityMultiplier = resources.displayMetrics.density
        val scaledPx = 14 * densityMultiplier
        val paint = Paint()
        paint.textSize = scaledPx
        size = paint.measureText("s").roundToInt()

    }

    private fun redirectVisitirPage() {

        val bundle = Bundle()
        bundle.putString("userId", userId)
        bundle.putString("userFulNAme", userFulNAme)

        findNavController().navigate(
            destinationId = R.id.action_global__to_fragment_visitor,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }

    private fun redirectToFolllowingPage() {

        val bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", userId)
        bundle.putString("userFulNAme", userFulNAme)

        findNavController().navigate(
            destinationId = R.id.action_global__to_fragment_follower,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }

    private fun getAllUserMoments(width: Int, size: Int, data: VMProfile.DataCombined) {
        Log.e(TAG, "getAllUserMoments: $width $size")

        lifecycleScope.launch {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetUserMomentsQuery(width, size, 10, "", userId.toString(), "")
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloException currentUserMoments ${e.message}")

                return@launch
            }

            var isUserHasMoments = false

            val allmoments = res.data?.allUserMoments?.edges
            if (!allmoments.isNullOrEmpty()) {
                for (item in allmoments) {
                    Log.e(TAG, "getAllUserMoments: ${item?.node?.user?.id} $userId")
                    if (item?.node?.user?.id.toString() == userId) {
                        isUserHasMoments = true
                        break
                    }
                }

                finalizeViewPagerSetup(isUserHasMoments, data)
            } else finalizeViewPagerSetup(false, data)
        }
    }

    var prevPos = 0

    private fun finalizeViewPagerSetup(userHasMoments: Boolean, data: VMProfile.DataCombined) {
        binding?.profileTabs?.setupWithViewPager(binding?.userDataViewPager)
        binding?.userDataViewPager?.isSaveEnabled = false
        binding?.userDataViewPager?.adapter = viewModel.setupViewPager(
            childFragmentManager, data.user, data.defaultPicker, requireContext(), userHasMoments
        )

        binding?.userDataViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (prevPos != position) viewModel.pauseVideo()
                prevPos = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        binding?.userDataViewPager?.offscreenPageLimit = 3
    }

    override fun setupTheme() {
        getTypeActivity<MainActivity>()?.reloadNavigationMenu()

        showProgressView()
        navController = findNavController()
        Log.e(TAG, "setupTheme: ")
        binding?.userDataViewPager?.setViewGone()
        viewStringConstModel.data.observe(this@UserProfileFragment) { data ->
            binding?.stringConstant = data
            Log.e(TAG, "Feed: ${data.feed}")
            Log.e(TAG, "Wallet: ${data.wallet}")
            viewModel.viewStringConstModel = data
        }

        binding?.inviteFriendBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), ContactActivity::class.java).apply {
                putExtra("isInviteFriendsLink", true)
            })
        }
        Log.e(TAG, "AppStr-Feed=User=>" + AppStringConstant1.feed)
        Log.e(TAG, "AppStr-Feed=User=>" + AppStringConstant1.wallet)
        viewStringConstModel.data.also {
            Log.e(TAG, "setupTheme: ${it.value}")
            binding?.stringConstant = it.value
        }

        binding?.userBaseInfo?.visibility = View.GONE
        binding?.otherProfileLayout?.rvOtherprofile?.visibility = View.GONE


        binding?.ownProfileLayout?.currentUserNotSubScribe?.visibility = View.VISIBLE
        binding?.ownProfileLayout?.lyNotSubScribeCoinVallet?.visibility = View.GONE
        binding?.ownProfileLayout?.llNotSubScribeCoinVallet?.visibility = View.GONE
        binding?.ownProfileLayout?.llButtonSubscribe?.visibility = View.GONE


        binding?.ownProfileLayout?.llButtonSubscribedPackage?.visibility = View.VISIBLE
        binding?.ownProfileLayout?.tvuserActiveSubscription?.visibility = View.VISIBLE
        binding?.ownProfileLayout?.lyUserSubScribeCoinWallet?.visibility = View.VISIBLE
        binding?.ownProfileLayout?.userSubScribeCoinWallet?.visibility = View.VISIBLE

        binding?.ownProfileLayout?.llButtonSubscribe?.setOnClickListener {
            noActiveSubScription()
        }

        binding?.ownProfileLayout?.tvuserActiveSubscription?.setOnClickListener {
            userSubScription(true)
        }

        binding?.followerLayout?.btnFollowing?.setOnClickListener {
            redirectToFolllowingPage()
        }

        binding?.followerLayout?.btnFollower?.setOnClickListener {
            redirectToFolllowingPage()
        }

        binding?.followerLayout?.btnVisitor?.setOnClickListener {
            redirectVisitirPage()
        }

        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            userId = getCurrentUserId()!!

            Log.e(TAG, "usertokenn $userToken")

            getTypeActivity<MainActivity>()?.enableNavigationDrawer()
            binding?.actionGifts1?.visibility = View.VISIBLE
            binding?.ownProfileLayout?.actionCoins?.visibility = View.VISIBLE
            Log.e(TAG, "callUserProfile1")
            viewModel.getProfile(userId) {
                Log.e(TAG, "datareceived: ")
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        requireActivity().runOnUiThread {
                            binding?.userDataViewPager?.setViewVisible()
                        }
                    }
                }, 750)
            }

            viewModel.data.observe(this@UserProfileFragment) { data ->
                Log.e(TAG, "Data: $data")
                if (data == null) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        userPreferences?.clear()
                        val intent = Intent(context, SplashActivity::class.java)
                        startActivity(intent)
                        requireActivity().finishAffinity()
                    }
                }

                if (data != null) {
                    if (data.user != null) {
                        val fullName = data.user!!.fullName
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        userFulNAme = name
                        Log.e(TAG, "UserFullName: $userFulNAme")
                        userData = data.user
                        val ava = Gson().toJson(data.user!!)
                        Log.e(TAG, "UserJson: $ava")
                        if (data.user!!.avatarPhotos != null && data.user!!.avatarPhotos!!.size != 0) {
                            binding?.userImgHeader?.setIndicatorVisibility(View.GONE)
                            adapter = fragmentManager?.let {
                                ImageSliderAdapter(
                                    it, data.user!!.avatarPhotos!!
                                )
                            }!!
                            binding?.container?.adapter = adapter

                            binding?.recyclerTabLayout?.setupWithViewPager(binding?.container, true)
                            binding?.container?.currentItem = scrollPos
                            userAvatarSize = data.user!!.avatarPhotos!!.size

                            if (data.user!!.giftCoins <= 0) {
                                binding?.giftCounter?.visibility = View.GONE
                            } else {
                                binding?.giftCounter?.visibility = View.VISIBLE
                                binding?.giftCounter?.text = "${data.user!!.giftCoins}"
                            }

                            binding?.userImgHeader?.addOnPageChangeListener(this@UserProfileFragment)

                            try {
                                binding?.userImgHeader?.setViewListener {
                                    val view: View = layoutInflater.inflate(
                                        R.layout.custom_imageview, null
                                    )

                                    try {
                                        val pos = it
                                        val imageView = view.findViewById<ImageView>(R.id.userIv)
                                        if (pos <= data.user!!.avatarPhotos!!.size) {

                                            data.user?.avatarPhotos?.get(pos)?.let { avatar ->

                                                val url = if (!BuildConfig.USE_S3) {
                                                    if (avatar.url.toString()
                                                            .startsWith(BuildConfig.BASE_URL)
                                                    ) avatar.url.toString()
                                                    else "${BuildConfig.BASE_URL}${avatar.url.toString()}"
                                                } else if (avatar.url.toString()
                                                        .startsWith(ApiUtil.S3_URL)
                                                ) avatar.url.toString()
                                                else ApiUtil.S3_URL.plus(avatar.url.toString())

                                                imageView.loadImage(url)

                                            }
                                        }
                                        imageView.setOnClickListener {

                                            if (data.user!!.avatarPhotos != null && data.user!!.avatarPhotos!!.size != 0) {

                                                val dataarray: ArrayList<Photo> = ArrayList()
                                                data.user!!.avatarPhotos!!.indices.forEach { i ->

                                                    val photo_ = data.user!!.avatarPhotos!![i]
                                                    dataarray.add(photo_)
                                                }
                                                addSliderImageIntent.launch(
                                                    getImageSliderIntent(
                                                        requireActivity(),
                                                        Gson().toJson(dataarray),
                                                        pos,
                                                        false,
                                                        "",
                                                        "",
                                                        "",
                                                        data.user?.id
                                                    )
                                                )
                                            }

                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "" + e.message)
                                    }
                                    view
                                }
                                binding?.userImgHeader?.pageCount = data.user!!.avatarPhotos!!.size
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            for (f in 0 until binding?.userImgHeader?.pageCount!!) {
                                binding?.userImgHeader?.removeViewAt(f)
                                binding?.userImgHeader?.setIndicatorVisibility(View.GONE)
                            }
                        }
                    }
                }

                if (data != null) {
                    if (data.user != null) {
                        if (data.user!!.avatarPhotos != null) {

                            if (data.user?.avatarPhotos?.size != 0 && data.user?.avatarIndex != null) {
                                if (data.user!!.avatarPhotos?.size!! > data.user?.avatarIndex!!) {
                                    binding?.userProfileImg?.loadCircleImage(
                                        data.user!!.avatarPhotos?.get(
                                            data.user?.avatarIndex!!
                                        )?.url?.replace(
                                            "${BuildConfig.BASE_URL_REP}media/",
                                            "${BuildConfig.BASE_URL}media/"
                                        ).toString()
                                    )
                                }
                            }
                        }
                        try {
                            binding?.textFlag1?.text =
                                data.user!!.city + ", " + data.user!!.countryCode
                            binding?.imgFlag?.loadImage(data.user!!.countryFlag.toString())

                            Log.e(TAG, "Flag Image " + data.user!!.countryFlag)
                        } catch (e: Exception) {
                            Log.e(TAG, "" + e.message)
                        }
                    }
                }
                if (data != null) {
                    if (data.user != null) {
                        getAllUserMoments(width, size, data)
                    }
                }
            }

            viewModel.removeMomentFromUserFeed.observe(this@UserProfileFragment) {
                viewModel.data.value?.let { it1 -> finalizeViewPagerSetup(false, it1) }
            }

            getNotificationIndex()
        }
        userSubScription()
        showLikeBottomSheet()
        GiftbottomSheetBehavior = BottomSheetBehavior.from(binding?.giftbottomSheet!!)

        GiftbottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })

        WalletGiftbottomSheetBehavior =
            BottomSheetBehavior.from<ConstraintLayout>(binding?.walletGiftbottomSheet!!)
        WalletGiftbottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })

        binding?.linearLayoutUserLikes?.setOnClickListener {
            try {
                loadLikesDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding?.giftsTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.giftsPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.giftsTabs?.setupWithViewPager(binding?.giftsPager)
        setupViewPager(binding?.giftsPager!!)
        binding?.giftsTabs?.tabIconTint = null
        binding?.giftsTabs?.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    if (tab.position == 0) {
                        binding?.unametitle?.text = "${AppStringConstant1.sender}"
                    } else if (tab.position == 1) {
                        binding?.unametitle?.text = AppStringConstant1.beneficiary_name
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })
        for (i in 0 until 2) {
            val tab: TabLayout.Tab? = binding?.giftsTabs?.getTabAt(i)
            if (tab != null) {
                val tabTextView = TextView(requireContext())
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = if (i == 0) AppStringConstant1.rec_gifts
                else AppStringConstant1.sent_gifts
                tabTextView.setTextColor(Color.WHITE)
                tabTextView.textSize = 15f
                tabTextView.gravity = View.TEXT_ALIGNMENT_CENTER
                tabTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.gift_icon_small, 0, 0, 0
                )
                tabTextView.compoundDrawablePadding = 15
            }
        }

        binding?.giftsTabs1?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.giftsPager1?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.giftsTabs1?.setupWithViewPager(binding?.giftsPager1!!)
        setupReceivedGiftViewPager(binding?.giftsPager1!!)
        binding?.giftsTabs1?.tabIconTint = null
        binding?.giftsTabs1?.getTabAt(0)!!.setIcon(tabIcons[0])
//        binding.giftsTabs1.getTabAt(1)!!.setIcon(tabIcons[1])
        binding?.giftsTabs1?.addOnTabSelectedListener(
//        binding.giftsTabs.setOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    if (tab.position == 0) {
                        binding?.unametitle1?.text = "${AppStringConstant1.sender}"
                    } else if (tab.position == 1) {
                        binding?.unametitle1?.text = AppStringConstant1.beneficiary_name
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })

        for (i in 0 until 1) {
            val tab: TabLayout.Tab? = binding?.giftsTabs1?.getTabAt(i)
            if (tab != null) {
                val tabTextView = TextView(requireContext())
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = if (i == 0) AppStringConstant1.rec_gifts
                else AppStringConstant1.sent_gifts
                tabTextView.setTextColor(Color.WHITE)
                tabTextView.textSize = 15f
                tabTextView.gravity = View.TEXT_ALIGNMENT_CENTER
                tabTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.gift_icon_small, 0, 0, 0
                )
                tabTextView.compoundDrawablePadding = 15
            }
        }


        Handler(Looper.getMainLooper()).post { hideProgressView() }

    }


    private fun showLikeBottomSheet() {
//        Log.i(TAG, "showLikeBottomSheet: UserId: ${item?.node?.pk}")

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

    private fun loadLikesDialog() {
        currentUserLikes.clear()
        userData?.likes?.let { currentUserLikes.addAll(it) }

        binding?.txtheaderlike?.text = currentUserLikes.size.toString() + " Likes"

        if (currentUserLikes.isNotEmpty()) {
            binding?.noDatas?.visibility = View.GONE
            binding?.rvLikes?.visibility = View.VISIBLE

            val items1: ArrayList<CommentsModel> = ArrayList()
            currentUserLikes.forEach { i ->
                val models = CommentsModel()
                models.commenttext = i.fullName
                models.uid = i.id
                models.userurl = try {
                    userData?.avatarIndex?.let {
                        if (i.avatarPhotos?.size!! > 0) i.avatarPhotos[it].url
                    }.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    i.avatarPhotos?.get(0)?.url.toString()
                }
                items1.add(models)
            }

            momentLikesAdapters.addAll(items1)
            momentLikesAdapters.notifyDataSetChanged()

        } else {
            binding?.noDatas?.visibility = View.VISIBLE
            binding?.rvLikes?.visibility = View.GONE
        }
        if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun userSubScription(isOpenDialog: Boolean = false) {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val userToken = getCurrentUserToken()!!
                val response = try {
                    apolloClient(requireContext(), userToken).query(
                        UserSubscriptionQuery()
                    ).execute()

                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    hideProgressView()
                    if (!isOpenDialog) {
                        binding?.ownProfileLayout?.currentUserNotSubScribe?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.lyNotSubScribeCoinVallet?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.llNotSubScribeCoinVallet?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.llButtonSubscribe?.visibility = View.VISIBLE

                        binding?.ownProfileLayout?.llButtonSubscribedPackage?.visibility = View.GONE
                        binding?.ownProfileLayout?.tvuserActiveSubscription?.visibility = View.GONE
                        binding?.ownProfileLayout?.lyUserSubScribeCoinWallet?.visibility = View.GONE
                        binding?.ownProfileLayout?.userSubScribeCoinWallet?.visibility = View.GONE

                    }
                    return@repeatOnLifecycle
                }

                if (response.hasErrors()) {
                    hideProgressView()
                    val errorMessage = response.errors?.get(0)?.message
                    Log.e(TAG, "UserSubscription: errorMessage: $errorMessage")
                    if (!isOpenDialog) {

                        binding?.ownProfileLayout?.currentUserNotSubScribe?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.lyNotSubScribeCoinVallet?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.llNotSubScribeCoinVallet?.visibility =
                            View.VISIBLE
                        binding?.ownProfileLayout?.llButtonSubscribe?.visibility = View.VISIBLE


                        binding?.ownProfileLayout?.llButtonSubscribedPackage?.visibility = View.GONE
                        binding?.ownProfileLayout?.tvuserActiveSubscription?.visibility = View.GONE
                        binding?.ownProfileLayout?.lyUserSubScribeCoinWallet?.visibility = View.GONE
                        binding?.ownProfileLayout?.userSubScribeCoinWallet?.visibility = View.GONE

                    }
                    if (errorMessage != null) {
                        binding?.root?.snackbar(errorMessage)
                    }
                } else {
                    Log.e(TAG, "UserSubscription:  ${Gson().toJson(response.data)}")
                    hideProgressView()
                    if (isOpenDialog) {
                        activeSubScriptionDetail(response.data!!.userSubscription!!)
                    } else {
                        if (response.data!!.userSubscription!!.`package` != null) {
                            binding?.ownProfileLayout?.tvuserActiveSubscription?.text =
                                response.data!!.userSubscription!!.`package`!!.name

                            binding?.ownProfileLayout?.currentUserNotSubScribe?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.lyNotSubScribeCoinVallet?.visibility =
                                View.GONE
                            binding?.ownProfileLayout?.llNotSubScribeCoinVallet?.visibility =
                                View.GONE
                            binding?.ownProfileLayout?.llButtonSubscribe?.visibility = View.GONE


                            binding?.ownProfileLayout?.llButtonSubscribedPackage?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.tvuserActiveSubscription?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.lyUserSubScribeCoinWallet?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.userSubScribeCoinWallet?.visibility =
                                View.VISIBLE
                        } else {
                            binding?.ownProfileLayout?.currentUserNotSubScribe?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.lyNotSubScribeCoinVallet?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.llNotSubScribeCoinVallet?.visibility =
                                View.VISIBLE
                            binding?.ownProfileLayout?.llButtonSubscribe?.visibility = View.VISIBLE


                            binding?.ownProfileLayout?.llButtonSubscribedPackage?.visibility =
                                View.GONE
                            binding?.ownProfileLayout?.tvuserActiveSubscription?.visibility =
                                View.GONE
                            binding?.ownProfileLayout?.lyUserSubScribeCoinWallet?.visibility =
                                View.GONE
                            binding?.ownProfileLayout?.userSubScribeCoinWallet?.visibility =
                                View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragReceivedGifts = FragmentReceivedGifts()
        fragSentGifts = FragmentSentGifts()

        adapter.addFragItem(fragReceivedGifts!!, AppStringConstant1.rec_gifts)
        adapter.addFragItem(fragSentGifts!!, AppStringConstant1.sent_gifts)

        viewPager.adapter = adapter
    }

    private fun setupReceivedGiftViewPager(viewPager: ViewPager) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragReceivedGifts = FragmentReceivedGifts()

        adapter.addFragItem(fragReceivedGifts!!, AppStringConstant1.rec_gifts)

        viewPager.adapter = adapter
    }

    private fun getNotificationIndex() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(GetNotificationCountQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception NotificationIndex${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "apolloResponse NotificationIndex ${res.hasErrors()}")
                if (res.hasErrors()) {
                    if (JSONObject(res.errors!![0].toString()).getString("code")
                            .equals("InvalidOrExpiredToken")
                    ) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            userPreferences?.clear()
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
                val notifyCount = res.data?.unseenCount
                if (notifyCount == null || notifyCount == 0) {
                    binding?.counter?.visibility = View.GONE
                } else {
                    binding?.counter?.visibility = View.VISIBLE
                    if (notifyCount > 10) {
                        binding?.counter?.text = "9+"
                    } else {
                        binding?.counter?.text = "$notifyCount"
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getMainActivity().setDrawerItemCheckedUnchecked(null)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            localBroadcastReceiver, IntentFilter(Constants.INTENTACTION)
        )
    }

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Log.e(TAG, "Broadcast: Broadcast received 22" + intent!!.getStringExtra("extra"))

            val title = intent.getStringExtra("extra")
            if ((title!!.lowercase(Locale.getDefault())
                    .contains("gifted coins deduction") || title.lowercase(
                    Locale.getDefault()
                ).contains("gifted coins added")) && intent.hasExtra("coins")
            ) {
                val coins = intent.getStringExtra("coins")
                Log.e(TAG, "Broadcast: Sent_message coins" + "$coins")

                updateView("")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the local broadcast receiver
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(localBroadcastReceiver)
    }

    fun updateView(state: String) {
        Log.e(TAG, "updateView: updateViewState : $state")
        viewModel.getProfile(userId) {

        }
    }

    override fun setupClickListeners() {
        val bundle = Bundle()
        bundle.putString("userId", userId)
        viewModel.onSendMsg = { requireActivity().onBackPressed() }
        binding?.actionBack?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.actionShare?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.onCoins = { userCoinDetail(it) }

        viewModel.onDrawer = { (activity as MainActivity).drawerSwitchState() }

        viewModel.onEditProfile =
            { navController?.navigate(R.id.action_userProfileFragment_to_userEditProfileFragment) }
        viewModel.onGift = {
            binding?.purchaseButton?.visibility = View.GONE
            binding?.topl?.visibility = View.VISIBLE

            if (GiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            } else {
                GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding?.ownProfileLayout?.walletIcon?.setOnClickListener {

            binding?.purchaseButton1?.visibility = View.GONE
            binding?.topl1?.visibility = View.VISIBLE


            if (WalletGiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                WalletGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                WalletGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding?.ownProfileLayout?.walletIcon1?.setOnClickListener {
            binding?.purchaseButton1?.visibility = View.GONE
            binding?.topl1?.visibility = View.VISIBLE

            if (WalletGiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                WalletGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                WalletGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding?.bell?.setOnClickListener {
            val dialog =
                NotificationDialogFragment(userToken, binding?.counter, userId, binding?.bell)
            dialog.show(
                childFragmentManager, "${AppStringConstant1.comments}"
            )
        }
    }

    private fun userCoinDetail(coins: String) {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_user_coin_option, null, false)
        bottomsheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val tvUserBalance = customView.findViewById<MaterialTextView>(R.id.tv_user_balance)
        val tvUserBalanceCoins =
            customView.findViewById<MaterialTextView>(R.id.tv_user_balance_coin)

        val upgrade_button = customView.findViewById<MaterialTextView>(R.id.upgrade_button)

        val cdUpgradeBalance = customView.findViewById<LinearLayout>(R.id.cd_upgrade_balance)

        val imageCross = customView.findViewById<ImageView>(R.id.iv_cross)

        val typeface_regular =
            Typeface.createFromAsset(activity?.assets, "fonts/poppins_semibold.ttf")
        val typeface_light = Typeface.createFromAsset(activity?.assets, "fonts/poppins_light.ttf")

        tvUserBalance.typeface = typeface_light
        upgrade_button.typeface = typeface_regular

        tvUserBalanceCoins.text = " $coins".plus("\nCoin")

        cdUpgradeBalance.setOnClickListener {
            bottomsheetDialog.dismiss()
            navigateToPurchase()
        }

        imageCross.setOnClickListener {
            bottomsheetDialog.dismiss()
        }

        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }


    private fun noActiveSubScription() {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_user_coin_option, null, false)
        bottomsheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val title = customView.findViewById<TextView>(R.id.description)
        val iv_gpay = customView.findViewById<ImageView>(R.id.iv_gpay)
        val tvUserBalance = customView.findViewById<MaterialTextView>(R.id.tv_user_balance)

        val upgrade_button = customView.findViewById<MaterialTextView>(R.id.upgrade_button)

        val cdUpgradeBalance = customView.findViewById<LinearLayout>(R.id.cd_upgrade_balance)

        val imageCross = customView.findViewById<ImageView>(R.id.iv_cross)

        val typeface_regular =
            Typeface.createFromAsset(activity?.assets, "fonts/poppins_semibold.ttf")
        val typeface_light = Typeface.createFromAsset(activity?.assets, "fonts/poppins_light.ttf")

        tvUserBalance.typeface = typeface_light
        upgrade_button.typeface = typeface_regular


        iv_gpay.setImageResource(R.drawable.subscription)
        title.visibility = View.GONE
        tvUserBalance.text = AppStringConstant1.no_active_subscription
        upgrade_button.text = AppStringConstant1.buy_subscription

        cdUpgradeBalance.setOnClickListener {
            bottomsheetDialog.dismiss()
            navigatePlanPurchase()
        }

        imageCross.setOnClickListener {
            bottomsheetDialog.dismiss()
        }

        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }

    private val formatterDateTimeUTC =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val formatterDateOnly = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        //timeZone = TimeZone.getTimeZone("UTC")
        timeZone = TimeZone.getDefault()
    }

    private fun activeSubScriptionDetail(userSubscription: UserSubscriptionQuery.UserSubscription) {
        val bottomsheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.dialog_upgrage_subscription, null, false)
        bottomsheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val iv_close = customView.findViewById<ImageView>(R.id.iv_cross)

        val tvSubscriptionName = customView.findViewById<TextView>(R.id.tv_subscription_name)
        val tvSubscriptionPrice = customView.findViewById<TextView>(R.id.tv_subscription_price)
        val tvSubscriptionDescription =
            customView.findViewById<TextView>(R.id.tv_subscription_description)

        val tv_subscription_date = customView.findViewById<TextView>(R.id.tv_subscription_date)
        val tv_subscription_left = customView.findViewById<TextView>(R.id.tv_subscription_left)


        val clUpGrade = customView.findViewById<LinearLayout>(R.id.cd_upgrade_subscription)

        iv_close.setOnClickListener { bottomsheetDialog.dismiss() }

        if (userSubscription.`package` != null) {
            tvSubscriptionName.text = userSubscription.`package`.name


        }
        if (userSubscription.plan != null) {
            tvSubscriptionPrice.text = "${userSubscription.plan.priceInCoins}"
            if (userSubscription.plan.isOnDiscount) {
                tvSubscriptionDescription.text = "${userSubscription.plan.dicountedPriceInCoins}"
            } else {
                tvSubscriptionDescription.visibility = View.GONE
            }
        }

        val startDate = formatterDateTimeUTC.parse(userSubscription.startsAt.toString())
        val endDate = formatterDateTimeUTC.parse(userSubscription.endsAt.toString())
        tv_subscription_date.text =
            formatterDateOnly.format(startDate).plus(" - ").plus(formatterDateOnly.format(endDate))
        val diffInMillisec: Long = endDate.time - Date().time
        val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diffInMillisec)
        tv_subscription_left.text = String.format(getString(R.string.days_left), diffInDays)

        if (userSubscription.`package`?.name?.contains(
                AppStringConstant1.platinum, true
            ) == true
        ) clUpGrade.setViewGone()


        clUpGrade.setOnClickListener {
            bottomsheetDialog.dismiss()
            navigatePlanPurchase()
        }

        bottomsheetDialog.setContentView(customView)
        bottomsheetDialog.show()
    }


    private fun navigateToPurchase() {
        findNavController().navigate(R.id.actionGoToPurchaseFragment)
    }

    private fun navigatePlanPurchase() {
        findNavController().navigate(R.id.action_global_plan)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        binding?.container?.currentItem = position
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun getMainActivity() = activity as MainActivity

}
