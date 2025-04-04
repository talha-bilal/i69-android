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
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.Moment
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


class OfflineNearbySharedMomentAdapter(
    private val ctx: Context,
    private var allusermoments2: ArrayList<Moment>,
    var userId: String?,
    var isShownearByUser: Boolean = true,
    var onClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TAG: String = OfflineNearbySharedMomentAdapter::class.java.simpleName
    private var selectedItemPosition: Int = -1

    private val differCallback = object : DiffUtil.ItemCallback<Moment>() {
        override fun areItemsTheSame(
            oldItem: Moment, newItem: Moment
        ): Boolean {
            return oldItem.node?.node?.pk == newItem.node?.node?.pk
        }

        override fun areContentsTheSame(
            oldItem: Moment, newItem: Moment
        ): Boolean {
            return when {
                oldItem.node?.node?.like != newItem.node?.node?.like -> false
                oldItem.node?.node?.comment != newItem.node?.node?.comment -> false
                oldItem.node?.node?.momentDescription != newItem.node?.node?.momentDescription -> false
                oldItem.node?.node?.momentDescriptionPaginated != newItem.node?.node?.momentDescriptionPaginated -> false
                else -> true
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private var allusermoments = emptyList<Moment>()
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

    fun setData(newWordList: List<Moment>) {
        val diffUtil = OfflineNearByMomentsAdapterDiff(allusermoments4, newWordList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        allusermoments4 = newWordList as ArrayList<Moment>
        diffResults.dispatchUpdatesTo(this@OfflineNearbySharedMomentAdapter)
    }

    fun updateList(updatedList: ArrayList<Moment>, uid: String?) {
        userId = uid
        notifyDataSetChanged()
    }


    fun add(r: Moment?) {
        notifyItemInserted(differ.currentList.size - 1)
    }

    fun addAll(newdata: ArrayList<Moment>) {
        newdata.indices.forEach { i ->
            add(newdata[i])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NearbySharedMomentHolder(val viewBinding: ItemSharedUserMomentBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(position: Int, item_data: Moment?, ctx: Context) {
            if (item_data!!.node!!.node?.user == null) {
                return
            }
            Log.e(TAG,"$item_data")

            val title = item_data.node!!.node?.user!!.fullName
            val name = if (title.length > 15) {
                title.substring(0, minOf(title.length, 15))
            } else {
                title
            }
            Log.e(TAG,"Title: $name")
            val s2 = SpannableString(name.uppercase(Locale.getDefault()))
            val s3 = SpannableString(AppStringConstant1.has_shared_moment)

            s2.setSpan(
                ForegroundColorSpan(this@OfflineNearbySharedMomentAdapter.ctx.resources.getColor(R.color.colorPrimary)),
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

            viewBinding.itemCell.setOnClickListener { onClick.invoke() }

            val url = if (!BuildConfig.USE_S3) {
                if (item_data.node?.node?.file.toString()
                        .startsWith(BuildConfig.BASE_URL)
                ) item_data.node?.node?.file.toString()
                else "${BuildConfig.BASE_URL}${item_data.node?.node?.file.toString()}"
            } else if (item_data.node?.node?.file.toString()
                    .startsWith(ApiUtil.S3_URL)
            ) item_data.node?.node?.file.toString()
            else ApiUtil.S3_URL.plus(item_data.node?.node?.file.toString())
            Log.e(TAG,"Url: $url")
            Log.e(TAG,"binnd user avatar= ${item_data.node?.node?.user?.avatar}")

            if (isImageFile(item_data)) {
                viewBinding.playerView.visibility = View.INVISIBLE
                viewBinding.ivPlay.setViewGone()
                viewBinding.imgSharedMoment.setViewVisible()
                viewBinding.imgSharedMoment.loadImage(url, placeHolderType = 1)
            } else {
                if (position == selectedItemPosition && selectedItemPosition != -1) {
                    Log.e(TAG,"NSMA"+ "play: $position")

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
                    Log.e(TAG, "dont play: $position ${viewBinding.playerView.player == null}")
                }
            }

            val avatarUrl = item_data.node?.node?.user?.avatar
            if (avatarUrl != null) {
                avatarUrl.url?.replace(
                    "http://95.216.208.1:8000/media/", "${BuildConfig.BASE_URL}media/"
                )?.let {
                    Log.e(TAG,"NSMA"+ "avatarUrl: $it")
                    viewBinding.imgNearbyUser.loadCircleImage(it)
                }
            } else {
                viewBinding.imgNearbyUser.loadImage(R.drawable.ic_default_user)
            }

            val sb = StringBuilder()
            item_data.node?.node?.momentDescriptionPaginated!!.forEach { sb.append(it) }
            val descstring = sb.toString().replace("", "")

            if (descstring.isNullOrEmpty()) {
                viewBinding.txtMomentDescription.visibility = View.GONE
            } else {
                viewBinding.txtMomentDescription.text = descstring
                viewBinding.txtMomentDescription.visibility = View.VISIBLE
            }
            val momentTime = try {
                var text = item_data.node.node.createdDate.toString()
                text = text.replace("T", " ")?.substring(0, text.indexOf(".")).toString()
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }


            val times = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )

            var publishAt = item_data.node.node.publishAt.toString()
            Log.e(TAG,"UMSDF"+ "setStory: $publishAt")
            var publishTimeInMillis = ""
            if (publishAt.isNotEmpty() && publishAt != "null") {
                publishAt = publishAt.replace("T", " ").substring(0, publishAt.indexOf("+"))
                val momentPublishTime = formatter.parse(publishAt)
                publishTimeInMillis = DateUtils.getRelativeTimeSpanString(
                    momentPublishTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }

            viewBinding.txtTimeAgo.text = publishTimeInMillis.ifEmpty { times }

            viewBinding.txtNearbyUserLikeCount.text = "" + item_data.node.node?.like

            viewBinding.lblItemNearbyUserCommentCount.text = "" + item_data.node.node?.comment
            if (item_data.node.node.user!!.gender != null) {
                if (item_data.node.node.user.gender!!.name.equals("A_0")) {

                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@OfflineNearbySharedMomentAdapter.ctx.resources,
                            R.drawable.yellow_gift_male,
                            null
                        )
                    )

                } else if (item_data.node.node?.user!!.gender!!.name.equals("A_1")) {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@OfflineNearbySharedMomentAdapter.ctx.resources,
                            R.drawable.red_gift_female,
                            null
                        )
                    )

                } else if (item_data.node.node?.user!!.gender!!.name.equals("A_2")) {
                    viewBinding.imgNearbyUserGift.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            this@OfflineNearbySharedMomentAdapter.ctx.resources,
                            R.drawable.purple_gift_nosay,
                            null
                        )
                    )

                }
            }

            if (isShownearByUser && (item_data.node.node?.like!! > 0)) {
                viewBinding.lblViewAllLikes.visibility = View.VISIBLE
            } else {
                viewBinding.lblViewAllLikes.visibility = View.GONE
            }

            if (item_data.node.node?.comment!! > 0) {
                viewBinding.lblViewAllComments.visibility = View.VISIBLE
            } else {
                viewBinding.lblViewAllComments.visibility = View.GONE
            }
        }

        private fun isImageFile(itemData: Moment): Boolean {
            return itemData.node?.node?.file.toString()
                .endsWith(".jpg") || itemData.node?.node?.file.toString()
                .endsWith(".jpeg") || itemData.node?.node?.file.toString()
                .endsWith(".png") || itemData.node?.node?.file.toString().endsWith(".webp")
        }

        @OptIn(UnstableApi::class)
        private fun playView(mediaItem: MediaItem, playWhenReady: Boolean) {
            viewBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }
}