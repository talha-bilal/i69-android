package com.i69.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.databinding.ItemSearchGenderBinding

import com.i69.utils.animateFromLeft
import com.i69.utils.animateFromRight
import com.i69.utils.defaultAnimate

const val ANIM_DELAY = 400L
const val ANIM_DELAY_MIN = 50L
const val ANIM_DURATION = 300L

const val ANIM_DIFF = 100L
const val ANIM_DELAY_DEFAULT = 200L
const val ANIM_DURATION_DEFAULT = 200L

class SearchInterestedAdapter(
    private val pos: Int,
    private var showAnim: Boolean,
    private val listener: SearchInterestedListener
) : RecyclerView.Adapter<SearchInterestedAdapter.MyViewHolder>() {

    private val items: MutableList<MenuItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemSearchGenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        holder.viewBinding.root.alpha = 1f

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
                duration - position * 15,
                delay + position * 30
            )
        }
    }

    override fun getItemCount(): Int = items.size


    fun setItems(items: List<MenuItem>) {
        this.items.clear()
        this.items.addAll(items)
    }

    inner class MyViewHolder(val viewBinding: ItemSearchGenderBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    @Keep
    data class MenuItem(
        val labelRes: String, val iconRes: Int
    )
    fun interface SearchInterestedListener {
        fun onViewClick(pos: Int)
    }

}