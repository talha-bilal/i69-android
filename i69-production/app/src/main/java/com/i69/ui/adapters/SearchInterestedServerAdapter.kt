package com.i69.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.databinding.ItemSearchInterestedNewBinding
import com.i69.utils.animateFromLeft
import com.i69.utils.animateFromRight
import com.i69.utils.defaultAnimate

class SearchInterestedServerAdapter(
    private val pos: Int,
    private var showAnim: Boolean,
    private val listener: SearchInterestedListener
) : RecyclerView.Adapter<SearchInterestedServerAdapter.MyViewHolder>() {

    private val items: MutableList<MenuItemString> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchInterestedNewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val viewHolder = MyViewHolder(binding)
        binding.root.alpha = 0f
        binding.root.setOnClickListener {
            binding.radioInterest.setImageResource(R.drawable.radio_on_icon)
            listener.onViewClick(viewHolder.bindingAdapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menuItem = items[position]

        holder.viewBinding.icon1.setImageResource(menuItem.iconRes)
        holder.viewBinding.label.text = menuItem.labelRes
        val radioDrawableRes =
            if (menuItem.active) R.drawable.radio_on_icon else R.drawable.radio_off_icon
        holder.viewBinding.radioInterest.setImageResource(radioDrawableRes)
        holder.viewBinding.root.alpha = 1f
        val labelView = holder.viewBinding.label
        labelView.isSelected = true
        labelView.isSingleLine = true
        labelView.ellipsize = android.text.TextUtils.TruncateAt.MARQUEE
        labelView.marqueeRepeatLimit = -1  // marquee_forever
        labelView.post {
            val textWidth = labelView.paint.measureText(labelView.text.toString())
            val viewWidth = labelView.width.toFloat()

            labelView.isSelected = textWidth > viewWidth
        }

        if (pos != -1) {
            val delay = ANIM_DELAY_DEFAULT + (ANIM_DIFF * position)
            holder.viewBinding.root.defaultAnimate(ANIM_DURATION_DEFAULT, delay)
            return
        }

        var delay = ANIM_DELAY_MIN
        var duration = ANIM_DURATION_DEFAULT
        if (showAnim) {
            showAnim = false
            delay = ANIM_DELAY
            duration = ANIM_DURATION
        }

        if (position % 2 == 0) {
            holder.viewBinding.root.animateFromLeft(duration - position * 15, delay + position * 30)
        } else {
            holder.viewBinding.root.animateFromRight(
                duration - position * 15, delay + position * 30
            )
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: List<MenuItemString>) {
        this.items.clear()
        this.items.addAll(items)
    }

    inner class MyViewHolder(val viewBinding: ItemSearchInterestedNewBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    @Keep
    data class MenuItemString(
        val labelRes: String, val iconRes: Int, val active: Boolean = false
    )

    fun interface SearchInterestedListener {
        fun onViewClick(pos: Int)
    }
}