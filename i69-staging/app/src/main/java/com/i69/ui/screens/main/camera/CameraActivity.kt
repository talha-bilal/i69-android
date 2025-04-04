package com.i69.ui.screens.main.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.VideoResizer
import com.i69.R
import com.i69.databinding.ActivityCameraBinding
import com.i69.utils.createLoadingDialog
import com.i69.utils.setVisibleOrInvisible
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.controls.Preview
import com.otaliastudios.cameraview.controls.WhiteBalance
import com.otaliastudios.cameraview.filter.Filters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    enum class ZoomLevel(val zoomValue: Float) {
        ZOOM_1X(0.0f),    // 0.5x zoom
        ZOOM_1_2X(0.2f),    // 0.5x zoom
        ZOOM_1_5X(0.5f),    // 0.5x zoom
        ZOOM_1_7X(0.75f),  // 0.0x zoom (no zoom)
        ZOOM_2X(2.0f);    // 1x full zoom
    }

    private var videoFileName = ""
    private lateinit var outputDirectory: File
    private lateinit var binding: ActivityCameraBinding
    private var loadingDialog: Dialog? = null

    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
    private var alertDialog: AlertDialog? = null
    private lateinit var cameraVM: CameraVM
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    private var currentFilter = 0
    private val allFilters = Filters.values()

    private lateinit var flashOverlay: View
    private var originalBrightness: Float = 0.5f  // Default brightness
    private var isFlashOn = true


    override fun onCreate(savedInstanceState: Bundle?) {
        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
//        supportActionBar?.hide()
        setContentView(binding.root)

        originalBrightness = getCurrentScreenBrightness()

        flashOverlay = View(this).apply {
            setBackgroundColor(Color.WHITE)
            visibility = View.GONE  // Initially hidden
        }

        // Add the overlay to the root view of the activity
        addContentView(
            flashOverlay, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        cameraVM = ViewModelProvider(this)[CameraVM::class.java]
        loadingDialog = createLoadingDialog()
        outputDirectory = getOutputDirectory()
        setUpRecyclerView()
        clickListeners()
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.addCameraListener(Listener())
//        binding.cameraNightModeBtn.paintFlags =
//            binding.cameraNightModeBtn.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        // Set zoom to 1x (no zoom)
        adjustZoom(ZoomLevel.ZOOM_1X.zoomValue)
        // Initialize the launcher for multiple permissions
        requestMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach { (permission, isGranted) ->
                    if (isGranted) {
                        // Permission granted
                        onPermissionGranted(permission)
                    } else {
                        // Permission denied
                        onPermissionDenied(permission)
                    }
                }
            }

        requestPermissions()
    }

    private fun setUpRecyclerView() {
        binding.filterList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.filterList.setHasFixedSize(true)

        imagePreviewAdapter = ImagePreviewAdapter(this@CameraActivity) { position ->
            val filter = allFilters[position]
            binding.cameraView.filter = filter.newInstance()
        }
        imagePreviewAdapter.refreshItems(allFilters)
        binding.filterList.adapter = imagePreviewAdapter
    }


    private fun clickListeners() {
        binding.ibCapture.setOnClickListener {
            if (binding.cameraView.isTakingPicture || binding.cameraView.isTakingVideo) {
                return@setOnClickListener
            }
            if (binding.cameraView.mode == Mode.VIDEO) {
                binding.cameraView.mode = Mode.PICTURE
            }

            if (binding.cameraView.facing == Facing.FRONT) {
                if (isFlashOn) {
                    simulateScreenFlash()
                }
            }

            binding.cameraView.takePictureSnapshot()
        }

        binding.changeFilter.setOnClickListener {
            if (binding.cameraView.preview != Preview.GL_SURFACE) {
                Toast.makeText(
                    this@CameraActivity,
                    "Filters are supported only when preview is Preview.GL_SURFACE.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (currentFilter < allFilters.size - 1) {
                currentFilter++
            } else {
                currentFilter = 0
            }
            val filter = allFilters[currentFilter]
            Toast.makeText(this@CameraActivity, filter.toString(), Toast.LENGTH_SHORT).show()
            // Normal behavior:
            binding.cameraView.filter = filter.newInstance()
        }
        binding.cameraNightModeBtn.setOnClickListener {
            if (binding.cameraView.whiteBalance == WhiteBalance.FLUORESCENT) {
                setNightMode(false)
            } else {
                setNightMode(true)
            }
        }
        binding.cameraFlashBtn.setOnClickListener {
            if (binding.cameraView.facing == Facing.BACK) {
                setCameraFlash(binding.cameraView.flash)
            } else {
                if (isFlashOn) {
                    isFlashOn = false
                    binding.cameraView.flash = Flash.OFF
                    binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_off_black_24dp)
                } else {
                    isFlashOn = true
                    binding.cameraView.flash = Flash.ON
                    binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_on_black_24dp)
                }
            }

        }
        binding.onePointZeroTV.setOnClickListener {
            adjustZoom(ZoomLevel.ZOOM_1X.zoomValue)
        }
        binding.onePointTwoTV.setOnClickListener {
            adjustZoom(ZoomLevel.ZOOM_1_2X.zoomValue)
        }
        binding.onePointFiveTV.setOnClickListener {
            adjustZoom(ZoomLevel.ZOOM_1_5X.zoomValue)
        }
        binding.onePointSevenTV.setOnClickListener {
            adjustZoom(ZoomLevel.ZOOM_1_7X.zoomValue)
        }
        binding.twoZoomTV.setOnClickListener {
            adjustZoom(ZoomLevel.ZOOM_2X.zoomValue)
        }

        binding.ivSwitchCamera.setOnClickListener {
            if (binding.cameraView.isTakingPicture || binding.cameraView.isTakingVideo) {
                return@setOnClickListener
            }
            binding.cameraView.toggleFacing()
        }

        binding.ivGallery.setOnClickListener {
            galleryImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
            )
        }

        binding.ivRecord.setOnClickListener {
            if (binding.cameraView.mode == Mode.PICTURE) {
                binding.cameraView.mode = Mode.VIDEO
            }
            if (binding.cameraView.isTakingVideo) {
                binding.cameraView.stopVideo()
            } else {
                binding.ivRecord.setImageResource(R.drawable.stop)
                binding.ibCapture.setVisibleOrInvisible(false)
                binding.changeFilter.setVisibleOrInvisible(false)
                binding.ivGallery.setVisibleOrInvisible(false)
                binding.ivSwitchCamera.setVisibleOrInvisible(false)

                videoFileName = System.currentTimeMillis().toString()
                val videoFile = File(outputDirectory, "$videoFileName.mp4")
                binding.cameraView.takeVideoSnapshot(videoFile, 60000)
            }
        }

        binding.ibClose.setOnClickListener { finish() }
    }


    private fun getCurrentScreenBrightness(): Float {
        return try {
            val brightness =
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            brightness / 255f  // Convert to a value between 0 and 1
        } catch (e: Settings.SettingNotFoundException) {
            0.5f  // Default brightness
        }
    }

    private fun setScreenBrightness(brightness: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
    }

    private fun simulateScreenFlash() {
        // Set screen brightness to maximum
        setScreenBrightness(1.0f)

        // Show the overlay
        flashOverlay.visibility = View.VISIBLE

        // Hide the overlay after a short delay and restore brightness
        Handler(Looper.getMainLooper()).postDelayed({
            flashOverlay.visibility = View.GONE
            // Restore original brightness
            setScreenBrightness(originalBrightness)
        }, 1000)  // Adjust the delay for the flash effect
    }

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            if (activityResult.resultCode == Activity.RESULT_OK) {

                val result = data?.data?.path
                val openInputStream = contentResolver?.openInputStream(data?.data!!)
                val type = if (result?.contains("video") == true) ".mp4" else ".jpg"
                val outputFile = filesDir.resolve("${System.currentTimeMillis()}$type")
                openInputStream?.copyTo(outputFile.outputStream())

                val intent = Intent()
                intent.putExtra("result", outputFile.path)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    fun showProgressView() {
        runOnUiThread {
            loadingDialog?.show()
        }
    }

    fun hideProgressView() {
        runOnUiThread {
            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }
        }
    }

    private inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
        }

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this@CameraActivity, "Video is taking", Toast.LENGTH_SHORT).show()
                return
            }

            val fileName = System.currentTimeMillis().toString()
            val photoFile = File(outputDirectory, "$fileName.jpg")
            val fileOutputStream = FileOutputStream(photoFile)
            fileOutputStream.write(result.data)
            fileOutputStream.flush()
            fileOutputStream.close()

            val intent = Intent()
            intent.putExtra("result", photoFile.path)
            intent.putExtra("fileName", fileName)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            showProgressView()
            val path = result.file.path
            hideProgressView()
            val intent = Intent()
            intent.putExtra("result", path)
            intent.putExtra("fileName", videoFileName)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            binding.ivRecord.setImageResource(R.drawable.record)
            binding.ibCapture.setVisibleOrInvisible(true)
        }

        override fun onExposureCorrectionChanged(
            newValue: Float, bounds: FloatArray, fingers: Array<PointF>?
        ) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
        }
    }


    private fun compressVideo(
        uriList: List<Uri>, onSuccess: (String) -> Unit, onFailure: (String) -> Unit
    ) {
        lifecycleScope.launch {
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
                        showProgressView()
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        hideProgressView()
                        onSuccess(path!!)
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        hideProgressView()
                        onFailure(failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        hideProgressView()
                    }
                })
        }
    }

    private fun setCameraFlash(flash: Flash) {
        when (flash) {
            Flash.AUTO -> {
                isFlashOn = true
                binding.cameraView.flash = Flash.ON
                binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_on_black_24dp)
            }

            Flash.ON -> {
                isFlashOn = true
                binding.cameraView.flash = Flash.OFF
                binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_off_black_24dp)
            }

