package com.i69.ui.screens.main.search.userProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i69.data.config.Constants
import com.i69.data.models.Photo
import com.i69.databinding.ImagesliderFragmentBinding
import com.i69.ui.adapters.ImageSliderAdapter
import com.i69.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type

fun getImageSliderIntent(
    context: Context,
    datas: String,
    pos: Int,
    isPrivateImageFound: Boolean,
    privatePhotoRequestStatus: String,
    userToken: String?,
    otherUserId: String?,
    currentUserId: String?
) = Intent(context, ImageSliderActivity::class.java).apply {
    putExtra(Constants.EXTRA_IMG_SLIDER, datas)
    putExtra(Constants.SLIDER_POSITION, pos)
    putExtra(Constants.PRIVATE_IMAGES_FOUND, isPrivateImageFound)
    putExtra(Constants.PRIVATE_IMAGES_REQUEST_STATUS, privatePhotoRequestStatus)
    putExtra(Constants.MY_ID, userToken)
    putExtra(Constants.OTHER_ID, otherUserId)
    putExtra(Constants.CURRENT_USER_ID, currentUserId)
}


class ImageSliderActivity : BaseActivity<ImagesliderFragmentBinding>() {

    private var TAG: String = ImageSliderActivity::class.java.simpleName
    private lateinit var adapter: ImageSliderAdapter

    private var pos = 0

    private var images: ArrayList<Photo> = ArrayList()

    var myId = ""

    var currentUserID = ""

    var otherUserId = ""
    var currenUserDataID = ""

    private var avatarPhotos: MutableList<Photo>? = mutableListOf()

    override fun getActivityBinding(inflater: LayoutInflater) =
        ImagesliderFragmentBinding.inflate(inflater)

    override fun setupTheme(savedInstanceState: Bundle?) {
        val type: Type = object : TypeToken<MutableList<Photo?>?>() {}.type
        images = Gson().fromJson(intent.getStringExtra(Constants.EXTRA_IMG_SLIDER), type)
        Log.e(TAG,"Images : $images")
        pos = intent.getIntExtra(Constants.SLIDER_POSITION, 0)
        Log.e(TAG,"Position : $pos")
        val isPrivateImagesFound = intent.getBooleanExtra(Constants.PRIVATE_IMAGES_FOUND, false)
        Log.e(TAG,"PrivateImagesFound : $isPrivateImagesFound")
        val privatePhotoRequestStatus =
            intent.getStringExtra(Constants.PRIVATE_IMAGES_REQUEST_STATUS)
        myId = intent.getStringExtra(Constants.MY_ID).toString()
        otherUserId = intent.getStringExtra(Constants.OTHER_ID).toString()
        currenUserDataID = intent.getStringExtra(Constants.CURRENT_USER_ID).toString()

        currentUserID = runBlocking(Dispatchers.IO) {
            getCurrentUserId()!!
        }

        if (currentUserID == currenUserDataID) {
            images.map {
                it.type = "PUBLIC"
            }
        }

        adapter = ImageSliderAdapter(
            supportFragmentManager,
            images,
            isPrivateImagesFound,
            privatePhotoRequestStatus.toString()
        )
        binding.container.adapter = adapter

        binding.recyclerTabLayout.setupWithViewPager(binding.container, true)
        binding.container.currentItem = pos


        binding.toolbarHamburger.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun setupClickListeners() {
    }
}