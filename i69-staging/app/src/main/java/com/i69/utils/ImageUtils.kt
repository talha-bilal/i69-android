package com.i69.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.i69.R
import java.io.File
import java.io.FileOutputStream

//Compressed Image quality param from 0 to 100
const val COMPRESS_IMAGE_QUALITY = 100
private const val MAX_IMAGE_DIMENSION = 512

suspend fun Context.getBitmap(imageSrc: String) = withContext(Dispatchers.IO) {
    Log.e("TAG", "getBitmap: " + imageSrc)
    Glide.with(this@getBitmap).asBitmap().fitCenter().load(imageSrc)
        .submit(MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION).get()
}

fun ImageView.loadCircleImage(
    imageUrl: String,
    placeHolderType: Int = -1,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().circleCrop()/*.placeholder(R.drawable.ic_default_user)*/
        .error(R.drawable.ic_default_user)
    if (placeHolderType == 0 || placeHolderType == 1) loadCircleImageWithPlaceholder(
        imageUrl,
        requestOptions,
        callback,
        failure,
        placeHolderType
    )
    else loadImage(imageUrl, requestOptions, callback, failure)
}

fun ImageView.loadCircleBlurImage(
    imageUrl: String,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().circleCrop().placeholder(R.drawable.ic_default_user)
        .error(R.drawable.ic_default_user)
    loadBlurImage(imageUrl, requestOptions, callback, failure)
}


fun ImageView.loadCircleImage(
    imageUrl: Int,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().circleCrop()
    loadImage(imageUrl, requestOptions, callback, failure)
}

fun ImageView.loadImage(
    imageSrc: Any,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().fitCenter()/*.placeholder(R.drawable.ic_default_user)*/
        .error(R.drawable.ic_default_user)
    loadImage(imageSrc, requestOptions, callback, failure)
}

fun ImageView.loadImage(
    imageSrc: Any,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null,
    placeHolderType: Int = -1
) {
    val requestOptions = RequestOptions().fitCenter()/*.placeholder(R.drawable.ic_default_user)*/
        .error(R.drawable.ic_default_user)
    if (placeHolderType == 0 || placeHolderType == 1) loadImageWithPlaceholder(
        imageSrc,
        placeHolderType,
        requestOptions,
        callback,
        failure
    )
    else loadImage(imageSrc, requestOptions, callback, failure)
}

fun ImageView.loadImageWithPlaceHolder(
    imageSrc: Any,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().fitCenter().placeholder(R.drawable.ic_default_user)
        .error(R.drawable.ic_default_user)
    loadImage(imageSrc, requestOptions, callback, failure)
}

fun ImageView.loadBlurImage(
    imageSrc: Any,
    requestOptions: RequestOptions,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)?
) {
    try {
        Glide.with(this.context).load(imageSrc).apply(requestOptions)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failure?.invoke(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            }).into(this)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun ImageView.loadCircleImageWithPlaceholder(
    imageSrc: Any,
    requestOptions: RequestOptions,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)?,
    placeHolderType: Int
) {
    val placeholder = if (placeHolderType == 0) R.drawable.ic_default_user
    else R.drawable.img_placeholder
    try {
        Glide.with(this.context).load(imageSrc).thumbnail(0.5f).placeholder(placeholder)
            .apply(requestOptions).diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failure?.invoke(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            }).into(this)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun ImageView.loadImageWithPlaceholder(
    imageSrc: Any,
    placeHolderType: Int,
    requestOptions: RequestOptions,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)?
) {
    val placeholder = if (placeHolderType == 1) R.drawable.ic_default_user
    else R.drawable.img_placeholder_small
    try {
        Glide.with(this.context).load(imageSrc).thumbnail(0.5f).placeholder(placeholder)
            .apply(requestOptions).diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failure?.invoke(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            }).override(300, Target.SIZE_ORIGINAL).into(this)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImage(
    imageSrc: Any,
    requestOptions: RequestOptions,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)?
) {
    try {
        Glide.with(this.context).load(imageSrc).thumbnail(0.5f).apply(requestOptions)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failure?.invoke(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            }).override(context.getScreenWidth(), Target.SIZE_ORIGINAL).into(this)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadImage(
    context: Context,
    imageSrc: String,
    callback: ((Bitmap?) -> Unit)? = null,
    failure: ((Drawable?) -> Unit)?
) {
    try {
        val requestOptions = RequestOptions().placeholder(R.drawable.ic_default_user)
            .error(R.drawable.ic_default_user)
        Glide.with(context).asBitmap().apply(requestOptions)
            .diskCacheStrategy(DiskCacheStrategy.ALL).load(imageSrc)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback?.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    failure?.invoke(placeholder)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    failure?.invoke(errorDrawable)
                }
            })

    } catch (e: Exception) {
        e.printStackTrace()
        failure?.invoke(null)
    }
}

fun ImageView.loadImage_bysize(
    imageSrc: Any,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)? = null
) {
    val requestOptions = RequestOptions().override(50, 50).circleCrop()
    loadImage_byspecificsize(imageSrc, requestOptions, callback, failure)
}

fun ImageView.loadImage_byspecificsize(
    imageSrc: Any,
    requestOptions: RequestOptions,
    callback: ((Drawable?) -> Unit)? = null,
    failure: ((GlideException?) -> Unit)?
) {
    try {
        Glide.with(this.context).load(imageSrc).apply(requestOptions)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failure?.invoke(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            }).into(this)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun convertURITOBitmapNSaveImage(
    context: Context, uri: Uri, outPutFile: File? = null
): String? {
    return try {
        val type = if (uri.path!!.contains("video")) ".mp4" else ".jpg"
        val resultFile =
            outPutFile ?: context.filesDir.resolve("${System.currentTimeMillis()}$type")
        val imageFile = File(resultFile.toURI())

        val bitmap = uriToBitmap(context, uri)
        FileOutputStream(imageFile).use { fos ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, COMPRESS_IMAGE_QUALITY, fos)
            fos.flush()
            fos.close()
        }
        imageFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}