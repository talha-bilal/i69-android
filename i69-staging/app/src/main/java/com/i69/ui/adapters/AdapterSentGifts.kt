package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.BuildConfig
import com.i69.GetSentGiftsQuery
import com.i69.R
import com.i69.databinding.ItemReceivedSentGiftsBinding
import com.i69.utils.loadCircleImage
import com.i69.utils.loadImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class AdapterSentGifts(
    var context: Context,
    private val listener: SentGiftPicUserPicInterface,
    var items: MutableList<GetSentGiftsQuery.Edge?>
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
        fun bind(item: GetSentGiftsQuery.Edge) {
            viewBinding.uname.text = item.node?.receiver?.fullName.toString()

            val avatarUrl = item.node?.receiver?.avatar
            if (avatarUrl?.url != null) {
                viewBinding.upic.loadCircleImage(avatarUrl.url)
            } else {
                viewBinding.upic.loadImage(R.drawable.ic_default_user)
            }

            viewBinding.upic.setOnClickListener {

                listener.onuserpiclicked(item.node?.receiver?.id.toString())
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

    interface SentGiftPicUserPicInterface {
        fun onpiclicked(
            urls: String
        )

        fun onuserpiclicked(
            userid: String
        )
    }
}
