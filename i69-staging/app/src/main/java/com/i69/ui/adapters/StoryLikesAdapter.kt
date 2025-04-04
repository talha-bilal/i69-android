package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.i69.R
import com.i69.databinding.ItemLikesNewBinding
import com.i69.ui.viewModels.CommentsModel
import com.i69.utils.loadCircleImage
import java.text.SimpleDateFormat
import java.util.*


class StoryLikesAdapter(
    private val ctx: Context,
    private val alldatas: ArrayList<CommentsModel>,
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private var onProfileClickListener: ((CommentsModel) -> Unit)? = null

    fun userProfileClicked(commentModel: (CommentsModel) -> Unit) {
        onProfileClickListener = commentModel
    }

    fun updateList(updatedList: ArrayList<CommentsModel>) {
        alldatas.clear()
        alldatas.addAll(updatedList)
        notifyDataSetChanged()
    }


    fun add(r: CommentsModel) {
        alldatas.add(r)
        notifyItemInserted(alldatas.size - 1)
    }

    fun addAll(newdata: ArrayList<CommentsModel>) {
        alldatas.clear()
        newdata.indices.forEach { i ->
            add(newdata[i])
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as NotificationHolder
        val item = alldatas[position]
        holder.bind(position, item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        NotificationHolder(
            ItemLikesNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = alldatas?.size ?: 0
    inner class NotificationHolder(val viewBinding: ItemLikesNewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int, item: CommentsModel) {
            viewBinding.txtNotificationTitle.text = item.commenttext
            if (item.userurl != null) {
                viewBinding.notificationIconBell.loadCircleImage(item.userurl!!)
            } else {
                viewBinding.notificationIconBell.setImageResource(R.drawable.ic_default_user)
            }
            viewBinding.root?.setOnClickListener {
                onProfileClickListener?.let { click ->
                    click(item)
                }
            }

        }
    }
}