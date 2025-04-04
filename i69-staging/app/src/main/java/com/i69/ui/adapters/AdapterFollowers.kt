package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.GetUserQuery
import com.i69.applocalization.AppStringConstant
import com.i69.databinding.ItemFollowersBinding
import com.i69.utils.loadImage


class AdapterFollowers(
    var context: Context,
    var items: MutableList<GetUserQuery.FollowerUser?>,
    private val listener: FollowerListListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cView = ItemFollowersBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return MyViewHolder(cView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class MyViewHolder(var viewBinding: ItemFollowersBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: GetUserQuery.FollowerUser?) {

            val fullName = item!!.fullName
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }

            viewBinding.title.text = name
            viewBinding.subtitle.text = item.firstName

            if (item.avatarPhotos!!.size > 0) {
                val imgSrc = item.avatarPhotos.get(0)

                if (imgSrc != null) {
                    viewBinding.img.loadImage(imgSrc.url!!)
                } else {
                    viewBinding.img.loadImage("")
                }

                if (item.isConnected != null && item.isConnected) {
                    viewBinding.tvFolllow.text = AppStringConstant(context).following_tab
                } else {
                    viewBinding.tvFolllow.text = AppStringConstant(context).follow
                }
            }
            viewBinding.tvFollowing.text = AppStringConstant(context).remove

            viewBinding.tvFollowing.setOnClickListener {
                listener.onItemClick(item, true)
            }

            viewBinding.tvFolllow.setOnClickListener {
                listener.onItemClick(item)
            }
            viewBinding.title.setOnClickListener {
                listener.onUserProfileClick(item)
            }

            viewBinding.img.setOnClickListener {
                listener.onUserProfileClick(item)
            }
        }
    }

    interface FollowerListListener {
        fun onItemClick(followinfUser: GetUserQuery.FollowerUser?, isRemove: Boolean = false)

        fun onUserProfileClick(followinfUser: GetUserQuery.FollowerUser?)
    }
}
