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
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.User
import com.i69.databinding.ItemAddNewNearbyThumbBinding
import com.i69.databinding.ItemNearbyThumbBinding
import com.i69.utils.ApiUtil
import com.i69.utils.loadCircleImage
import com.i69.utils.setViewGone


private const val TYPE_NEW_STORY = 0
private const val TYPE_DEFAULT = 1

class UserMultiStoriesAdapter(
    private val ctx: Context,
    var mUser: User?,
    private val listener: UserMultiStoryListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TAG: String = UserMultiStoriesAdapter::class.java.simpleName
    private fun getViewHolderByType(type: Int, viewBinding: ViewDataBinding) =
        if (type == TYPE_DEFAULT) {
            UserStoryHolder(viewBinding as ItemNearbyThumbBinding)
        } else {
            NewUserStoryHolder(viewBinding as ItemAddNewNearbyThumbBinding)
        }

    private fun getViewDataBinding(viewType: Int, parent: ViewGroup) =
        if (viewType == TYPE_DEFAULT) {
            ItemNearbyThumbBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            ItemAddNewNearbyThumbBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

    override fun getItemViewType(position: Int): Int = if (position == 0) {
        TYPE_NEW_STORY
    } else {
        TYPE_DEFAULT
    }

    override fun getItemCount() = storyList.size.plus(1)

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
                            "${BuildConfig.BASE_URL}media/",
                            "${BuildConfig.BASE_URL}media/"
                        ).toString()
                        Glide.with(ctx).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .optionalCircleCrop()
                            .into(holder.viewBinding.ivProfile)
                    }
                }
            }

            holder.viewBinding.root.setOnClickListener {
                val inflater =
                    ctx.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.layout_attach_story, null)
                view.findViewById<TextView>(R.id.header_title).text =
                    AppStringConstant1.select_story_pic

                val popupWindow = PopupWindow(
                    view,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    true
                )

                val llCamera = view.findViewById<View>(R.id.llCamera) as LinearLayoutCompat
                val llGallery = view.findViewById<View>(R.id.ll_gallery) as LinearLayoutCompat
                llCamera.setOnClickListener {
                    listener.onAddNewUserStoryClick(isCamera = true)
                    popupWindow.dismiss()
                }
                llGallery.setOnClickListener {
                    listener.onAddNewUserStoryClick(isCamera = false)
                    popupWindow.dismiss()
                }
                popupWindow.showAsDropDown(holder.viewBinding.root, -153, 0)
            }

            holder.viewBinding.addstorytext.text =
                if (mUser?.userLanguageCode == "de" || mUser?.userLanguageCode == "ru" || mUser?.userLanguageCode == "no" || mUser?.userLanguageCode == "nl") {
                    AppStringConstant1.add_story.lowercase()
                } else {
                    AppStringConstant1.add_story
                }
        } else {
            holder as UserStoryHolder
            if (storyList.size > position - 1) {
                val item = storyList.get(position - 1)
                holder.bind(item)
                holder.viewBinding.root.setOnClickListener {
                    listener.onUserMultiStoryClick(holder.bindingAdapterPosition, item!!)
                }
            }
        }
    }

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
        fun bind(item: GetAllUserMultiStoriesQuery.AllUserMultiStory?) {
            val fullName = item?.user?.fullName
            val name = if (fullName != null && fullName.length > 15) {
                fullName.substring(0, minOf(fullName.length, 15))
            } else {
                fullName
            }
            viewBinding.txtItemNearbyName.text = name
            val storyImage: String
            val node = item?.stories?.edges?.get(0)?.node
            val user = item?.user
            if (node?.fileType.equals("video")) {
                storyImage = if (!BuildConfig.USE_S3) {
                    if (node?.thumbnail.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) {
                        node?.thumbnail.toString()
                    } else if (node?.thumbnail.toString().startsWith(ApiUtil.S3_URL)) {
                        node?.thumbnail.toString()
                    } else {
                        "${BuildConfig.BASE_URL}${node?.thumbnail}"
                    }
                } else if (node?.thumbnail.toString()
                        .startsWith(ApiUtil.S3_URL)
                ) {
                    node?.thumbnail.toString()
                } else if (node?.thumbnail.toString().startsWith(ApiUtil.S3_URL)) {
                    node?.thumbnail.toString()
                } else if (node?.thumbnail.toString()
                        .startsWith(BuildConfig.BASE_URL)
                ) {
                    node?.thumbnail.toString()
                } else {
                    ApiUtil.S3_URL.plus(node?.thumbnail.toString())
                }
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
            Log.e(TAG,"User: ${user?.avatarPhotos}")
            Log.e(TAG,"AvatarIndex: ${user?.avatarIndex}")
            val url: String =
                if (user?.avatarPhotos != null && (user.avatarPhotos.isNotEmpty()) && (user.avatarIndex < user.avatarPhotos.size)) {
                    if (!BuildConfig.USE_S3) {
                        if (user.avatarPhotos[user.avatarIndex].url.toString()
                                .startsWith(BuildConfig.BASE_URL)
                        ) {
                            user.avatarPhotos[user.avatarIndex].url.toString()
                        } else if (user.avatarPhotos[user.avatarIndex].url.toString()
                                .startsWith(ApiUtil.S3_URL)
                        ) {
                            user.avatarPhotos[user.avatarIndex].url.toString()
                        } else {
                            "${BuildConfig.BASE_URL}${user.avatarPhotos[user.avatarIndex].url}"
                        }
                    } else if (user.avatarPhotos[user.avatarIndex].url.toString()
                            .startsWith(ApiUtil.S3_URL)
                    ) {
                        user.avatarPhotos[user.avatarIndex].url.toString()
                    } else if (user.avatarPhotos[user.avatarIndex].url.toString()
                            .startsWith(ApiUtil.S3_URL)
                    ) {
                        user.avatarPhotos[user.avatarIndex].url.toString()
                    } else if (user.avatarPhotos[user.avatarIndex].url.toString()
                            .startsWith(BuildConfig.BASE_URL)
                    ) {
                        user.avatarPhotos[user.avatarIndex].url.toString()
                    } else {
                        ApiUtil.S3_URL.plus(user.avatarPhotos[user.avatarIndex].url.toString())
                    }
                } else {
                    ""
                }
            Log.e(TAG,"bind: $url")
            Log.e(TAG,"url: $url")

            viewBinding.progressindicatorStories.blockCount = item?.stories?.edges!!.size
            val profileUrl =
                url.replace("http://95.216.208.1:8000/media/", "${BuildConfig.BASE_URL}media/")
            Log.e(TAG,"ProfileUrl: $profileUrl")
            viewBinding.imgNearbyProfile.loadCircleImage(profileUrl)
        }
    }

    private val diffUtilCallBack =
        object : DiffUtil.ItemCallback<GetAllUserMultiStoriesQuery.AllUserMultiStory?>() {

            override fun areItemsTheSame(
                oldItem: GetAllUserMultiStoriesQuery.AllUserMultiStory,
                newItem: GetAllUserMultiStoriesQuery.AllUserMultiStory
            ): Boolean {
                return oldItem.user.id == newItem.user.id && oldItem.batchNumber == newItem.batchNumber
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: GetAllUserMultiStoriesQuery.AllUserMultiStory,
                newItem: GetAllUserMultiStoriesQuery.AllUserMultiStory
            ): Boolean {

                return if (oldItem.user.id == newItem.user.id) {
                    if (oldItem.batchNumber == newItem.batchNumber) {
                        oldItem.stories.edges.size == newItem.stories.edges.size
                    } else {
                        false
                    }
                } else {
                    false
                }
            }

        }

    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    var storyList: List<GetAllUserMultiStoriesQuery.AllUserMultiStory?>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    interface UserMultiStoryListener {
        fun onUserMultiStoryClick(
            position: Int, userStory: GetAllUserMultiStoriesQuery.AllUserMultiStory
        )

        fun onAddNewUserStoryClick(isCamera: Boolean)
    }
}