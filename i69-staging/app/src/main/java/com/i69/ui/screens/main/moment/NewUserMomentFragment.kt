package com.i69.ui.screens.main.moment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.i69.BuildConfig
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.User
import com.i69.databinding.BottomsheetShareOptionsBinding
import com.i69.databinding.DialogBuySubscriptionOrCoinsBinding
import com.i69.databinding.DialogPreviewImageBinding
import com.i69.databinding.FragmentNewUserMomentBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.camera.CameraActivity
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.DrawableClickListener
import com.i69.utils.KeyboardUtils
import com.i69.utils.KeyboardUtils.SoftKeyboardToggleListener
import com.i69.utils.Utils
import com.i69.utils.convertURITOBitmapNSaveImage
import com.i69.utils.loadCircleImage
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.showKeyboard
import com.i69.utils.snackbar
import com.i69.utils.snackbarOnTop
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.launch
import net.vrgsoft.videcrop.VideoCropActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NewUserMomentFragment : BaseFragment<FragmentNewUserMomentBinding>() {

    private val viewModel: UserViewModel by activityViewModels()
    private var mFilePath: String = ""
    private lateinit var contentUri: Uri
    lateinit var file: File
    lateinit var fileType: String
    protected var mUser: User? = null
    var VIDEO_CROP_REQUEST = 10001
    var outputPath = ""
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = NewUserMomentFragment::class.java.simpleName
    private val photosLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                val fileName = data?.getStringExtra("fileName").toString()
                mFilePath = data?.getStringExtra("result").toString()
                contentUri = Uri.fromFile(File(mFilePath)) ?: throw IllegalArgumentException("No data URI")
                file = File(mFilePath)
                if (mFilePath.contains(".")) {
                    val regex = "\\.".toRegex()
                    val type: String =
                        mFilePath.reversed().split(regex).get(0).reversed().toString()
                    fileType = ".$type"
                }
//                if (fileType == ".mp4") {
                if (Utils.isVideoFile(contentUri, requireContext()) || Utils.isVideoFile(contentUri)) {
                    showFilePreview(file, fileType)
//                    cropVideoPopup(file)
                } else {
//                    showFilePreview(file, fileType)
                    val imageUri = Uri.fromFile(mFilePath.let { File(it) })
                    CropImage.activity(imageUri)
                        .start(requireContext(), this)
                }
                binding?.imgUploadFile?.loadCircleImage(mFilePath)
            }
        }

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                mFilePath = data?.data.toString()
//
                val result = data?.data?.path
                contentUri = data?.data ?: throw IllegalArgumentException("No data URI")
                val openInputStream =
                    requireActivity().contentResolver?.openInputStream(contentUri)
                        ?: throw IllegalStateException("Unable to open input stream")
//                val type = if (result?.contains("video") == true) ".mp4" else ".jpg"
                //                fileType = type
                var type = ""
                if (mFilePath.contains(".")) {
                    val regex = "\\.".toRegex()
                    type = mFilePath.reversed().split(regex).get(0)?.reversed().toString()
                    fileType = ".$type"
                }

                fileType = if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(contentUri)) ".jpg" else ".mp4"

                val fileName =  "${System.currentTimeMillis()}$fileType"
                val outputFile = File(requireContext().filesDir, fileName)
                openInputStream.use { inputStream ->
                    outputFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
//                openInputStream?.copyTo(outputFile.outputStream())
                file = outputFile

//                val type = if (mFilePath.contains("video")) ".mp4" else ".jpg"
//                fileType = type
//                val outputFile =
//                    requireContext().filesDir.resolve("${System.currentTimeMillis()}$type")
//                file = File(outputFile.toURI())
//                var compressedFilePath: String? = null

//                if (type == ".jpg" || type == ".jpeg") {
                if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(contentUri)) {
                    val compressedPath = convertURITOBitmapNSaveImage(
                        requireContext(),
                        data.data!!,
                        outputFile,
                    )
                    compressedPath?.let {
                        file = File(it)
                    }
                }
