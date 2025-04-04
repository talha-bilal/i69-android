package com.i69.ui.screens.main.moment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.DefaultUpload
import com.apollographql.apollo3.api.content
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.i69.BuildConfig
import com.i69.DeleteStoryMutation
import com.i69.DeletemomentMutation
import com.i69.GetAllUserMomentsQuery
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.GetNotificationCountQuery
import com.i69.GiftPurchaseMutation
import com.i69.LikeOnMomentMutation
import com.i69.MomentMutation
import com.i69.OnDeleteMomentSubscription
import com.i69.OnDeleteStorySubscription
import com.i69.OnNewMomentSubscription
import com.i69.OnNewStorySubscription
import com.i69.OnUpdateMomentSubscription
import com.i69.R
import com.i69.ReportonmomentMutation
import com.i69.ScheduleMomentMutation
import com.i69.ScheduleStoryMutation
import com.i69.StoryMutation
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.ModelGifts
import com.i69.data.models.OfflineStory
import com.i69.data.models.User
import com.i69.data.models.market.Products
import com.i69.data.remote.responses.MomentLikes
import com.i69.databinding.BottomsheetShareOptionsBinding
import com.i69.databinding.DialogBuySubscriptionOrCoinsBinding
import com.i69.databinding.DialogPreviewImageBinding
import com.i69.databinding.FragmentUserMomentsBinding
import com.i69.di.modules.AppModule.provideGraphqlApi
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentReceivedGifts
import com.i69.gifts.FragmentVirtualGifts
import com.i69.ui.adapters.AdapterMoments
import com.i69.ui.adapters.OfflineMultiStoriesAdapter
import com.i69.ui.adapters.ProductMomentAdapter
import com.i69.ui.adapters.StoryLikesAdapter
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.adapters.UserMultiStoriesAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.camera.CameraActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.ui.viewModels.MarketPlacesViewModel
import com.i69.ui.viewModels.UserMomentsModelView
import com.i69.ui.viewModels.UserViewModel
import com.i69.ui.views.InsLoadingView
import com.i69.utils.AnimationTypes
import com.i69.utils.ApiUtil
import com.i69.utils.LinearLayoutManagerWrapper
import com.i69.utils.LogUtil
import com.i69.utils.Utils
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.autoSnackbarOnTop
import com.i69.utils.convertURITOBitmapNSaveImage
import com.i69.utils.getResponse
import com.i69.utils.loadImage
import com.i69.utils.navigate
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import com.i69.utils.snackbarOnTop
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import net.vrgsoft.videcrop.VideoCropActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


