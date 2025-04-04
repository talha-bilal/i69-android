package com.i69.ui.base.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.bumptech.glide.Glide
import com.i69.BuildConfig
import com.i69.R
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.config.Constants.INTEREST_MOVIE
import com.i69.data.config.Constants.INTEREST_MUSIC
import com.i69.data.config.Constants.INTEREST_SPORT_TEAM
import com.i69.data.config.Constants.INTEREST_TV_SHOW
import com.i69.data.enums.InterestedInGender
import com.i69.data.models.IdWithValue
import com.i69.data.models.Photo
import com.i69.data.models.User
import com.i69.data.remote.responses.DefaultPicker
import com.i69.databinding.DialogPreviewImageBinding
import com.i69.databinding.FragmentEditProfileBinding
import com.i69.ui.adapters.AddPhotoAdapter
import com.i69.ui.adapters.PhotosAdapter
import com.i69.ui.adapters.PhotosData
import com.i69.ui.adapters.PhotosNewAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.ImagePickerActivity
import com.i69.ui.screens.interest.getInterestsListActivityIntent
import com.i69.ui.screens.main.camera.CameraActivity
import com.i69.ui.views.InterestsView
import com.i69.ui.views.ToggleImageView
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import java.io.File

const val PRIVATE = "PRIVATE"
const val PUBLIC = "PUBLIC"

