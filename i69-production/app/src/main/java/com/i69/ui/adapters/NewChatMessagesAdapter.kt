package com.i69.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.i69.BuildConfig
import com.i69.GetChatMessagesByRoomIdQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.ItemNewIncomingTextMessageBinding
import com.i69.databinding.ItemNewOutcomingTextMessageBinding
import com.i69.type.MessageMessageType
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.messenger.chat.chatList.ChatMessageListener
import com.i69.utils.LogUtil
import com.i69.utils.NetworkUtils
import com.i69.utils.TranslationUtils
import com.i69.utils.copyToClipboard
import com.i69.utils.formatDayDate
import com.i69.utils.isAudioFile
import com.i69.utils.isImageFile
import com.i69.utils.isVideoFile
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.paypal.pyplcheckout.ui.feature.sca.runOnUiThread
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class NewChatMessagesAdapter(
    private val context: Context,
    private val userId: String?,
    private val lifecycleOwner: LifecycleOwner,
    private val listener: ChatMessageListener
) : ListAdapter<GetChatMessagesByRoomIdQuery.Edge, RecyclerView.ViewHolder>(DiffUtilCallBack),
    CoroutineScope {

    private var job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private var TAG: String = NewChatMessagesAdapter::class.java.simpleName

    private val formatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val formatterNew = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    private val displayTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    private var lastMessageItem: GetChatMessagesByRoomIdQuery.Edge? = null
    var lastSeenMessageId = ""
    var otherUserAvtar = ""
    private var mCurrentPlaySpeed: Float = 1f
    var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1
    private var currentOutgoingHolder: OutgoingHolder? = null
    private var currentIncomingHolder: IncomingHolder? = null
    var isInComingAudioPlaying = false

    private val TYPE_NEW_INCOMING = 0
    private val TYPE_NEW_OUTGOING = 1
    var translationUtils: TranslationUtils? = null


    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        return if (item?.node?.userId?.id == userId) TYPE_NEW_OUTGOING else TYPE_NEW_INCOMING
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NEW_INCOMING) {
            val holder = holder as IncomingHolder
            holder.bind(getItem(position), position)
        } else {
            val holder = holder as OutgoingHolder
            holder.bind(getItem(position), position)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_NEW_INCOMING) {
            val inComingHolder = ItemNewIncomingTextMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            IncomingHolder(inComingHolder)
        } else {
            val outGoingHolder = ItemNewOutcomingTextMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            OutgoingHolder(outGoingHolder)
        }
    }

    fun updateList(newItems: ArrayList<GetChatMessagesByRoomIdQuery.Edge>) {
        val currentItems = currentList.toMutableList()
        currentItems.addAll(newItems)
        submitList(currentItems)
    }

    inner class OutgoingHolder(val viewBinding: ItemNewOutcomingTextMessageBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        private var updateSeekBarTask: Runnable? = null

        private fun loadVideoImage(imageUrl: String) {
            launch {
                // Show progress bar while loading the image
                viewBinding.progressBar.visibility = View.VISIBLE

                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(
                        imageUrl,
                        HashMap()
                    ) // Use a HashMap for headers if needed
                    val bitmap = retriever.getFrameAtTime(
                        0,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    ) // Extract the first frame
                    retriever.release()

                    bitmap?.let {
                        viewBinding.messageImage.setImageBitmap(it)
                    } ?: run {
                        // Set a placeholder image if the bitmap is null
                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle error and set a fallback image
                    viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                } finally {
                    // Hide progress bar once loading is complete
                    viewBinding.progressBar.visibility = View.GONE
                }
            }
        }

        private fun loadGlideImage(imageUrl: String) {
//            launch {
//                GlideImageLoader.loadImageWithProgress(context = context,
//                    imageUrl = imageUrl,
//                    progressBar = viewBinding.progressBar,
//                    onBitmapLoaded = { bitmap ->
//                        bitmap?.let {
//                            viewBinding.messageImage.setImageBitmap(bitmap)
//                        }
//                    },
//                    onError = {
//                        // Handle error, e.g., show a placeholder image
//                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
//                    })
//            }
            viewBinding.progressBar.visibility = View.VISIBLE

            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_default_user) // Fallback image on error
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.progressBar.visibility = View.GONE
                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                        return false // Return false to allow Glide to handle the error
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.progressBar.visibility = View.GONE
                        return false // Return false to let Glide handle setting the image
                    }
                })
                .into(viewBinding.messageImage) // Load image into ImageView
        }

        @SuppressLint("NewApi")
        fun bind(item: GetChatMessagesByRoomIdQuery.Edge?, position: Int) {
            val content = item?.node?.content
            val mainContent = item?.node
            if (content?.contains("media/chat_files") == true) {
                var fullUrl = content
                var message = ""
                if (fullUrl.contains(" ")) {
                    val link = fullUrl.substring(0, fullUrl.indexOf(" "))
                    val giftMessage = content.substring(fullUrl.indexOf(" ") + 1)
                    fullUrl = link
                    message = giftMessage
                } else {
                    if (content.startsWith("/media/chat_files/")) {
                        fullUrl = "${BuildConfig.BASE_URL}$content"
                    }
                }

                val uri = Uri.parse(fullUrl)
                val lastSegment = uri.lastPathSegment
                val ext = lastSegment?.substring(lastSegment.lastIndexOf(".") + 1)
                if (ext?.isImageFile() == true || ext?.isVideoFile() == true) {
                    viewBinding.audioMessageLayout.visibility = View.GONE
                    viewBinding.outGoingMessageLayout.visibility = View.VISIBLE
                    if (message.isNotEmpty()) {
                        viewBinding.messageText.text = message
                    } else {
                        viewBinding.messageText.text = ""
                    }
                    viewBinding.messageImage.setImageBitmap(null)
                    Log.e("ImageUrl outgoing --- > ", " " + fullUrl)

                    if (ext.isVideoFile()) {
                        loadVideoImage(fullUrl)
                        viewBinding.videoDurationTV.visibility = View.VISIBLE
                        lifecycleOwner.lifecycleScope.launch((Dispatchers.IO)) {
                            val durationTimeOfAudio = getVideoDurationFromUrl(fullUrl)
                            runOnUiThread {
                                viewBinding.videoDurationTV.text = String.format(
                                    "%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio),
                                    TimeUnit.MILLISECONDS.toSeconds(durationTimeOfAudio) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio)
                                    )
                                )
                            }
                        }
                    } else {
                        loadGlideImage(fullUrl)
                        viewBinding.videoDurationTV.visibility = View.GONE
                    }

                    viewBinding.messageImage.visibility = View.VISIBLE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.mapView.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility =
                        if (ext.isVideoFile()) View.VISIBLE else View.GONE
                } else if (ext?.isAudioFile() == true) {
                    viewBinding.audioMessageLayout.visibility = View.VISIBLE
                    viewBinding.outGoingMessageLayout.visibility = View.GONE
                    lifecycleOwner.lifecycleScope.launch((Dispatchers.IO)) {
                        val durationTimeOfAudio = getVideoDurationFromUrl(fullUrl)
                        runOnUiThread {
                            viewBinding.currentTimeTV.text = String.format(
                                "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio),
                                TimeUnit.MILLISECONDS.toSeconds(durationTimeOfAudio) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio)
                                )
                            )
                        }
                    }

                    var text = item.node.timestamp.toString()
                    val messageTime: Date
                    try {
                        messageTime = formatter.parse(text) as Date
                        val messageTimeToFormat = formatterNew.format(messageTime)
                        val messageTimeToCheck = formatterNew.parse(messageTimeToFormat) as Date
                        viewBinding.audioDurationTV.text = displayTime.format(messageTime)
                        val displayDate = formatDayDate(context, messageTime.time)
                        if (currentList.size > bindingAdapterPosition + 1) {
                            val preItem = currentList[bindingAdapterPosition + 1]
                            var preTime = preItem?.node?.timestamp.toString()
                            LogUtil.debug("Pre  : : : $preTime")
                            val preDateToCheck = formatter.parse(preTime)
                            val preDateToFormat = formatterNew.format(preDateToCheck)
                            val preDate = formatterNew.parse(preDateToFormat) as Date
                            LogUtil.debug("preDate  $preDate")
                            if (messageTimeToCheck == preDate) {
                                viewBinding.lblDate.text = ""
                                viewBinding.llDateLayout.visibility = View.GONE
                            } else {
                                viewBinding.llDateLayout.visibility = View.VISIBLE
                                viewBinding.lblDate.text = displayDate
                            }
                        } else {
                            viewBinding.llDateLayout.visibility = View.VISIBLE
                            viewBinding.lblDate.text = displayDate
                        }
                    } catch (e: Exception) {
                        viewBinding.audioDurationTV.text = e.message
                        viewBinding.llDateLayout.visibility = View.GONE
                    }

                    val avatarUrl = try {
                        val avatarPhoto =
                            item?.node?.userId?.avatarPhotos?.get(item.node.userId.avatarIndex)
                        Log.e(TAG,"AvatarPhoto: ${avatarPhoto.toString()}")
                        item?.node?.userId?.avatarPhotos?.get(item.node.userId.avatarIndex)?.url
                    } catch (e: Exception) {
                        ""
                    }

                    Log.e(TAG,"AvatarPhotos: ${item?.node?.userId?.avatarPhotos}")
                    if (avatarUrl != null) {
                        viewBinding.userAvatar.loadCircleImage(avatarUrl)
                    } else {
                        viewBinding.userAvatar.loadCircleImage(R.drawable.ic_chat_item_logo_new)
                    }



                    viewBinding.playAudioSpeed.setOnClickListener {
                        handlePlayAudioSpeed(viewBinding)
                        viewBinding.playAudio.setImageResource(R.drawable.pause)
                        if (currentPlayingPosition == position) {
                            mediaPlayer?.playbackParams =
                                mediaPlayer?.playbackParams?.setSpeed(mCurrentPlaySpeed)!!
                            mediaPlayer?.start()
                            viewBinding.playAudio.setImageResource(R.drawable.pause)

                        } else {
                            viewBinding.audioLoading.visibility = View.VISIBLE
                            viewBinding.playAudioSpeed.visibility = View.GONE
                            viewBinding.playAudio.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                playAudio(fullUrl, position)
                            }, 100)
                        }
                    }

                    viewBinding.playAudio.setOnClickListener {
                        if (currentPlayingPosition == position) {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                viewBinding.playAudio.setImageResource(R.drawable.play)
                            } else {
                                mediaPlayer?.start()
                                viewBinding.playAudio.setImageResource(R.drawable.pause)
                            }
                        } else {
                            viewBinding.audioLoading.visibility = View.VISIBLE
                            viewBinding.playAudioSpeed.visibility = View.GONE
                            viewBinding.playAudio.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                playAudio(fullUrl, position)
                            }, 100)
                        }
                    }
                } else {
                    viewBinding.audioMessageLayout.visibility = View.GONE
                    viewBinding.outGoingMessageLayout.visibility = View.VISIBLE
                    viewBinding.messageText.text = lastSegment
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageFileIcon.visibility = View.VISIBLE
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                    viewBinding.mapView.visibility = View.GONE
                }

                viewBinding.ctRequestview.visibility = View.GONE
            } else {
                viewBinding.progressBar.visibility = View.GONE
                viewBinding.videoDurationTV.visibility = View.GONE
                if (mainContent?.messageType == MessageMessageType.GL) {
                    viewBinding.outGoingMessageLayout.visibility = View.VISIBLE
                    viewBinding.audioMessageLayout.visibility = View.GONE
                    viewBinding.messageText.text = ""
                    viewBinding.mapView.visibility = View.VISIBLE
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                    viewBinding.ctRequestview.visibility = View.GONE
                    val items = content?.split(",")?.toTypedArray()
                    val latitude = items?.get(0)?.toDouble()
                    val longitude = items?.get(1)?.toDouble()
                    viewBinding.mapView.onCreate(null)
                    viewBinding.mapView.onResume()
                    viewBinding.mapView.getMapAsync(object : OnMapReadyCallback {
                        override fun onMapReady(googleMap: GoogleMap) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(latitude!!, longitude!!), 13f
                                )
                            )
                            googleMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(latitude, longitude)
                                )
                            )
                            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                            googleMap.uiSettings.setAllGesturesEnabled(false)
                        }
                    })
                } else if (mainContent?.messageType == MessageMessageType.P) {
                    viewBinding.audioMessageLayout.visibility = View.GONE
                    viewBinding.ctRequestview.visibility = View.VISIBLE
                    when (mainContent.requestStatus) {
                        "PENDING" -> {
                            viewBinding.cdView.visibility = View.GONE
                        }

                        "APPROVE" -> {
                            viewBinding.cdView.visibility = View.VISIBLE
                        }

                        else -> {
                            viewBinding.cdView.visibility = View.GONE
                        }
                    }
                    viewBinding.outGoingMessageLayout.visibility = View.VISIBLE
                    viewBinding.messageText.text = content
                    viewBinding.mapView.visibility = View.GONE
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                } else {
                    viewBinding.outGoingMessageLayout.visibility = View.VISIBLE
                    viewBinding.audioMessageLayout.visibility = View.GONE
                    viewBinding.ctRequestview.visibility = View.GONE
                    viewBinding.messageText.text = content
                    viewBinding.mapView.visibility = View.GONE
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                }
            }

            viewBinding.cdView.setOnClickListener {
                listener.onChatUserAvtarClick()
            }
            if (lastSeenMessageId == item?.node?.id) {
                if (otherUserAvtar.isNotEmpty()) {
                    viewBinding.ivSeenimage.loadCircleImage(otherUserAvtar)
                } else {
                    viewBinding.ivSeenimage.loadCircleImage(R.drawable.ic_chat_item_logo_new)
                }
                viewBinding.cdSeenimage.visibility = View.VISIBLE
            } else {
                viewBinding.cdSeenimage.visibility = View.GONE
            }

            val privatePhotoRequestId = item?.node?.privatePhotoRequestId ?: 0
            Log.e(TAG,"PhotoRequestId: $privatePhotoRequestId")

            val avatarUrl = try {
                item?.node?.userId?.avatarPhotos?.get(item.node.userId.avatarIndex)?.url
            } catch (e: Exception) {
                ""
            }
            if (avatarUrl != null) {
                viewBinding.messageUserAvatar.loadCircleImage(avatarUrl)
            } else {
                viewBinding.messageUserAvatar.loadCircleImage(R.drawable.ic_chat_item_logo_new)
            }
            var text = item?.node?.timestamp.toString()
            val messageTime: Date?
            try {
                messageTime = formatter.parse(text)
                val messageTimeToFormat = formatterNew.format(messageTime)
                val messageTimeToCheck = formatterNew.parse(messageTimeToFormat) as Date
                viewBinding.messageTime.text = displayTime.format(messageTime)
                val displayDate = formatDayDate(context, messageTime.time)
                if (currentList.size > bindingAdapterPosition + 1) {
                    val preItem = currentList[bindingAdapterPosition + 1]
                    var preTime = preItem?.node?.timestamp.toString()
                    LogUtil.debug("Pre  : : : $preTime")
                    val preDateToCheck = formatter.parse(preTime)
                    val preDateToFormat = formatterNew.format(preDateToCheck)
                    val preDate = formatterNew.parse(preDateToFormat) as Date
                    if (messageTimeToCheck == preDate) {
                        viewBinding.lblDate.text = ""
                        viewBinding.llDateLayout.visibility = View.GONE
                    } else {
                        viewBinding.llDateLayout.visibility = View.VISIBLE
                        viewBinding.lblDate.text = displayDate
                    }

                } else {
                    viewBinding.llDateLayout.visibility = View.VISIBLE
                    viewBinding.lblDate.text = displayDate
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewBinding.messageTime.text = e.message
            }
            viewBinding.root.setOnClickListener {
                if (!item!!.node!!.roomId.name.equals("") || !item.node!!.roomId.id.equals("")) {
                    listener.onChatMessageClick(bindingAdapterPosition, item)
                }
            }
            viewBinding.ivMenu.setOnClickListener {
                var powerMenu = PowerMenu.Builder(context)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setTextColor(context.resources.getColor(R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setShowBackground(false)
                    .setSelectedMenuColor(context.resources.getColor(R.color.white)).build()

                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.copy, false
                    )
                )

                if (mainContent?.messageType != MessageMessageType.G) {
                    powerMenu.addItem(
                        PowerMenuItem(
                            AppStringConstant1.delete, false
                        )
                    ) // add an item.
                }
                if (!viewBinding.translatedMessageText.text.toString().isNullOrEmpty()) {
                    powerMenu.addItem(
                        PowerMenuItem(
                            AppStringConstant1.copy_translated_message, false
                        )
                    )
                }

                powerMenu.showAsDropDown(
                    viewBinding.root,
                    viewBinding.root.measuredWidth / 2,
                    -viewBinding.root.measuredHeight / 2
                )
                powerMenu.setOnMenuItemClickListener { position, menuItem ->
                    when (position) {
                        0 -> {
                            powerMenu.dismiss()
                            copyToClipboard(viewBinding.messageText.text.toString(), context)
                            powerMenu.dismiss()
                        }

                        1 -> {
                            powerMenu.dismiss()
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                releaseMediaPlayer()
                                currentPlayingPosition = -1
                            }
                            listener.onChatMessageDelete(item)
                        }

                        2 -> {
                            powerMenu.dismiss()
                            copyToClipboard(
                                viewBinding.translatedMessageText.text.toString(), context
                            )
                        }
                    }
                }
            }

            viewBinding.root.setOnLongClickListener { v ->
                val powerMenu = PowerMenu.Builder(context)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setTextColor(context.resources.getColor(R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setShowBackground(false)
                    .setSelectedMenuColor(context.resources.getColor(R.color.white)).build()
                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.delete, false
                    )
                )
                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.copy, false
                    )
                )
                if (!viewBinding.translatedMessageText.text.toString().isNullOrEmpty()) {
                    powerMenu.addItem(
                        PowerMenuItem(
                            AppStringConstant1.copy_translated_message, false
                        )
                    )
                }
                powerMenu.showAsDropDown(
                    viewBinding.root,
                    viewBinding.root.measuredWidth / 2,
                    -viewBinding.root.measuredHeight / 2
                )
                powerMenu.setOnMenuItemClickListener { position, menuItem ->
                    when (position) {
                        0 -> {
                            listener.onChatMessageDelete(item)
                            powerMenu.dismiss()
                        }

                        1 -> {
                            powerMenu.dismiss()
                            copyToClipboard(viewBinding.messageText.text.toString(), context)
                        }

                        2 -> {
                            powerMenu.dismiss()
                            copyToClipboard(
                                viewBinding.translatedMessageText.text.toString(), context
                            )
                        }
                    }
                }
                false
            }
            lastMessageItem = item
        }

        @SuppressLint("NewApi")
        private fun handlePlayAudioSpeed(viewBinding: ItemNewOutcomingTextMessageBinding) {
            when (viewBinding.playAudioSpeed.text) {
                "1x" -> {
                    mCurrentPlaySpeed = 1.5f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(1.5f)!!
                    viewBinding.playAudioSpeed.text = "1.5x"
                }

                "1.5x" -> {
                    mCurrentPlaySpeed = 2f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(2f)!!
                    viewBinding.playAudioSpeed.text = "2x"
                }

                "2x" -> {
                    mCurrentPlaySpeed = 1f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(1f)!!
                    viewBinding.playAudioSpeed.text = "1x"
                }
            }
        }

        @SuppressLint("NewApi")
        fun playAudio(audioUrl: String, position: Int) {
            if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                //Check to see if incoming audio is playing
                if (!isInComingAudioPlaying) {
                    currentOutgoingHolder?.stopSeekBarUpdates()
                    currentOutgoingHolder?.resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                } else {
//                    currentIncomingHolder?.stopSeekBarUpdates()
//                    currentIncomingHolder?.resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                }
                releaseMediaPlayer()
            }

            // Update references to the new holder and position
            currentPlayingPosition = position
            currentOutgoingHolder = this
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(audioUrl)
                mediaPlayer?.prepare()
                mediaPlayer?.playbackParams =
                    mediaPlayer?.playbackParams?.setSpeed(mCurrentPlaySpeed)!!
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener {
                    resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                    currentPlayingPosition = -1
                    currentOutgoingHolder = null
                    viewBinding.playAudioSpeed.visibility = View.GONE
                    releaseMediaPlayer()
                }
                mediaPlayer?.setOnPreparedListener {
                    isInComingAudioPlaying = false
                    viewBinding.audioSeekbar.max = mediaPlayer?.duration!!
                    viewBinding.audioLoading.visibility = View.GONE
                    viewBinding.playAudio.visibility = View.VISIBLE
                    viewBinding.playAudioSpeed.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Update SeekBar progress
            updateSeekBarTask = object : Runnable {
                override fun run() {
                    mediaPlayer?.let {
                        viewBinding.audioSeekbar.progress = it.currentPosition
                        val startTime = it.currentPosition
                        viewBinding.currentTimeTV.text = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    startTime.toLong()
                                )
                            )
                        )
                    }
                    viewBinding.audioSeekbar.postDelayed(this, 100)
                }
            }
            viewBinding.audioSeekbar.post(updateSeekBarTask)

            // SeekBar change listener
            viewBinding.audioSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            viewBinding.playAudio.setImageResource(R.drawable.pause)
        }

        fun stopSeekBarUpdates() {
            if (updateSeekBarTask != null) {
                viewBinding.audioSeekbar.removeCallbacks(updateSeekBarTask)
            }
        }

        fun resetSeekBarAndButton(durationTimeOfAudio: Long) {
            stopSeekBarUpdates()
            viewBinding.audioSeekbar.progress = 0
            viewBinding.playAudio.setImageResource(R.drawable.play)
            runOnUiThread {
                viewBinding.currentTimeTV.text = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio),
                    TimeUnit.MILLISECONDS.toSeconds(durationTimeOfAudio) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            durationTimeOfAudio
                        )
                    )
                )
            }
        }
    }


    inner class IncomingHolder(val viewBinding: ItemNewIncomingTextMessageBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        private var updateSeekBarTask: Runnable? = null

        private fun loadVideoImage(imageUrl: String) {
            launch {
                // Show progress bar while loading the image
                viewBinding.progressBar.visibility = View.VISIBLE

                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(
                        imageUrl,
                        HashMap()
                    ) // Use a HashMap for headers if needed
                    val bitmap = retriever.getFrameAtTime(
                        0,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    ) // Extract the first frame
                    retriever.release()

                    bitmap?.let {
                        viewBinding.messageImage.setImageBitmap(it)
                    } ?: run {
                        // Set a placeholder image if the bitmap is null
                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle error and set a fallback image
                    viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                } finally {
                    // Hide progress bar once loading is complete
                    viewBinding.progressBar.visibility = View.GONE
                }
            }
        }

        private fun loadGlideImage(imageUrl: String) {
//            launch {
//                GlideImageLoader.loadImageWithProgress(context = context,
//                    imageUrl = imageUrl,
//                    progressBar = viewBinding.progressBar,
//                    onBitmapLoaded = { bitmap ->
//                        bitmap?.let {
//                            viewBinding.messageImage.setImageBitmap(bitmap)
//                        }
//                    },
//                    onError = {
//                        // Handle error, e.g., show a placeholder image
//                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
//                    })
//            }

            viewBinding.progressBar.visibility = View.VISIBLE

            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_default_user) // Fallback image on error
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.progressBar.visibility = View.GONE
                        viewBinding.messageImage.setImageResource(R.drawable.ic_default_user)
                        return false // Return false to allow Glide to handle the error
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.progressBar.visibility = View.GONE
                        return false // Return false to let Glide handle setting the image
                    }
                })
                .into(viewBinding.messageImage) // Load image into ImageView
        }

        @SuppressLint("NewApi")
        fun bind(item: GetChatMessagesByRoomIdQuery.Edge?, position: Int) {
            val content = item?.node?.content
            val mcontent = item?.node
            if (mcontent?.messageType == MessageMessageType.P) {
                viewBinding.ctRequestview.visibility = View.VISIBLE
                when (mcontent.requestStatus) {
                    "PENDING" -> {
                        viewBinding.cdAccept.visibility = View.VISIBLE
                        viewBinding.cdReject.visibility = View.VISIBLE
                    }

                    "APPROVE" -> {
                        viewBinding.cdAccept.visibility = View.GONE
                        viewBinding.cdReject.visibility = View.GONE
                        viewBinding.cdCancel.visibility = View.VISIBLE
                    }

                    else -> {
                        viewBinding.cdAccept.visibility = View.GONE
                        viewBinding.cdReject.visibility = View.GONE
                        viewBinding.cdCancel.visibility = View.GONE
                    }
                }
                viewBinding.cdAccept.setOnClickListener {
                    listener.onPrivatePhotoAccessResult("A", item.node.privatePhotoRequestId!!)
                }
                viewBinding.cdReject.setOnClickListener {
                    listener.onPrivatePhotoAccessResult("R", item.node.privatePhotoRequestId!!)
                }
                viewBinding.cdCancel.setOnClickListener {
                    listener.onPrivatePhotoAccessResult("C", item.node.privatePhotoRequestId!!)
                }
            } else {
                viewBinding.ctRequestview.visibility = View.GONE
            }
            if (content?.contains("media/chat_files") == true) {
                var fullUrl = content
                var message = ""
                if (fullUrl.contains(" ")) {
                    val link = fullUrl.substring(0, fullUrl.indexOf(" "))
                    val giftMessage = content.substring(fullUrl.indexOf(" ") + 1)
                    fullUrl = link
                    message = giftMessage
                } else {
                    if (content.startsWith("/media/chat_files/")) {
                        fullUrl = "${BuildConfig.BASE_URL}$content"
                    }
                }
                val uri = Uri.parse(fullUrl)
                val lastSegment = uri.lastPathSegment
                val ext = lastSegment?.substring(lastSegment.lastIndexOf(".") + 1)

                val typeface_regular = Typeface.createFromAsset(
                    viewBinding.root.context.assets, "fonts/poppins_semibold.ttf"
                )
                val typeface_light = Typeface.createFromAsset(
                    viewBinding.root.context.assets, "fonts/poppins_light.ttf"
                )
                viewBinding.messageText.typeface = typeface_regular
                viewBinding.messageTime.typeface = typeface_light
                if (ext?.isImageFile() == true || ext?.isVideoFile() == true) {
                    if (message.isNotEmpty()) {
                        viewBinding.messageText.text = message
                    } else {
                        viewBinding.messageText.text = ""
                    }
                    viewBinding.messageImage.setImageBitmap(null)
                    Log.e("ImageUrl --- > ", " " + fullUrl)

                    if (ext.isVideoFile()) {
                        loadVideoImage(fullUrl)
                        viewBinding.videoDurationTV.visibility = View.VISIBLE
                        lifecycleOwner.lifecycleScope.launch((Dispatchers.IO)) {
                            val durationTimeOfAudio = getVideoDurationFromUrl(fullUrl)
                            runOnUiThread {
                                viewBinding.videoDurationTV.text = String.format(
                                    "%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio),
                                    TimeUnit.MILLISECONDS.toSeconds(durationTimeOfAudio) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                            durationTimeOfAudio
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        loadGlideImage(fullUrl)
                        viewBinding.videoDurationTV.visibility = View.GONE
                    }

                    viewBinding.messageImage.visibility = View.VISIBLE
                    viewBinding.mapView.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility =
                        if (ext.isVideoFile()) View.VISIBLE else View.GONE
                } else if (ext?.isAudioFile() == true) {
                    viewBinding.audioMessageLayout.visibility = View.VISIBLE
                    viewBinding.bubble.visibility = View.GONE
                    lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

                        val audioDurationTime = getVideoDurationFromUrl(fullUrl)
                        runOnUiThread {
                            viewBinding.currentTimeTV.text = String.format(
                                "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(audioDurationTime),
                                TimeUnit.MILLISECONDS.toSeconds(audioDurationTime) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(audioDurationTime)
                                )
                            )
                        }
                    }

                    var text = item?.node?.timestamp.toString()
                    val messageTime: Date
                    try {
                        messageTime = formatter.parse(text) as Date
                        val messageTimeToFormat = formatterNew.format(messageTime)
                        val messageTimeToCheck = formatterNew.parse(messageTimeToFormat) as Date
                        viewBinding.audioDurationTV.text = displayTime.format(messageTime)
                        val displayDate = formatDayDate(context, messageTime.time)
                        if (currentList.size > bindingAdapterPosition + 1) {
                            val preItem = currentList[bindingAdapterPosition + 1]
                            var preTime = preItem?.node?.timestamp.toString()
                            val preDateToCheck = formatter.parse(preTime)
                            val preDateToFormat = formatterNew.format(preDateToCheck)
                            val preDate = formatterNew.parse(preDateToFormat) as Date
                            if (messageTimeToCheck == preDate) {
                                viewBinding.lblDate.text = ""
                                viewBinding.llDateLayout.visibility = View.GONE
                            } else {
                                viewBinding.llDateLayout.visibility = View.VISIBLE
                                viewBinding.lblDate.text = displayDate
                            }
                        } else {
                            viewBinding.llDateLayout.visibility = View.VISIBLE
                            viewBinding.lblDate.text = displayDate
                        }
                    } catch (e: Exception) {
                        viewBinding.audioDurationTV.text = e.message
                        viewBinding.llDateLayout.visibility = View.GONE
                    }

                    val avatarUrl = otherUserAvtar
                    viewBinding.userAvatar.loadCircleImage(avatarUrl)

                    viewBinding.playAudio.setOnClickListener {
                        if (currentPlayingPosition == position) {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                viewBinding.playAudio.setImageResource(R.drawable.play)
                            } else {
                                mediaPlayer?.start()
                                viewBinding.playAudio.setImageResource(R.drawable.pause)
                            }
                        } else {
                            viewBinding.audioLoading.visibility = View.VISIBLE
                            viewBinding.playAudio.visibility = View.GONE
                            viewBinding.playAudioSpeed.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                playIncomingAudio(fullUrl, position)
                            }, 100)
                        }
                    }

                    viewBinding.playAudioSpeed.setOnClickListener {
                        handleIncomingPlayAudioSpeed(viewBinding)
                        viewBinding.playAudio.setImageResource(R.drawable.pause)
                        if (currentPlayingPosition == position) {
                            mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(
                                mCurrentPlaySpeed
                            )!!
                            mediaPlayer?.start()
                            viewBinding.playAudio.setImageResource(R.drawable.pause)

                        } else {
                            viewBinding.audioLoading.visibility = View.VISIBLE
                            viewBinding.playAudioSpeed.visibility = View.GONE
                            viewBinding.playAudio.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                playIncomingAudio(fullUrl, position)
                            }, 100)
                        }
                    }


                } else {
                    viewBinding.videoDurationTV.visibility = View.GONE
                    viewBinding.messageText.text = lastSegment
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageFileIcon.visibility = View.VISIBLE
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                    viewBinding.mapView.visibility = View.GONE
                }
            } else {
                viewBinding.progressBar.visibility = View.GONE
                viewBinding.videoDurationTV.visibility = View.GONE
                viewBinding.audioMessageLayout.visibility = View.GONE
                viewBinding.bubble.visibility = View.VISIBLE

                if (mcontent?.messageType == MessageMessageType.GL) {
                    viewBinding.ctRequestview.visibility = View.VISIBLE
                    viewBinding.messageText.text = ""
                    viewBinding.mapView.visibility = View.VISIBLE
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                    viewBinding.cdReject.visibility = View.GONE
                    viewBinding.cdAccept.visibility = View.GONE
                    var latitude = 0.0
                    var longitude = 0.0
                    try {
                        val items = content?.split(",")?.toTypedArray()
                        latitude = items?.get(0)?.toDouble() ?: 0.0
                        longitude = items?.get(1)?.toDouble() ?: 0.0

                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                    viewBinding.mapView.onCreate(null)
                    viewBinding.mapView.onResume()
                    viewBinding.mapView.getMapAsync { googleMap ->
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(latitude, longitude), 13f
                            )
                        )
                        googleMap.addMarker(
                            MarkerOptions().position(
                                LatLng(latitude, longitude)
                            )
                        )
                        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        googleMap.uiSettings.setAllGesturesEnabled(false)
                    }
                } else {
                    viewBinding.messageText.text = content
                    viewBinding.messageImage.setImageBitmap(null)
                    viewBinding.messageImage.visibility = View.GONE
                    viewBinding.messageFileIcon.visibility = View.GONE
                    viewBinding.messagePlayIcon.visibility = View.GONE
                    viewBinding.mapView.visibility = View.GONE
                }
            }
            val avatarUrl: String = otherUserAvtar
            Log.e(TAG,"Avatar Url: $avatarUrl")
            if (avatarUrl != "") {
                viewBinding.messageUserAvatar.loadCircleImage(avatarUrl)
            } else {
                viewBinding.messageUserAvatar.loadCircleImage(R.drawable.ic_chat_item_logo_new)
            }
            var text = item?.node?.timestamp.toString()
            val messageTime: Date
            try {
                messageTime = formatter.parse(text) as Date
                val messageTimeToFormat = formatterNew.format(messageTime)
                val messageTimeToCheck = formatterNew.parse(messageTimeToFormat) as Date
                viewBinding.messageTime.text = displayTime.format(messageTime)
                val displayDate = formatDayDate(context, messageTime.time)
                if (currentList.size > bindingAdapterPosition + 1) {
                    val preItem = currentList[bindingAdapterPosition + 1]
                    var preTime = preItem?.node?.timestamp.toString()
                    val preDateToCheck = formatter.parse(preTime)
                    val preDateToFormat = formatterNew.format(preDateToCheck)
                    val preDate = formatterNew.parse(preDateToFormat) as Date
                    if (messageTimeToCheck == preDate) {
                        viewBinding.lblDate.text = ""
                        viewBinding.llDateLayout.visibility = View.GONE
                    } else {

                        viewBinding.llDateLayout.visibility = View.VISIBLE
                        viewBinding.lblDate.text = displayDate
                    }
                } else {
                    viewBinding.llDateLayout.visibility = View.VISIBLE
                    viewBinding.lblDate.text = displayDate
                }
            } catch (e: Exception) {
                viewBinding.messageTime.text = e.message
                viewBinding.llDateLayout.visibility = View.GONE
            }
            viewBinding.root.setOnClickListener {
                if (item!!.node!!.roomId.name != "" || item.node!!.roomId.id != "") {
                    listener.onChatMessageClick(bindingAdapterPosition, item)
                }
            }
            viewBinding.messageUserAvatar.setOnClickListener {
                if (item!!.node!!.roomId.name != "" || item.node!!.roomId.id != "") {
                    listener.onChatUserAvtarClick()
                }
            }
            viewBinding.ivMenu.setOnClickListener {
                setPowerMenu()
            }
            viewBinding.root.setOnLongClickListener(OnLongClickListener { v ->
                val powerMenu = PowerMenu.Builder(context)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setTextColor(context.resources.getColor(R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setShowBackground(false)
                    .setSelectedMenuColor(context.resources.getColor(R.color.white)).build()
                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.translation, false
                    )
                ) // add an item.
                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.copy, false
                    )
                )
                if (!viewBinding.translatedMessageText.text.toString().isNullOrEmpty()) {
                    powerMenu.addItem(
                        PowerMenuItem(
                            AppStringConstant1.copy_translated_message, false
                        )
                    )
                }
                powerMenu.showAsDropDown(
                    viewBinding.root,
                    viewBinding.root.measuredWidth / 2,
                    -viewBinding.root.measuredHeight / 2
                )
                powerMenu.setOnMenuItemClickListener { position, item ->
                    when (position) {
                        0 -> {
                            callTranslation(
                                viewBinding.translationDivider,
                                viewBinding.translatedMessageText,
                                viewBinding.messageText.text.toString(),
                                "auto",
                                MainActivity.getMainActivity()?.pref?.getString("language", "en")
                                    .toString()
//                                getMainActivity()?.sharedPref?.getLanguageName("en")!!
                            )
                            powerMenu.dismiss()
                        }

                        1 -> {
                            powerMenu.dismiss()
                            copyToClipboard(viewBinding.messageText.text.toString(), context)
                        }

                        2 -> {
                            powerMenu.dismiss()

                            copyToClipboard(
                                viewBinding.translatedMessageText.text.toString(), context
                            )
                        }
                    }
                }
                false
            })
            lastMessageItem = item
        }

        @SuppressLint("NewApi")
        private fun playIncomingAudio(audioUrl: String, position: Int) {
            if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                if (!isInComingAudioPlaying) {
                    currentOutgoingHolder?.stopSeekBarUpdates()
                    currentOutgoingHolder?.resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                } else {
                    currentIncomingHolder?.stopSeekBarUpdates()
                    currentIncomingHolder?.resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                }
                viewBinding.playAudioSpeed.visibility = View.GONE
                releaseMediaPlayer()
            }

            // Update references to the new holder and position
            currentPlayingPosition = position
            currentIncomingHolder = this
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(audioUrl)
                mediaPlayer?.prepare()
                mediaPlayer?.playbackParams =
                    mediaPlayer?.playbackParams?.setSpeed(mCurrentPlaySpeed)!!
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener {
                    resetSeekBarAndButton(mediaPlayer?.duration!!.toLong())
                    currentPlayingPosition = -1
                    currentIncomingHolder = null
                    viewBinding.playAudioSpeed.visibility = View.GONE
                    releaseMediaPlayer()
                }
                mediaPlayer?.setOnPreparedListener {
                    isInComingAudioPlaying = true
                    viewBinding.audioSeekbar.max = mediaPlayer?.duration!!
                    viewBinding.audioLoading.visibility = View.GONE
                    viewBinding.playAudio.visibility = View.VISIBLE
                    viewBinding.playAudioSpeed.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Update SeekBar progress
            updateSeekBarTask = object : Runnable {
                override fun run() {
                    mediaPlayer?.let {
                        viewBinding.audioSeekbar.progress = it.currentPosition
                        val startTime = it.currentPosition
                        viewBinding.currentTimeTV.text = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    startTime.toLong()
                                )
                            )
                        )
                    }
                    viewBinding.audioSeekbar.postDelayed(this, 100)
                }
            }
            viewBinding.audioSeekbar.post(updateSeekBarTask)

            // SeekBar change listener
            viewBinding.audioSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            viewBinding.playAudio.setImageResource(R.drawable.pause)
        }

        @SuppressLint("NewApi")
        private fun handleIncomingPlayAudioSpeed(viewBinding: ItemNewIncomingTextMessageBinding) {
            when (viewBinding.playAudioSpeed.text) {
                "1x" -> {
                    mCurrentPlaySpeed = 1.5f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(1.5f)!!
                    viewBinding.playAudioSpeed.text = "1.5x"
                }

                "1.5x" -> {
                    mCurrentPlaySpeed = 2f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(2f)!!
                    viewBinding.playAudioSpeed.text = "2x"
                }

                "2x" -> {
                    mCurrentPlaySpeed = 1f
                    //mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(1f)!!
                    viewBinding.playAudioSpeed.text = "1x"
                }
            }
        }

        fun stopSeekBarUpdates() {
            if (updateSeekBarTask != null) {
                viewBinding.audioSeekbar.removeCallbacks(updateSeekBarTask)
            }
        }

        fun resetSeekBarAndButton(durationTimeOfAudio: Long) {
            stopSeekBarUpdates()
            viewBinding.audioSeekbar.progress = 0
            viewBinding.playAudio.setImageResource(R.drawable.play)
            runOnUiThread {
                viewBinding.currentTimeTV.text = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationTimeOfAudio),
                    TimeUnit.MILLISECONDS.toSeconds(durationTimeOfAudio) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            durationTimeOfAudio
                        )
                    )
                )
            }
        }

        private fun setPowerMenu() {
            var powerMenu = PowerMenu.Builder(context)
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(context.resources.getColor(R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                .setShowBackground(false)
                .setSelectedMenuColor(context.resources.getColor(R.color.white)).build()
            powerMenu.addItem(
                PowerMenuItem(
                    AppStringConstant1.translation, false
                )
            ) // add an item.
            powerMenu.addItem(
                PowerMenuItem(
                    AppStringConstant1.copy, false
                )
            )
            if (!viewBinding.translatedMessageText.text.toString().isNullOrEmpty()) {
                powerMenu.addItem(
                    PowerMenuItem(
                        AppStringConstant1.copy_translated_message, false
                    )
                )
            }
            powerMenu.showAsDropDown(
                viewBinding.root,
                viewBinding.root.measuredWidth / 2,
                -viewBinding.root.measuredHeight / 2
            )
            powerMenu.setOnMenuItemClickListener { position, item ->
                when (position) {
                    0 -> {
                        callTranslation(
                            viewBinding.translationDivider,
                            viewBinding.translatedMessageText,
                            viewBinding.messageText.text.toString(),
                            "auto",
                            MainActivity.getMainActivity()?.pref?.getString("language", "en")
                                .toString()
//                            getMainActivity()?.sharedPref?.getLanguageName("en")!!
                        )
                        powerMenu.dismiss()
                    }

                    1 -> {
                        powerMenu.dismiss()
                        copyToClipboard(viewBinding.messageText.text.toString(), context)
                    }

                    2 -> {
                        powerMenu.dismiss()

                        copyToClipboard(viewBinding.translatedMessageText.text.toString(), context)
                    }
                }
            }
        }
    }

    private fun callTranslation(
        divider: View,
        translatedtoTextview: TextView,
        textfortranslation: String,
        fromLangCodeSupport: String,
        toLangCodeSupport: String
    ) {
        if (NetworkUtils.isNetworkConnected(context)) {
            val textToTranslate = textfortranslation.trim()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            } else {
            }
            if (translationUtils != null) {
                translationUtils?.StopBackground()
            }
            translationUtils = TranslationUtils(object : TranslationUtils.ResultCallBack {
                override fun onReceiveResult(result: String?) {
                    translatedtoTextview.visibility = View.VISIBLE
                    divider.visibility = View.VISIBLE
                    translatedtoTextview.text = result.toString()
                    Log.e(TAG, "TranslationText"+ result.toString())
                }

                override fun onFailedResult() {
                    Toast.makeText(
                        context, AppStringConstant1.translation_failed, Toast.LENGTH_LONG
                    ).show()
                }

            }, textToTranslate, fromLangCodeSupport, toLangCodeSupport)
            translationUtils?.execute()
        } else {
            Toast.makeText(context, "Turn on Internet Connection", Toast.LENGTH_LONG).show()
        }
    }

    //This check runs on background thread
    object DiffUtilCallBack : DiffUtil.ItemCallback<GetChatMessagesByRoomIdQuery.Edge>() {
        override fun areItemsTheSame(
            oldItem: GetChatMessagesByRoomIdQuery.Edge, newItem: GetChatMessagesByRoomIdQuery.Edge
        ): Boolean {
            return oldItem.node?.id?.toInt() == newItem.node?.id?.toInt()
        }

        override fun areContentsTheSame(
            oldItem: GetChatMessagesByRoomIdQuery.Edge, newItem: GetChatMessagesByRoomIdQuery.Edge
        ): Boolean {
            return when {
                oldItem.node?.id?.toInt() != newItem.node?.id?.toInt() -> false
                oldItem.node?.content != newItem.node?.content -> false
                else -> true
            }
        }
    }


    suspend fun getVideoDurationFromUrl(videoUrl: String): Long {
        return withContext(Dispatchers.IO) {
            var retriever: MediaMetadataRetriever? = null
            try {
                retriever = MediaMetadataRetriever()
                retriever.setDataSource(videoUrl, HashMap()) // Set the video URL
                val durationStr =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationStr?.toLongOrNull() ?: 0L
            } catch (e: Exception) {
                e.printStackTrace()
                0L  // Return 0 in case of any failure
            } finally {
                retriever?.release()
            }
        }
    }


    fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}

