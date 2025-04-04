package com.i69.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.GetUserMomentsQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.ItemSharedUserMomentBinding
import com.i69.utils.ApiUtil
import com.i69.utils.hideKeyboard
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CurrentUserMomentAdapter(
    private val ctx: Context,
    private val listener: CurrentUserMomentListener,
    private var currentUserMoments: ArrayList<GetUserMomentsQuery.Edge>,
    var userId: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedItemPosition: Int = -1

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun getViewHolderByType(type: Int, viewBinding: ViewDataBinding) =
        NearbySharedMomentHolder(viewBinding as ItemSharedUserMomentBinding)

    private fun getViewDataBinding(viewType: Int, parent: ViewGroup) =
        ItemSharedUserMomentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder =
        getViewHolderByType(type, getViewDataBinding(type, parent))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as NearbySharedMomentHolder
        val item = currentUserMoments.get(position)
        holder.bind(position, item)
    }

    fun add(r: GetUserMomentsQuery.Edge?) {
        currentUserMoments.add(r!!)
        notifyItemInserted(currentUserMoments.size - 1)
    }

    fun addAll(newdata: ArrayList<GetUserMomentsQuery.Edge>) {
        newdata.indices.forEach { i ->
            add(newdata[i])
        }
    }

    override fun getItemCount(): Int {
        return if (currentUserMoments == null) 0 else currentUserMoments.size
    }

    fun pauseAll() {
        selectedItemPosition = -1
        notifyDataSetChanged()
    }

    inner class NearbySharedMomentHolder(val viewBinding: ItemSharedUserMomentBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int, item_data: GetUserMomentsQuery.Edge) {
            val title = item_data.node!!.user!!.fullName
            val name = if (title.length > 15) {
                title.substring(0, minOf(title.length, 15))
            } else {
                title
            }

            val s2 = SpannableString(name.plus(" "))
            val s3 = SpannableString(AppStringConstant1.has_shared_moment)


            s2.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(ctx, R.color.colorPrimary)),
                0,
                s2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            s2.setSpan(StyleSpan(Typeface.BOLD), 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s3.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val builder = SpannableStringBuilder()
            builder.append(s2)
            builder.append(s3)

            viewBinding.lblItemNearbyName.text = builder
            val url = if (!BuildConfig.USE_S3) {
                if (item_data.node.file.toString()
                        .startsWith(BuildConfig.BASE_URL)
                ) item_data.node.file.toString()
                else "${BuildConfig.BASE_URL}${item_data.node.file.toString()}"
            } else if (item_data.node.file.toString()
                    .startsWith(ApiUtil.S3_URL)
            ) item_data.node.file.toString()
            else ApiUtil.S3_URL.plus(item_data.node.file.toString())
            viewBinding.imgNearbyUser.setOnClickListener {
                viewBinding.lblItemNearbyName.hideKeyboard()
            }
            viewBinding.lblItemNearbyName.setOnClickListener {
                viewBinding.lblItemNearbyName.hideKeyboard()
            }

            if (isImageFile(item_data)) {
                viewBinding.playerView.visibility = View.INVISIBLE
                viewBinding.ivPlay.setViewGone()
                viewBinding.imgSharedMoment.setViewVisible()
                viewBinding.imgSharedMoment.loadImage(url)
            } else {
                if (position == selectedItemPosition && selectedItemPosition != -1) {
                    viewBinding.ivPlay.setViewGone()
                    viewBinding.imgSharedMoment.visibility = View.INVISIBLE
                    viewBinding.playerView.setViewVisible()

                    val uri: Uri = Uri.parse(url)
                    val mediaItem =
                        MediaItem.Builder().setUri(uri).setMimeType(MimeTypes.VIDEO_MP4).build()
                    playView(mediaItem, true)
                } else {
                    viewBinding.playerView.visibility = View.INVISIBLE
                    viewBinding.imgSharedMoment.setViewVisible()
                    viewBinding.imgSharedMoment.loadImage(url)
                    viewBinding.ivPlay.setViewVisible()
                }

                viewBinding.ivPlay.setOnClickListener {
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
            }

            val avatarUrl = item_data.node.user?.avatar
            if (avatarUrl != null) {
                viewBinding.imgNearbyUser.loadCircleImage(avatarUrl.url.toString())
            } else {
                viewBinding.imgNearbyUser.loadImage(R.drawable.ic_default_user)
            }
            viewBinding.txtMomentDescription.text =
                item_data.node.momentDescriptionPaginated.toString()
            val momentTime = try {
                var text = item_data.node.createdDate.toString()
                text = text.replace("T", " ").substring(0, text.indexOf("."))
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }

            viewBinding.txtTimeAgo.text = DateUtils.getRelativeTimeSpanString(
                momentTime.time,
                Date().time,
                DateUtils.MINUTE_IN_MILLIS
            )

            viewBinding.txtNearbyUserLikeCount.text = "" + item_data.node.like

            viewBinding.lblItemNearbyUserCommentCount.text = "" + item_data.node.comment
            if (item_data.node.user!!.gender != null) {

                if (item_data.node.user.gender!!.name == "A_0") {

                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            ctx.resources, R.drawable.yellow_gift_male, null
                        )
                    )

                } else if (item_data.node.user.gender.name == "A_1") {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            ctx.resources, R.drawable.red_gift_female, null
                        )
                    )

                } else if (item_data.node.user.gender.name == "A_2") {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            ctx.resources, R.drawable.purple_gift_nosay, null
                        )
                    )

                }
            }

            viewBinding.ivFullscreen.setOnClickListener {
                if (listener.isPlaying() && !isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                }
                if (!isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                }
                selectedItemPosition = -1
                notifyDataSetChanged()
                listener.onCommentofMomentClick(bindingAdapterPosition, item_data)
            }

            viewBinding.imgNearbyUserLikes.setOnClickListener {
                listener.onLikeofMomentClick(bindingAdapterPosition, item_data)
            }

            viewBinding.imgNearbyUserComment.setOnClickListener {
                if (listener.isPlaying() && !isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(item_data)) {
                        listener.pauseVideo()
                        viewBinding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    notifyDataSetChanged()
                    listener.onCommentofMomentClick(bindingAdapterPosition, item_data)
                }
            }

            viewBinding.lblViewAllComments.setOnClickListener {
                if (listener.isPlaying() && !isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(item_data)) {
                        listener.pauseVideo()
                        viewBinding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    notifyDataSetChanged()
                    listener.onCommentofMomentClick(bindingAdapterPosition, item_data)
                }
            }
            viewBinding.itemCell.setOnClickListener {
                if (listener.isPlaying() && !isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(item_data)) {
                        listener.pauseVideo()
                        viewBinding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    notifyDataSetChanged()
                    listener.onCommentofMomentClick(bindingAdapterPosition, item_data)
                }
            }
            viewBinding.imgNearbyUserGift.setOnClickListener {
                if (listener.isPlaying() && !isImageFile(item_data)) {
                    listener.pauseVideo()
                    viewBinding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(item_data)) {
                        listener.pauseVideo()
                        viewBinding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    notifyDataSetChanged()
                    listener.onCommentofMomentClick(bindingAdapterPosition, item_data)
                }
            }

            viewBinding.imgNearbySharedMomentOption.setOnClickListener {

                if (userId!! == item_data.node.user.id) {

                    val popup = PopupMenu(ctx, viewBinding.imgNearbySharedMomentOption)
                    popup.menuInflater.inflate(R.menu.more_options, popup.menu)

                    //adding click listener
                    popup.setOnMenuItemClickListener { item: MenuItem? ->

                        when (item!!.itemId) {
                            R.id.nav_item_delete -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition,
                                    item_data,
                                    "delete"
                                )
                            }

                            R.id.nav_item_report -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition,
                                    item_data,
                                    "report"
                                )
                            }
                        }
                        true
                    }
                    popup.show()
                } else {
                    val popup = PopupMenu(ctx, viewBinding.imgNearbySharedMomentOption)
                    popup.menuInflater.inflate(R.menu.more_options1, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem? ->
                        when (item!!.itemId) {

                            R.id.nav_item_report -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition,
                                    item_data,
                                    "report"
                                )
                            }
                        }
                        true
                    }
                    popup.show()
                }
            }
        }

        private fun isImageFile(itemData: GetUserMomentsQuery.Edge): Boolean {
            return itemData.node?.file.toString()
                .endsWith(".jpg") || itemData.node?.file.toString()
                .endsWith(".jpeg") || itemData.node?.file.toString()
                .endsWith(".png") || itemData.node?.file.toString().endsWith(".webp")
        }

        @OptIn(UnstableApi::class)
        private fun playView(mediaItem: MediaItem, playWhenReady: Boolean) {
            val exoPlayer = listener.playVideo(mediaItem, playWhenReady)
            viewBinding.playerView.player = exoPlayer
            viewBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    interface CurrentUserMomentListener {

        fun playVideo(mediaItem: MediaItem, playWhenReady: Boolean): ExoPlayer

        fun isPlaying(): Boolean

        fun pauseVideo()

        fun onSharedMomentClick(
            position: Int, item: GetUserMomentsQuery.Edge
        )

        fun onMoreShareMomentClick()
        fun onLikeofMomentClick(
            position: Int, item: GetUserMomentsQuery.Edge
        )

        fun onCommentofMomentClick(
            position: Int, item: GetUserMomentsQuery.Edge
        )

        fun onDotMenuofMomentClick(
            position: Int, item: GetUserMomentsQuery.Edge, types: String
        )


        fun onMomentGiftClick(
            position: Int, item: GetUserMomentsQuery.Edge?
        )

        fun onProfileOpen(position: Int, edge: GetUserMomentsQuery.Edge)
    }

}