abstract class BaseEditProfileFragment : BaseFragment<FragmentEditProfileBinding>(),
                                         PhotosAdapter.PhotoAdapterListener {
    lateinit var photosnewAdapter: PhotosNewAdapter
    var defaultPicker: DefaultPicker? = null
    var user_assign: User? = null
    var avtarindex = 0
    var profilePictureAmt: Int = 50
    private var TAG: String = BaseEditProfileFragment::class.java.simpleName
    val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    companion object {
        @JvmField
        var listOfInterestedIn: ArrayList<Int> = java.util.ArrayList<Int>()
    }

    var imageType = ""
    private val photosLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")

                val imageUri = Uri.fromFile(result?.let { File(it) })
                CropImage.activity(imageUri)
                    .start(requireContext(), this)
            }
        }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val photoData = PhotosData(uri.toString(), imageType)
            photosnewAdapter.addItem(photoData)
            avtarindex = avtarindex + 1
        }
    }

    val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {

                val imageUri = data?.data
                CropImage.activity(imageUri)
                    .start(requireContext(), this)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val pathOfFile = result.uri.path

                showFilePreview(pathOfFile)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun showFilePreview(pathOfFile: String?) {
        val dialogBinding = DialogPreviewImageBinding.inflate(layoutInflater, null, false)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(R.color.color_transparant_80)

        dialogBinding.ivPreview.setViewVisible()
        dialogBinding.vvPreview.setViewGone()
        Glide.with(requireContext()).load(pathOfFile).into(dialogBinding.ivPreview)

        dialogBinding.ibClose.setViewGone()
        dialogBinding.tvShare.text = getString(R.string.next)
        dialogBinding.btnShareMoment.setOnClickListener {
            val photoData = PhotosData(pathOfFile!!, imageType)
            photosnewAdapter.addItem(photoData)
            avtarindex = avtarindex + 1

            dialog.dismiss()
        }
        dialog.show()
    }

    private val interestedInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == RESULT_OK) {
                val interestType =
                    data?.getIntExtra(com.i69.data.config.Constants.EXTRA_INTEREST_TYPE, -1)
                val interestValue =
                    data?.getStringArrayListExtra(com.i69.data.config.Constants.EXTRA_INTEREST_VALUE)
                when (interestType) {
                    INTEREST_MUSIC -> interestValue?.let { binding?.music?.setInterests(it) }
                    INTEREST_MOVIE -> interestValue?.let { binding?.movies?.setInterests(it) }
                    INTEREST_TV_SHOW -> interestValue?.let { binding?.tvShows?.setInterests(it) }
                    INTEREST_SPORT_TEAM -> interestValue?.let { binding?.sportTeams?.setInterests(it) }
                }
                if (interestType != null && interestValue != null) setInterestedInToViewModel(
                    interestType, interestValue
                )
            }
        }

    abstract fun callparentmethod(pos: Int, photo_url: String)

    abstract fun showBuyDialog(photoQuota: Int, coinSpendAmt: Int)

    abstract fun setupScreen()

    abstract fun getInterestedInValues(interestsType: Int): List<String>

    abstract fun setInterestedInToViewModel(interestType: Int, interestValue: List<String>)

    abstract fun onDoneClick(increment: Boolean)

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEditProfileBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding?.toolbar?.inflateMenu(R.menu.done_menu)
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_keyboard_right_arrow)
        binding?.toolbar?.setNavigationOnClickListener { moveUp() }
        initGroups()
        initTags()
        initPhotoGallery()
        setupScreen()

        binding?.addInterest?.setOnClickListener {

            showChooseInterestedInPopup(it)
        }

    }

    override fun setupClickListeners() {
        binding?.toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_done) onDoneClick(false)
            false
        }
    }

    private fun initGroups() {
        initExpandableLayout(binding?.groupsExpand, binding?.toggleGroupsExpand, binding?.groups)
        initExpandableLayout(
            binding?.interestsExpand, binding?.toggleInterestsExpand, binding?.interests
        )
    }

    private fun initExpandableLayout(
        button: View?, toggleImageView: ToggleImageView?, expandableLayout: ExpandableLayout?
    ) {
        toggleImageView?.onCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    expandableLayout?.expand(false)
                    binding?.scrollContainer?.post {
                        binding?.scrollContainer?.smoothScrollTo(0, expandableLayout?.bottom!!)
                    }
                } else {
                    expandableLayout?.collapse()
                }
            }

        button?.setOnClickListener {
            toggleImageView?.toggle()
        }

    }

    private fun initTags() {
        initInterestsView(binding?.music, INTEREST_MUSIC)
        initInterestsView(binding?.movies, INTEREST_MOVIE)
        initInterestsView(binding?.tvShows, INTEREST_TV_SHOW)
        initInterestsView(binding?.sportTeams, INTEREST_SPORT_TEAM)
    }

    private fun initInterestsView(chips: InterestsView?, interestsType: Int) {
        chips?.setOnAddButtonClickListener {
            val interestedInValues = getInterestedInValues(interestsType)

            interestedInLauncher.launch(
                getInterestsListActivityIntent(
                    requireContext(), interestsType, interestedInValues
                )
            )
        }
    }

    private fun initPhotoGallery() {
        val addPhotosAdapter = AddPhotoAdapter {
            val limit = user_assign!!.photosQuota
            Log.e(TAG, "AvatarIndex $avtarindex Limit $limit")
            if (avtarindex < limit) {
                showChooseImageSectionPopup(it)
            } else {
                showBuyDialog(user_assign!!.photosQuota, profilePictureAmt)
            }
        }
        photosnewAdapter = PhotosNewAdapter(requireActivity()) { position, photourl ->
            Log.e(TAG,"$position   ::  $photourl")
            Log.e(TAG,"BaseUrl: ${BuildConfig.BASE_URL}")
            callparentmethod(position, photourl.link)
            if (!photourl.link.startsWith(BuildConfig.BASE_URL)) {
                avtarindex -= 1
            }
        }
        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(0, addPhotosAdapter)
        concatAdapter.addAdapter(1, photosnewAdapter)
        binding?.photosRecycler?.adapter = concatAdapter
    }

    fun showChooseImageDialog() {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_image_option, null)
        view.findViewById<TextView>(R.id.header_title).text =
            resources.getString(R.string.select_profile_image)

        val myPopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val llCamera = view.findViewById<View>(R.id.ll_camera) as LinearLayoutCompat
        val llGalerry = view.findViewById<View>(R.id.ll_gallery) as LinearLayoutCompat

        llCamera.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent(requireActivity(), CameraActivity::class.java)
            } else {
                Intent(requireActivity(), ImagePickerActivity::class.java)
            }
            intent.putExtra("video_duration_limit", 60)
            intent.putExtra("withCrop", false)
            photosLauncher.launch(intent)
            myPopupWindow.dismiss()
        }

        llGalerry.setOnClickListener {
            galleryImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
            )
            myPopupWindow.dismiss()
        }

        myPopupWindow.showAsDropDown(binding?.photosRecycler, 25, 0)
    }

    fun showChooseImageSectionPopup(v: View) {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_section_option, null)
        view.findViewById<TextView>(R.id.header_title).text =
            resources.getString(R.string.select_section_image)

        val myPopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val llPublic = view.findViewById<View>(R.id.ll_public) as LinearLayoutCompat
        val llPrivate = view.findViewById<View>(R.id.ll_private) as LinearLayoutCompat

        llPublic.setOnClickListener {
            imageType = PUBLIC
            showChooseImageSectionPopupNew(v)
            myPopupWindow.dismiss()
        }

        llPrivate.setOnClickListener {
            imageType = PRIVATE
            showChooseImageSectionPopupNew(v)
            myPopupWindow.dismiss()
        }

        myPopupWindow.showAsDropDown(v, -153, 0)
    }

    @SuppressLint("MissingInflatedId")
    fun showChooseInterestedInPopup(v: View) {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_intersted_in_option, null)
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val myPopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        view.findViewById<RelativeLayout>(R.id.relSerious).setOnClickListener {
            showChooseInterestedInSubPopup(it, 0)
        }

        view.findViewById<RelativeLayout>(R.id.relCasulaDating).setOnClickListener {
            showChooseInterestedInSubPopup(it, 1)
        }

        view.findViewById<RelativeLayout>(R.id.relRommate).setOnClickListener {
            showChooseInterestedInSubPopup(it, 3)
        }

        view.findViewById<RelativeLayout>(R.id.relNewFrnd).setOnClickListener {
            showChooseInterestedInSubPopup(it, 2)
        }

        view.findViewById<RelativeLayout>(R.id.relBusiContact).setOnClickListener {
            showChooseInterestedInSubPopup(it, 4)
        }

        myPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    @SuppressLint("MissingInflatedId")
    fun showChooseInterestedInSubPopup(v: View, pos: Int) {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_intersted_in_sub_option, null)
        val myPopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        if (pos == 0) {
            if (listOfInterestedIn.contains(1)) {
                view.findViewById<RelativeLayout>(R.id.relMale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(2)) {
                view.findViewById<RelativeLayout>(R.id.relFemale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(3)) {
                view.findViewById<RelativeLayout>(R.id.relBoth).visibility = View.GONE
            }
        } else if (pos == 1) {
            if (listOfInterestedIn.contains(4)) {
                view.findViewById<RelativeLayout>(R.id.relMale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(5)) {
                view.findViewById<RelativeLayout>(R.id.relFemale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(6)) {
                view.findViewById<RelativeLayout>(R.id.relBoth).visibility = View.GONE
            }
        } else if (pos == 2) {
            if (listOfInterestedIn.contains(7)) {
                view.findViewById<RelativeLayout>(R.id.relMale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(8)) {
                view.findViewById<RelativeLayout>(R.id.relFemale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(9)) {
                view.findViewById<RelativeLayout>(R.id.relBoth).visibility = View.GONE
            }
        } else if (pos == 3) {
            if (listOfInterestedIn.contains(10)) {
                view.findViewById<RelativeLayout>(R.id.relMale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(11)) {
                view.findViewById<RelativeLayout>(R.id.relFemale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(12)) {
                view.findViewById<RelativeLayout>(R.id.relBoth).visibility = View.GONE
            }
        } else if (pos == 4) {
            if (listOfInterestedIn.contains(13)) {
                view.findViewById<RelativeLayout>(R.id.relMale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(14)) {
                view.findViewById<RelativeLayout>(R.id.relFemale).visibility = View.GONE
            } else if (listOfInterestedIn.contains(15)) {
                view.findViewById<RelativeLayout>(R.id.relBoth).visibility = View.GONE
            }
        }

        view.findViewById<ImageView>(R.id.ivMale).setOnClickListener {
            if (pos == 0) {
                if (!listOfInterestedIn.contains(1) && !listOfInterestedIn.contains(2) && !listOfInterestedIn.contains(
                        3
                    )
                ) {
                    listOfInterestedIn.add(1)
                    setupUserLookingFor()
                }
            } else if (pos == 1) {
                if (!listOfInterestedIn.contains(4) && !listOfInterestedIn.contains(5) && !listOfInterestedIn.contains(
                        6
                    )
                ) {
                    listOfInterestedIn.add(4)
                    setupUserLookingFor()
                }
            } else if (pos == 2) {
                if (!listOfInterestedIn.contains(7) && !listOfInterestedIn.contains(8) && !listOfInterestedIn.contains(
                        9
                    )
                ) {
                    listOfInterestedIn.add(7)
                    setupUserLookingFor()
                }
            } else if (pos == 3) {
                if (!listOfInterestedIn.contains(10) && !listOfInterestedIn.contains(11) && !listOfInterestedIn.contains(
                        12
                    )
                ) {
                    listOfInterestedIn.add(10)
                    setupUserLookingFor()
                }
            } else if (pos == 4) {
                if (!listOfInterestedIn.contains(13) && !listOfInterestedIn.contains(14) && !listOfInterestedIn.contains(
                        15
                    )
                ) {
                    listOfInterestedIn.add(13)
                    setupUserLookingFor()
                }
            }
            myPopupWindow.dismiss()

        }


        view.findViewById<ImageView>(R.id.ivFeMale).setOnClickListener {
            if (pos == 0) {
                if (!listOfInterestedIn.contains(1) && !listOfInterestedIn.contains(2) && !listOfInterestedIn.contains(
                        3
                    )
                ) {
                    listOfInterestedIn.add(2)
                    setupUserLookingFor()
                }
            } else if (pos == 1) {
                if (!listOfInterestedIn.contains(4) && !listOfInterestedIn.contains(5) && !listOfInterestedIn.contains(
                        6
                    )
                ) {
                    listOfInterestedIn.add(5)
                    setupUserLookingFor()
                }
            } else if (pos == 2) {
                if (!listOfInterestedIn.contains(7) && !listOfInterestedIn.contains(8) && !listOfInterestedIn.contains(
                        9
                    )
                ) {
                    listOfInterestedIn.add(8)
                    setupUserLookingFor()
                }
            } else if (pos == 3) {
                if (!listOfInterestedIn.contains(10) && !listOfInterestedIn.contains(11) && !listOfInterestedIn.contains(
                        12
                    )
                ) {
                    listOfInterestedIn.add(11)
                    setupUserLookingFor()
                }
            } else if (pos == 4) {
                if (!listOfInterestedIn.contains(13) && !listOfInterestedIn.contains(14) && !listOfInterestedIn.contains(
                        15
                    )
                ) {
                    listOfInterestedIn.add(14)
                    setupUserLookingFor()
                }
            }
            myPopupWindow.dismiss()
        }

        view.findViewById<ImageView>(R.id.ivBoth).setOnClickListener {
            if (pos == 0) {
                if (!listOfInterestedIn.contains(1) && !listOfInterestedIn.contains(2) && !listOfInterestedIn.contains(
                        3
                    )
                ) {
                    listOfInterestedIn.add(3)
                    setupUserLookingFor()
                }
            } else if (pos == 1) {
                if (!listOfInterestedIn.contains(4) && !listOfInterestedIn.contains(5) && !listOfInterestedIn.contains(
                        6
                    )
                ) {
                    listOfInterestedIn.add(6)
                    setupUserLookingFor()
                }
            } else if (pos == 2) {
                if (!listOfInterestedIn.contains(7) && !listOfInterestedIn.contains(8) && !listOfInterestedIn.contains(
                        9
                    )
                ) {
                    listOfInterestedIn.add(9)
                    setupUserLookingFor()
                }
            } else if (pos == 3) {
                if (!listOfInterestedIn.contains(10) && !listOfInterestedIn.contains(11) && !listOfInterestedIn.contains(
                        12
                    )
                ) {
                    listOfInterestedIn.add(12)
                    setupUserLookingFor()
                }
            } else if (pos == 4) {
                if (!listOfInterestedIn.contains(13) && !listOfInterestedIn.contains(14) && !listOfInterestedIn.contains(
                        15
                    )
                ) {
                    listOfInterestedIn.add(15)
                    setupUserLookingFor()
                }
            }
            myPopupWindow.dismiss()
        }


        myPopupWindow.showAsDropDown(v, -153, 0)
    }

    fun showChooseImageSectionPopupNew(v: View) {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_image_option, null)
        view.findViewById<TextView>(R.id.header_title).text =
            resources.getString(R.string.select_profile_image)

        val myPopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val llPublic = view.findViewById<View>(R.id.ll_public) as LinearLayoutCompat
        val llPrivate = view.findViewById<View>(R.id.ll_private) as LinearLayoutCompat

        llPublic.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent(requireActivity(), CameraActivity::class.java)
            } else {
                Intent(requireActivity(), ImagePickerActivity::class.java)
            }
            intent.putExtra("video_duration_limit", 60)
            intent.putExtra("withCrop", false)
            photosLauncher.launch(intent)
            myPopupWindow.dismiss()
        }

        llPrivate.setOnClickListener {
            galleryImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
            )
            myPopupWindow.dismiss()
        }

        myPopupWindow.showAsDropDown(v, -153, 0)
    }

    protected fun prefillEditProfile(user: User) {
        user_assign = user
        avtarindex = user_assign!!.avatarPhotos!!.size
        Log.e(TAG, "${user.avatarPhotos}")
        val photosNew = user.avatarPhotos?.map {
            PhotosData(
                it.url?.replace(
                    "${BuildConfig.BASE_URL_REP}media/", "${BuildConfig.BASE_URL}media/"
                ) ?: "", it.type
            )
        }
        photosNew?.let {
            photosnewAdapter.updateList(it)
        }
        user.gender?.let {
            binding?.genderPicker
        }
        user.music?.let {
            binding?.music?.setInterests(it)
        }
        user.movies?.let {
            binding?.movies?.setInterests(it)
        }
        user.tvShows?.let {
            binding?.tvShows?.setInterests(it)
        }
        user.sportsTeams?.let {
            binding?.sportTeams?.setInterests(it)
        }

        photosnewAdapter.avtar_index = user.avatarIndex!!
        photosnewAdapter.notifyDataSetChanged()
    }

    protected fun isProfileValid(isLogin: Boolean = false): Boolean {
        if (photosnewAdapter.photos.isNullOrEmpty()) {
            binding?.root?.snackbar(getString(R.string.photo_error))
            return false
        }
        if (isLogin && photosnewAdapter.photos.size > 3) {
            binding?.root?.snackbar(getString(R.string.max_photo_login_error))
            return false
        }
        if (binding?.editProfileName?.text.isNullOrEmpty()) {
            binding?.root?.snackbar(getString(R.string.name_cannot_be_empty))
            return false
        }
        if (binding?.genderPicker?.selectedItemPosition == 3) {
            binding?.root?.snackbar(getString(R.string.gender_cannot_be_empty))
            return false
        }
        if (binding?.agePicker?.selectedItemPosition == -1) {
            binding?.root?.snackbar(getString(R.string.age_cannot_be_empty))
            return false
        }
        if (binding?.heightsPicker?.selectedItemPosition == -1) {
            binding?.root?.snackbar(getString(R.string.height_cannot_be_empty))
            return false
        }
        return true
    }

    protected fun getViewModelUser(
        user: User, login: Boolean = false, increment: Boolean = false
    ): User {
        val photos = ArrayList<Photo>()
        photosnewAdapter.photos.forEach { photo ->
            photos.add(Photo(id = "1", url = photo.link, type = photo.type))
        }
        if (login) user.purchaseCoins = 50
        if (increment) user.photosQuota = user.photosQuota + 1
        user.avatarPhotos = photos
        return user
    }

    protected fun getApiUser(user: User): User {
        try {
            var age: Int = user.age?.minus(1) ?: 0
            if (age < 0) {
                age = 0
            }
            Log.e(TAG,"UserAge: $age")
            Log.e(TAG,"DefaultPicker: ${defaultPicker!!.agePicker[age].id}")

            var someval = binding!!.agePicker.selectedItemPosition
            Log.e(TAG,"someval: ${someval}")

            if(someval == 0){
                user.age = 1
            } else {
                if (someval == (age)) {
                    user.age?.let {
                        user.age = defaultPicker!!.agePicker[it].id - 1
                    }
                } else {
                    user.age?.let {
                        user.age = defaultPicker!!.agePicker[it].id
                    }
                }
            }
            user.ageValue = defaultPicker!!.agePicker[age].value
            Log.e(TAG,"UserAge: ${user.age}")
            Log.e(TAG,"UserAgeValue: ${user.ageValue}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.avatarIndex?.let {
                user.avatarIndex = photosnewAdapter.avtar_index
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.ethnicity?.let {
                user.ethnicity = defaultPicker!!.ethnicityPicker[it].id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.familyPlans?.let {
                user.familyPlans = defaultPicker!!.familyPicker[it].id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if ((binding!!.heightsPicker.selectedItemPosition) == (user.height?.minus(1))) {
                user.height?.let {
                    user.height = defaultPicker!!.heightsPicker[it].id - 1
                }
            } else {
                user.height?.let {
                    user.height = defaultPicker!!.heightsPicker[it].id
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            user.gender?.let {
                user.gender = ((binding?.genderPicker?.selectedItemPosition) ?: 0) + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.politics?.let {
                user.politics = defaultPicker!!.politicsPicker[it].id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.religion?.let {
                user.religion = defaultPicker!!.religiousPicker[it].id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            user.zodiacSign?.let {
                user.zodiacSign = defaultPicker!!.zodiacSignPicker[it].id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        user.country.let {
            user.country = ""
        }
        user.city.let {
            user.city = ""
        }
        user.state.let {
            user.state = ""
        }
        return user
    }

    fun setupUserLookingFor() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (listOfInterestedIn != null && listOfInterestedIn.size > 0) {
                val value = getLookingForNameFromId(listOfInterestedIn.last())
                val split = value.split(":")
                binding?.userLookingForChipsCloudLabel?.addChip(
                    IdWithValue(
                        listOfInterestedIn.last(), value, value
                    ), false
                )
            }

        }
    }

    fun setupUserLookingFor(user: User?) {
        binding?.addInterest?.visibility = View.VISIBLE
        if (user?.interestedIn?.isNotEmpty() == true) {

            listOfInterestedIn.clear()
            if (user.interestedIn != null) listOfInterestedIn.addAll(user.interestedIn)
            lifecycleScope.launch(Dispatchers.Main) {
                listOfInterestedIn.forEach {
                    val value = getLookingForNameFromId(it)
                    val split = value.split(":")
                    binding?.userLookingForChipsCloudLabel?.addChip(
                        IdWithValue(it, value, value), false
                    )
                }

            }
        } else binding?.addInterest?.visibility = View.GONE
    }

    private fun getLookingForNameFromId(id: Int): String {
        when (id) {
            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }

            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
            }

            InterestedInGender.SERIOUS_RELATIONSHIP_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
            }

            InterestedInGender.CAUSAL_DATING_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }

            InterestedInGender.CAUSAL_DATING_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
            }

            InterestedInGender.CAUSAL_DATING_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
            }

            InterestedInGender.NEW_FRIENDS_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }

            InterestedInGender.NEW_FRIENDS_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
            }

            InterestedInGender.NEW_FRIENDS_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
            }

            InterestedInGender.ROOM_MATES_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }

            InterestedInGender.ROOM_MATES_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
            }

            InterestedInGender.ROOM_MATES_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
            }

            InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }

            InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
            }

            InterestedInGender.BUSINESS_CONTACTS_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
            }
        }
        return ""
    }

}