//
//                if (compressedFilePath == null) {
//                    compressedFilePath = mFilePath
//                }

//                if (fileType == ".mp4") {
                 if (Utils.isImageFile(contentUri, requireContext()) || Utils.isImageFile(contentUri)) {
                     //                    showFilePreview(file, fileType)
                     val imageUri = data.data
                     CropImage.activity(imageUri)
                         .start(requireContext(), this)
                } else {
                    showFilePreview(file, fileType)
//                     cropVideoPopup(file)
                }
                binding?.imgUploadFile?.loadCircleImage(mFilePath)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val pathOfFile = result.uri.path
                file = File(pathOfFile)
                if (pathOfFile != null) {
                    mFilePath = pathOfFile
                }
                contentUri = Uri.fromFile(file)

                showFilePreview(file, fileType)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        } else if (requestCode == VIDEO_CROP_REQUEST && resultCode == RESULT_OK) {
            mFilePath = outputPath
            showFilePreview(File(outputPath), fileType)
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

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentNewUserMomentBinding.inflate(inflater, container, false).apply {
        stringConstant = AppStringConstant(requireContext())
    }

    override fun initObservers() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupTheme() {
        getTypeActivity<MainActivity>()?.reloadNavigationMenu()

        viewStringConstModel.data.observe(this@NewUserMomentFragment) { data ->

            binding?.stringConstant = data

        }
        (requireActivity() as MainActivity).loadUser()
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }
        binding?.btnSelectFileToUpload?.setOnClickListener {
            hideKeyboard(it)
            val inflater =
                requireContext().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.layout_attach_moment, null)
            view.findViewById<TextView>(R.id.header_title).text =
                AppStringConstant1.select_moment_pic
            val mypopupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            val llCamera = view.findViewById<View>(R.id.llCamera) as LinearLayout
            val llGallary = view.findViewById<View>(R.id.ll_gallery) as LinearLayout

            llCamera.setOnClickListener {
                val intent = Intent(requireActivity(), CameraActivity::class.java)
                intent.putExtra("video_duration_limit", 60)
                intent.putExtra("withCrop", false)
                photosLauncher.launch(intent)
                mypopupWindow.dismiss()
            }

            llGallary.setOnClickListener {
//                galleryImageLauncher.launch(
////                    Intent (Intent.ACTION_GET_CONTENT).apply {
////                        type = "*/*"
////                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
////                    }
//                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//                )
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*" // Allow both images and videos
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/*", "video/*")
                    ) // Specify MIME types
                }
                galleryImageLauncher.launch(intent)
                mypopupWindow.dismiss()
            }
            mypopupWindow.showAsDropDown(it, (-it.x).toInt(), 0)
        }

        binding?.imgUploadFile?.setOnClickListener {
            if (this::file.isInitialized) showFilePreview(
                file,
                fileType
            )
        }

        binding?.cdAllowedComment?.setOnClickListener {
            binding?.rbAllowed?.isChecked = true
            binding?.rbNotAllowed?.isChecked = false
        }

        binding?.cdNotAllowedComment?.setOnClickListener {
            binding?.rbAllowed?.isChecked = false
            binding?.rbNotAllowed?.isChecked = true
        }

        binding?.rbAllowed?.setOnClickListener { binding?.cdAllowedComment?.performClick() }

        binding?.rbNotAllowed?.setOnClickListener { binding?.cdNotAllowedComment?.performClick() }

        binding?.editWhatsGoing?.setOnTouchListener(object :
            DrawableClickListener.BottomDrawableClickListener(binding?.editWhatsGoing) {
            override fun onDrawableClick(): Boolean {
                if (binding?.editWhatsGoing?.hasFocus() == true) {
                    binding?.editWhatsGoing?.clearFocus()
                    hideKeyboard(binding?.root)
                } else {
                    binding?.editWhatsGoing?.requestFocus()
                    binding?.editWhatsGoing?.showKeyboard()
                }
                return true
            }
        })

        KeyboardUtils.addKeyboardToggleListener(
            requireActivity(),
            SoftKeyboardToggleListener { isVisible: Boolean ->
                binding?.lblPostingMomentTip?.visibility = if (isVisible) GONE else VISIBLE
            })

        binding?.btnShareMoment?.setOnClickListener {
            if (!this::file.isInitialized) {
                binding?.root?.snackbar(AppStringConstant1.you_cant_share_moment)
                return@setOnClickListener
            }
            binding?.btnShareMoment?.setViewGone()
            showShareOptions {}
        }

        showProgressView()
        lifecycleScope.launch {
            val userId = getCurrentUserId()!!
            val token = getCurrentUserToken()!!
            viewModel.getCurrentUser(userId, token = token, false)
                .observe(viewLifecycleOwner) { user ->
                    user?.let {
                        mUser = it.copy()
                        Log.e(TAG,"Userrname ${mUser?.username}")

                        if (mUser != null) {
                            if (mUser!!.avatarPhotos != null && mUser!!.avatarPhotos!!.size != 0) {

                                if (mUser!!.avatarPhotos!!.size != 0 && mUser?.avatarPhotos?.size!! > mUser?.avatarIndex!!) {
                                    binding?.imgCurrentUser?.loadCircleImage(
                                        mUser!!.avatarPhotos!!.get(
                                            mUser!!.avatarIndex!!
                                        ).url?.replace(
                                            "http://95.216.208.1:8000/media/",
                                            "${BuildConfig.BASE_URL}media/"
                                        ).toString()
                                    )
                                }
                            }
                        }
                    }
                    hideProgressView()
                }
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showFilePreview(file: File?, fileType: String) {
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
        dialogBinding.ibClose.setViewGone()
        dialogBinding.tvShare.text = getString(R.string.next)
        dialogBinding.btnShareMoment.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showShareOptions(onShared: () -> Unit) {
        val shareOptionsDialog = BottomSheetDialog(requireContext())
        val bottomSheet = BottomsheetShareOptionsBinding.inflate(layoutInflater, null, false)
        shareOptionsDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        var shareAt = ""
        bottomSheet.llShareLaterRoot.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.profileTransBlackOverlayColor,
                null
            )
        )
        bottomSheet.rbShareLater.setViewGone()
        bottomSheet.ivLocked.setViewVisible()

        if ((requireActivity() as MainActivity).isUserAllowedToPostMoment()) {
            bottomSheet.llShareNowRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.CE4F3FF,
                    null
                )
            )
            bottomSheet.rbShareNow.setViewVisible()
            bottomSheet.ivPostLocked.setViewGone()
            if ((requireActivity() as MainActivity).isUserHasMomentQuota()) {
                bottomSheet.tvShareNowCoins.setViewGone()
            } else {
                (requireActivity() as MainActivity).getRequiredCoins("POST_MOMENT_COINS") {
                    bottomSheet.tvShareNowCoins.setViewVisible()
                    bottomSheet.tvShareNowCoins.text = it.toString()
                }
            }
        } else {
            bottomSheet.llShareNowRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.profileTransBlackOverlayColor,
                    null
                )
            )
            bottomSheet.rbShareNow.setViewGone()
            bottomSheet.ivPostLocked.setViewVisible()

            bottomSheet.llShareLaterRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.profileTransBlackOverlayColor,
                    null
                )
            )
            bottomSheet.rbShareLater.setViewGone()
            bottomSheet.ivLocked.setViewVisible()

            bottomSheet.cvShareNow.setOnClickListener {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }

            bottomSheet.cvShareLater.setOnClickListener {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }
            shareOptionsDialog.setContentView(bottomSheet.root)
            shareOptionsDialog.show()
            return
        }

        if ((requireActivity() as MainActivity).isUserAllowedToScheduleMoment()) {
            bottomSheet.llShareLaterRoot.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.CE4F3FF,
                    null
                )
            )
            bottomSheet.rbShareLater.setViewVisible()
            bottomSheet.ivLocked.setViewGone()

            if ((requireActivity() as MainActivity).isUserHasSubscription()) {
                bottomSheet.tvShareLaterCoins.setViewGone()
            } else {
                (requireActivity() as MainActivity).getRequiredCoins("SCHEDULE_MOMENT_COINS") {
                    bottomSheet.tvShareLaterCoins.setViewVisible()
                    bottomSheet.tvShareLaterCoins.text = it.toString()
                }
            }
        }

        bottomSheet.rbShareNow.setOnClickListener { bottomSheet.cvShareNow.performClick() }
        bottomSheet.rbShareLater.setOnClickListener { bottomSheet.cvShareLater.performClick() }
        bottomSheet.cvShareNow.setOnClickListener {
            bottomSheet.rbShareNow.isChecked = true
            bottomSheet.rbShareLater.isChecked = false
        }

        bottomSheet.cvShareLater.setOnClickListener {
            if ((requireActivity() as MainActivity).isUserAllowedToScheduleMoment()) {
                bottomSheet.rbShareLater.isChecked = true
                bottomSheet.rbShareNow.isChecked = false
                showDateTimePicker { displayTime, apiTime ->
                    if (displayTime.isNotEmpty() && apiTime.isNotEmpty()) {
                        bottomSheet.tvShareLater.text = "Scheduled for $displayTime"
                        shareAt = apiTime
                    } else {
                        bottomSheet.rbShareLater.isChecked = false
                        bottomSheet.rbShareNow.isChecked = true
                    }
                }
            } else {
                if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
                onShared.invoke()
                showUpgradePlanDialog()
            }
        }

        bottomSheet.btnShareMoment.setOnClickListener {
            if (shareOptionsDialog.isShowing) shareOptionsDialog.dismiss()
            onShared.invoke()
            if (bottomSheet.rbShareNow.isChecked) {
                shareNow()
            } else if (bottomSheet.rbShareLater.isChecked) {
                if (shareAt.isNotEmpty()) {
                    shareLater(shareAt)
                }
            }
        }

        shareOptionsDialog.setOnDismissListener { binding?.btnShareMoment?.setViewVisible() }
        shareOptionsDialog.setContentView(bottomSheet.root)
        shareOptionsDialog.show()
    }

    private fun showUpgradePlanDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
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
            { view, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    context,
                    { view, hourOfDay, minute ->
                        if (getMainActivity().isValidTime(
                                year,
                                monthOfYear,
                                dayOfMonth,
                                hourOfDay,
                                minute
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

                            Log.v("UMF", "The choosen one $formattedTime")
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

                    // Set the text color for positive, negative, and neutral buttons
                    positiveButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    negativeButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.iconGray
                        )
                    )
                    neutralButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.iconGray
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
            positiveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.iconGray))
            neutralButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.iconGray))
        }
        datePickerDialog.show()
    }

    private fun shareNow() {
        val description = binding?.editWhatsGoing?.text.toString()

        Log.e(TAG,"filee $mFilePath")

        Log.e(TAG,"NewUserMomentFragment"+ "calling")

        getMainActivity().openUserAllMoments(
            file,
            description,
            binding?.rbAllowed?.isChecked == true
        )
        binding?.editWhatsGoing?.setText("")
    }

    private fun shareLater(shareAt: String) {
        val description = binding?.editWhatsGoing?.text.toString()
        Log.e(TAG,"filee $mFilePath")
        getMainActivity().openUserAllMoments(
            file,
            description,
            binding?.rbAllowed?.isChecked == true,
            shareAt
        )
        binding?.editWhatsGoing?.setText("")
    }

    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            hideKeyboard(binding?.root)
            binding?.editWhatsGoing?.clearFocus()
            getMainActivity().drawerSwitchState()
        }
    }

    fun hideKeyboard(view: View?) =
        (requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)!!.hideSoftInputFromWindow(
            view?.windowToken,
            0
        )

    override fun onResume() {
        super.onResume()
        getMainActivity().setDrawerItemCheckedUnchecked(null)

    }

    fun getMainActivity() = activity as MainActivity
}