class UserMomentsFragment : BaseFragment<FragmentUserMomentsBinding>(),
    AdapterMoments.SharedMomentListener,
    UserMultiStoriesAdapter.UserMultiStoryListener, ProductMomentAdapter.SharedProductListener {

    var TAG: String = UserMomentsFragment::class.java.simpleName
    private var tempMomentsEdge = mutableListOf<GetAllUserMomentsQuery.Edge>()
    private var tempMoments = mutableListOf<ApolloResponse<OnNewMomentSubscription.Data>>()
    private var tempStories = mutableListOf<ApolloResponse<OnNewStorySubscription.Data>>()
    private var stories: ArrayList<GetAllUserMultiStoriesQuery.AllUserMultiStory?> = ArrayList()
    private val momentsViewModel: UserMomentsModelView by activityViewModels()
    private val viewModel: UserViewModel by activityViewModels()
    private var showNotification: String? = ""
    var width = 0
    var size = 0
    private var userToken: String? = null
    private lateinit var offlineMultiStoryAdapter: OfflineMultiStoriesAdapter
    private lateinit var usersMultiStoryAdapter: UserMultiStoriesAdapter
    private lateinit var adapterMoments: AdapterMoments
    private var mFilePath: String? = null
    private lateinit var contentUri: Uri
    lateinit var file: File
    var fileType1: String = ""
    var layoutManager: LinearLayoutManager? = null
    var allUserMoments: ArrayList<GetAllUserMomentsQuery.Edge> = ArrayList()
    var allUserMomentsNew: ArrayList<GetAllUserMomentsQuery.Edge> = ArrayList()
    private var userId: String? = null
    private var userName: String? = null
    var endCursor: String = ""
    var hasNextPage: Boolean = false
    private lateinit var GiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var LikebottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    var momentLikesUsers: ArrayList<CommentsModel> = ArrayList()
    var likeMomentItemPK: String? = null
    var VIDEO_CROP_REQUEST = 10001
    var momentLikeUserAdapters: StoryLikesAdapter? = null
    var outputPath = ""
    var giftUserid: String? = null
    private val marketPlacesViewModel: MarketPlacesViewModel by activityViewModels()
    var fragVirtualGifts: FragmentVirtualGifts? = null
    var fragRealGifts: FragmentRealGifts? = null
    private var mUser: User? = null
    private lateinit var productMomentAdaper: ProductMomentAdapter
    private val productList = mutableListOf<Products>()
    private lateinit var receivedGiftbottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var exoPlayer: ExoPlayer

    override fun playVideo(mediaItem: MediaItem, playWhenReady: Boolean): ExoPlayer {
        exoPlayer.apply {
            setMediaItem(mediaItem, false)
            this.playWhenReady = playWhenReady
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
        return exoPlayer
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun pauseVideo() {
        if (isPlaying()) exoPlayer.pause()
    }

    override fun onStart() {
        super.onStart()
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
        exoPlayer.release()
    }

    private val photosLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                var fileType = ""
                mFilePath = data?.getStringExtra("result")
                contentUri =
                    Uri.fromFile(File(mFilePath!!)) ?: throw IllegalArgumentException("No data URI")
                file = File(mFilePath.toString())
                if (mFilePath?.contains(".") == true) {
                    val regex = "\\.".toRegex()
                    val type: String =
                        mFilePath?.reversed()?.split(regex)?.get(0)?.reversed().toString()
                    fileType = ".$type"
                    fileType1 = fileType
                }
//                if (fileType == ".mp4") {
                if (Utils.isVideoFile(
                        contentUri,
                        requireContext()
                    ) || Utils.isVideoFile(contentUri)
                ) {
                    showFilePreview(file, fileType)
//                    cropVideoPopup(file)
                } else {
//                    showFilePreview(file, fileType)

                    val imageUri = Uri.fromFile(mFilePath?.let { File(it) })
                    CropImage.activity(imageUri)
                        .start(requireContext(), this)
                }
            }
        }

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                mFilePath = data?.data.toString()
                contentUri = data?.data ?: throw IllegalArgumentException("No data URI")
                val result = data.data?.path
                val openInputStream =
                    requireActivity().contentResolver?.openInputStream(contentUri)
                        ?: throw IllegalStateException("Unable to open input stream")

//                val type = if (result?.contains("video") == true) ".mp4" else ".jpg"
                var type = ""
                if (mFilePath!!.contains(".")) {
                    val regex = "\\.".toRegex()
                    type = mFilePath!!.reversed().split(regex).get(0).reversed().toString()
                    fileType1 = ".$type"
                }

                fileType1 =
                    if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(
                            contentUri
                        )
                    ) ".jpg" else ".mp4"
                val fileName = "${System.currentTimeMillis()}$fileType1"
                val outputFile = File(requireContext().filesDir, fileName)
                openInputStream.use { inputStream ->
                    outputFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file = outputFile

//                val type = if (mFilePath!!.contains("video")) ".mp4" else ".jpg"
//                val outputFile =
//                    requireContext().filesDir.resolve("${System.currentTimeMillis()}$type")
//                file = File(outputFile.toURI())


                if (Utils.isImageFile(
                        contentUri,
                        requireContext()
                    ) || Utils.isImageFile(contentUri)
                ) {
//                if (type == ".jpg" || type == ".jpeg") {
                    val imagePath = convertURITOBitmapNSaveImage(
                        requireContext(),
                        data.data!!,
                        outputFile,
                    )
                    imagePath?.let {
                        file = File(it)
                    }
                }

//                if (type == ".mp4") {
                if (Utils.isVideoFile(
                        contentUri,
                        requireContext()
                    ) || Utils.isVideoFile(contentUri)
                ) {
                    showFilePreview(file, type)
//                    cropVideoPopup(file)
                } else {
//                    showFilePreview(file, type)

                    val imageUri = data.data
                    CropImage.activity(imageUri)
                        .start(requireContext(), this)
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val pathOfFile = result.uri.path
                file = File(pathOfFile!!)
                mFilePath = pathOfFile

                showFilePreview(file, fileType1)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        } else if (requestCode == VIDEO_CROP_REQUEST && resultCode == RESULT_OK) {
            //crop successful
            mFilePath = outputPath
            showFilePreview(File(outputPath), fileType1)
        }
    }

    private fun cropVideoPopup(file: File) {
        val outputDir =
            File(context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.getAbsolutePath())
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val currentTimestamp = System.currentTimeMillis()
        outputPath =
            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                    "/output_video_$currentTimestamp.mp4"

        startActivityForResult(
            VideoCropActivity.createIntent(
                requireContext(),
                file.path,
                outputPath
            ), VIDEO_CROP_REQUEST
        )
    }

    private fun showShareOptions(onShared: () -> Unit) {
        val shareOptionsDialog = BottomSheetDialog(requireContext())
        val bottomsheet = BottomsheetShareOptionsBinding.inflate(layoutInflater, null, false)

        var shareAt = ""
        shareOptionsDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomsheet.llShareLaterRoot.setBackgroundColor(
            ResourcesCompat.getColor(
                resources, R.color.profileTransBlackOverlayColor, null
            )
        )
        bottomsheet.rbShareLater.setViewGone()
        bottomsheet.ivLocked.setViewVisible()

        if ((requireActivity() as MainActivity).isUserAllowedToPostStory()) {
            bottomsheet.llShareNowRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources, R.color.CE4F3FF, null
                )
            )
            bottomsheet.rbShareNow.setViewVisible()
            bottomsheet.ivPostLocked.setViewGone()
            if ((requireActivity() as MainActivity).isUserHasStoryQuota()) {
                bottomsheet.tvShareNowCoins.setViewGone()
            } else {
                (requireActivity() as MainActivity).getRequiredCoins("POST_STORY_COINS") {
                    bottomsheet.tvShareNowCoins.setViewVisible()
                    bottomsheet.tvShareNowCoins.text = it.toString()
                }
            }
        } else {
            bottomsheet.llShareNowRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources, R.color.profileTransBlackOverlayColor, null
                )
            )
            bottomsheet.rbShareNow.setViewGone()
            bottomsheet.ivPostLocked.setViewVisible()

            bottomsheet.llShareLaterRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources, R.color.profileTransBlackOverlayColor, null
                )
            )
            bottomsheet.rbShareLater.setViewGone()
            bottomsheet.ivLocked.setViewVisible()

            bottomsheet.cvShareNow.setOnClickListener {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }

            bottomsheet.cvShareLater.setOnClickListener {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }

            shareOptionsDialog.setContentView(bottomsheet.root)
            shareOptionsDialog.show()

            return
        }

        if ((requireActivity() as MainActivity).isUserAllowedToScheduleStory()) {
            if ((requireActivity() as MainActivity).isUserHasSubscription()) {
                bottomsheet.llShareLaterRoot.setBackgroundColor(
                    ResourcesCompat.getColor(
                        resources, R.color.CE4F3FF, null
                    )
                )
                bottomsheet.rbShareLater.setViewVisible()
                bottomsheet.ivLocked.setViewGone()
                bottomsheet.tvShareLaterCoins.setViewGone()
            } else {
                (requireActivity() as MainActivity).getRequiredCoins("SCHEDULE_STORY_COINS") {
                    bottomsheet.llShareLaterRoot.setBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.CE4F3FF, null
                        )
                    )
                    bottomsheet.rbShareLater.setViewVisible()
                    bottomsheet.ivLocked.setViewGone()
                    bottomsheet.tvShareLaterCoins.setViewVisible()
                    bottomsheet.tvShareLaterCoins.text = it.toString()
                }
            }
        }
        bottomsheet.cvShareNow.setOnClickListener {
            bottomsheet.rbShareNow.isChecked = true
            bottomsheet.rbShareLater.isChecked = false
        }

        bottomsheet.cvShareLater.setOnClickListener {
            if ((requireActivity() as MainActivity).isUserAllowedToScheduleStory()) {
                bottomsheet.rbShareLater.isChecked = true
                bottomsheet.rbShareNow.isChecked = false
                showDateTimePicker { displayTime, apiTime ->
                    if (displayTime.isNotEmpty() && apiTime.isNotEmpty()) {
                        val scheduleText = "Scheduled for $displayTime"
                        bottomsheet.tvShareLater.text = scheduleText
                        shareAt = apiTime
                    } else {
                        bottomsheet.rbShareLater.isChecked = false
                        bottomsheet.rbShareNow.isChecked = true
                    }
                }
            } else {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }
        }

        bottomsheet.rbShareNow.setOnClickListener { bottomsheet.cvShareNow.performClick() }
        bottomsheet.rbShareLater.setOnClickListener { bottomsheet.cvShareLater.performClick() }

        bottomsheet.btnShareMoment.setOnClickListener {
            if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
            onShared.invoke()
            if (bottomsheet.rbShareNow.isChecked) {
                uploadStory()
            } else if (bottomsheet.rbShareLater.isChecked) {
                if (shareAt.isNotEmpty()) {
                    uploadStoryLater(shareAt)
                }
            }
        }
        shareOptionsDialog.setContentView(bottomsheet.root)
        shareOptionsDialog.show()
    }

    private fun showUpgradePlanDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(FEATURE_NO_TITLE)
        val dialogBinding = DialogBuySubscriptionOrCoinsBinding.inflate(layoutInflater)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.ivCross.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.clBuyCoins.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_global_purchase)
        }

        dialogBinding.clBuySubscription.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_global_plan)
        }

        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun showDateTimePicker(onDateAndTimePicked: (String, String) -> Unit) {
        val currentDate: Calendar = Calendar.getInstance()
        val date = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { v, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    context,
                    { view, hourOfDay, minute ->
                        if (getMainActivity().isValidTime(
                                year, monthOfYear, dayOfMonth, hourOfDay, minute
                            )
                        ) {
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            date.set(Calendar.MINUTE, minute)

                            val sdf1 = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
                            val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                            sdf2.timeZone = TimeZone.getTimeZone("UTC")
                            val now = date.time
                            val displayTime = sdf1.format(now)
                            val formattedTime = sdf2.format(now)
                            onDateAndTimePicked.invoke(displayTime, formattedTime)

                            Log.e(TAG, "The choosen one $formattedTime")
                        } else {
                            onDateAndTimePicked.invoke("", "")
                            binding?.root?.snackbarOnTop(getString(R.string.please_select_a_future_time))
                        }
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.setOnShowListener { dialog ->
                    val positiveButton =
                        (dialog as TimePickerDialog).getButton(TimePickerDialog.BUTTON_POSITIVE)
                    val negativeButton = dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                    val neutralButton = dialog.getButton(TimePickerDialog.BUTTON_NEUTRAL)
                    positiveButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.colorPrimary
                        )
                    )
                    negativeButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.iconGray
                        )
                    )
                    neutralButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.iconGray
                        )
                    )
                }

                timePickerDialog.show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DATE)
        )
        datePickerDialog.setOnShowListener { dialog ->
            val positiveButton =
                (dialog as DatePickerDialog).getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            val neutralButton = dialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)

            // Set the text color for positive, negative, and neutral buttons
            positiveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.colorPrimary
                )
            )
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.iconGray))
            neutralButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.iconGray))
        }

        datePickerDialog.show()
    }

    private fun showFilePreview(file: File?, fileType: String) {
        (requireActivity() as MainActivity).loadUser()
        val dialogBinding = DialogPreviewImageBinding.inflate(layoutInflater, null, false)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(R.color.color_transparant_80)

        if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(contentUri)) {
//        if (fileType == ".jpg" || fileType == ".jpeg") {
            dialogBinding.ivPreview.setViewVisible()
            dialogBinding.vvPreview.setViewGone()
            Glide.with(requireContext()).load(file).into(dialogBinding.ivPreview)
        } else {
            dialogBinding.ivPreview.setViewGone()
            dialogBinding.vvPreview.setViewVisible()
            dialogBinding.vvPreview.setVideoPath(file?.path)
            dialogBinding.vvPreview.start()
            dialogBinding.vvPreview.setOnCompletionListener {
                if (dialog.isShowing && it != null) dialogBinding.vvPreview.start()
            }
        }

        dialogBinding.ibClose.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnShareMoment.setOnClickListener {
            showShareOptions {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserMomentsBinding.inflate(inflater, container, false)


    private fun getFeedItems(
        search: String, categoryId: String?
    ) {
        context?.let {
            marketPlacesViewModel.feedItems(
                it,
                search,
                categoryId,
                25,
                1, ""
            )
        }
    }

    override fun initObservers() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false).apply {
            isItemPrefetchEnabled = true
        }

        binding?.rvSharedMoments?.layoutManager = layoutManager

//        adapterMoments = AdapterMoments(requireContext(), this, userId.toString())
//        binding?.rvSharedMoments?.adapter = adapterMoments

        // Initialize adapter only if null (Avoid resetting)
//        if (adapterMoments == null) {
            adapterMoments = AdapterMoments(requireContext(), this, userId.toString())
            binding?.rvSharedMoments?.adapter = adapterMoments
//        }

        getFeedItems("", "")

        productMomentAdaper = ProductMomentAdapter(activity, this)
        binding?.recyclerViewMarket?.adapter = productMomentAdaper
        binding?.recyclerViewMarket?.visibility = View.GONE

        marketPlacesViewModel.feedItems.observe(viewLifecycleOwner) { data ->
            productList.addAll(data)
            productMomentAdaper.addProducts(data)
        }

        momentsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
            hideProgressView()
        }
