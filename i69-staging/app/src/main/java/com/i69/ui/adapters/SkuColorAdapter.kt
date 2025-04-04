package com.i69.ui.adapters

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.i69.R
import com.i69.data.models.market.FilterColorSkus

class SkuColorAdapter(
    val activity: Activity?,
    private var groupedColors: MutableList<FilterColorSkus>,
    private val clickColorItemListener: ClickColorItemListener,
    private val showImageOnViewPager: ShowImageOnViewPager
) : RecyclerView.Adapter<SkuColorAdapter.ViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sku_color_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.progressBar.visibility = View.VISIBLE
        Glide.with(activity!!).load(groupedColors[position].skuImage)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                   return false
                }
            }).into(holder.imgColor)

        if (selectedPosition == position) {
            holder.cardImgColor.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.black
                )
            )
            clickColorItemListener.onClickColorItemListener(groupedColors[position], position)
        } else {
            holder.cardImgColor.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.white
                )
            )
        }

        holder.imgColor.setOnClickListener {
            selectedPosition = position
            showImageOnViewPager.onShowColorItemListener(groupedColors[position], position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return groupedColors.size
    }

    fun addColorItems(filterColorSkus: MutableList<FilterColorSkus>) {
        groupedColors = filterColorSkus
        notifyDataSetChanged()
    }

    fun setSelectedItem(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImgColor: CardView = itemView.findViewById(R.id.cardImgColor)
        val imgColor: ImageView = itemView.findViewById(R.id.imgColor)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    interface ClickColorItemListener {
        fun onClickColorItemListener(filterColorSkus: FilterColorSkus, position: Int)
    }

    interface ShowImageOnViewPager {
        fun onShowColorItemListener(filterColorSkus: FilterColorSkus, position: Int)
    }
}