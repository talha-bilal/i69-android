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
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.GetAllUserMomentsQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.ItemSharedUserMomentBinding
import com.i69.utils.ApiUtil
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class NearbySharedMomentAdapter(
    private val ctx: Context,
    private val listener: NearbySharedMomentListener,
    private var allusermoments2: ArrayList<GetAllUserMomentsQuery.Edge>,
    var userId: String?,
    var isShownearByUser: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedItemPosition: Int = -1
    private var TAG: String = NearbySharedMomentAdapter::class.java.simpleName

    private val differCallback = object : DiffUtil.ItemCallback<GetAllUserMomentsQuery.Edge>() {
        override fun areItemsTheSame(
            oldItem: GetAllUserMomentsQuery.Edge, newItem: GetAllUserMomentsQuery.Edge
        ): Boolean {
            return oldItem.node?.pk == newItem.node?.pk
        }

        override fun areContentsTheSame(
            oldItem: GetAllUserMomentsQuery.Edge, newItem: GetAllUserMomentsQuery.Edge
        ): Boolean {
            return when {
                oldItem.node?.like != newItem.node?.like -> false
                oldItem.node?.comment != newItem.node?.comment -> false
                oldItem.node?.momentDescription != newItem.node?.momentDescription -> false
                oldItem.node?.momentDescriptionPaginated != newItem.node?.momentDescriptionPaginated -> false
                else -> true
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private var allusermoments = emptyList<GetAllUserMomentsQuery.Edge>()
    var allusermoments4 = ArrayList(allusermoments)

    private fun getViewHolderByType(type: Int, viewBinding: ViewDataBinding) =
        NearbySharedMomentHolder(viewBinding as ItemSharedUserMomentBinding)

    private fun getViewDataBinding(viewType: Int, parent: ViewGroup) =
        ItemSharedUserMomentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder =
        getViewHolderByType(type, getViewDataBinding(type, parent))

    var holder: NearbySharedMomentHolder? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as NearbySharedMomentHolder
        this.holder = holder
        val item = differ.currentList.get(position)
        holder.bind(position, item, ctx)
    }

    fun submitList1(list: MutableList<GetAllUserMomentsQuery.Edge>) {
        val x = mutableListOf<GetAllUserMomentsQuery.Edge>().apply {
            addAll(list)
        }
        differ.submitList(x)
    }

    fun updateList(updatedList: ArrayList<GetAllUserMomentsQuery.Edge>, uid: String?) {
        userId = uid
        notifyDataSetChanged()
    }

    fun add(r: GetAllUserMomentsQuery.Edge?) {
        Log.e("notify_adapter", "add 134")
        notifyItemInserted(differ.currentList.size - 1)
    }

    fun addAll(newdata: ArrayList<GetAllUserMomentsQuery.Edge>) {
        newdata.indices.forEach { i ->
            Log.e("notify_adapter", "addAll 140")
            add(newdata[i])
        }
    }

    fun pauseAll() {
        selectedItemPosition = -1
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class NearbySharedMomentHolder(val viewBinding: ItemSharedUserMomentBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(position: Int, item_data: GetAllUserMomentsQuery.Edge?, ctx: Context) {
            if (item_data!!.node!!.user == null) {
                Log.e("obj_node", "data is null ${differ.currentList.size}")
                return
            }

            Log.e(TAG, "$item_data")

            val title = item_data.node!!.user!!.fullName
            val name = if (title.length > 15) {
                title.substring(0, minOf(title.length, 15))
            } else {
                title
            }
            Log.e(TAG, "Title: $name")
            val s2 = SpannableString(name.uppercase(Locale.getDefault()))
            val s3 = SpannableString(AppStringConstant1.has_shared_moment)

            s2.setSpan(
                ForegroundColorSpan(this@NearbySharedMomentAdapter.ctx.resources.getColor(R.color.colorPrimary)),
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
            builder.append(" ")
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
            Log.e(TAG, "Url: $url")
            Log.e(TAG, "binnd user avatar= ${item_data.node.user?.avatar}")

            if (isImageFile(item_data)) {
                viewBinding.playerView.visibility = View.INVISIBLE
                viewBinding.ivPlay.setViewGone()
                viewBinding.imgSharedMoment.setViewVisible()
                viewBinding.imgSharedMoment.loadImage(url, placeHolderType = 1)
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
            Log.e(TAG, "AvatarUrl: ${avatarUrl?.url}")
            if (avatarUrl != null) {
                avatarUrl.url?.replace(
                    "http://95.216.208.1:8000/media/",
                    "${BuildConfig.BASE_URL}media/"
                )?.let {
                    viewBinding.imgNearbyUser.loadCircleImage(it)
                }
            } else {
                viewBinding.imgNearbyUser.loadImage(R.drawable.ic_default_user)
            }

            val sb = StringBuilder()
            item_data.node.momentDescriptionPaginated!!.forEach { sb.append(it) }
            val descstring = sb.toString().replace("", "")

            if (descstring.isNullOrEmpty()) {
                viewBinding.txtMomentDescription.visibility = View.GONE
            } else {
                viewBinding.txtMomentDescription.text = descstring
                viewBinding.txtMomentDescription.visibility = View.VISIBLE
            }
            val momentTime = try {
                var text = item_data.node.createdDate.toString()
                text = text.replace("T", " ").substring(0, text.indexOf("."))
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }

            val times = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )

            var publishAt = item_data.node.publishAt.toString()
            Log.e(TAG, "UMSDF" + "setStory: $publishAt")
            var publishTimeInMillis = ""
            if (publishAt.isNotEmpty() && publishAt != "null") {
                publishAt = publishAt.replace("T", " ").substring(0, publishAt.indexOf("+"))
                val momentPublishTime = formatter.parse(publishAt)
                publishTimeInMillis = DateUtils.getRelativeTimeSpanString(
                    momentPublishTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }

            viewBinding.txtTimeAgo.text = publishTimeInMillis.ifEmpty { times }
            viewBinding.txtNearbyUserLikeCount.text = "" + item_data.node.like
            viewBinding.lblItemNearbyUserCommentCount.text = "" + item_data.node.comment
            if (item_data.node.user!!.gender != null) {
                if (item_data.node.user.gender!!.name.equals("A_0")) {

                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@NearbySharedMomentAdapter.ctx.resources,
                            R.drawable.yellow_gift_male,
                            null
                        )
                    )

                } else if (item_data.node.user.gender.name.equals("A_1")) {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@NearbySharedMomentAdapter.ctx.resources,
                            R.drawable.red_gift_female,
                            null
                        )
                    )

                } else if (item_data.node.user.gender.name.equals("A_2")) {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@NearbySharedMomentAdapter.ctx.resources,
                            R.drawable.purple_gift_nosay,
                            null
                        )
                    )

                }
            }

            if (isShownearByUser && (item_data.node.like!! > 0)) {
                viewBinding.lblViewAllLikes.visibility = View.VISIBLE
            } else {
                viewBinding.lblViewAllLikes.visibility = View.GONE
            }

            if (item_data.node.comment!! > 0) {
                viewBinding.lblViewAllComments.visibility = View.VISIBLE
            } else {
                viewBinding.lblViewAllComments.visibility = View.GONE
            }


            viewBinding.lblViewAllLikes.setOnClickListener {
                listener.onLikeofMomentshowClick(bindingAdapterPosition, item_data)
            }
            viewBinding.imgNearbyUserLikes.setOnClickListener {
                listener.onLikeofMomentClick(bindingAdapterPosition, item_data)
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
                if (userId!!.equals(item_data.node.user.id)) {
                    val popup = PopupMenu(
                        this@NearbySharedMomentAdapter.ctx, viewBinding.imgNearbySharedMomentOption
                    )
                    popup.menuInflater.inflate(R.menu.more_options, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem? ->

                        when (item!!.itemId) {
                            R.id.nav_item_delete -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition, item_data, "delete"
                                )

                            }

                            R.id.nav_item_edit -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition, item_data, "edit"
                                )
                            }
                        }
                        true
                    }
                    popup.show()
                } else {
                    val popup = PopupMenu(
                        this@NearbySharedMomentAdapter.ctx, viewBinding.imgNearbySharedMomentOption
                    )
                    popup.menuInflater.inflate(R.menu.more_options1, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem? ->
                        when (item!!.itemId) {
                            R.id.nav_item_report -> {
                                listener.onDotMenuofMomentClick(
                                    bindingAdapterPosition, item_data, "report"
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
            val exoPlayer = listener.playVideo(mediaItem, playWhenReady)
            viewBinding.playerView.player = exoPlayer
            viewBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    interface NearbySharedMomentListener {
        fun playVideo(mediaItem: MediaItem, playWhenReady: Boolean): ExoPlayer

        fun isPlaying(): Boolean

        fun pauseVideo()

        fun onSharedMomentClick(
            position: Int, item: GetAllUserMomentsQuery.Edge?
        )

        fun onMoreShareMomentClick()

        fun onLikeofMomentshowClick(
            position: Int, item: GetAllUserMomentsQuery.Edge?
        )

        fun onLikeofMomentClick(
            position: Int, item: GetAllUserMomentsQuery.Edge?
        )

        fun onCommentofMomentClick(
            position: Int, item: GetAllUserMomentsQuery.Edge?
        )

        fun onMomentGiftClick(
            position: Int, item: GetAllUserMomentsQuery.Edge?
        )

        fun onDotMenuofMomentClick(position: Int, item: GetAllUserMomentsQuery.Edge?, types: String)

    }

}