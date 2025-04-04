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
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.GetAllUserMomentsQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.market.Products
import com.i69.databinding.ItemSharedUserMomentBinding
import com.i69.utils.ApiUtil
import com.i69.utils.GlideImageLoader
import com.i69.utils.hideKeyboard
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AdapterMoments(
    val context: Context,
    private val momentListener: SharedMomentListener?,
    val userId: String,
    var isShownByUser: Boolean = true
) : RecyclerView.Adapter<AdapterMoments.MomentViewHolder>(), CoroutineScope {

    private var job = Job()
    var TAG: String = AdapterMoments::class.java.simpleName
    override val coroutineContext = Dispatchers.Main + job

    val items: MutableList<GetAllUserMomentsQuery.Edge?> = mutableListOf()
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private var selectedItemPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding =
            ItemSharedUserMomentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MomentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        holder.bind(position, items[position], momentListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(data: GetAllUserMomentsQuery.Edge?) {
        items.add(data)
        notifyItemInserted(items.size - 1)
    }

    fun addData(index: Int, data: GetAllUserMomentsQuery.Edge?) {
        items.add(index, data)
        notifyItemInserted(index)
    }

    fun deleteData(pkId: String?) {
        val position = items.indexOfFirst { it?.node?.pk.toString() == pkId }
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateData(position: Int, newData: GetAllUserMomentsQuery.Edge?) {
        items[position] = newData
        notifyItemChanged(position)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }

    inner class MomentViewHolder(val binding: ItemSharedUserMomentBinding) :
        RecyclerView.ViewHolder(binding.root), AdapterStore.SharedProductListener {

        private fun loadGlideImage(imageUrl: String) {
            if (binding.progressBar != null) {
                launch {
                    GlideImageLoader.loadImageWithProgress(
                        context = context,
                        imageUrl = imageUrl,
                        progressBar = binding.progressBar,
                        onBitmapLoaded = { bitmap ->
                            bitmap?.let {
                                binding.imgSharedMoment.setImageBitmap(bitmap)
                            }
                        },
                        onError = {
                            binding.imgSharedMoment.setImageResource(R.drawable.ic_default_user)
                        }
                    )
                }
            }
        }

        fun bind(
            position: Int, edge: GetAllUserMomentsQuery.Edge?, momentListener: SharedMomentListener?
        ) {

            binding.marketPlacesLL?.setOnClickListener {
                momentListener?.onMarketPlacesClick(
                    position,
                    -1
                )
            }
            binding.leftRVIV?.setOnClickListener {
                val l =
                    (binding.recyclerViewMarket?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val f =
                    (binding.recyclerViewMarket.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                Log.e(TAG, "last: $l")
                Log.e(TAG, "first: $f")

                if (f > 0)
                    binding.recyclerViewMarket.smoothScrollToPosition(f - 1)
            }
            binding.rightRVIV?.setOnClickListener {
                val l =
                    (binding.recyclerViewMarket?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val f =
                    (binding.recyclerViewMarket.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                Log.e(TAG, "last: $l")
                Log.e(TAG, "first: $f")

                val adapter = binding.recyclerViewMarket.adapter
                if (l < adapter?.getItemCount()!!)
                    binding.recyclerViewMarket.smoothScrollToPosition(l + 1)
            }

            binding.recyclerViewMarket?.adapter = AdapterStore(context, this, null)

            if (edge?.node?.user == null) {
                Log.e(TAG, "Node is null")
                return
            }
            val title = edge.node.user.fullName
            val name = if (title.length > 15) {
                title.substring(0, minOf(title.length, 15))
            } else {
                title
            }
            val s2 = SpannableString(name.uppercase(Locale.getDefault()))
            val s3 = SpannableString(AppStringConstant1.has_shared_moment)

            s2.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        binding.lblItemNearbyName.context, R.color.colorPrimary
                    )
                ), 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            s2.setSpan(StyleSpan(Typeface.BOLD), 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s3.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val builder = SpannableStringBuilder()
            builder.append(s2)
            builder.append(" ")
            builder.append(s3)
            binding.lblItemNearbyName.text = builder
            val url = if (!BuildConfig.USE_S3) {
                if (edge.node.file.toString()
                        .startsWith(BuildConfig.BASE_URL)
                ) {
                    edge.node.file.toString()
                } else if (edge.node.file.toString().startsWith(ApiUtil.S3_URL)) {
                    edge.node.file.toString()
                } else {
                    "${BuildConfig.BASE_URL}${edge.node.file.toString()}"
                }
            } else if (edge.node.file.toString().startsWith(ApiUtil.S3_URL)) {
                edge.node.file.toString()
            } else if (edge.node.file.toString()
                    .startsWith(BuildConfig.BASE_URL)
            ) {
                edge.node.file.toString()
            } else {
                ApiUtil.S3_URL.plus(edge.node.file.toString())
            }
            binding.lblItemNearbyName.setOnClickListener {
                binding.lblItemNearbyName.hideKeyboard()
                momentListener?.onProfileOpen(position, edge)
            }
            binding.lblItemNearbyName.setOnClickListener {
                binding.lblItemNearbyName.hideKeyboard()
                momentListener?.onProfileOpen(position, edge)
            }
            Log.e(TAG, "Url: $url")
            Log.e(TAG, "binnd user avatar= ${edge.node.user.avatar}")
            if (isImageFile(edge)) {
                binding.playerView.visibility = View.INVISIBLE
                binding.ivPlay.setViewGone()
                binding.imgSharedMoment.setViewVisible()
                loadGlideImage(url)

            } else {
                if (position == selectedItemPosition && selectedItemPosition != -1) {
                    Log.e(TAG, "play: $position")
                    binding.ivPlay.setViewGone()
                    binding.imgSharedMoment.visibility = View.INVISIBLE
                    binding.playerView.setViewVisible()
                    val uri: Uri = Uri.parse(url)
                    val mediaItem =
                        MediaItem.Builder().setUri(uri).setMimeType(MimeTypes.VIDEO_MP4).build()
                    playView(mediaItem, true)
                } else {

                    binding.playerView.visibility = View.INVISIBLE
                    binding.imgSharedMoment.setViewVisible()
                    binding.imgSharedMoment.loadImage(url)
                    binding.ivPlay.setViewVisible()
                    Log.e(TAG, "dont play: $position ${binding.playerView.player == null}")
                }

                binding.ivPlay.setOnClickListener {
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
            }

            val avatarUrl = edge.node.user.avatar
            Log.e(TAG, "AvatarUrl: ${avatarUrl?.url}")
            if (avatarUrl != null) {
                avatarUrl.url?.replace(
                    "http://95.216.208.1:8000/media/", "${BuildConfig.BASE_URL}media/"
                )?.let {
                    Log.e(TAG, "NSMA" + "avatarUrl: $it")
                    binding.imgNearbyUser.loadCircleImage(it)
                }
            } else {
                binding.imgNearbyUser.loadImage(R.drawable.ic_default_user)
            }

            val sb = StringBuilder()
            edge.node.momentDescriptionPaginated!!.forEach { sb.append(it) }
            val descString = sb.toString().replace("", "")

            if (descString.isEmpty()) {
                binding.txtMomentDescription.visibility = View.GONE
            } else {
                binding.txtMomentDescription.text = descString
                binding.txtMomentDescription.visibility = View.VISIBLE
            }
            val momentTime = try {
                var text = edge.node.createdDate.toString()
                text = text.replace("T", " ").substring(0, text.indexOf("."))
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }

            val times = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )

            var publishAt = edge.node.publishAt.toString()
            Log.e(TAG, "setStory: $publishAt")
            var publishTimeInMillis = ""
            if (publishAt.isNotEmpty() && publishAt != "null") {
                publishAt = publishAt.replace("T", " ").substring(0, publishAt.indexOf("+"))
                val momentPublishTime = formatter.parse(publishAt)
                publishTimeInMillis = DateUtils.getRelativeTimeSpanString(
                    momentPublishTime!!.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }
            binding.txtTimeAgo.text = publishTimeInMillis.ifEmpty { times }
            binding.txtNearbyUserLikeCount.text = "${edge.node.like}"
            binding.lblItemNearbyUserCommentCount.text = "${edge.node.comment}"
            val context = binding.imgNearbyUserGift.context
            if (edge.node.user.gender != null) {

                val drawable = if (edge.node.user.gender.name == "A_0") {
                    ResourcesCompat.getDrawable(
                        context.resources, R.drawable.yellow_gift_male, null
                    )
                } else if (edge.node.user.gender.name == "A_1") {
                    ResourcesCompat.getDrawable(context.resources, R.drawable.red_gift_female, null)
                } else if (edge.node.user.gender.name == "A_2") {
                    ResourcesCompat.getDrawable(
                        context.resources, R.drawable.purple_gift_nosay, null
                    )
                } else {
                    null
                }
                if (drawable != null) {
                    binding.imgNearbyUserGift.setImageDrawable(drawable)
                }
            }

            if (isShownByUser && (edge.node.like!! > 0)) {
                binding.lblViewAllLikes.visibility = View.VISIBLE
            } else {
                binding.lblViewAllLikes.visibility = View.GONE
            }
            if (edge.node.comment!! > 0) {
                binding.lblViewAllComments.visibility = View.VISIBLE
            } else {
                binding.lblViewAllComments.visibility = View.GONE
            }
            binding.lblViewAllLikes.setOnClickListener {
                momentListener?.onLikeOfMomentShowClick(bindingAdapterPosition, edge)
            }
            binding.imgNearbyUserLikes.setOnClickListener {
                momentListener?.onLikeOfMomentClick(bindingAdapterPosition, edge)
            }
            binding.ivFullscreen.setOnClickListener {
                try {
                    if (momentListener?.isPlaying() == true && !isImageFile(edge)) {
                        momentListener.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    if (!isImageFile(edge)) {
                        momentListener?.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                momentListener?.onCommentOfMomentClick(bindingAdapterPosition, edge)
            }
            binding.imgNearbyUserComment.setOnClickListener {
                if (momentListener?.isPlaying() == true && !isImageFile(edge)) {
                    momentListener.pauseVideo()
                    binding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(edge)) {
                        momentListener?.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    momentListener?.onCommentOfMomentClick(bindingAdapterPosition, edge)
                }
            }

            binding.lblViewAllComments.setOnClickListener {
                if (momentListener?.isPlaying() == true && !isImageFile(edge)) {
                    momentListener.pauseVideo()
                    binding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(edge)) {
                        momentListener?.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    momentListener?.onCommentOfMomentClick(bindingAdapterPosition, edge)
                }
            }
            binding.itemCell.setOnClickListener {
                if (momentListener?.isPlaying() == true && !isImageFile(edge)) {
                    momentListener.pauseVideo()
                    binding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(edge)) {
                        momentListener?.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    momentListener?.onCommentOfMomentClick(bindingAdapterPosition, edge)
                }
            }

            binding.imgNearbyUserGift.setOnClickListener {
                if (momentListener?.isPlaying() == true && !isImageFile(edge)) {
                    momentListener.pauseVideo()
                    binding.ivPlay.setViewVisible()
                } else {
                    if (!isImageFile(edge)) {
                        momentListener?.pauseVideo()
                        binding.ivPlay.setViewVisible()
                    }
                    selectedItemPosition = -1
                    momentListener?.onCommentOfMomentClick(bindingAdapterPosition, edge)
                }
            }

            binding.imgNearbySharedMomentOption.setOnClickListener {

                if (userId.equals(edge.node.user.id)) {
                    val popup = PopupMenu(it.context, binding.imgNearbySharedMomentOption)
                    popup.menuInflater.inflate(R.menu.more_options, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem? ->

                        when (item!!.itemId) {
                            R.id.nav_item_delete -> {
                                momentListener?.onDotMenuOfMomentClick(
                                    bindingAdapterPosition, edge, "delete"
                                )
                            }

                            R.id.nav_item_edit -> {
                                momentListener?.onDotMenuOfMomentClick(
                                    bindingAdapterPosition, edge, "edit"
                                )
                            }
                        }
                        true
                    }
                    popup.show()
                } else {
                    val popup = PopupMenu(it.context, binding.imgNearbySharedMomentOption)
                    popup.menuInflater.inflate(R.menu.more_options1, popup.menu)

                    popup.setOnMenuItemClickListener { item: MenuItem? ->
                        when (item!!.itemId) {
                            R.id.nav_item_report -> {
                                momentListener?.onDotMenuOfMomentClick(
                                    bindingAdapterPosition, edge, "report"
                                )
                            }
                        }
                        true
                    }
                    popup.show()
                }
            }
        }

        private fun isImageFile(itemData: GetAllUserMomentsQuery.Edge): Boolean {
            return itemData.node?.file.toString().endsWith(".jpg") || itemData.node?.file.toString()
                .endsWith(".jpeg") || itemData.node?.file.toString()
                .endsWith(".png") || itemData.node?.file.toString().endsWith(".webp")
        }

        @OptIn(UnstableApi::class)
        private fun playView(mediaItem: MediaItem, playWhenReady: Boolean) {
            val exoPlayer = momentListener?.playVideo(mediaItem, playWhenReady)
            binding.playerView.player = exoPlayer
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }

        override fun onProductClick(position: Int, productId: String, product: Products) {
            momentListener?.onMarketPlacesClick(position, productId.toInt())
        }
    }

    interface SharedMomentListener {
        fun playVideo(mediaItem: MediaItem, playWhenReady: Boolean): ExoPlayer

        fun isPlaying(): Boolean

        fun pauseVideo()

        fun onSharedMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?)

        fun onMarketPlacesClick(position: Int, positionProduct: Int)

        fun onMoreShareMomentClick()

        fun onLikeOfMomentShowClick(position: Int, item: GetAllUserMomentsQuery.Edge?)

        fun onLikeOfMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?)

        fun onCommentOfMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?)

        fun onMomentGiftClick(position: Int, item: GetAllUserMomentsQuery.Edge?)

        fun onDotMenuOfMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?, types: String)

        fun onProfileOpen(position: Int, item: GetAllUserMomentsQuery.Edge?)
    }
}