package com.i69.ui.screens.main.search.userProfile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.i69.*
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.ModelGifts
import com.i69.data.models.Photo
import com.i69.databinding.AlertFullImageBinding
import com.i69.databinding.FragmentUserProfileBinding
import com.i69.di.modules.AppModule
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.BuildConfig
import com.i69.R
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.ImageSliderAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.base.profile.PRIVATE
import com.i69.ui.base.profile.PUBLIC
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.messenger.chat.contact.ContactActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.*
import com.synnapps.carouselview.ViewListener
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.roundToInt

@AndroidEntryPoint
class SearchUserProfileFragment : BaseFragment<FragmentUserProfileBinding>(),
    ViewPager.OnPageChangeListener {
    companion object {
        const val ARGS_FROM_CHAT = "from_chat"
    }

    private val viewModel: VMProfile by activityViewModels()
    private val mViewModel: UserViewModel by activityViewModels()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    lateinit var adapter: ImageSliderAdapter
    private var fromChat: Boolean = false
    private var otherUserId: String? = ""
    private var otherUserName: String? = ""
    private var otherFirstName: String? = ""
    private var TAG: String = SearchUserProfileFragment::class.java.simpleName
    private var chatBundle = Bundle()

    var privatePhotoRequestStatus = ""

    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null
    private var userToken: String? = null
    private var LuserId: String? = null
    private var userFullName: String = ""

    var width = 0
    var size = 0

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserProfileBinding.inflate(inflater, container, false).apply {
            viewModel.isMyUser = false
            this.vm = viewModel
            this.stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    private val addSliderImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(TAG, "RESULT" + result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        width = displayMetrics.widthPixels
        val densityMultiplier = getResources().getDisplayMetrics().density;
        val scaledPx = 14 * densityMultiplier;
        val paint = Paint()
        paint.setTextSize(scaledPx);
        size = paint.measureText("s").roundToInt();
    }

    private fun redirectToFolllowingPage() {

        val bundle = Bundle()
        bundle.putBoolean(ARGS_FROM_CHAT, false)
        bundle.putString("userId", otherUserId)
        bundle.putString("userFulNAme", otherFirstName)

        findNavController().navigate(
            destinationId = R.id.action_global__to_fragment_follower,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }


    private fun redirectVisitorPage() {

        val bundle = Bundle()
        bundle.putString("userId", otherUserId)
        bundle.putString("userFulNAme", userFullName)

        findNavController().navigate(
            destinationId = R.id.action_global__to_fragment_visitor,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }

    override fun setupTheme() {
        showProgressView()
        binding?.userDataViewPager?.setViewGone()
        viewStringConstModel.data.observe(this@SearchUserProfileFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }

        binding?.inviteFriendBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), ContactActivity::class.java).apply {
                putExtra("isInviteFriendsLink", true)
            })
        }
        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            LuserId = getCurrentUserId()!!

            viewModel.data.observe(this@SearchUserProfileFragment) { data ->

                if (data != null) {
                    if (data.user != null) {
                        val fullName = data.user!!.fullName!!
                        val name = if (fullName != null && fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        userFullName = name
                    }
                }
            }

            fromChat = requireArguments().getBoolean(ARGS_FROM_CHAT)
            otherUserId = requireArguments().getString("userId")
            Log.e(TAG, "UserID: 222" + otherUserId)
            userFullName = requireArguments().getString("fullName").toString()
            chatBundle.putString("otherUserId", otherUserId)
            chatBundle.putString("otherUserPhoto", "")

            viewModel.getProfile(otherUserId) {
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().runOnUiThread {
                        if (view != null) binding?.userDataViewPager?.setViewVisible()
                    }
                }, 500)
            }

            subscribeonUpdatePrivatePhotoRequest()

            setLastSeen()
            setDistance()
            checkFirstMessageStatus()
            addProfileVisit(otherUserId.toString())

            val glide = Glide.with(requireContext())
            viewModel.data.observe(this@SearchUserProfileFragment) { data ->
                //Check if fragment is added or detached from activity
                if (isAdded) {
                    privatePhotoRequestStatus =
                        data?.user?.privatePhotoRequestStatus ?: "Request Access"

                    if (data == null) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                AppStringConstant1.somethig_went_wrong_please_try_again,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        if (data.user != null) {
                            otherUserName = data.user?.username
                            otherFirstName = data.user?.fullName
                            binding?.sendgiftto?.text =
                                AppStringConstant1.send_git_to + " " + otherFirstName!!
                            if (data.user!!.isConnected) {
                                binding?.followerLayout?.lyfollowBtns?.weightSum = 2f
                                binding?.followerLayout?.btnFollow?.visibility = View.GONE
                                binding?.followerLayout?.btnVisitorSearchUser?.visibility = View.VISIBLE
                            } else {
                                binding?.followerLayout?.lyfollowBtns?.weightSum = 3f
                                binding?.followerLayout?.btnFollow?.visibility = View.VISIBLE
                                binding?.followerLayout?.btnVisitorSearchUser?.visibility = View.GONE
                            }
                            Log.e(TAG,"SUPF"+ "setupTheme: ${data.user?.subscription}")
                            if (!data.user!!.subscription.isNullOrEmpty()) {
                                if (data.user!!.subscription.equals("PLATINUM", true)) {
                                    binding?.otherProfileLayout?.txtMember?.text =
                                        getString(R.string.text_platnium)
                                    binding?.otherProfileLayout?.llPlanBack?.setBackgroundResource(R.drawable.ic_paltinum_back)
                                } else if (data.user!!.subscription.equals("GOLD", true)) {
                                    binding?.otherProfileLayout?.txtMember?.text =
                                        getString(R.string.text_gold)
                                    binding?.otherProfileLayout?.llPlanBack?.setBackgroundResource(R.drawable.ic_gold_back)
                                } else if (data.user!!.subscription.equals("SILVER", true)) {
                                    binding?.otherProfileLayout?.txtMember?.text =
                                        getString(R.string.text_silver)
                                    binding?.otherProfileLayout?.llPlanBack?.setBackgroundResource(R.drawable.ic_silver_back)
                                } else {
                                    binding?.otherProfileLayout?.txtMember?.text =
                                        data.user!!.subscription
                                }
                            }
                            //
                            if (data.user!!.isOnline) {
                                binding?.otherProfileLayout?.tvLastseen?.visibility = View.GONE
                                binding?.otherProfileLayout?.tvGreen?.setBackgroundResource(R.drawable.green_online)
                            } else {
                                binding?.otherProfileLayout?.tvLastseen?.visibility = View.VISIBLE
                                binding?.otherProfileLayout?.tvGreen?.setBackgroundResource(R.drawable.gray_offline)
                            }
                            if (data.user!!.avatarPhotos != null && data.user!!.avatarPhotos!!.size != 0) {
                                binding?.userImgHeader?.setIndicatorVisibility(View.VISIBLE)
                                adapter = fragmentManager?.let {
                                    ImageSliderAdapter(
                                        it, data.user!!.avatarPhotos!!
                                    )
                                }!!
                                binding?.container?.adapter = adapter

                                binding?.recyclerTabLayout?.setupWithViewPager(binding?.container, true)
                                binding?.container?.currentItem = 0

                                binding?.userImgHeader?.addOnPageChangeListener(this@SearchUserProfileFragment)
                                var isPrivateImagesFound = false
                                var privateUrl = ""
                                var privatePhoto: Photo? = null

                                try {
                                    binding?.userImgHeader?.setViewListener(ViewListener {

                                        val photo = data.user!!.avatarPhotos?.get(it)

                                        var view: View = if (photo?.type == PUBLIC) {
                                            layoutInflater.inflate(
                                                R.layout.custom_imageview, null
                                            )
                                        } else {
                                            requireActivity().getWindow().setFlags(
                                                WindowManager.LayoutParams.FLAG_SECURE,
                                                WindowManager.LayoutParams.FLAG_SECURE
                                            )
                                            layoutInflater.inflate(
                                                R.layout.custom_privateimageview, null
                                            )
                                        }

                                        var pos = it
                                        var imageView =
                                            view.findViewById<ImageView>(com.i69.R.id.userIv)
                                        Log.e(TAG, "Position: $it")
                                        if (it <= data.user?.avatarPhotos?.size!!) {
                                            data.user?.avatarPhotos?.get(it)?.let {
                                                if (photo?.type != PUBLIC) {
                                                    isPrivateImagesFound = true
                                                    val requestView =
                                                        view.findViewById<MaterialTextView>(R.id.cd_requestview)
                                                    val cancelView =
                                                        view.findViewById<MaterialTextView>(R.id.cd_cancelview)
                                                    Log.e(
                                                        TAG,
                                                        "AvatarPhotoSize: ${data.user?.avatarPhotos?.size!!}"
                                                    )
                                                    Log.e(
                                                        TAG,
                                                        "AvatarIndex: ${data.user!!.avatarIndex!!}"
                                                    )
                                                    val index =
                                                        if (data.user?.avatarPhotos?.size!! == data.user!!.avatarIndex!!) {
                                                            data.user!!.avatarIndex!! - 1
                                                        } else {
                                                            data.user!!.avatarIndex!!
                                                        }
                                                    privateUrl =
                                                        data.user!!.avatarPhotos!![index].url.toString()
                                                    privatePhoto =
                                                        Photo(photo?.id.toString(), privateUrl, PRIVATE)

                                                    glide.load(
                                                        privateUrl.replace(
                                                            "${BuildConfig.BASE_URL_REP}media/",
                                                            "${BuildConfig.BASE_URL}media/"
                                                        )
                                                    ).apply(
                                                        RequestOptions.bitmapTransform(
                                                            BlurTransformation(
                                                                25, 3
                                                            )
                                                        )
                                                    ).into(imageView)

                                                    requestView.setOnClickListener {
                                                        sentRequest()
                                                        cancelView.visibility = View.VISIBLE
                                                        requestView.visibility = View.GONE
                                                        privatePhotoRequestStatus = "Cancel Request"
                                                    }

                                                    if (privatePhotoRequestStatus == "Cancel Request") {
                                                        cancelView.visibility = View.VISIBLE
                                                        requestView.visibility = View.GONE
                                                    } else {
                                                        cancelView.visibility = View.GONE
                                                        requestView.visibility = View.VISIBLE
                                                    }

                                                    cancelView.setOnClickListener {
                                                        cancelRequest()
                                                        cancelView.visibility = View.GONE
                                                        requestView.visibility = View.VISIBLE
                                                        privatePhotoRequestStatus = "Request Access"
                                                    }

                                                } else {
                                                    it?.url?.replace(
                                                        "${BuildConfig.BASE_URL_REP}media/",
                                                        "${BuildConfig.BASE_URL}media/"
                                                    )?.let { it1 ->
                                                        imageView.loadImage(
                                                            it1
                                                        )
                                                    }
                                                }

                                            }
                                        }

                                        imageView.setOnClickListener {
                                            if (data.user?.avatarPhotos != null && data.user?.avatarPhotos!!.size != 0) {

                                                val dataarray: ArrayList<Photo> = ArrayList()

                                                data.user?.avatarPhotos?.indices?.forEach { i ->

                                                    val photo_ = data.user!!.avatarPhotos!![i]

                                                    if (photo_.type == PUBLIC) {
                                                        dataarray.add(photo_)
                                                    } else {
                                                        privatePhoto?.let { it1 -> dataarray.add(it1) }
                                                    }
                                                }

                                                addSliderImageIntent.launch(
                                                    getImageSliderIntent(
                                                        requireActivity(),
                                                        Gson().toJson(dataarray),
                                                        pos,
                                                        isPrivateImagesFound,
                                                        privatePhotoRequestStatus,
                                                        userToken,
                                                        otherUserId,
                                                        data.user?.id
                                                    )
                                                )
                                            }
                                        }
                                        view
                                    })
                                    binding?.userImgHeader?.pageCount = data.user!!.avatarPhotos!!.size
                                } catch (e: Exception) {
                                }
                            } else {
                                for (f in 0 until binding?.userImgHeader?.pageCount!!) {
                                    try {
                                        binding?.userImgHeader?.removeViewAt(f)
                                        binding?.userImgHeader?.setIndicatorVisibility(View.GONE)
                                    } catch (e: NullPointerException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            chatBundle.putString("otherUserName", data.user?.fullName)
                            if (data.user?.gender != null) chatBundle.putInt(
                                "otherUserGender",
                                data.user?.gender!!
                            )

                            if (data.user?.avatarPhotos != null) {
                                if ((data.user?.avatarPhotos?.size!! > 0) && (data.user?.avatarIndex!! < data.user!!.avatarPhotos!!.size)) {
                                    chatBundle.putString(
                                        "otherUserPhoto", data.user!!.avatarPhotos!!.get(
                                            data.user!!.avatarIndex!!
                                        ).url?.replace(
                                            "http://95.216.208.1:8000/media/",
                                            "${BuildConfig.BASE_URL}media/"
                                        )
                                    )

                                    binding?.userProfileImg?.loadCircleImage(
                                        data.user!!.avatarPhotos!![data.user!!.avatarIndex!!].url?.replace(
                                            "http://95.216.208.1:8000/media/",
                                            "${BuildConfig.BASE_URL}media/"
                                        )!!
                                    )
                                }
                            }
                            try {
                                val substring = data.user!!.country?.subSequence(0, 2)
                                binding?.otherProfileLayout?.textFlag?.setText(data.user!!.city + ", " + substring)

                                binding?.textFlag1?.setText(data.user!!.city + ", " + substring)
                                binding?.imgFlag?.loadImage(data.user!!.countryFlag.toString())
                                binding?.otherProfileLayout?.imageFlag?.loadImage(data.user!!.countryFlag.toString())
                            } catch (e: Exception) {
                                Log.e(TAG, "" + e.message)
                            }
                        }
                    }

                    if (data != null) {
                        getAllUserMoments(width, size, data)
                    }
                }
            }
        }

        binding?.bell?.setOnClickListener {
            val dialog =
                NotificationDialogFragment(userToken, binding?.counter, LuserId, binding?.bell)
            dialog.show(
                childFragmentManager, "${AppStringConstant1.comments}"
            )
        }

        binding?.followerLayout?.btnFollowing?.setOnClickListener {
            redirectToFolllowingPage()
        }

        binding?.followerLayout?.btnFollower?.setOnClickListener {
            redirectToFolllowingPage()
        }

        binding?.followerLayout?.btnFollow?.setOnClickListener {
            userFollowMutationCall()

        }

        binding?.followerLayout?.btnVisitorSearchUser?.setOnClickListener {
            redirectVisitorPage()
        }


        binding?.actionGifts1?.visibility = View.GONE
//      binding.actionGifts.visibility = View.VISIBLE
        binding?.otherProfileLayout?.actionCoins123?.visibility = View.GONE
        adjustScreen()
        GiftbottomSheetBehavior =
            BottomSheetBehavior.from<ConstraintLayout>(binding?.giftbottomSheet!!)
        GiftbottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        binding?.sendgiftto?.setOnClickListener(View.OnClickListener {

            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }

            lifecycleScope.launchWhenCreated() {
                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->
                        Log.e(TAG, "gift.id" + gift.id)
                        Log.e(TAG, "otherUserId" + "--> " + otherUserId)
                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(
                                requireContext(), userToken!!
                            ).mutation(GiftPurchaseMutation(gift.id, otherUserId!!, LuserId!!))
                                .execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            Toast.makeText(
                                requireContext(), "${e.message}", Toast.LENGTH_LONG
                            ).show()
//                                views.snackbar("Exception ${e.message}")
                            //hideProgressView()
                            //return@launchWhenResumed
                        }

                        Log.e(TAG, "res" + "--> " + Gson().toJson(res))
                        if (res?.hasErrors() == false) {
//                                views.snackbar("You bought ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} successfully!")
                            Toast.makeText(
                                requireContext(),
                                AppStringConstant1.you_bought + "${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + AppStringConstant1.successfully,
                                Toast.LENGTH_LONG
                            ).show()

                        }
                        if (res!!.hasErrors()) {
                            Log.e(TAG, "resc" + "--> " + Gson().toJson(res))
                            Toast.makeText(
                                requireContext(), "${res.errors!![0].message}", Toast.LENGTH_LONG
                            ).show()
                            Log.e(TAG, "resd" + "--> " + Gson().toJson(res))
                        }
                        if (res.hasErrors()) {
                            try {
                                Log.e(
                                    TAG,
                                    "rese" + "--> " + JSONObject(
                                        res.errors!!.get(0).toString()
                                    ).getString("code")
                                )
                                if (JSONObject(
                                        res.errors!!.get(0).toString()
                                    ).getString("code") != null
                                ) {
                                    Log.e(TAG, "resf" + "--> " + Gson().toJson(res))
                                    if (JSONObject(res.errors!!.get(0).toString()).getString("code")
                                            .equals("InvalidOrExpiredToken")
                                    ) {
                                        Log.e(TAG, "resg" + "--> " + Gson().toJson(res))

                                        lifecycleScope.launch(Dispatchers.Main) {
                                            userPreferences?.clear()
                                            //App.userPreferences.saveUserIdToken("","","")
                                            val intent =
                                                Intent(activity, SplashActivity::class.java)
                                            startActivity(intent)
                                            requireActivity().finishAffinity()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "resez" + "--> " + e)
                            }
                        }
                        Log.e(
                            TAG,
                            "apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}"
                        )
                    }
                    hideProgressView()
                }
            }

        })

        binding?.giftsTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.giftsPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.giftsTabs?.setupWithViewPager(binding?.giftsPager!!)
        setupViewPager(binding?.giftsPager)
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                hideProgressView()
            }
        }, 1000)
    }

    private fun getAllUserMoments(width: Int, size: Int, data: VMProfile.DataCombined) {

        Log.e(TAG,"SUPF"+ "getAllUserMoments: $width $size")

        lifecycleScope.launch {
            val res = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetUserMomentsQuery(width, size, 10, "", otherUserId.toString(), "")
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloException currentUserMoments ${e.message}")
                Log.e(TAG, "getAllUserMoments" + "${e.message}")
                return@launch
            }

            var isUserHasMoments = false

            val allmoments = res.data?.allUserMoments?.edges
            if (!allmoments.isNullOrEmpty()) {
                for (item in allmoments) {
                    Log.e(TAG,"SUPF"+ "getAllUserMoments: ${item?.node?.user?.id} ${otherUserId}")
                    if (item?.node?.user?.id.toString() == otherUserId) {
                        isUserHasMoments = true
                        break
                    }
                }

                finalizeViewPagerSetup(isUserHasMoments, data)
            } else finalizeViewPagerSetup(false, data)
        }
    }

    private fun finalizeViewPagerSetup(userHasMoments: Boolean, data: VMProfile.DataCombined) {
        binding?.profileTabs?.setupWithViewPager(binding?.userDataViewPager)
        binding?.userDataViewPager?.isSaveEnabled = false;
        binding?.userDataViewPager?.adapter = viewModel.setupViewPager(
            childFragmentManager, data?.user, data?.defaultPicker, requireContext(), userHasMoments
        )
        binding?.userDataViewPager?.offscreenPageLimit = 3
    }

    private fun addProfileVisit(otherUserId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val myUserId = getCurrentUserId().toString()
            val token = getCurrentUserToken().toString()
            Log.e(TAG,"SearchUserProfileFrag"+ "myUserId: $myUserId, token: $token")

            val response = viewModel.addUserProfileVisit(myUserId, otherUserId, token)
            Log.e("SearchUserProfileFrag", "${response.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun sentRequest() {
        lifecycleScope.launch {

            try {

                val response = apolloClient(requireContext(), userToken!!).mutation(
                    RequestUserPrivatePhotosMutation(otherUserId!!)
                ).execute()

                if (response.hasErrors()) {

                    Toast.makeText(
                        requireContext(), "${AppStringConstant1.request_access_error} ${
                            response.errors?.get(0)?.message
                        }", Toast.LENGTH_LONG
                    ).show()

                } else {

                    binding?.root?.snackbar(AppStringConstant1.rewuest_sent)

                }


            } catch (e: ApolloException) {
//                Toast.makeText(requireContext(),"Request access ApolloException ${e.message}",Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
//                Toast.makeText(requireContext(),"Request access Exception ${e.message}",Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun cancelRequest() {

        lifecycleScope.launch {

            try {

                val response = apolloClient(requireContext(), userToken!!).mutation(
                    CancelPrivatePhotoRequestMutation(otherUserId!!)
                ).execute()

                if (response.hasErrors()) {

                    Toast.makeText(
                        requireContext(), "${AppStringConstant1.cancel_request_error} ${
                            response.errors?.get(0)?.message
                        }", Toast.LENGTH_LONG
                    ).show()

                } else {
                    binding?.root?.snackbar(AppStringConstant1.request_cancelled)
                }
            } catch (e: ApolloException) {
//                Toast.makeText(requireContext(),"Cancel Request  ApolloException ${e.message}",Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
//                Toast.makeText(requireContext(),"Cancel Request Exception ${e.message}",Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun setLastSeen() {
        lifecycleScope.launch {

            try {

                val lastSeenResponse = apolloClient(requireContext(), userToken!!).query(
                    GetLastOnlineQuery(otherUserId!!)
                ).execute()

                if (lastSeenResponse.hasErrors()) {
                    val responsError = lastSeenResponse.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$responsError", Toast.LENGTH_LONG).show()
//                    Toast.makeText(requireContext(),"Last seen Exception $responsError",Toast.LENGTH_LONG).show()
                } else {
                    try {
                        val lastSeen = lastSeenResponse.data?.lastOnline?.lastSeen!!.split(",")[0]
                        val last = lastSeen + AppStringConstant1.ago
                        binding?.otherProfileLayout?.tvLastseen?.text = last.toString()
                    } catch (e: Exception) {
                        Log.e(TAG, ""+e.message)
                    }

                }

            } catch (e: ApolloException) {
//                Toast.makeText(requireContext(),"LastSeen ApolloException ${e.message}",Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
//                Toast.makeText(requireContext(),"LastSeen Exception ${e.message}",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun setDistance() {
        lifecycleScope.launch {

            try {

                val distanceResponse = apolloClient(requireContext(), userToken!!).query(
                    GetUserLocationQuery(otherUserId!!)
                ).execute()

                if (distanceResponse.hasErrors()) {
                    val responsError = distanceResponse.errors?.get(0)?.message
                    Toast.makeText(requireContext(), "$responsError", Toast.LENGTH_LONG).show()
//                    Toast.makeText(requireContext(),"Distance Exception $responsError",Toast.LENGTH_LONG).show()
                } else {
                    val distance = "${distanceResponse.data?.userLocation?.distance}"
                    binding?.otherProfileLayout?.tvDistance?.text = distance
                }

            } catch (e: ApolloException) {
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
//                Toast.makeText(requireContext(),"Distance ApolloException ${e.message}",Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
//                Toast.makeText(requireContext(),"Distance Exception ${e.message}",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun checkFirstMessageStatus() {
        lifecycleScope.launch {
            try {
                val hasMessageResponse = apolloClient(requireContext(), userToken!!).query(
                    HasMessageQuery(otherUserId!!)
                ).execute()

                if (hasMessageResponse.hasErrors()) {
                    val responsError = hasMessageResponse.errors?.get(0)?.message
                    Toast.makeText(
                        requireContext(), "Message Exception $responsError", Toast.LENGTH_LONG
                    ).show()
                } else {

                    if (LuserId.equals(otherUserId)) {

                    }

                    val hasMessage = hasMessageResponse.data?.hasMessage?.showMessageButton
                    if (hasMessage == true) {
                        binding?.otherProfileLayout?.initChatMsgBtn?.visibility = View.VISIBLE
                        binding?.otherProfileLayout?.linearFlag2?.visibility = View.GONE
                        binding?.linearFlag1?.visibility = View.VISIBLE
                        binding?.sendMsgBtn?.visibility = View.GONE

                    } else {
                        binding?.sendMsgBtn?.visibility = View.VISIBLE
                        binding?.otherProfileLayout?.initChatMsgBtn?.visibility = View.GONE
                        binding?.otherProfileLayout?.linearFlag2?.visibility = View.VISIBLE
                        binding?.linearFlag1?.visibility = View.GONE
                    }

                    if (LuserId.equals(otherUserId)) {
                        binding?.otherProfileLayout?.initChatMsgBtn?.visibility = View.GONE
                        binding?.otherProfileLayout?.lytCoinsGifts123?.visibility = View.VISIBLE
                    }
                }

            } catch (e: ApolloException) {
//                Toast.makeText(requireContext(),"Message ApolloException ${e.message}",Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
//                Toast.makeText(requireContext(),"Message Exception ${e.message}",Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun blockAccount() {
        lifecycleScope.launch(Dispatchers.Main) {
            when (val response = mViewModel.blockUser(LuserId, otherUserId, token = userToken)) {
                is Resource.Success -> {
                    mViewModel.getCurrentUser(LuserId!!, userToken!!, true)
                    hideProgressView()
                    binding?.root?.snackbar("${otherFirstName} blocked!")
                    getMainActivity()?.pref?.edit()?.putString("chatListRefresh", "true")
                        ?.putString("readCount", "false")?.apply()
                    findNavController().popBackStack()
                }

                is Resource.Error -> {
                    hideProgressView()
                    Log.e(TAG, "${getString(R.string.something_went_wrong)} ${response.message}")
                    binding?.root?.snackbar("${AppStringConstant1.something_went_wrong} ${response.message}")
                }

                else -> {}
            }
        }
    }

    private fun reportAccount(otherUserId: String?, reasonMsg: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            reportUserAccount(
                token = userToken,
                currentUserId = LuserId,
                otherUserId = otherUserId,
                reasonMsg = reasonMsg,
                mViewModel = mViewModel
            ) { message ->
                hideProgressView()
                binding?.root?.snackbar(message)
            }
        }
    }

    fun fireGiftBuyNotificationforreceiver(gid: String, userid: String?) {

        lifecycleScope.launchWhenResumed {

            val queryName = "sendNotification"
            val query = StringBuilder().append("mutation {").append("$queryName (")
                .append("userId: \"${userid}\", ").append("notificationSetting: \"GIFT RLVRTL\", ")
                .append("data: {giftId:${gid}}").append(") {").append("sent").append("}")
                .append("}").toString()

            val result = AppModule.provideGraphqlApi().getResponse<Boolean>(
                query, queryName, userToken
            )
            Log.e(TAG, "RSLT" + "" + result.message)
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()

        adapter.addFragItem(fragRealGifts!!, AppStringConstant1.real_gifts)
        adapter.addFragItem(fragVirtualGifts!!, AppStringConstant1.virtual_gifts)
        viewPager?.adapter = adapter
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDestroy() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDestroy()
    }

    private fun showImageDialog(imageUrl: String) {
        val dialog = Dialog(
            requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
        )
        val dialogBinding = AlertFullImageBinding.inflate(layoutInflater, null, false)
        dialogBinding.fullImg.loadImage(imageUrl) {
            dialogBinding.alertTitle.setViewGone()
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialogBinding.root.setOnClickListener {
            dialog.cancel()
        }
        dialogBinding.alertTitle.setViewGone()
    }

    override fun setupClickListeners() {
        with(viewModel) {
            onReport = {
                openMenuItem()
            }
            onSendMsg = {
                createNewChat()
            }
            onGift = {
                binding?.purchaseButton?.visibility = View.VISIBLE
                binding?.topl?.visibility = View.GONE
                if (GiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                }
            }
            onBackPressed = {
                onDetach()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun openMenuItem() {
        val popup = PopupMenu(
            requireContext(), binding?.actionReport!!, 10
        )
        popup.getMenuInflater().inflate(R.menu.search_profile_options, popup.getMenu());

        //adding click listener
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.nav_item_report -> {
                    reportDialog()
                }
                R.id.nav_item_block -> {
                    blockUserAlert()
                }
            }
            true
        })
        popup.show()
    }

    private fun blockUserAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("${AppStringConstant1.are_you_sure_you_want_to_block} $otherFirstName ?")
            .setCancelable(false).setPositiveButton("${AppStringConstant1.yes} ") { dialog, id ->
                blockAccount()

            }.setNegativeButton("${AppStringConstant1.no} ") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }

    private fun reportDialog() {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportView = dialogLayout.findViewById<TextView>(R.id.report_view)
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
            reportAccount(otherUserId, message)
            dialog.dismiss()
        }

        cancleButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun createNewChat() {
        lifecycleScope.launchWhenCreated() {
            showProgressView()

            var res: ApolloResponse<CreateChatMutation.Data>? = null
            val idOfUserYouWantToChatWith = otherUserName
            Log.e(TAG, "apolloResponse $idOfUserYouWantToChatWith")
            try {
                res = apolloClient(
                    requireContext(), getCurrentUserToken()!!
                ).mutation(CreateChatMutation(idOfUserYouWantToChatWith!!)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
            }
            if (res?.hasErrors() == false) {
                hideProgressView()
                val chatroom = res.data?.createChat?.room
                Log.e(TAG, "apolloResponse success ${chatroom?.id}")
                chatBundle.putInt("chatId", chatroom?.id?.toInt()!!)
                chatBundle.putString("ChatType", "normal")
                findNavController().navigate(R.id.globalUserToNewChatAction, chatBundle)
            }
            if (res?.hasErrors() == true && res.errors?.size!! > 0) {
                hideProgressView()
                binding?.root?.snackbar("${res.errors?.get(0)?.message}")
                Log.e(TAG, "apolloResponse end ${res.hasErrors()} ${res.errors?.get(0)?.message}")
            }
        }
    }


    private fun adjustScreen() {
//        var height = Utils.getScreenHeight()
//        var calculated = (height * 70) / 100
//
//        var params = binding?.userCollapseToolbar?.layoutParams
//        params?.height = calculated
//        binding?.userCollapseToolbar?.layoutParams = params
    }

    private fun userFollowMutationCall() {
        lifecycleScope.launchWhenResumed {
            showProgressView()

            val res = try {
                apolloClient(
                    requireContext(), getCurrentUserToken()!!
                ).mutation(UserFollowMutation(otherUserId!!)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")

                hideProgressView()
                return@launchWhenResumed
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e(TAG, "errorAllPackage" + "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                binding?.followerLayout?.lyfollowBtns?.weightSum = 2f
                binding?.followerLayout?.btnFollow?.visibility = View.GONE
                binding?.followerLayout?.btnVisitorSearchUser?.visibility = View.VISIBLE
                binding?.root?.snackbar(AppStringConstant1.successfully)

                hideProgressView()
            }
        }
    }

    private fun subscribeonUpdatePrivatePhotoRequest() {
        val userid = requireArguments().getString("userId")
        lifecycleScope.launch(Dispatchers.IO) {

//            apolloClient(requireActivity(), userToken!!).subscription(
//                subscribeonUpdatePrivatePhotoRequest(userid!!, userToken!!))
//            )
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnUpdatePrivateRequestSubscription()
//                    OnUpdatePrivateRequestSubscription(userid!!, userToken!!)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMessage ->
                    if (newMessage.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newMessage.errors?.get(0)?.message}"
                        )
                    } else {
                        Log.e(
                            TAG,
                            "reealltime onNewMessage ${newMessage.data?.onUpdatePrivateRequest?.requestedUser}"
                        )
                        Log.e(
                            TAG,
                            "reealltime" +
                                    "reealltime ${newMessage.data?.onUpdatePrivateRequest?.requestedUser}"
                        )
                        Log.e(
                            TAG,
                            "reealltime" + "reealltime ${newMessage.data}"
                        )
                        lifecycleScope.launchWhenResumed {
                            viewModel.getProfile(otherUserId) {
                            }
                        }
                    }
                }
                Log.e(TAG, "reealltime 2")
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        binding?.container?.currentItem = position
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}
