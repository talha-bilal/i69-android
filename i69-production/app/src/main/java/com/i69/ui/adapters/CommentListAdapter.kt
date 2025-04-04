package com.i69.ui.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.databinding.ItemCommentMomentBinding
import com.i69.ui.viewModels.CommentsModel
import com.i69.utils.loadCircleImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CommentListAdapter(
    private val ctx: Context,
    private val listener: ClickPerformListener,
    private var allusermoments: ArrayList<CommentsModel>?,
    private var momentAddCommentFragment: CommentReplyListAdapter.ClickonListener,
    private var isAdminOfMoments: Boolean,
    private val userId: String
) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onBindViewHolder(holder: CommentListAdapter.ViewHolder, position: Int) {

        val item = allusermoments?.get(position)
        holder.bind(position, item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CommentListAdapter.ViewHolder = ViewHolder(
        ItemCommentMomentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )


    override fun getItemCount(): Int {
        return if (allusermoments == null) 0 else allusermoments?.size!!
    }

    fun updateList(updatedList: List<CommentsModel>) {
        allusermoments!!.clear()
        allusermoments!!.addAll(updatedList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val viewBinding: ItemCommentMomentBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int, item: CommentsModel?) {

            viewBinding.txtMomentDescription.text = item!!.commenttext
            viewBinding.imgNearbyUser.loadCircleImage(item.userurl.toString())
            val name = if (item.username != null && item.username!!.length > 15) {
                item.username!!.substring(0, minOf(item.username!!.length, 15))
            } else {
                item.username
            }
            viewBinding.lblItemNearbyName.text = name?.uppercase()
            viewBinding.txtNearbyUserLikeCount.text = item.cmtlikes
            val momentTime = try {
                var text = item.timeago
                text = text?.replace("T", " ")?.substring(0, text.indexOf(".")).toString()
                formatter.parse(text)
            } catch (e: Exception) {
                e.printStackTrace()
                Date()
            }

            viewBinding.txtTimeAgo.text = DateUtils.getRelativeTimeSpanString(
                momentTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS
            )


            val headlineAdapter =
                CommentReplyListAdapter(ctx, momentAddCommentFragment, item.replylist)
            viewBinding.rvReplies.adapter = headlineAdapter
            viewBinding.rvReplies.layoutManager = LinearLayoutManager(ctx)
            viewBinding.txtMomentDescription.setOnClickListener { onItemClicked(item) }
            if (item.isExpanded!!) {
                viewBinding.rvReplies.visibility = View.VISIBLE
            } else {
                viewBinding.rvReplies.visibility = View.GONE
            }
            viewBinding.imgNearbySharedMomentOption.setOnClickListener {
                val popup = PopupMenu(ctx, viewBinding.imgNearbySharedMomentOption)
                popup.menuInflater.inflate(R.menu.comment_more_option, popup.menu)
                if (isAdminOfMoments || userId.equals(item.uid)) {
                    popup.menu.findItem(R.id.nav_item_delete).setVisible(true)
                    popup.menu.findItem(R.id.nav_item_report).setVisible(false)

                } else {
                    popup.menu.findItem(R.id.nav_item_delete).setVisible(false)
                    popup.menu.findItem(R.id.nav_item_report).setVisible(true)
                }
                popup.setOnMenuItemClickListener { itemd: MenuItem? ->
                    when (itemd!!.itemId) {
                        R.id.nav_item_delete -> {
                            listener.oncommentDelete(bindingAdapterPosition, item)
                        }

                        R.id.nav_item_report -> {
                            listener.oncommentReport(bindingAdapterPosition, item)
                        }
                    }
                    true
                }
                popup.show()
            }

            viewBinding.imgNearbyUserComment.setOnClickListener {
                listener.onreply(bindingAdapterPosition, item)
            }
            viewBinding.imgNearbyUserLikes.setOnClickListener {
                listener.oncommentLike(bindingAdapterPosition, item)
            }
            viewBinding.lblItemNearbyName.setOnClickListener {
                listener.onUsernameClick(bindingAdapterPosition, item)
            }

            viewBinding.imgNearbyUser.setOnClickListener {
                listener.onUsernameClick(bindingAdapterPosition, item)
            }
        }

        private fun onItemClicked(newspaperModel: CommentsModel?) {
            newspaperModel?.isExpanded = !newspaperModel?.isExpanded!!
            notifyDataSetChanged()
        }
    }

    interface ClickPerformListener {
        fun onreply(position: Int, models: CommentsModel)
        fun oncommentLike(position: Int, models: CommentsModel)
        fun oncommentDelete(position: Int, models: CommentsModel)
        fun oncommentReport(position: Int, models: CommentsModel)
        fun onUsernameClick(
            position: Int, models: CommentsModel
        )
    }
}