//        momentsViewModel.moments.observe(viewLifecycleOwner) {
//            it.moments.forEach {
//                Log.e(TAG, "$it")
//            }
//            requireActivity().runOnUiThread {
//                binding?.llSharing?.visibility = View.GONE
//                stopMomentsShimmerEffect()
//            }
//            allUserMoments = it.moments
//
//            allUserMomentsNew.forEach { moment ->
//                adapterMoments.addData(moment)
//            }
//
//            allUserMoments.forEach { moment ->
//                Log.e(TAG, "$moment")
//                adapterMoments.addData(moment)
//            }
//
//            endCursor = it.endCursor.toString()
//            hasNextPage = it.hasNextPage == true
//            alreadyFetching = false
//            Log.e(TAG, "EndCursor: $endCursor")
//            Log.e(TAG, "HasNextPage: $hasNextPage")
//            hideProgressView()
//
//
//            Log.e(TAG, "adapterMoments: ${adapterMoments.itemCount}")
//        }

        momentsViewModel.moments.observe(viewLifecycleOwner) { response ->
            requireActivity().runOnUiThread {
                binding?.llSharing?.visibility = View.GONE
                stopMomentsShimmerEffect()
            }

            // Avoid duplicates by checking existing items
            response.moments.forEach { moment ->
                val existingMoment = adapterMoments.items.find { it?.node?.pk == moment?.node?.pk }
                if (existingMoment == null) {
                    adapterMoments.addData(moment)
                }
            }

            endCursor = response.endCursor.toString()
            hasNextPage = response.hasNextPage == true
            alreadyFetching = false

            Log.e(TAG, "EndCursor: $endCursor")
            Log.e(TAG, "HasNextPage: $hasNextPage")
            Log.e(TAG, "adapterMoments: ${adapterMoments.itemCount}")

            hideProgressView()
        }

        momentsViewModel.stories.observe(viewLifecycleOwner) { multiStories ->

            multiStories?.forEach {
                Log.e(TAG, "${it?.user}")
                Log.e(TAG, "${it?.stories}")
            }
            usersMultiStoryAdapter =
                UserMultiStoriesAdapter(requireContext(), mUser, this@UserMomentsFragment)
            stories.clear()


            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            for (item in multiStories!!) {
                item?.stories?.edges?.sortedBy { edge ->
                    try {
                        dateFormat.parse(edge?.node?.createdDate.toString())
                    } catch (e: Exception) {
                        null
                    }
                }
                Log.e(TAG, "" + item?.stories?.edges?.size)
            }

            Log.e(TAG, "Stories: $multiStories")

            multiStories.let { stories.addAll(it) }
            usersMultiStoryAdapter.storyList = stories

            viewModel.getCurrentUser(userId!!, token = userToken!!, false)
                .observe(viewLifecycleOwner) { userDetails ->
                    userDetails?.let {
                        mUser = it
                        usersMultiStoryAdapter.mUser = it
                        usersMultiStoryAdapter.notifyItemChanged(0)
                    }
                }
            binding?.rvUserStories?.layoutManager =
                LinearLayoutManagerWrapper(activity, LinearLayoutManager.HORIZONTAL, false)
            binding?.rvUserStories?.adapter = usersMultiStoryAdapter
            cacheStories()
            setupStoryAdapter()
        }
    }

    var scrollY1 = 0
    var height = 0
    override fun setupTheme() {
        getTypeActivity<MainActivity>()?.reloadNavigationMenu()

        if (getMainActivity().isShare) {
            getMainActivity().isShare = false
            binding?.llSharing?.visibility = View.VISIBLE
            momentSharing(
                getMainActivity().filePath,
                getMainActivity().description,
                getMainActivity().checked
            )
        } else if (getMainActivity().isShareLater) {
            getMainActivity().isShareLater = false
            scheduleMomentForLater(
                getMainActivity().filePath,
                getMainActivity().description,
                getMainActivity().checked,
                getMainActivity().publishAt
            )
        }

        val displayMetrics = DisplayMetrics()
        height = displayMetrics.heightPixels

        width = displayMetrics.widthPixels
        val densityMultiplier = resources.displayMetrics.density
        val scaledPx = 14 * densityMultiplier
        val paint = Paint()
        paint.textSize = scaledPx
        size = paint.measureText("s").roundToInt()
        if (!hasInitializedRootView) {

            navController = findNavController()
            binding?.model = momentsViewModel
            lifecycleScope.launch {
                userId = getCurrentUserId()!!
                userToken = getCurrentUserToken()!!
                Log.e(TAG, "usertokenn $userToken")

                Log.e(TAG, "userID $userId")

                layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

                binding?.rvSharedMoments?.layoutManager = layoutManager

                showCachedStories()

                subscribeForNewStory()

                subscribeForDeleteStory()

                subscribeForNewMoment()

                subscribeForDeleteMoment()

                subscribeForUpdateMoment()
                Log.e(
                    TAG,
                    "-----------------------9" + "userMomentsListSize: ${momentsViewModel.userMomentsList.size}"
                )
                if (momentsViewModel.userMomentsList.size == 0) {
                    Log.e(TAG, "Moments list is empty...")
                    endCursor = ""
                    binding?.llSharing?.visibility = View.VISIBLE
                    binding?.momentSharing?.text = getString(R.string.refreshing)

                    binding?.rvSharedMoments?.isNestedScrollingEnabled = false
                    Log.e(TAG, "userMomentsListSize: ${momentsViewModel.userMomentsList.size}")
                    getMainActivity().pref.edit().putString("checkUserMomentUpdate", "false")
                        .apply()
//                    startMomentsShimmerEffect()
                    getUserMomentNextPage(width, size, 5, endCursor)
                } else {
                    try {
                        showNewMomentsResults()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (getMainActivity().pref.getString("checkUserMomentUpdate", "false")
                        .equals("true")
                ) {
                    getMainActivity().pref.edit().putString("checkUserMomentUpdate", "false")
                        .apply()
                    getMainActivity().pref.getString("mID", "")?.let {
                        getParticularMoments(
                            getMainActivity().pref.getInt("itemPosition", 0), it
                        )
                    }
                }
            }
            binding?.bubble?.setOnClickListener {
                try {
                    binding?.rvSharedMoments?.scrollToPosition(0)
                    binding?.bubble?.isVisible = false
                } catch (e: Exception) {
                    Log.e(TAG, "setupTheme: ${e.message.toString()}")
                }
            }

            binding?.btnNewPostCheck?.setOnClickListener {
                try {
                    val layabouts: LinearLayoutManager =
                        binding?.rvSharedMoments?.layoutManager as LinearLayoutManager
                    layabouts.scrollToPositionWithOffset(0, 0)
                    binding?.scrollView?.fullScroll(View.FOCUS_UP)
                    binding?.bubble?.isVisible = false
                } catch (e: Exception) {
                    Log.e(TAG, "setupTheme: ${e.message.toString()}")
                }
            }
            if (binding?.rvSharedMoments?.itemDecorationCount == 0) {
                binding?.rvSharedMoments?.addItemDecoration(object :
                    RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.top = 10
                    }
                })
            }

            binding?.scrollView?.setOnScrollChangeListener { v: NestedScrollView?, _, scrollY: Int, _, _ ->
                if (v != null) {
                    // Pause video if scrolling up or down
                    if (scrollY != scrollY1) {
                        pauseVideo()
                    }

                    scrollY1 = scrollY // Update previous scroll position

                    // Handle reaching the bottom for pagination
                    val lastChild = v.getChildAt(v.childCount - 1)
                    val bottomReached = lastChild.bottom <= v.height + v.scrollY
                    if (!alreadyFetching && bottomReached) {
                        showProgressView()
                        alreadyFetching = true
                        getUserMomentNextPage(width, size, 5, endCursor)
                    }

                    // Handle button visibility
                    binding?.btnNewPostCheck?.isVisible = scrollY > height

                    // Hide bubble when at the top
                    if (binding?.bubble?.isVisible == true && scrollY == 0) {
                        binding?.bubble?.isVisible = false
                    }
                }
            }
        }
    }

    private fun showCachedStories() {
        if (!this::usersMultiStoryAdapter.isInitialized) {
            offlineMultiStoryAdapter = OfflineMultiStoriesAdapter(requireContext(), mUser) {
                binding?.root?.autoSnackbarOnTop("Waiting for network")
            }

//            momentsViewModel.getAllOfflineStories {
//                Log.e(TAG, "showCachedStories: ${it.size}")
//                if (it.isNotEmpty()) offlineMultiStoryAdapter.storyList = it
////                else startShimmerEffect()
//            }

            momentsViewModel.getAllOfflineStories { stories ->
                Log.e(TAG, "showCachedStories: ${stories.size}")

                if (stories.isNullOrEmpty()) {
                    Log.e(TAG, "No cached stories found")
                    // Handle empty state (e.g., show shimmer effect)
                } else {
                    if (!binding?.rvUserStories?.isComputingLayout!!) {
                        offlineMultiStoryAdapter.storyList = stories
                    } else {
                        binding?.rvUserStories?.post {
                            offlineMultiStoryAdapter.storyList = stories
                        }
                    }

                }
            }


//        offlineMultiStoryAdapter.mUser = it
            activity?.runOnUiThread {
                offlineMultiStoryAdapter.notifyItemChanged(0)
                binding?.rvUserStories?.adapter = offlineMultiStoryAdapter
                setupStoryAdapter()
            }
        }
        getAllUserMultiStories()
    }

    private fun stopMomentsShimmerEffect() {
        activity?.runOnUiThread {
            TransitionManager.beginDelayedTransition(
                binding?.receivedGiftContainer, AutoTransition()
            )
            binding?.shimmerMoments?.apply {
                setViewGone()
                stopShimmer()
            }
        }
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            getMainActivity().drawerSwitchState()
        }
        binding?.bell?.setOnClickListener {
            val dialog =
                NotificationDialogFragment(userToken, binding?.counter, userId, binding?.bell)
            getMainActivity().notificationDialog(
                dialog, childFragmentManager, getString(R.string.notifications)
            )
        }

        showLikeBottomSheet()

        GiftbottomSheetBehavior = BottomSheetBehavior.from(binding?.giftbottomSheet!!)
        GiftbottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        binding?.sendgiftto?.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
                fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
                fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }

                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->
                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        userToken = getCurrentUserToken()!!
                        try {
                            res = apolloClient(requireContext(), userToken!!).mutation(
                                GiftPurchaseMutation(
                                    gift.id,
                                    giftUserid!!,
                                    getCurrentUserId()!!
                                )
                            ).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponseException ${e.message}")
                            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                        if (res?.hasErrors() == false) {
                            //views.snackbar("You bought ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} successfully!")
                            val resources = context?.resources
                            Toast.makeText(
                                requireContext(),
                                resources?.getString(R.string.you_bought) + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + context?.resources?.getString(
                                    R.string.successfully
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            // fireGiftBuyNotificationforreceiver(gift.id,giftUserid)
                        }
                        if (res!!.hasErrors()) {
//                                views.snackbar(""+ res.errors!![0].message)
                            Toast.makeText(
                                requireContext(),
                                "${res.errors?.get(0)?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e(TAG, "-->" + Gson().toJson(res))
                        Log.e(
                            TAG,
                            "apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}"
                        )
                    }
                    hideProgressView()
                } else {
                    Log.e(TAG, "giftsSizearenotmoreThan")
                }
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
        setupViewPager(binding?.giftsPager)

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
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()
        adapter.addFragItem(fragRealGifts!!, getString(R.string.real_gifts))
        adapter.addFragItem(fragVirtualGifts!!, getString(R.string.virtual_gifts))
        viewPager?.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val arguments = arguments
        if (arguments != null) {
//            ShowNotification = arguments.get("ShowNotification") as String?
            showNotification = arguments.getString("ShowNotification")
            if (showNotification.equals("true")) {
//                Handler().postDelayed({ binding.bell.performClick() }, 500)
                Handler(Looper.getMainLooper()).postDelayed(
                    { binding?.bell?.performClick() },
                    500
                )
            }
        }
        showNotification = getMainActivity().pref.getString("ShowNotification", "false")
        if (showNotification.equals("true")) {
            getMainActivity().pref.edit().putString("ShowNotification", "false").apply()
            showNotification = "false"
            Handler(Looper.getMainLooper()).postDelayed({ binding?.bell?.performClick() }, 500)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun getNotificationIndex() {
        lifecycleScope.launchWhenResumed {
            userToken = getCurrentUserToken()!!
            val res = try {
                apolloClient(requireContext(), userToken!!).query(GetNotificationCountQuery())
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponseException NotificationIndex  ${e.message}")
                binding?.root?.snackbar("${e.message}")
                return@launchWhenResumed
            }
            Log.e(TAG, "apolloResponse NotificationIndex ${res.hasErrors()}")
            Log.e(TAG, "-->" + Gson().toJson(res))
            if (res.hasErrors()) {
                Log.e(TAG, "-->" + res.errors!![0].nonStandardFields!!["code"].toString())
                if (res.errors!![0].nonStandardFields!!["code"].toString() == "InvalidOrExpiredToken") {
                    lifecycleScope.launch(Dispatchers.Main) {
                        userPreferences?.clear()
                        if (activity != null) {
                            val intent = Intent(activity, SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
            val notifyCount = res.data?.unseenCount
            if (notifyCount == null || notifyCount == 0) {
                binding?.counter?.visibility = View.GONE
            } else {
                binding?.counter?.visibility = View.VISIBLE

                binding?.counter?.text = if (notifyCount > 10) {
                    "9+"
                } else {
                    "$notifyCount"
                }
            }
        }
    }

    private fun subscribeForNewMoment() {
        lifecycleScope.launch(Dispatchers.IO) {
            userToken = getCurrentUserToken()!!
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnNewMomentSubscription("", userToken!!)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    false
                }.collect { newMoment ->
                    if (newMoment.hasErrors()) {
                        Log.e(TAG, "realtime response error = ${newMoment.errors?.get(0)?.message}")
                    } else {
                        Log.e(TAG, "MomentSubsValidation 1. $tempMoments")
                        Log.e(TAG, "MomentSubsValidation 2. ${newMoment.data}")

                        if (!tempMoments.contains(newMoment)) {
                            tempMoments.add(newMoment)

                            val newMomentToAdd = newMoment.data?.onNewMoment?.moment
                            if (newMomentToAdd != null && !allUserMomentsNew.any { it.node!!.id == newMomentToAdd.id }) {
                                val newMomentUser = newMoment.data?.onNewMoment?.moment?.user
                                val url = if (!BuildConfig.USE_S3) {
                                    if (newMomentUser?.avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) {
                                        newMomentUser?.avatar?.url.toString()
                                    } else {
                                        "${BuildConfig.BASE_URL}${newMomentUser?.avatar?.url.toString()}"
                                    }
                                } else if (newMomentUser?.avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) {
                                    newMomentUser?.avatar?.url.toString()
                                } else {
                                    ApiUtil.S3_URL.plus(newMomentUser?.avatar?.url.toString())
                                }

                                val avatar = GetAllUserMomentsQuery.Avatar(
                                    url,
                                    newMomentUser?.avatar?.id.toString(),
                                    newMomentUser?.avatar?.user
                                )
                                val avatarPhotos = newMomentUser?.avatarPhotos?.map { detail ->
                                    val avatarUrl = if (!BuildConfig.USE_S3) {
                                        if (detail.url.toString()
                                                .startsWith(BuildConfig.BASE_URL)
                                        ) {
                                            detail.url.toString()
                                        } else {
                                            "${BuildConfig.BASE_URL}${detail.url.toString()}"
                                        }
                                    } else if (detail.url.toString()
                                            .startsWith(ApiUtil.S3_URL)
                                    ) {
                                        detail.url.toString()
                                    } else {
                                        ApiUtil.S3_URL.plus(detail.url.toString())
                                    }

                                    GetAllUserMomentsQuery.AvatarPhoto(
                                        avatarUrl, detail.id, detail.user
                                    )
                                }
                                val user = GetAllUserMomentsQuery.User(
                                    newMomentUser?.id.toString(),
                                    newMomentUser?.email.toString(),
                                    newMomentUser?.fullName.toString(),
                                    newMomentUser?.username.toString(),
                                    newMomentUser?.gender,
                                    avatar,
                                    newMomentUser?.onesignalPlayerId,
                                    avatarPhotos ?: listOf()
                                )
                                val node = GetAllUserMomentsQuery.Node(
                                    newMomentToAdd.pk,
                                    newMomentToAdd.comment,
                                    newMomentToAdd.createdDate,
                                    newMomentToAdd.publishAt,
                                    newMomentToAdd.file,
                                    newMomentToAdd.id,
                                    newMomentToAdd.like,
                                    newMomentToAdd.momentDescription,
                                    newMomentToAdd.momentDescriptionPaginated ?: listOf(),
                                    user
                                )
                                val newMomentEdge = GetAllUserMomentsQuery.Edge("", node)

                                Log.e(TAG, "Initial Size  ${allUserMomentsNew.size}")

                                if (!tempMomentsEdge.contains(newMomentEdge)) {
                                    tempMomentsEdge.add(newMomentEdge)

                                    allUserMomentsNew.add(0, newMomentEdge)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        adapterMoments.addData(0, newMomentEdge)

                                        Log.e(TAG, "subscribeForNewMoment: $scrollY1  $height")
                                        binding?.bubble?.isVisible = scrollY1 > height
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "moment realtime exception= ${e2.message}")
            }
        }
    }

    private fun subscribeForDeleteMoment() {
        lifecycleScope.launch(Dispatchers.IO) {
            userToken = getCurrentUserToken()!!
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnDeleteMomentSubscription("", userToken!!)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMoment ->
                    if (newMoment.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newMoment.errors?.get(0)?.message}"
                        )
                    } else {
                        Log.e(
                            TAG,
                            "DeleteMoment Realtime  ID ${newMoment.data?.onDeleteMoment?.id}"
                        )
                        Log.e(TAG, "DeleteMoment Realtime  ID ${newMoment.data}")
                        Log.e(TAG, "Before Delete ${allUserMomentsNew.size}")

                        CoroutineScope(Dispatchers.Main).launch {
                            adapterMoments.deleteData(newMoment.data?.onDeleteMoment?.id)
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "Delete moment realtime exception= ${e2.message}")
            }
        }
    }

    private fun subscribeForUpdateMoment() {
        lifecycleScope.launch(Dispatchers.IO) {
            userToken = getCurrentUserToken()!!
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnUpdateMomentSubscription("", userToken!!)
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newMoment ->
                    if (newMoment.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newMoment.errors?.get(0)?.message}"
                        )
                    } else {
                        Log.e(
                            TAG,
                            " moment realtime UpdateStoryData content ${newMoment.data?.onUpdateMoment?.moment}"
                        )
                        Log.e(TAG, "moment realtime UpdateStoryData ${newMoment.data}")
                        val newMomentToAdd = newMoment.data?.onUpdateMoment?.moment
                        val newMomentUser = newMoment.data?.onUpdateMoment?.moment?.user
                        val likedUser = newMoment.data?.onUpdateMoment?.likedByUsersList

                        if (LikebottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {

                            if (likeMomentItemPK.toString() == newMomentToAdd!!.pk.toString()) {
                                momentLikesUsers.clear()

                                likedUser!!.map {

                                    val models = CommentsModel()
                                    val fullName = it?.username
                                    val name = if (fullName != null && fullName.length > 15) {
                                        fullName.substring(0, minOf(fullName.length, 15))
                                    } else {
                                        fullName
                                    }
                                    models.commenttext = name
                                    models.uid = it?.id.toString()
                                    models.userurl = it?.avatar?.url
                                    momentLikesUsers.add(models)

                                }

                                if (momentLikeUserAdapters != null) {
                                    momentLikeUserAdapters?.notifyDataSetChanged()
                                }
                            }
                        }

                        val avatar = GetAllUserMomentsQuery.Avatar(
                            newMomentUser?.avatar?.url,
                            newMomentUser?.avatar?.id.toString(),
                            newMomentUser?.avatar?.user
                        )
                        val avatarPhotos = newMomentUser?.avatarPhotos?.map {
                            GetAllUserMomentsQuery.AvatarPhoto(
                                it.url, it.id, it.user
                            )
                        }
                        val fullName = newMomentUser?.fullName.toString()
                        val name = if (fullName.length > 15) {
                            fullName.substring(0, minOf(fullName.length, 15))
                        } else {
                            fullName
                        }
                        val user = GetAllUserMomentsQuery.User(
                            newMomentUser?.id.toString(),
                            newMomentUser?.email.toString(),
                            name,
                            newMomentUser?.username.toString(),
                            newMomentUser?.gender,
                            avatar,
                            newMomentUser?.onesignalPlayerId,
                            avatarPhotos ?: listOf()
                        )
                        val node = GetAllUserMomentsQuery.Node(
                            newMomentToAdd?.pk,
                            newMomentToAdd?.comment,
                            newMomentToAdd?.createdDate!!,
                            newMomentToAdd.publishAt,
                            newMomentToAdd.file,
                            newMomentToAdd.id,
                            newMomentToAdd.like,
                            newMomentToAdd.momentDescription,
                            newMomentToAdd.momentDescriptionPaginated ?: listOf(),
                            user
                        )

                        val newMomentEdge = GetAllUserMomentsQuery.Edge("", node)
                        val replaceMoment =
                            allUserMomentsNew.filter { it.node?.pk.toString() == newMomentToAdd.pk.toString() }
                        Log.e(TAG, "replaceMoment: $replaceMoment")
                        Log.e(TAG, "UserMomentSubsc Update 1. ${replaceMoment.isEmpty()}")
                        if (replaceMoment.isNotEmpty()) {

                            Log.e(TAG, "subscribeForUpdateMoment: replace")
                            Log.e(
                                TAG,
                                "2. Item found before update ${replaceMoment[0].node?.momentDescriptionPaginated}"
                            )
                            val position = allUserMomentsNew.indexOf(replaceMoment[0])
                            allUserMomentsNew.removeAt(position)
                            allUserMomentsNew.add(position, newMomentEdge)
                            Log.e(
                                TAG,
                                "3. Item found after update $position ${allUserMomentsNew[position].node?.momentDescriptionPaginated}"
                            )
                            CoroutineScope(Dispatchers.Main).launch {
//                                sharedMomentAdapter.submitList1(allUserMomentsNew)
//                                sharedMomentAdapter.notifyItemRemoved(position)
//                                sharedMomentAdapter.notifyItemChanged(position)
                            }
                        } else {
                            Log.e(TAG, "Item not found")
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "moment realtime exception= ${e2.message}")
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }

    private fun subscribeForNewStory() {
        lifecycleScope.launch(Dispatchers.IO) {
            userToken = getCurrentUserToken()!!
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnNewStorySubscription()
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
                        Log.e(
                            TAG,
                            " story realtime NewStoryData content ${
                                tempStories.contains(
                                    newStory
                                )
                            }"
                        )
                        if (!tempStories.contains(newStory)) {
                            tempStories.add(newStory)
                            Log.e(
                                TAG,
                                " story realtime NewStoryData content ${!stories.any { it?.user?.id == newStory.data?.onNewStory?.user?.id }}"
                            )

                            val user =
                                stories.filter { it?.user?.id == newStory.data?.onNewStory?.user?.id }
                                    .filter { it?.batchNumber == newStory.data?.onNewStory?.batchNumber }
                            Log.e(
                                TAG,
                                "Filter, story realtime NewStoryData content $user ${user.isEmpty()}"
                            )
                            if (user.isEmpty()) {
                                val newStoryData = newStory.data?.onNewStory
                                val newStoryUser = newStory.data?.onNewStory?.user
                                val avatar = newStory.data?.onNewStory?.user?.avatar
                                val listOfAvatar = newStory.data?.onNewStory?.user?.avatarPhotos
                                val storiesTemp = newStory.data?.onNewStory?.stories

                                val url = if (!BuildConfig.USE_S3) {
                                    if (avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${avatar?.url.toString()}"
                                } else if (avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(avatar?.url.toString())
                                Log.e(TAG, "subscribeForNewStory: $url")

                                val newStoryCollection =
                                    GetAllUserMultiStoriesQuery.AllUserMultiStory(
                                        GetAllUserMultiStoriesQuery.User(
                                            newStoryUser?.id!!,
                                            newStoryUser.fullName,
                                            GetAllUserMultiStoriesQuery.Avatar(
                                                url, avatar?.id!!
                                            ),
                                            newStoryUser.avatarIndex,
                                            convertAvatarListQueryToSubscription(listOfAvatar)
                                        ),
                                        batchNumber = newStoryData?.batchNumber!!,
                                        stories = GetAllUserMultiStoriesQuery.Stories(
                                            convertEdgeListQueryToSubscrption(storiesTemp),
                                            GetAllUserMultiStoriesQuery.PageInfo2(
                                                storiesTemp?.pageInfo?.endCursor,
                                                storiesTemp?.pageInfo?.hasNextPage!!,
                                                storiesTemp.pageInfo.hasPreviousPage,
                                                storiesTemp.pageInfo.startCursor
                                            )
                                        )
                                    )
                                Log.e(
                                    TAG,
                                    "If, Before Add TotalUserStories ${stories.size} and newStory ${storiesTemp.edges.size}"
                                )
                                stories.add(0, newStoryCollection)
                                Log.e(TAG, "Add UserStories $stories")
                                Log.e(TAG, "After Add TotalUserStories ${stories.size}")
                                CoroutineScope(Dispatchers.Main).launch {
                                    usersMultiStoryAdapter.storyList = stories
                                    usersMultiStoryAdapter.notifyItemInserted(1)
                                }
                            } else {
                                val position = stories.indexOf(user[0])
                                val newStoryData = newStory.data?.onNewStory
                                val newStoryUser = newStory.data?.onNewStory?.user
                                val avatar = newStory.data?.onNewStory?.user?.avatar
                                val listOfAvatar = newStory.data?.onNewStory?.user?.avatarPhotos
                                val storiesTemp = newStory.data?.onNewStory?.stories

                                val url = if (!BuildConfig.USE_S3) {
                                    if (avatar?.url.toString()
                                            .startsWith(BuildConfig.BASE_URL)
                                    ) avatar?.url.toString()
                                    else "${BuildConfig.BASE_URL}${avatar?.url.toString()}"
                                } else if (avatar?.url.toString()
                                        .startsWith(ApiUtil.S3_URL)
                                ) avatar?.url.toString()
                                else ApiUtil.S3_URL.plus(avatar?.url.toString())

                                Log.e(TAG, "subscribeForNewStory: $url")

                                val newStoryCollection =
                                    GetAllUserMultiStoriesQuery.AllUserMultiStory(
                                        GetAllUserMultiStoriesQuery.User(
                                            newStoryUser?.id!!,
                                            newStoryUser.fullName,
                                            GetAllUserMultiStoriesQuery.Avatar(
                                                url, avatar?.id!!
                                            ),
                                            newStoryUser.avatarIndex,
                                            convertAvatarListQueryToSubscription(listOfAvatar)
                                        ),
                                        batchNumber = newStoryData?.batchNumber!!,
                                        stories = GetAllUserMultiStoriesQuery.Stories(
                                            convertEdgeListQueryToSubscrption(storiesTemp),
                                            GetAllUserMultiStoriesQuery.PageInfo2(
                                                storiesTemp?.pageInfo?.endCursor,
                                                storiesTemp?.pageInfo?.hasNextPage!!,
                                                storiesTemp.pageInfo.hasPreviousPage,
                                                storiesTemp.pageInfo.startCursor
                                            )
                                        )
                                    )
                                Log.e(
                                    TAG,
                                    "Else, Before Set TotalUserStories ${stories.size} and newStory ${storiesTemp.edges.size}"
                                )
                                stories.removeAt(position)
                                stories.add(0, newStoryCollection)
                                Log.e(TAG, "Set UserStories $stories")
                                Log.e(TAG, "After Set TotalUserStories ${stories.size}")
                                CoroutineScope(Dispatchers.Main).launch {
                                    usersMultiStoryAdapter.storyList = stories
                                    usersMultiStoryAdapter.notifyItemRemoved(position + 1)
                                    usersMultiStoryAdapter.notifyItemInserted(1)
                                }
                            }
                            //  viewModel.onNewMessage(newMessage = newMessage.data?.onNewMessage?.message)
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }

    private fun subscribeForDeleteStory() {
        lifecycleScope.launch(Dispatchers.IO) {
            userToken = getCurrentUserToken()!!
            try {
                apolloClientSubscription(requireActivity(), userToken!!).subscription(
                    OnDeleteStorySubscription()
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
                        Log.e(
                            TAG,
                            " story realtime DeleteStory content ${newStory.data?.onDeleteStory}"
                        )
                        Log.e(TAG, "story realtime DeleteStory ${newStory.data}")
                        val oldBatches =
                            stories.filter { it?.user?.id == newStory.data?.onDeleteStory?.userId }
                                .sortedBy { it?.batchNumber }
                        Log.e(TAG, "Old Batches size ${oldBatches.size}")
                        var allStories = mutableListOf<GetAllUserMultiStoriesQuery.Edge?>()
                        val newBatches =
                            mutableListOf<GetAllUserMultiStoriesQuery.AllUserMultiStory>()
                        for (batch in oldBatches) {
                            batch?.stories?.edges?.filter { it?.node?.pk != newStory.data?.onDeleteStory?.storyId }
                                ?.let { allStories.addAll(it) }
                        }
                        Log.e(TAG, "allStories size ${oldBatches.size} after removed")
                        if (allStories.isNotEmpty()) {
                            if (oldBatches.size > 1) {
                                val batchSize = oldBatches[0]?.stories?.edges?.size ?: 0
                                Log.e(TAG, "Batchsize more than story size $batchSize")
                                for (batch in oldBatches) {
                                    if (allStories.isNotEmpty()) {
                                        val newBatch =
                                            GetAllUserMultiStoriesQuery.AllUserMultiStory(
                                                batch?.user!!,
                                                batch.batchNumber,
                                                GetAllUserMultiStoriesQuery.Stories(
                                                    pageInfo = batch.stories.pageInfo,
                                                    edges = allStories.take(batchSize)
                                                )
                                            )
                                        newBatches.add(newBatch)
                                        allStories = allStories.drop(batchSize).toMutableList()
                                        Log.e(
                                            TAG,
                                            "Newbatch added and remaining Stories ${allStories.size}"
                                        )
                                    }
                                }
                            } else {
                                val batch = oldBatches[0]
                                val newBatch = GetAllUserMultiStoriesQuery.AllUserMultiStory(
                                    batch?.user!!,
                                    batch.batchNumber,
                                    GetAllUserMultiStoriesQuery.Stories(
                                        pageInfo = batch.stories.pageInfo, edges = allStories
                                    )
                                )
                                newBatches.add(newBatch)
                                Log.e(TAG, "Batch size less than one and newBatch added")
                            }
                            val modifiedPositions = mutableListOf<Int>()
                            val removedPositions = mutableListOf<Int>()
                            newBatches.sortedBy { it.batchNumber }
                                .forEachIndexed { index, newBatch ->
                                    val oldStoryPosition = stories.indexOf(oldBatches[index])
                                    if (oldStoryPosition > -1) {
                                        modifiedPositions.add(oldStoryPosition)
                                        stories[oldStoryPosition] = newBatch
                                    }
                                }
                            if (oldBatches.size > newBatches.size) {
                                val oldBatch = oldBatches[oldBatches.size - 1]
                                val oldBatchPosition = stories.indexOf(oldBatch)
                                stories.removeAt(oldBatchPosition)
                                removedPositions.add(oldBatchPosition)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                usersMultiStoryAdapter.storyList = stories
                                modifiedPositions.forEach {
                                    usersMultiStoryAdapter.notifyItemRemoved(it + 1)
                                    usersMultiStoryAdapter.notifyItemInserted(it + 1)
                                }
                                removedPositions.forEach {
                                    usersMultiStoryAdapter.notifyItemRemoved(it + 1)
                                }
                            }
                        } else {
                            val oldBatchPosition = stories.indexOf(oldBatches[0])
                            stories.removeAt(oldBatchPosition)
                            CoroutineScope(Dispatchers.Main).launch {
                                usersMultiStoryAdapter.storyList = stories
                                usersMultiStoryAdapter.notifyItemRemoved(oldBatchPosition + 1)
                            }
                        }
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "reealltime exception= ${e2.message}")
            }
        }
    }

    private fun convertEdgeListQueryToSubscrption(stories: OnNewStorySubscription.Stories?): List<GetAllUserMultiStoriesQuery.Edge?> {
        if (stories == null) return listOf()
        val newList = mutableListOf<GetAllUserMultiStoriesQuery.Edge>()
        for (story in stories.edges) {
            val node = story?.node
            val newStoryUser = story?.node?.user
            val avatar = story?.node?.user?.avatar
            val listOfAvatar = newStoryUser?.avatarPhotos
            newList.add(
                GetAllUserMultiStoriesQuery.Edge(
                    story?.cursor.toString(), GetAllUserMultiStoriesQuery.Node(
                        node?.createdDate.toString(),
                        node?.publishAt.toString(),
                        node?.file.toString(),
                        node?.fileType,
                        node?.id.toString(),
                        node?.pk,
                        node?.thumbnail,
                        node?.commentsCount,
                        GetAllUserMultiStoriesQuery.Comments(
                            GetAllUserMultiStoriesQuery.PageInfo(
                                node?.comments?.pageInfo?.endCursor,
                                node?.comments?.pageInfo?.hasNextPage!!,
                                node.comments.pageInfo.hasPreviousPage,
                                node.comments.pageInfo.startCursor
                            ), convertCommentListQueryToSubscription(node.comments.edges)
                        ),
                        node.likesCount,
                        GetAllUserMultiStoriesQuery.Likes(
                            GetAllUserMultiStoriesQuery.PageInfo1(
                                node.likes?.pageInfo?.endCursor,
                                node.likes?.pageInfo?.hasNextPage!!,
                                node.likes.pageInfo.hasPreviousPage,
                                node.likes.pageInfo.startCursor
                            ), convertListListQueryToSubscription(node.likes.edges)
                        ),
                        GetAllUserMultiStoriesQuery.User3(
                            newStoryUser?.id!!,
                            newStoryUser.fullName,
                            GetAllUserMultiStoriesQuery.Avatar3(avatar?.url, avatar?.id!!),
                            newStoryUser.avatarIndex,
                            convertAvatar3ListQueryToSubscription(listOfAvatar)
                        )
                    )
                )
            )
        }
        return newList
    }

    override fun onResume() {
        getNotificationIndex()
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("moment_added")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.registerReceiver(
                broadCastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity?.registerReceiver(broadCastReceiver, intentFilter)
        }
        getMainActivity().setDrawerItemCheckedUnchecked(null)
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val extras = intent?.extras
            val state = extras!!.getString("extra")
            Log.e(TAG, "onReceive: $state")
            binding?.bubble?.isVisible = binding?.scrollView?.scrollY != 0
            Log.e(TAG, "BroadCastReceiver Calling")
        }
    }

    private fun convertAvatarListQueryToSubscription(list: List<OnNewStorySubscription.AvatarPhoto>?): List<GetAllUserMultiStoriesQuery.AvatarPhoto> {
        if (list == null) return listOf()
        val newList = mutableListOf<GetAllUserMultiStoriesQuery.AvatarPhoto>()
        for (item in list) {
            newList.add(GetAllUserMultiStoriesQuery.AvatarPhoto(item.url, item.id))
        }
        return newList
    }

    private fun convertAvatar3ListQueryToSubscription(list: List<OnNewStorySubscription.AvatarPhoto3>?): List<GetAllUserMultiStoriesQuery.AvatarPhoto3> {
        if (list == null) return listOf()
        val newList = mutableListOf<GetAllUserMultiStoriesQuery.AvatarPhoto3>()
        for (item in list) {
            newList.add(GetAllUserMultiStoriesQuery.AvatarPhoto3(item.url, item.id))
        }
        return newList
    }

    private fun convertCommentListQueryToSubscription(list: List<OnNewStorySubscription.Edge1?>?): List<GetAllUserMultiStoriesQuery.Edge1> {
        if (list == null) return listOf()
        val newList = mutableListOf<GetAllUserMultiStoriesQuery.Edge1>()
        return newList
    }

    private fun convertListListQueryToSubscription(list: List<OnNewStorySubscription.Edge2?>?): List<GetAllUserMultiStoriesQuery.Edge2> {
        if (list == null) return listOf()
        val newList = mutableListOf<GetAllUserMultiStoriesQuery.Edge2>()
        return newList
    }

    private fun getAllUserMultiStories() {
        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            momentsViewModel.getAllStories(requireContext(), userToken.toString())
        }
    }

    private fun cacheStories() {
        CoroutineScope(Dispatchers.IO).launch {
            momentsViewModel.deleteOfflineStories()
            val cache = mutableListOf<OfflineStory>()
            stories.forEach { item ->
                val story = OfflineStory(stories = item)
                cache.add(story)
            }
            Log.e(TAG, "getAllUserMultiStories: ${cache.size}")
            momentsViewModel.insertOfflineStories(cache)
        }
    }

    private fun setupStoryAdapter() {
        if (binding?.rvUserStories?.itemDecorationCount == 0) {
            binding?.rvUserStories?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    outRect.top = 20
                    outRect.bottom = 10
                    outRect.left = 20
                }
            })
        }
    }

    private fun uploadStoryLater(publishAt: String) {
        startLoader(0)
        lifecycleScope.launchWhenCreated {
            val f = file
            val buildder = DefaultUpload.Builder()
            Log.e(TAG, "story upload ")
            buildder.contentType("Content-Disposition: form-data;")
            buildder.fileName(f.name)

            val upload = buildder.content(f).build()
            Log.e(TAG, "filee ${f.exists()} ${f.length()}")
            val userToken = getCurrentUserToken()!!
            Log.e(TAG, "userToken $userToken File ${upload.fileName} Publish at: $publishAt")
            val response: ApolloResponse<ScheduleStoryMutation.Data> = try {
                Log.e(TAG, "story upload 1")
                apolloClient(
                    context = requireContext(), token = userToken, isMultipart = true
                ).mutation(
                    ScheduleStoryMutation(file = upload, publishAt)
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, Gson().toJson(e))
                Log.e(TAG, "ApolloException==> ${e.message}")
                binding?.root?.snackbar("${e.message}")
                return@launchWhenCreated
            } catch (e: Exception) {
                Log.e(TAG, "Exception==> ${e.message}")
                Log.e(TAG, "filee General Exception ${e.message} $userToken")
                binding?.root?.snackbar(" ${e.message}")
                return@launchWhenCreated
            } finally {
                stopLoader(0)
            }
            Log.e(TAG, "uploadStoryLater: ${response.data}")
            if (response.hasErrors()) {
                Log.e(TAG, "${response.errors}")
                Log.e(TAG, "ResponceError==> ${response.errors}")
                Log.e(TAG, Gson().toJson(response))

                if (response.errors?.get(0)?.message != null) {
                    if (response.errors?.get(0)?.message!!.contains(
                            "purchase a package",
                            true
                        )
                    ) {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(R.id.action_global_plan)
                            })
                    } else {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {

                            })
                    }
                }
            } else {
                binding?.root?.snackbar("New Story scheduled for later")
            }
            Log.e(TAG, "filee hasError= $response")
            Log.e(TAG, Gson().toJson(response))
            Log.e(TAG, "story upload 2")

            getAllUserMultiStories()
        }
    }

    private fun uploadStory() {
        startLoader(0)
        lifecycleScope.launchWhenCreated {
            val f = file
            val buildder = DefaultUpload.Builder()
            Log.e(TAG, "story upload ")
            buildder.contentType("Content-Disposition: form-data;")
            buildder.fileName(f.name)

            val upload = buildder.content(f).build()
            Log.e(TAG, "filee ${f.exists()} ${f.length()}")
            val userToken = getCurrentUserToken()!!
            Log.e(TAG, "userToken $userToken File ${upload.fileName}")

            val response: ApolloResponse<StoryMutation.Data> = try {
                Log.e(TAG, "story upload 1")
                apolloClient(
                    context = requireContext(), token = userToken, isMultipart = true
                ).mutation(
                    StoryMutation(file = upload)
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "filee Apollo Exception ${e.message}")
                Log.e(TAG, Gson().toJson(e))

                Log.e(TAG, "ApolloException==> ${e.message}")
                binding?.root?.snackbar(" ${e.message}")
                return@launchWhenCreated
            } catch (e: Exception) {
                Log.e(TAG, "Exception==> ${e.message}")

                Log.e(TAG, "UserMomentFragment" + "Exception $e")
                Log.e(TAG, "filee General Exception ${e.message} $userToken")
                binding?.root?.snackbar(" ${e.message}")
                return@launchWhenCreated
            } finally {
                stopLoader(0)
            }
            if (response.hasErrors()) {
                Log.e(TAG, "NewUserMomentFragment : ${response.errors}")
                Log.e(TAG, "ResponceError==> ${response.errors}")
                Log.e(TAG, Gson().toJson(response))

                if (response.errors?.get(0)?.message != null) {
                    if (response.errors?.get(0)?.message!!.contains(
                            "purchase a package",
                            true
                        )
                    ) {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(R.id.action_global_plan)
                            })
                    } else {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {

                            })
                    }
                }
            }
            Log.e(TAG, "filee hasError= $response")
            Log.e(TAG, "UsermomentsSuces : ${Gson().toJson(response)}")

            getAllUserMultiStories()
        }
    }

    private fun startLoader(position: Int) {
        val viewHolder: RecyclerView.ViewHolder? =
            binding?.rvUserStories?.findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            if (position == 0) {
                viewHolder as UserMultiStoriesAdapter.NewUserStoryHolder
                viewHolder.viewBinding.loadingView.status = InsLoadingView.Status.LOADING
                viewHolder.viewBinding.root.isClickable = false
            } else {
                viewHolder as UserMultiStoriesAdapter.UserStoryHolder
                viewHolder.viewBinding.loadingView?.status = InsLoadingView.Status.LOADING
                viewHolder.viewBinding.root.isClickable = false
            }
        }
    }

    private fun stopLoader(position: Int) {
        val viewHolder: RecyclerView.ViewHolder? =
            binding?.rvUserStories?.findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            if (position == 0) {
                viewHolder as UserMultiStoriesAdapter.NewUserStoryHolder
                viewHolder.viewBinding.loadingView.status = InsLoadingView.Status.CLICKED
                viewHolder.viewBinding.root.isClickable = true
            } else {
                viewHolder as UserMultiStoriesAdapter.UserStoryHolder
                viewHolder.viewBinding.loadingView?.status = InsLoadingView.Status.CLICKED
                viewHolder.viewBinding.root.isClickable = true
            }
        }
    }

    override fun onUserMultiStoryClick(
        position: Int, userStory: GetAllUserMultiStoriesQuery.AllUserMultiStory
    ) {

        startLoader(position)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val node = userStory.stories.edges[0]?.node
        val url = if (!BuildConfig.USE_S3) {
            if (node?.file.toString().startsWith(BuildConfig.BASE_URL)) node?.file.toString()
            else "${BuildConfig.BASE_URL}${node?.file.toString()}"
        } else if (node?.file.toString().startsWith(ApiUtil.S3_URL)) node?.file.toString()
        else ApiUtil.S3_URL.plus(node?.file.toString())

        val avatarUrl = if (node?.user?.avatar?.url != null) {
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
        val username = node?.user!!.fullName
        val UserID = userId
        val objectId = node.pk
        var text = node.createdDate.toString()
        text = text.replace("T", " ").substring(0, text.indexOf("."))
        val momentTime = formatter.parse(text)
        val times = DateUtils.getRelativeTimeSpanString(
            momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
        )

        val dialog = UserMultiStoryDetailFragment()
        dialog.setListener(object : UserMultiStoryDetailFragment.DeleteCallback {
            override fun deleteCallback(objectId: Int) {
                // call api for delete
                showProgressView()
                Handler(Looper.getMainLooper()).postDelayed({
                    lifecycleScope.launch {
                        userToken = getCurrentUserToken()!!
                        try {
                            apolloClient(requireContext(), userToken!!).mutation(
                                DeleteStoryMutation("$objectId")
                            ).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            binding?.root?.snackbar(" ${e.message}")
                            hideProgressView()
                            return@launch
                        }
                    }
                    hideProgressView()
                    getAllUserMultiStories()
                }, 1000)
            }
        })
        val b = Bundle()
        b.putString("Uid", UserID)
        b.putString("url", url)
        b.putString("userurl", avatarUrl)
        b.putString("username", username)
        b.putString("times", times.toString())
        b.putString("token", userToken)
        b.putInt("objectID", objectId!!)
        b.putBoolean("showDelete", userId == node.user.id)
        val gson = Gson()
        val json = gson.toJson(userStory.stories)
        b.putString("stories", json)
        dialog.arguments = b
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.show(childFragmentManager, "story")
            stopLoader(position)
        }, 0)
    }

    override fun onAddNewUserStoryClick(isCamera: Boolean) {
        if (isCamera) {
            val intent = Intent(requireActivity(), CameraActivity::class.java)
            intent.putExtra("video_duration_limit", 60)
            intent.putExtra("withCrop", false)
            photosLauncher.launch(intent)
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*" // Allow both images and videos
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf("image/*", "video/*")
                ) // Specify MIME types
            }
            galleryImageLauncher.launch(intent)
        }
    }

    override fun onSharedMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {

    }

    override fun onMarketPlacesClick(position: Int, positionProduct: Int) {

        // market place open
        if (positionProduct == -1) {
            getMainActivity().openMarketPlace()
        } else {
            val bundle = Bundle().apply {
                putString("productId", "id")
                putInt("itemPosition", position)

            }
            findNavController().navigate(
                destinationId = R.id.action_userMomentFragment_to_fragmentProduct,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = bundle
            )
        }
    }

    override fun onLikeOfMomentShowClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {
        getMomentLikes(item)
    }

    override fun onLikeOfMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {
        showProgressView()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                userId = getCurrentUserId()!!
                userName = getCurrentUserName()!!
                Log.e(TAG, "OnLikeOfMomentClick: UserId: $userId   Username: $userName")
                val selectedUserId = item?.node?.user?.id
                Log.e(TAG, "OnLikeOfMomentClick: SelectedUserId: $selectedUserId")
                if (selectedUserId == userId) {
                    Log.e(TAG, "$selectedUserId == $userId")
                    getMomentLikes(item)
                } else {
                    Log.e(TAG, "$selectedUserId != $userId")
                    val res = try {
                        val queryString = LikeOnMomentMutation(item?.node!!.pk!!.toString())
                        Log.e(TAG, "QueryString: $queryString")
                        apolloClient(requireContext(), userToken!!).mutation(queryString).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponseException ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }
                    getParticularMoments(position, item.node.pk.toString())
                }
                hideProgressView()
                Log.e(TAG, "Size: " + allUserMoments.size)
            }
        }
    }

    private fun getMomentLikes(item: GetAllUserMomentsQuery.Edge?) {
        likeMomentItemPK = (item?.node?.pk ?: "").toString()
        Log.e(TAG, "getMomentLikes:  likeMomentItemPK : $likeMomentItemPK")
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken?.let {
                    Log.e(TAG, "UserMomentNextPage Calling")
                    momentsViewModel.getMomentLikes(
                        it,
                        (item?.node?.pk ?: "").toString()
                    ) { error ->
                        if (error == null) {
                            activity?.runOnUiThread {
                                try {
                                    Log.e(TAG, "ViewModel: Likes: ${momentsViewModel.coinPrice}")
                                    loadLikesDialog(momentsViewModel.coinPrice)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadLikesDialog(momentLikes: ArrayList<MomentLikes>) {
        Log.e(TAG, "---------------------------------------1: $momentLikes")

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                momentLikesUsers.clear()

                val rvLikes = binding?.rvLikes
                val no_data = binding?.noDatas
                val txtHeaderLike = binding?.txtheaderlike

                txtHeaderLike?.text =
                    momentLikes.size.toString() + " ${requireActivity().resources.getString(R.string.like)}"
                Log.e(
                    TAG,
                    "---------------------------------------5: ${txtHeaderLike?.text.toString()}"
                )
                try {
                    if (momentLikes.isNotEmpty()) {
                        no_data?.visibility = View.GONE
                        rvLikes?.visibility = View.VISIBLE
                        if (rvLikes?.itemDecorationCount == 0) {
                            rvLikes.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

                        momentLikes.forEach { i ->
                            Log.e(TAG, "$i")
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
                            momentLikesUsers.add(models)
                        }

                        momentLikeUserAdapters = StoryLikesAdapter(
                            requireActivity(), momentLikesUsers, Glide.with(requireContext())
                        )

                        rvLikes?.adapter = momentLikeUserAdapters
                        momentLikeUserAdapters?.notifyDataSetChanged()

                        momentLikeUserAdapters?.userProfileClicked {
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

                        rvLikes?.layoutManager = LinearLayoutManager(activity)
                    } else {
                        no_data?.visibility = View.VISIBLE
                        rvLikes?.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "MomentsLikesException: ${e.message}")
                }
                if (LikebottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    LikebottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    likeMomentItemPK = ""
                }
            }
        }
    }


    private fun getParticularMoments(pos: Int, ids: String) {
        if (pos < 0) {
            return
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userToken = getCurrentUserToken()!!
                val res = try {
                    Log.e(TAG, "getParticularMoments: $width $size $ids")
                    apolloClient(requireContext(), userToken!!).query(
                        GetAllUserMomentsQuery(
                            width, size, 1, "", ids
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponseException all moments ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@repeatOnLifecycle
                }
                val allmoments = res.data?.allUserMoments!!.edges
                allmoments.indices.forEach { i ->
                    if (ids == allmoments[i]!!.node!!.pk.toString()) {
                        adapterMoments.updateData(pos, allmoments[i]!!)
                        return@forEach
                    }
                }
            }
        }
    }

    fun fireLikeNotificationForReceiver(item: GetAllUserMomentsQuery.Edge) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val queryName = "sendNotification"
                val query = StringBuilder().append("mutation {").append("$queryName (")
                    .append("userId: \"${item.node!!.user!!.id}\", ")
                    .append("notificationSetting: \"LIKE\", ")
                    .append("data: {momentId:${item.node.pk}}").append(") {").append("sent")
                    .append("}")
                    .append("}").toString()
                val result = provideGraphqlApi().getResponse<Boolean>(
                    query, queryName, userToken
                )
                Log.e(TAG, "RSLT" + "" + result.message)
            }
        }
    }

    private var alreadyFetching = false
    private fun getUserMomentNextPage(width: Int, size: Int, i: Int, endCursors: String) {
        Log.e(TAG, "alreadyFetching: $alreadyFetching")
        lifecycleScope.launch {
            userToken?.let {
                Log.e(TAG, "UserMomentNextPage Calling")
                momentsViewModel.getAllMoments(requireContext(), it, width, size, i, endCursor)
            }
        }
    }

    private fun showNewMomentsResults() {
    }

    override fun onCommentOfMomentClick(
        position: Int, item: GetAllUserMomentsQuery.Edge?
    ) {
        val bundle = Bundle().apply {
            putString("momentID", item?.node?.pk.toString())
            putInt("itemPosition", position)
            putString("filesUrl", item?.node?.file.toString())
            putString("Likes", item?.node?.like?.toString())
            putString("Comments", item?.node?.comment!!.toString())
            val gson = Gson()
            putString("Desc", gson.toJson(item.node.momentDescriptionPaginated))
            val fullName = item.node.user?.fullName
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            putString("fullnames", name)
            if (item.node.user?.gender != null) {
                putString("gender", item.node.user.gender.name)
            } else {
                putString("gender", null)
            }
            putString("momentuserID", item.node.user?.id.toString())
        }
        navController?.navigate(R.id.momentsAddCommentFragment, bundle)
    }

    override fun onMomentGiftClick(position: Int, item: GetAllUserMomentsQuery.Edge?) {
        if (userId!! != item!!.node!!.user!!.id) {
            giftUserid = item.node!!.user!!.id.toString()
            binding?.sendgiftto?.text =
                context?.resources?.getString(R.string.send_git_to) + " " + item.node.user!!.fullName
            if (GiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                GiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        } else {
            if (receivedGiftbottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                receivedGiftbottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun deleteConfirmation(item: GetAllUserMomentsQuery.Edge?) {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_delete, null)
        val headerTitle = dialogLayout.findViewById<TextView>(R.id.header_title)
        val noButton = dialogLayout.findViewById<TextView>(R.id.no_button)
        val yesButton = dialogLayout.findViewById<TextView>(R.id.yes_button)

        headerTitle.text =
            "${AppStringConstant(getMainActivity()).are_you_sure_you_want_to_delete_moment}"
        noButton.text = "${AppStringConstant(getMainActivity()).no}"
        yesButton.text = "${AppStringConstant(getMainActivity()).yes}"

        val builder = AlertDialog.Builder(getMainActivity(), R.style.DeleteDialogTheme)
        builder.setView(dialogLayout)
        val dialog = builder.create()

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            dialog.dismiss()
            showProgressView()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userToken = getCurrentUserToken()!!
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).mutation(DeletemomentMutation(item?.node!!.pk!!.toString())).execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponseException ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        return@repeatOnLifecycle
                    }
                    hideProgressView()
                }
            }
        }
        dialog.show()
    }

    override fun onDotMenuOfMomentClick(
        position: Int, item: GetAllUserMomentsQuery.Edge?, types: String
    ) {
        if (types == "delete") {
            deleteConfirmation(item)
        } else if (types == "report") {
            reportDialog(item)
        } else if (types == "edit") {
            val sb = StringBuilder()
            item?.node?.momentDescriptionPaginated?.forEach { sb.append(it) }
            val desc = sb.toString().replace("", "")
            val args = Bundle()
            args.putString("moment_desc", desc)
            args.putInt("moment_pk", item?.node?.pk ?: -1)
            findNavController().navigate(
                destinationId = R.id.userMomentUpdateFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = true,
                args = args
            )
        }
    }

    override fun onProfileOpen(position: Int, item: GetAllUserMomentsQuery.Edge?) {
        val mUserID = item?.node?.user?.id.toString()
        Log.e(TAG, "onProfileOpen:mUserId: $mUserID")
        val bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", mUserID)
        Log.e(TAG, "onProfileOpen:userId: $userId")
        if (userId == mUserID) {
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

    private fun reportDialog(item: GetAllUserMomentsQuery.Edge?) {

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_report, null)
        val reportView = dialogLayout.findViewById<TextView>(R.id.report_view)
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
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    userToken = getCurrentUserToken()!!
                    val res = try {
                        apolloClient(
                            requireContext(), userToken!!
                        ).mutation(ReportonmomentMutation(item?.node!!.pk!!.toString(), message))
                            .execute()
                    } catch (e: ApolloException) {
                        Log.e(TAG, "apolloResponse Exception ${e.message}")
                        binding?.root?.snackbar(" ${e.message}")
                        hideProgressView()
                        dialog.dismiss()
                        return@repeatOnLifecycle
                    }

                    binding?.root?.autoSnackbarOnTop("Moment reported")

                    hideProgressView()
                    dialog.dismiss()
                }
            }
        }

        cancleButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onMoreShareMomentClick() {

    }

    private fun showLikeBottomSheet() {
        val likebottomSheet = binding?.likebottomSheet
        LikebottomSheetBehavior = BottomSheetBehavior.from(likebottomSheet!!)
        LikebottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    fun getMainActivity() = activity as MainActivity

    private fun momentSharing(filePath: File?, description: String?, commentAllowed: Boolean) {
        binding?.imageviewSharing?.loadImage(filePath!!)
        binding?.momentSharing?.text = "Uploading..."
        lifecycleScope.launchWhenCreated {

            val f = filePath!!
            val buildder = DefaultUpload.Builder()
            buildder.contentType("Content-Disposition: form-data;")
            buildder.fileName(f.name)
            val upload = buildder.content(f).build()
            Log.e(TAG, "filee ${f.exists()}")
            val userToken = getCurrentUserToken()!!

            Log.e(TAG, "useriddd ${mUser?.id}")
            if (mUser?.id != null) {
                val response = try {
                    apolloClient(context = requireContext(), token = userToken).mutation(
                        MomentMutation(
                            file = upload,
                            detail = description!!,
                            userField = mUser?.id!!,
                            allowComment = commentAllowed
                        )
                    ).execute()

                } catch (e: ApolloException) {
                    hideProgressView()
                    Log.e(TAG, "filee Apollo Exception ApolloException ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@launchWhenCreated
                } catch (e: Exception) {
                    hideProgressView()
                    Log.e(TAG, "filee General Exception ${e.message} $userToken")
                    binding?.root?.snackbar(" ${e.message}")
                    return@launchWhenCreated
                }
                Log.e(TAG, "--->" + Gson().toJson(response))
                hideProgressView()

                if (response.hasErrors()) {
                    if (response.errors?.get(0)?.message!!.contains(
                            "purchase a package",
                            true
                        )
                    ) {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = { findNavController().navigate(R.id.action_global_plan) })
                    } else {
                        binding?.root?.snackbar("${response.errors?.get(0)?.message}")
                    }
                } else {
                    binding?.root?.snackbar("New Moment Shared")
                    binding?.llSharing?.visibility = View.GONE
                }
            }
            binding?.llSharing?.visibility = View.GONE
        }
    }


    private fun scheduleMomentForLater(
        filePath: File?, description: String?, commentAllowed: Boolean, publishAt: String
    ) {
        binding?.imageviewSharing?.loadImage(filePath!!)
        binding?.momentSharing?.text = "Uploading..."
        lifecycleScope.launchWhenCreated {

            val f = filePath!!
            val buildder = DefaultUpload.Builder()
            buildder.contentType("Content-Disposition: form-data;")
            buildder.fileName(f.name)
            val upload = buildder.content(f).build()
            Log.e(TAG, "filee ${f.exists()}")
            val userToken = getCurrentUserToken()!!

            Log.e(TAG, "useriddd ${mUser?.id}")
            if (mUser?.id != null) {
                val response = try {
                    apolloClient(context = requireContext(), token = userToken).mutation(
                        ScheduleMomentMutation(
                            file = upload,
                            detail = description!!,
                            userField = mUser?.id!!,
                            allowComment = commentAllowed,
                            publishAt = publishAt
                        )
                    ).execute()

                } catch (e: ApolloException) {
                    hideProgressView()
                    Log.e(TAG, "filee Apollo Exception ApolloException ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    return@launchWhenCreated
                } catch (e: Exception) {
                    hideProgressView()
                    Log.e(TAG, "filee General Exception ${e.message} $userToken")
                    binding?.root?.snackbar(" ${e.message}")
                    return@launchWhenCreated
                }
                Log.e(TAG, "--->" + Gson().toJson(response))
                hideProgressView()

//                File(filePath.toString()).delete()
                if (response.hasErrors()) {
                    if (response.errors?.get(0)?.message!!.contains(
                            "purchase a package",
                            true
                        )
                    ) {
                        binding?.root?.snackbarOnTop("${response.errors?.get(0)?.message}",
                            Snackbar.LENGTH_INDEFINITE,
                            callback = { findNavController().navigate(R.id.action_global_plan) })
                    } else {
                        binding?.root?.snackbar("${response.errors?.get(0)?.message}")
                    }
                } else {
                    binding?.root?.snackbar("New Moment scheduled for later")
                    binding?.llSharing?.visibility = View.GONE
                }
            }
            binding?.llSharing?.visibility = View.GONE
        }
    }

    override fun onProductClick(position: Int, productId: String, product: Products) {
        val bundle = Bundle().apply {
            putString("productId", productId)
            putParcelable("selectedProduct", product)
            putInt("itemPosition", position)
        }
        findNavController().navigate(
            destinationId = R.id.action_userMomentFragment_to_fragmentProduct,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }
}