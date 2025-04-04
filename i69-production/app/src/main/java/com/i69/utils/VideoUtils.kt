package com.i69.utils

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.VideoResizer
import kotlinx.coroutines.launch


private fun compressVideo(
    lifecycleOwner: LifecycleOwner,
    applicationContext: Context,
    uriList: List<Uri>, onSuccess: (String) -> Unit, onFailure: (String) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        VideoCompressor.start(context = applicationContext,
            uriList,
            isStreamable = false,
            storageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies, subFolderName = "i69"
            ),
            configureWith = Configuration(
                videoNames = uriList.map { uri -> uri.pathSegments.last() },
                quality = VideoQuality.LOW,
                isMinBitrateCheckEnabled = false,
                resizer = VideoResizer.auto
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {

                }

                override fun onStart(index: Int) {
//                    showProgressView()
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
//                    hideProgressView()
                    onSuccess(path!!)
                }

                override fun onFailure(index: Int, failureMessage: String) {
//                    hideProgressView()
                    onFailure(failureMessage)
                }

                override fun onCancelled(index: Int) {
//                    hideProgressView()
                }
            })
    }
}