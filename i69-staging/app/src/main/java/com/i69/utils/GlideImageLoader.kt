package com.i69.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.i69.R
import com.paypal.pyplcheckout.ui.feature.sca.runOnUiThread
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GlideImageLoader {

    suspend fun loadImageWithProgress(
        context: Context,
        imageUrl: String,
        progressBar: ProgressBar,
        requestOptions: RequestOptions? = null,  // Added RequestOptions
        onBitmapLoaded: (Bitmap?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            progressBar.visible()
            val bitmap = withContext(Dispatchers.IO) {
                loadBitmapWithGlide(context, imageUrl, progressBar, requestOptions)
            }

            onBitmapLoaded(bitmap)
        } catch (e: Exception) {
            progressBar.gone()
            onError(e)
        } finally {
            progressBar.gone()
        }
    }

    private suspend fun loadBitmapWithGlide(
        context: Context,
        imageUrl: String,
        progressBar: ProgressBar,
        requestOptions: RequestOptions?
    ): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            val target = object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (continuation.isActive) {
                        continuation.resume(resource)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle clearing of the target if necessary
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(Exception("Failed to load image"))
                    }
                }
            }

            val glideRequest = Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .listener(object : RequestListener<Bitmap> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception("Failed to load image"))
                        }
                        return false // Pass the error down the chain
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.gone() // Hide progress bar when loading is done
                        return false // Pass the resource down the chain
                    }
                })
            if (requestOptions != null) {
                glideRequest.apply(requestOptions)
            }
            glideRequest.into(target)

            continuation.invokeOnCancellation {
                // Properly clear the Glide request if the coroutine is cancelled
                Glide.with(context).clear(target)
            }
        }
    }

    fun ProgressBar.visible() {
        runOnUiThread {
            this.visibility = View.VISIBLE
        }
    }

    fun ProgressBar.gone() {
        runOnUiThread {
            this.visibility = View.GONE
        }
    }
}