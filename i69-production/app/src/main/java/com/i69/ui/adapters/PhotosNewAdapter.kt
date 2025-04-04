package com.i69.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import com.i69.R
import com.i69.databinding.ProfilePhotoThumbnailLayoutBinding
import com.i69.ui.base.profile.PRIVATE
import com.i69.utils.loadImage

class PhotosNewAdapter(private val activity: Activity, private val listener: PhotoAdapterListener) :
    RecyclerView.Adapter<PhotosNewAdapter.MyViewHolder>() {

    val photos: MutableList<PhotosData> = ArrayList()
    var avtar_index: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        ProfilePhotoThumbnailLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            photo.loadImage(photos[holder.bindingAdapterPosition].link)
            removePhotoButton.setOnClickListener {
                if (avtar_index == holder.bindingAdapterPosition) {
                    avtar_index = 0
                } else if (holder.bindingAdapterPosition < avtar_index) {
                    avtar_index = avtar_index - 1
                }

                listener.onRemoveBtnClick(
                    holder.bindingAdapterPosition, photos[holder.bindingAdapterPosition]
                )
                notifyDataSetChanged()
            }

            if (photos[holder.bindingAdapterPosition].type == PRIVATE) {
                holder.viewBinding.ivPrivate.visibility = View.VISIBLE
            } else {
                holder.viewBinding.ivPrivate.visibility = View.GONE
            }

            photo.setOnClickListener(View.OnClickListener {
                if (photos[holder.bindingAdapterPosition].type != PRIVATE) {
                    avtar_index = position
                    notifyDataSetChanged()
                }
            })
            imgView.setOnClickListener {
                showFullScreenImage(photos[position].link)
            }
        }

        if (avtar_index == position) {
            holder.viewBinding.BB.visibility = View.VISIBLE
        } else {
            holder.viewBinding.BB.visibility = View.GONE

        }
    }

    override fun getItemCount() = photos.size

    private fun showFullScreenImage(link: String) {
        val dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        // Inflate the full-screen dialog layout
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.full_screen_image, null)

        // Set the PhotoView
        val photoView = view.findViewById<PhotoView>(R.id.photoView)
        val imgClose = view.findViewById<AppCompatImageView>(R.id.imgClose)
        photoView.loadImage(link) // Replace with your image source

        // Create and show the dialog
        dialog.setContentView(view)

        // Define top and bottom margins
        val topMargin = activity.resources.getDimension(com.intuit.sdp.R.dimen._175sdp).toInt() // Top margin in pixels
        val bottomMargin = activity.resources.getDimension(com.intuit.sdp.R.dimen._28sdp).toInt() // Bottom margin in pixels

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                activity.resources.displayMetrics.heightPixels - (topMargin + bottomMargin)
            )
            setBackgroundDrawableResource(android.R.color.transparent)

            // Adjust the position of the dialog to respect the top margin
            attributes = attributes.apply {
                gravity = android.view.Gravity.TOP
                y = topMargin
            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.show()

        imgClose.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    fun updateList(updated: List<PhotosData>) {
        photos.clear()
        photos.addAll(updated)
        notifyDataSetChanged()
    }

    fun addItem(newPhoto: PhotosData) {
        photos.add(newPhoto)
        if (photos.size == 1) notifyDataSetChanged() else notifyItemInserted(photos.size - 1)
    }

    fun removeItem(pos: Int) {
        photos.removeAt(pos)
        if (photos.isEmpty()) notifyDataSetChanged() else notifyItemRemoved(pos)
    }

    class MyViewHolder(val viewBinding: ProfilePhotoThumbnailLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    fun interface PhotoAdapterListener {
        fun onRemoveBtnClick(position: Int, photo_url: PhotosData)
    }

}

@Keep
data class PhotosData(
    val link: String = "", val type: String = ""
)