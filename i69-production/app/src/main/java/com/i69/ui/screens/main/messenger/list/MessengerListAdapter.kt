package com.i69.ui.screens.main.messenger.list

import android.graphics.Typeface
import android.os.Build
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.data.models.MessageQuery
import com.i69.databinding.ItemMessageBinding
import com.i69.type.MessageMessageType
import com.i69.utils.LogUtil
import com.i69.utils.findFileExtension
import com.i69.utils.isImageFile
import com.i69.utils.isVideoFile
import com.i69.utils.loadCircleImage
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MessengerListAdapter(
    private val listener: MessagesListListener, private val userId: String?
) : RecyclerView.Adapter<MessengerListAdapter.ViewHolder>() {

    private val itemColors = listOf(
        R.color.message_list_container_6,
        R.color.message_list_container_2,
        R.color.message_list_container_3,
        R.color.message_list_container_4,
        R.color.message_list_container_5
    )
    private val formatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val formatterNew =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
    private val textPairColors = listOf(

        Pair(R.color.message_list_text_title_color_2, R.color.message_list_text_title_color_2),
        Pair(R.color.message_list_text_title_color_2, R.color.message_list_text_title_color_2),
        Pair(R.color.message_list_text_title_color_3, R.color.message_list_text_title_color_2),
        Pair(R.color.message_list_text_title_color_4, R.color.message_list_text_title_color_2),
        Pair(R.color.message_list_text_title_color_5, R.color.message_list_text_title_color_2)
    )

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<MessageQuery>() {
        override fun areItemsTheSame(
            oldItem: MessageQuery, newItem: MessageQuery
        ): Boolean {
            return oldItem.edge?.node?.id?.toInt() == newItem.edge?.node?.id?.toInt()
        }

        override fun areContentsTheSame(
            oldItem: MessageQuery, newItem: MessageQuery
        ): Boolean {
            return when {
                oldItem.edge?.node?.id != newItem.edge?.node?.id -> false
                oldItem.edge?.node?.unread != newItem.edge?.node?.unread -> false
                else -> true
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MessengerListAdapter.ViewHolder =
        ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MessengerListAdapter.ViewHolder, position: Int) {
        val context = holder.viewBinding.root.context
        val roomData = differ.currentList[position]
        val msg = if (roomData.edge?.node?.messageSet?.edges?.isEmpty() == true) {
            null
        } else {
            roomData.edge?.node?.messageSet?.edges?.get(0)?.node
        }
        val typeface_regular =
            Typeface.createFromAsset(context.assets, "fonts/poppins_semibold.ttf")
        val typeface_light = Typeface.createFromAsset(context.assets, "fonts/poppins_light.ttf")


        holder.viewBinding.title.typeface = typeface_regular
        holder.viewBinding.subtitle.typeface = typeface_light
        holder.viewBinding.time.typeface = typeface_light
        holder.viewBinding.unseenMessages.typeface = typeface_regular

        LogUtil.debug("MessageType : : : ${msg?.messageType}")
        var option: Int
        holder.viewBinding.apply {
            this.obj = roomData.edge
            subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null, null, null
            )
            ivDelete.setOnClickListener {
                listener.onItemDeleteClicked(
                    roomData.edge?.node?.id.toString(),
                    holder.bindingAdapterPosition
                )
            }

            if (roomData.edge?.node?.userId?.id != null && userId != null && roomData.edge.node.userId.id.equals(
                    userId
                )
            ) {
                roomData.edge.node.target.avatar.let { imgSrc ->
                    if (imgSrc != null) {
                        imgSrc.url?.let { img.loadCircleImage(it, 0) }
                    } else {
                        img.loadCircleImage("")
                    }
                }
                val fullName = roomData.edge.node.target.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                title.text = name
                if (roomData.edge.node.target.isOnline == true) {
                    onlineStatus.setImageResource(R.drawable.round_green)
                } else {
                    onlineStatus.setImageResource(R.drawable.round_yellow)
                }
                if (!roomData.edge.node.unread.equals("0")) {
                    option = 1
                    unseenMessages.text = roomData.edge.node.unread
                    unseenMessages.visibility = View.VISIBLE
                    continueDialog.visibility = View.GONE
                    ivDelete.setViewGone()
                    holder.viewBinding.time.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )
                    holder.viewBinding.rootChiled.background = context.getDrawable(R.color.white)
                    holder.viewBinding.continueDialog.setColorFilter(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )

                    holder.viewBinding.title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )
                    holder.viewBinding.subtitle.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )


                } else {
                    option = 0
                    unseenMessages.visibility = View.GONE
                    continueDialog.visibility = View.VISIBLE
                    ivDelete.setViewVisible()

                    holder.viewBinding.rootChiled.background =
                        context.getDrawable(android.R.color.transparent)
                    holder.viewBinding.title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.subtitle.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.time.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.continueDialog.setColorFilter(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )

                }

                if (msg?.content?.contains("media/chat_files") == true) {
                    val ext = msg.content.findFileExtension()
                    val stringResId = if (ext.isImageFile()) {
                        R.string.photo
                    } else if (ext.isVideoFile()) {
                        R.string.video
                    } else {
                        R.string.file
                    }
                    var icon = if (ext.isImageFile()) {
                        R.drawable.ic_photo
                    } else if (ext.isVideoFile()) {
                        R.drawable.ic_video
                    } else {
                        R.drawable.ic_baseline_attach_file_24
                    }
                    subtitle.text = context.getString(stringResId)

                    if (stringResId.equals(R.string.file) && (msg.messageType
                            ?: "") == MessageMessageType.G
                    ) {
                        icon = R.drawable.ic_gift_card
                        subtitle.text = context.getString(R.string.gifts)
                    } else {
                        subtitle.text = context.getString(stringResId)
                    }
                    subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            context, icon
                        ), null, null, null
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        subtitle.compoundDrawableTintList = ContextCompat.getColorStateList(
                            context, textPairColors[option].second
                        )
                    }
                } else {
                    if (roomData.edge?.node?.id == "001" || roomData.edge?.node?.id == "000") {
                        ivDelete.setViewGone()
                        subtitle.text = roomData.edge.node.name
                        subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null, null, null, null
                        )
                    } else {
                        if ((msg?.messageType ?: "") == MessageMessageType.GL) {
                            subtitle.text = context.resources.getString(R.string.location)
                            subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    context, R.drawable.location_icon_new
                                ), null, null, null
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                subtitle.compoundDrawableTintList = null
                            }
                        } else {
                            subtitle.text = msg?.content
                            subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null, null, null, null
                            )
                        }
                    }
                }
                if (roomData.edge.node.lastModified != null) {
                    var text = roomData.edge.node.lastModified.toString()
                    val momentTime = formatter.parse(text)
                    time.text = DateUtils.getRelativeTimeSpanString(
                        momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                    )
                }
            } else {
                roomData.edge?.node?.userId?.avatar.let { imgSrc ->
                    when {
                        imgSrc != null -> {
                            img.loadCircleImage(imgSrc.url.toString(), 0)
                        }

                        roomData.edge?.node?.id == "001" || roomData.edge?.node?.id == "000" -> {
                            img.loadCircleImage(R.drawable.ic_chat_item_logo_new)
                        }

                        else -> {
                            img.loadCircleImage("")
                        }
                    }
                }
                val fullName = roomData.edge?.node?.userId?.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                title.text = name

                if (roomData.edge?.node?.userId?.isOnline == true) {
                    onlineStatus.setImageResource(R.drawable.round_green)
                } else {
                    onlineStatus.isVisible =
                        !(roomData.edge?.node?.id == "001" || roomData.edge?.node?.id == "000")
                    onlineStatus.setImageResource(R.drawable.round_yellow)
                }
                if (!roomData.edge?.node?.unread.equals("0")) {
                    option = 1
                    unseenMessages.text = roomData.edge?.node?.unread
                    unseenMessages.visibility = View.VISIBLE
                    continueDialog.visibility = View.GONE
                    ivDelete.setViewGone()

                    holder.viewBinding.time.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )
                    holder.viewBinding.rootChiled.background = context.getDrawable(R.color.white)
                    holder.viewBinding.continueDialog.setColorFilter(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )

                    holder.viewBinding.title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )
                    holder.viewBinding.subtitle.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )

                } else {
                    option = 0
                    unseenMessages.visibility = View.GONE
                    continueDialog.visibility = View.VISIBLE
                    ivDelete.setViewVisible()


                    holder.viewBinding.rootChiled.background =
                        context.getDrawable(android.R.color.transparent)
                    holder.viewBinding.title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.subtitle.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.time.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    holder.viewBinding.continueDialog.setColorFilter(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )


                }
                if (msg?.content?.contains("media/chat_files") == true) {
                    val ext = msg.content.findFileExtension()
                    val stringResId = if (ext.isImageFile()) {
                        R.string.photo
                    } else if (ext.isVideoFile()) {
                        R.string.video
                    } else {
                        R.string.file
                    }
                    var icon = when {
                        ext.isImageFile() -> {
                            R.drawable.ic_photo
                        }

                        ext.isVideoFile() -> {
                            R.drawable.ic_video
                        }

                        else -> {
                            R.drawable.ic_baseline_attach_file_24
                        }
                    }

                    if (stringResId.equals(R.string.file) && (msg.messageType
                            ?: "") == MessageMessageType.G
                    ) {
                        icon = R.drawable.ic_gift_card
                        subtitle.text = context.getString(R.string.gifts)
                    } else {
                        subtitle.text = context.getString(stringResId)
                    }



                    subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            context, icon
                        ), null, null, null
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        subtitle.compoundDrawableTintList = ContextCompat.getColorStateList(
                            context, textPairColors[option].second
                        )
                    }
                } else {
                    if (roomData.edge?.node?.id == "001" || roomData.edge?.node?.id == "000") {
                        ivDelete.setViewGone()
                        subtitle.text = roomData.edge.node.name
                        subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null, null, null, null
                        )
                    } else {
                        if ((msg?.messageType ?: "") == MessageMessageType.GL) {
                            subtitle.text = context.resources.getString(R.string.location)
                            subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    context, R.drawable.location_icon_new
                                ), null, null, null
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                subtitle.compoundDrawableTintList = null
                            }
                        } else {
                            subtitle.text = msg?.content
                            subtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null, null, null, null
                            )
                        }
                    }
                }
                if (roomData.edge?.node?.lastModified != null) {
                    var text = roomData.edge.node.lastModified.toString()
                    val momentTime = formatter.parse(text)
                    time.text = DateUtils.getRelativeTimeSpanString(
                        momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
                    )
                }
            }
            root.setOnClickListener {
                LogUtil.debug("Clicked")
                listener.onItemClick(roomData, position)
            }
            executePendingBindings()
        }
    }

    override fun getItemCount() = differ.currentList.size
    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateList(updatedList: List<MessageQuery>) {
        if (updatedList.isEmpty()) {
            differ.submitList(emptyList())
            return
        }
        val filteredData = updatedList.sortedWith(comparator)

        differ.submitList(filteredData)
        notifyDataSetChanged()
    }

    private val comparator = Comparator<MessageQuery?> { query1, query2 ->
        if (query1?.edge?.node?.id == "001" && query2?.edge?.node?.id != "001") {
            1
        } else if (query1?.edge?.node?.id != "001" && query2?.edge?.node?.id == "001") {
            -1
        } else {
            val dateComparison = query1?.edge?.node?.lastModified.toString()
                .compareTo(query2?.edge?.node?.lastModified.toString())
            // If dueDate is the same, sort by isCompleted (true before false)
            if (dateComparison != 0) {
                if (query1?.isBroadcast == true && query2?.isBroadcast == false) -1
                else if (query1?.isBroadcast == false && query2?.isBroadcast == true) 1
                else 0
            } else {
                dateComparison
            }
        }
    }

    fun submitList1(list: MutableList<MessageQuery>) {
        if (list.size <= 0) return
        val newList = list.toSet()
        val x = mutableListOf<MessageQuery?>().apply {
            addAll(newList)
        }
        val filteredData = x.sortedWith(comparator)
        differ.submitList(filteredData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val viewBinding: ItemMessageBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface MessagesListListener {
        fun onItemClick(userId: MessageQuery, position: Int)

        fun onItemDeleteClicked(roomId: String, position: Int)
    }

}
