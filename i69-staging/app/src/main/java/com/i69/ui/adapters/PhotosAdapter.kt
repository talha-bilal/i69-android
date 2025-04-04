package com.i69.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.databinding.ProfilePhotoThumbnailLayoutBinding
import com.i69.utils.loadImage

class PhotosAdapter(private val listener: PhotoAdapterListener) :
    RecyclerView.Adapter<PhotosAdapter.MyViewHolder>() {

    val photos: ArrayList<String> = ArrayList()
    var avtar_index: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        ProfilePhotoThumbnailLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            photo.loadImage(photos[holder.bindingAdapterPosition])
            removePhotoButton.setOnClickListener {
                if (avtar_index == holder.bindingAdapterPosition) {
                    avtar_index = 0
                } else if (holder.bindingAdapterPosition < avtar_index) {
                    avtar_index = avtar_index - 1
                }

                listener.onRemoveBtnClick(
                    holder.bindingAdapterPosition,
                    photos[holder.bindingAdapterPosition]
                )
                notifyDataSetChanged()
            }

            photo.setOnClickListener(View.OnClickListener {
                avtar_index = position
                notifyDataSetChanged()
            })
        }

        if (avtar_index == position) {
            holder.viewBinding.BB.visibility = View.VISIBLE
        } else {
            holder.viewBinding.BB.visibility = View.GONE

        }
    }

    override fun getItemCount() = photos.size

    fun updateList(updated: List<String>) {
        photos.clear()
        photos.addAll(updated)
        notifyDataSetChanged()
    }


    class MyViewHolder(val viewBinding: ProfilePhotoThumbnailLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    fun interface PhotoAdapterListener {
        fun onRemoveBtnClick(position: Int, photo_url: String)
    }

}