//            Flash.TORCH -> {
//                binding.cameraView.flash = Flash.OFF
//                binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_off_black_24dp)
//            }

            Flash.OFF -> {
                isFlashOn = false
                binding.cameraView.flash = Flash.AUTO
                binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_auto_black_24dp)
            }

            else -> {
                isFlashOn = true
                binding.cameraView.flash = Flash.AUTO
                binding.cameraFlashBtn.setImageResource(R.drawable.ic_flash_auto_black_24dp)
            }
        }
    }


    private fun setNightMode(isNightMode: Boolean) {
        if (isNightMode) {
            binding.cameraView.whiteBalance = WhiteBalance.FLUORESCENT
            binding.cameraView.exposureCorrection = -1.0f
            binding.cameraNightModeBtn.text = "Night Mode"
        } else {
            binding.cameraView.whiteBalance = WhiteBalance.AUTO
            binding.cameraView.exposureCorrection = 0.0f
            binding.cameraNightModeBtn.text = "Day Mode"
        }
    }

    // Function to adjust the zoom programmatically
    private fun adjustZoom(zoomLevel: Float) {
        binding.cameraView.zoom = zoomLevel
        resetZoomTextResources(zoomLevel)
    }

    private fun resetZoomTextResources(zoomLevel: Float) {
        binding.onePointZeroTV.setTextColor(
            ContextCompat.getColor(
                this@CameraActivity, R.color.white
            )
        )
        binding.onePointTwoTV.setTextColor(
            ContextCompat.getColor(
                this@CameraActivity, R.color.white
            )
        )
        binding.onePointFiveTV.setTextColor(
            ContextCompat.getColor(
                this@CameraActivity, R.color.white
            )
        )
        binding.onePointSevenTV.setTextColor(
            ContextCompat.getColor(
                this@CameraActivity, R.color.white
            )
        )
        binding.twoZoomTV.setTextColor(
            ContextCompat.getColor(
                this@CameraActivity, R.color.white
            )
        )


        binding.onePointZeroTV.text = "1.0"
        binding.onePointTwoTV.text = "1.2"
        binding.onePointFiveTV.text = "1.5"
        binding.onePointSevenTV.text = "1.7"
        binding.twoZoomTV.text = "2.0"

        binding.onePointZeroTV.background = null
        binding.onePointTwoTV.background = null
        binding.onePointFiveTV.background = null
        binding.onePointSevenTV.background = null
        binding.twoZoomTV.background = null

        when (zoomLevel) {
            ZoomLevel.ZOOM_1X.zoomValue -> {
                binding.onePointZeroTV.setTextColor(
                    ContextCompat.getColor(
                        this@CameraActivity, R.color.black
                    )
                )
                binding.onePointZeroTV.setBackgroundResource(R.drawable.circle_filled)
                binding.onePointZeroTV.text = "1.0"
            }

            ZoomLevel.ZOOM_1_2X.zoomValue -> {
                binding.onePointTwoTV.setTextColor(
                    ContextCompat.getColor(
                        this@CameraActivity, R.color.black
                    )
                )
                binding.onePointTwoTV.setBackgroundResource(R.drawable.circle_filled)
                binding.onePointTwoTV.text = "1.2"
            }

            ZoomLevel.ZOOM_1_5X.zoomValue -> {
                binding.onePointFiveTV.setTextColor(
                    ContextCompat.getColor(
                        this@CameraActivity, R.color.black
                    )
                )
                binding.onePointFiveTV.setBackgroundResource(R.drawable.circle_filled)
                binding.onePointFiveTV.text = "1.5"
            }

            ZoomLevel.ZOOM_1_7X.zoomValue -> {
                binding.onePointSevenTV.setTextColor(
                    ContextCompat.getColor(
                        this@CameraActivity, R.color.black
                    )
                )
                binding.onePointSevenTV.setBackgroundResource(R.drawable.circle_filled)
                binding.onePointSevenTV.text = "1.7"
            }

            ZoomLevel.ZOOM_2X.zoomValue -> {
                binding.twoZoomTV.setTextColor(
                    ContextCompat.getColor(
                        this@CameraActivity, R.color.black
                    )
                )
                binding.twoZoomTV.setBackgroundResource(R.drawable.circle_filled)
                binding.twoZoomTV.text = "2.0"
            }
        }
    }

    /**
     * Permission section started
     */

    private val permissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_VIDEO)
        }
    }.toTypedArray()


    private fun requestPermissions() {
        when {
            arePermissionsGranted() -> {
                // All required permissions are granted, proceed with the action
                onAllPermissionsGranted()
            }

            shouldShowRationale() -> {
                // Show rationale and then request permissions
                showPermissionRationaleDialog()
            }

            else -> {
                // Directly request the permissions
                requestMultiplePermissionsLauncher.launch(permissions)
            }
        }
    }

    private fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                // Handle camera permission granted
                onAllPermissionsGranted()
            }

            Manifest.permission.RECORD_AUDIO -> {
                // Handle audio recording permission granted
            }

            Manifest.permission.READ_MEDIA_VIDEO -> {
                // Handle video media read permission granted (for Android 13+)
            }
        }
    }

    private fun onPermissionDenied(permission: String) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // "Don't ask again" selected
            showSettingsDialog(permission)
        } else {
            // Permission denied temporarily
            showPermissionDeniedDialog(permission)
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val permissionsToCheck = permissions.map {
            ContextCompat.checkSelfPermission(this, it)
        }
        return permissionsToCheck.all { it == PackageManager.PERMISSION_GRANTED }
    }

    private fun shouldShowRationale(): Boolean {
        return permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }
    }

    private fun onAllPermissionsGranted() {
        binding.cameraView.open()
    }

    private fun showPermissionRationaleDialog() {
        if (alertDialog != null && alertDialog?.isShowing == true) {
            return
        }
        alertDialog = AlertDialog.Builder(this).setTitle("Permissions Required")
            .setMessage("Camera, Audio Recording, and Storage permissions are required to proceed. Please grant these permissions.")
            .setPositiveButton("OK") { _, _ ->
                requestMultiplePermissionsLauncher.launch(permissions)
            }.setNegativeButton("Cancel") { _, _ ->
                finish()
            }.setCancelable(false).create()

        if (alertDialog?.isShowing == false) {
            alertDialog?.show()
        }
    }

    private fun showPermissionDeniedDialog(permission: String) {
        val alertDialog = AlertDialog.Builder(this).setTitle("Permission Denied")
            .setMessage("The permission for $permission is required to proceed. Please allow it.")
            .setPositiveButton("Try Again") { _, _ ->
                requestMultiplePermissionsLauncher.launch(arrayOf(permission))
            }.setNegativeButton("Cancel") { _, _ ->
                finish()
            }.setCancelable(false).create()
        alertDialog.show()
    }

    private fun showSettingsDialog(permission: String) {
        val alertDialog = AlertDialog.Builder(this).setTitle("Permission Denied")
            .setMessage("You have denied the $permission permission and selected 'Don't ask again'. Please go to settings and allow it.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }.setNegativeButton("Cancel") { _, _ ->
                finish()
            }.setCancelable(false).create()
        alertDialog.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
        finish()
    }


    /**
     * Permission section ended
     */
}