package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.GetReceivedGiftsQuery
import com.i69.R
import com.i69.databinding.ItemReceivedSentGiftsBinding
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class AdapterReceiveGifts(
    var context: Context,
    private val listener: ReceivedGiftPicUserPicInterface,
    var items: MutableList<GetReceivedGiftsQuery.Edge?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val oldFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val newFormat = SimpleDateFormat("HH:mm a, dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cView = ItemReceivedSentGiftsBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return MyViewHolder(cView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(items[position]!!)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(var viewBinding: ItemReceivedSentGiftsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: GetReceivedGiftsQuery.Edge) {
            val fullName = item.node?.user?.fullName.toString()
            val name = if (fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            viewBinding.uname.text = name

            val avatarUrl = item.node?.user?.avatar
            if (avatarUrl != null) {
                avatarUrl.url?.let { viewBinding.upic.loadCircleImage(it) }
            } else {
                viewBinding.upic.loadImage(R.drawable.ic_default_user)
            }

            viewBinding.upic.setOnClickListener {
                listener.onuserpiclicked(item.node?.user?.id.toString())
            }

            viewBinding.gname.text = item.node?.gift?.giftName

            val giftUrl = item.node?.gift?.picture
            if (giftUrl != null) {
                viewBinding.gpic.loadCircleImage(BuildConfig.BASE_URL + "/media/" + giftUrl)
            } else {
                viewBinding.gpic.loadImage(R.drawable.ic_default_user)
            }
            viewBinding.gpic.setOnClickListener {
                val url = item.node?.gift?.picture ?: ""
                listener.onpiclicked(BuildConfig.BASE_URL + "/media/" + url)
            }
            viewBinding.amount.text = item.node?.gift?.cost?.roundToInt().toString()

            val myDate = try {
                var text = item.node?.purchasedOn.toString()
                text = text.replace("T", " ").substring(0, text.indexOf("."))
                oldFormat.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }
            val formattedDate = newFormat.format(myDate)
            viewBinding.times.text = formattedDate
        }
    }


    interface ReceivedGiftPicUserPicInterface {
        fun onpiclicked(
            urls: String
        )

        fun onuserpiclicked(
            userid: String
        )
    }
}
