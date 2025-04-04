package com.i69.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
//import androidx.navigation.Navigation.*
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.i69.R


class MyBottomNavigation : BottomNavigationView {

    private var navController: NavController? = null

    private var isImageLoaded = false
    private var TAG: String = MyBottomNavigation::class.java.simpleName


    constructor(context: Context) : super(context)


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )


    override fun getMaxItemCount(): Int {
        return /*super.getMaxItemCount()*/6
    }

    fun loadImage(
        imageUrl: String,
        @IdRes itemId: Int,
        @DrawableRes placeHolderResourceId: Int,
        @IdRes fragmentNavigationId: Int = 0
    ) {
        val navigationItemView = findViewById<BottomNavigationItemView>(itemId)

        for (index in 0 until (navigationItemView as ViewGroup).childCount) {
            val nextChild: View = (navigationItemView as ViewGroup).getChildAt(index)
            for (dd in 0 until (nextChild as ViewGroup).childCount) {
                val nextChild: View = nextChild.getChildAt(dd)
                if (nextChild is ImageView) {
                    Log.e(TAG, "ImageViewfind" + nextChild.toString())
                    loadProfileImage(
                        nextChild,
                        imageUrl,
                        placeHolderResourceId,
                        itemId
                    )
                }
            }

        }
//        val imageView = navigationItemView.children.find {
//            it is AppCompatImageView
//        } as? AppCompatImageView ?: return
//
//
//        loadProfileImage(imageView, imageUrl, placeHolderResourceId, itemId)

        //handleProfileBottomNavigationItemSelection(imageView, itemId, fragmentNavigationId)

    }


    private fun loadProfileImage(
        imageView: ImageView,
        imageUrl: String,
        @DrawableRes placeHolderResourceId: Int,
        @IdRes itemId: Int
    ) {
        Log.e("SET_IMAGE", imageUrl)

        Glide.with(this).load(imageUrl)
            .override(imageView.width, imageView.height) // Match the ImageView dimensions
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .optionalCircleCrop()
            //.placeholder(placeHolderResourceId)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("SET_IMAGE", e!!.message.toString())
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    isImageLoaded = true
                    Log.e("SET_IMAGE", "image loaded")
                    //imageView.setBackgroundColor(Color.YELLOW)
                    //handleImageItemSelected(imageView)
                    imageView.setBackgroundResource(R.drawable.shape_profile_image_border)
                    if (selectedItemId == itemId) {
                        imageView.clearColorFilter()
                        handleImageItemSelected(imageView)
                    }
                    return false
                }

            }).into(imageView)

    }


    fun setupWithNavController(navController: NavController) {

        this.navController = navController

        NavigationUI.setupWithNavController(this, navController)

    }


    private fun handleProfileBottomNavigationItemSelection(
        imageView: ImageView, itemId: Int, @IdRes fragmentNavigationId: Int
    ) {
        // navController= findNavController(imageView)

        setOnItemSelectedListener { menuItem ->

            if (menuItem.itemId == itemId) {
                handleImageItemSelected(imageView)
            } else {
                handleImageItemNotSelected(imageView)
            }

            true
        }
    }


    private fun handleImageItemNotSelected(imageView: ImageView) {
        if (isImageLoaded) {
            imageView.setBackgroundColor(Color.YELLOW)
            return
        }

        imageView.clearColorFilter()

    }

    private fun handleImageItemSelected(imageView: ImageView) {

        if (isImageLoaded) {/* val newHeight = 100 // New height in pixels
            val newWidth = 100 // New width in pixels
            imageView.requestLayout()
            imageView.getLayoutParams().height = newHeight
            imageView.getLayoutParams().width = newWidth
            imageView.setScaleType(ImageView.ScaleType.FIT_XY)*/
            imageView.setBackgroundResource(R.drawable.shape_profile_image_border)
            return
        }

        imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))

    }

    fun addBadge(number: Int) {
        val navigationItemView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val badge = navigationItemView?.getOrCreateBadge(R.id.nav_chat_graph)
        if (number > 0) {
            if (badge != null) {
                badge.isVisible = true
                badge.number = number
                //badge.badgeTextColor = R.color.white
                badge.backgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
            }
        } else {
            if (badge != null) {
                badge.isVisible = false
                badge.clearNumber()
            }
        }
    }

}