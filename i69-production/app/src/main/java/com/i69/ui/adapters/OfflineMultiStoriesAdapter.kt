package com.i69.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.i69.BuildConfig
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.OfflineStory
import com.i69.data.models.User
import com.i69.databinding.ItemAddNewNearbyThumbBinding
import com.i69.databinding.ItemNearbyThumbBinding
import com.i69.utils.ApiUtil
import com.i69.utils.loadCircleImage
import com.i69.utils.setViewGone
import java.util.Locale

private const val TYPE_NEW_STORY = 0
private const val TYPE_DEFAULT = 1

class OfflineMultiStoriesAdapter(
    private val ctx: Context,
    var mUser: User?,
    private val listener: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TAG: String = OfflineMultiStoriesAdapter::class.java.simpleName
    private fun getViewHolderByType(type: Int, viewBinding: ViewDataBinding) =
        if (type == TYPE_DEFAULT) UserStoryHolder(viewBinding as ItemNearbyThumbBinding)
        else NewUserStoryHolder(
            viewBinding as ItemAddNewNearbyThumbBinding
        )

    private fun getViewDataBinding(viewType: Int, parent: ViewGroup) =
        if (viewType == TYPE_DEFAULT) ItemNearbyThumbBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        else ItemAddNewNearbyThumbBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

    override fun getItemViewType(position: Int): Int =
        if (position == 0) TYPE_NEW_STORY else TYPE_DEFAULT

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return getViewHolderByType(type, getViewDataBinding(type, parent))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NEW_STORY) {
            holder as NewUserStoryHolder
            mUser?.let {
                it.avatarPhotos?.let { avtarPhoto ->
                    if (it.avatarIndex != null && avtarPhoto.size > it.avatarIndex!!) {
                        val imageUrl = avtarPhoto[it.avatarIndex!!].url?.replace(
                            "${BuildConfig.BASE_URL}media/", "${BuildConfig.BASE_URL}media/"
                        ).toString()
                        Glide.with(ctx).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .optionalCircleCrop()
                            .into(holder.viewBinding.ivProfile)
                    }
                }
            }

            holder.viewBinding.root.setOnClickListener {
                val inflater = ctx.applicationContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
                ) as LayoutInflater
                var view = inflater.inflate(R.layout.layout_attach_story, null)
                view.findViewById<TextView>(R.id.header_title).text =
                    AppStringConstant1.select_story_pic

                var mypopupWindow = PopupWindow(
                    view,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    true
                )
                var llCamera = view.findViewById<View>(R.id.llCamera) as LinearLayoutCompat
                var llGallary = view.findViewById<View>(R.id.ll_gallery) as LinearLayoutCompat
                llCamera.setOnClickListener {
                    listener.invoke()
                    mypopupWindow.dismiss()
                }

                llGallary.setOnClickListener {
                    listener.invoke()
                    mypopupWindow.dismiss()
                }
                mypopupWindow.showAsDropDown(holder.viewBinding.root, -153, 0)
            }
            if (mUser?.userLanguageCode == "de" || mUser?.userLanguageCode == "ru" || mUser?.userLanguageCode == "no" || mUser?.userLanguageCode == "nl") {
                holder.viewBinding.addstorytext.text =
                    AppStringConstant1.add_story.lowercase(Locale.getDefault())
            } else {
                holder.viewBinding.addstorytext.text = AppStringConstant1.add_story
            }
        } else {
            holder as UserStoryHolder
            if (storyList.size > position - 1) {
                val item = storyList.get(position - 1)
                holder.bind(item)

                holder.viewBinding.root.setOnClickListener {
                    listener.invoke()
                }
            }
        }
    }

    override fun getItemCount() = storyList.size.plus(1)

    inner class NewUserStoryHolder(val viewBinding: ItemAddNewNearbyThumbBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            if (mUser?.userLanguageCode == "de" || mUser?.userLanguageCode == "ru" || mUser?.userLanguageCode == "no" || mUser?.userLanguageCode == "nl") {
                val layoutParam =
                    viewBinding.addstorytext.layoutParams as ConstraintLayout.LayoutParams
                layoutParam.setMargins(0, 0, 0, 0)
            }
        }
    }

    inner class UserStoryHolder(val viewBinding: ItemNearbyThumbBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: OfflineStory?) {
            Log.e(TAG,"OfflineStory : $item")
            val title = item?.stories?.user?.fullName
            viewBinding.txtItemNearbyName.text = title
            val storyImage: String
            val node = item?.stories?.stories?.edges?.get(0)?.node
            val user = item?.stories?.user
            if (node?.fileType.equals("video")) {
                storyImage = if (!BuildConfig.USE_S3) {
                    if (node?.thumbnail.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) node?.thumbnail.toString()
                    else "${BuildConfig.BASE_URL}${node?.thumbnail}"
                } else if (node?.thumbnail.toString()
                        .startsWith(ApiUtil.S3_URL)
                ) node?.thumbnail.toString()
                else ApiUtil.S3_URL.plus(node?.thumbnail.toString())
                viewBinding.ivPlay.setViewGone()
            } else {
                viewBinding.ivPlay.setViewGone()
                storyImage = if (!BuildConfig.USE_S3) {
                    if (node?.file.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) node?.file.toString()
                    else "${BuildConfig.BASE_URL}${node?.file.toString()}"
                } else if (node?.file.toString().startsWith(ApiUtil.S3_URL)) node?.file.toString()
                else ApiUtil.S3_URL.plus(node?.file.toString())
            }

            Log.e(TAG,"UMSA thumbnail : $storyImage")
            viewBinding.imgUserStory.loadCircleImage(storyImage, 0)

            val url: String =
                if (user?.avatarPhotos != null && (user.avatarPhotos.isNotEmpty()) && (user.avatarIndex < user.avatarPhotos.size)) {
                    if (!BuildConfig.USE_S3) {
                        if (user.avatarPhotos[user.avatarIndex].url.toString()
                                .startsWith(BuildConfig.BASE_URL)
                        ) user.avatarPhotos[user.avatarIndex].url.toString()
                        else "${BuildConfig.BASE_URL}${user.avatarPhotos[user.avatarIndex].url}"
                    } else if (user.avatarPhotos[user.avatarIndex].url.toString()
                            .startsWith(ApiUtil.S3_URL)
                    ) user.avatarPhotos[user.avatarIndex].url.toString()
                    else ApiUtil.S3_URL.plus(user.avatarPhotos[user.avatarIndex].url.toString())
                } else ""
            Log.e(TAG,"bind: $url")

            if (item?.stories?.stories != null) {
                viewBinding.progressindicatorStories.blockCount =
                    item.stories?.stories?.edges!!.size
            }
            viewBinding.imgNearbyProfile.loadCircleImage(
                url.replace(
                    "http://95.216.208.1:8000/media/", "${BuildConfig.BASE_URL}media/"
                ).toString()
            )
        }
    }

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<OfflineStory?>() {

        override fun areItemsTheSame(
            oldItem: OfflineStory, newItem: OfflineStory
        ): Boolean {
            return oldItem.stories?.user?.id == newItem.stories?.user?.id && oldItem.stories?.batchNumber == newItem.stories?.batchNumber

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: OfflineStory, newItem: OfflineStory
        ): Boolean {
            var isEquall = false
            if (oldItem.stories?.user?.id == newItem.stories?.user?.id) {
                if (oldItem.stories?.batchNumber == newItem.stories?.batchNumber) {
                    if (oldItem.stories?.stories?.edges != null && newItem.stories?.stories?.edges != null) {
                        if (oldItem.stories?.stories?.edges?.size == newItem.stories?.stories?.edges?.size) {
                            isEquall = true
                        }
                    }
                } else {
                    isEquall = false
                }
            } else {
                isEquall = false
            }
            return isEquall
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    var storyList: List<OfflineStory?>
        get() = differ.currentList
        set(value) = differ.submitList(value)
}