package com.i69.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.i69.data.models.Photo
import com.i69.ui.screens.main.search.userProfile.ImageFragment

class ImageSliderAdapter(
    fragmentManager: FragmentManager,
    private val movies: MutableList<Photo>?
) : FragmentStatePagerAdapter(fragmentManager) {
    private var isPrivateImagesFound = false
    private var privatePhotoRequestStatus = ""

    constructor(
        fragmentManager: FragmentManager,
        movies: MutableList<Photo>?,
        isPrivateImagesFound: Boolean,
        privatePhotoRequestStatus: String
    ) : this(fragmentManager, movies) {
        this.isPrivateImagesFound = isPrivateImagesFound
        this.privatePhotoRequestStatus = privatePhotoRequestStatus
    }

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(
            movies!![position],
            movies[position].type == "PRIVATE",
            privatePhotoRequestStatus
        )
    }

    override fun getCount(): Int {
        return movies!!.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return movies!![position].id.toString()
    }
}