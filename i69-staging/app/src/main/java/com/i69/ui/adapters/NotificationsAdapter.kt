package com.i69.ui.adapters

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.GetAllNotificationQuery
import com.i69.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class NotificationsAdapter(
    private val ctx: Context,
    private val listener: NotificationListener,
    private val allNotifications: MutableList<GetAllNotificationQuery.Edge?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun updateList(updatedList: MutableList<GetAllNotificationQuery.Edge>) {
        Log.e("inAddapterDataAdded", "-->" + updatedList.size)
//
        allNotifications.clear()
        allNotifications.addAll(updatedList)
//        notifyDataSetChanged()

        notifyItemInserted(allNotifications.size - 1)
    }


    fun add(r: GetAllNotificationQuery.Edge) {
        allNotifications.add(r)
        notifyDataSetChanged()
//        notifyItemInserted(allNotifications.size - 1)
    }

    fun addAll(newdata: ArrayList<GetAllNotificationQuery.Edge>) {

        newdata.indices.forEach { i ->
            add(newdata[i])
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as NotificationHolder

        val item = allNotifications.get(position)
        holder.bind(position, item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder = NotificationHolder(
        ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )


    override fun getItemCount() = allNotifications.size


    inner class NotificationHolder(val viewBinding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int, item: GetAllNotificationQuery.Edge?) {

            viewBinding.txtNotificationTitle.text = item?.node!!.notificationData!!.title
            viewBinding.txtNotificationBody.text = item.node.notificationData?.body

            var text = item.node.createdDate.toString()
            text = text.replace("T", " ").substring(0, text.indexOf("."))
            val momentTime = formatter.parse(text)

            viewBinding.txtNotificationTime.text = DateUtils.getRelativeTimeSpanString(
                momentTime.time,
                Date().time,
                DateUtils.MINUTE_IN_MILLIS
            )
            viewBinding.root.setOnClickListener {
                listener.onNotificationClick(bindingAdapterPosition, item)
            }

        }
    }

    interface NotificationListener {
        fun onNotificationClick(position: Int, notificationdata: GetAllNotificationQuery.Edge?)
    }
}