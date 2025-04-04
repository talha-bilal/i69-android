package com.i69.ui.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.databinding.ItemCommentMomentBinding
import com.i69.ui.screens.main.moment.PlayUserStoryDialogFragment
import com.i69.ui.viewModels.CommentsModel
import com.i69.utils.loadCircleImage
import java.text.SimpleDateFormat
import java.util.*

class VideoStoryCommentListAdapter(
    private val ctx: Context,
    private val listener: PlayUserStoryDialogFragment,
    private var allusermoments: ArrayList<CommentsModel>?,
    private var momentAddCommentFragment: PlayUserStoryDialogFragment
) : RecyclerView.Adapter<VideoStoryCommentListAdapter.ViewHolder>() {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onBindViewHolder(holder: VideoStoryCommentListAdapter.ViewHolder, position: Int) {
        val item = allusermoments?.get(position)
        holder.bind(position, item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoStoryCommentListAdapter.ViewHolder =
        ViewHolder(
            ItemCommentMomentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
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

            viewBinding.txtMomentDescription.setText(item!!.commenttext)
            viewBinding.imgNearbyUser.loadCircleImage(item!!.userurl.toString())
            viewBinding.lblItemNearbyName.setText(item.username?.uppercase())
            viewBinding.txtNearbyUserLikeCount.setText(item.cmtlikes)


            var text = item.timeago
            text = text!!.replace("T", " ").substring(0, text.indexOf("."))
            val momentTime = formatter.parse(text)

            viewBinding.txtTimeAgo.text = DateUtils.getRelativeTimeSpanString(
                momentTime.time,
                Date().time,
                DateUtils.MINUTE_IN_MILLIS
            )
            viewBinding.txtMomentDescription.setOnClickListener { onItemClicked(item) }
            viewBinding.imgNearbyUserComment.setOnClickListener(View.OnClickListener {
                listener.onreply(bindingAdapterPosition, item)
            })
            viewBinding.imgNearbyUserLikes.setOnClickListener{
                listener.oncommentLike(bindingAdapterPosition, item)
            }
            viewBinding.lblItemNearbyName.setOnClickListener{
                listener.onUsernameClick(bindingAdapterPosition, item)
            }

            viewBinding.imgNearbyUser.setOnClickListener{
                listener.onUsernameClick(bindingAdapterPosition, item)
            }
        }

        private fun onItemClicked(newspaperModel: CommentsModel?) {
            newspaperModel?.isExpanded = !newspaperModel?.isExpanded!!
            notifyDataSetChanged()
        }
    }
}