package com.i69.ui.adapters

import android.graphics.BlurMaskFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.i69.BuildConfig
import com.i69.data.models.MyPermission
import com.i69.data.models.User
import com.i69.data.remote.responses.DefaultPicker
import com.i69.databinding.ListItemLockSearchedUserBinding
import com.i69.databinding.ListItemUnlockFeatureBinding
import com.i69.utils.getSelectedValueFromDefaultPicker
import com.i69.utils.loadCircleImage


private const val TYPE_UNLOCK_PAID = 0
private const val TYPE_DEFAULT = 1

class LockUsersSearchListAdapter(
    private val listener: LockUserSearchListener,
    private val defaultPicker: DefaultPicker
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<User> = ArrayList()

    private var userhashPermission: MyPermission = MyPermission(false)

    private fun getViewHolderByType(type: Int, viewBinding: ViewDataBinding) =
        if (type == TYPE_DEFAULT) SearchedUserHolder(viewBinding as ListItemLockSearchedUserBinding) else UnlockHolder(
            viewBinding as ListItemUnlockFeatureBinding
        )

    private fun getViewDataBinding(viewType: Int, parent: ViewGroup) =
        if (viewType == TYPE_DEFAULT) ListItemLockSearchedUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        else ListItemUnlockFeatureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

    override fun getItemViewType(position: Int): Int = TYPE_DEFAULT

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder =
        getViewHolderByType(type, getViewDataBinding(type, parent))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_UNLOCK_PAID) {
            holder as UnlockHolder
        } else {
            holder as SearchedUserHolder
            val item = items[position]
            holder.bind(item)

            holder.viewBinding.root.setOnClickListener {
                holder.viewBinding.photoCover.transitionName = "profilePhoto"
                listener.onLockItemClick(holder.bindingAdapterPosition, item)
            }
            holder.viewBinding
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(updated: List<User>, userhashPermission: MyPermission) {
        items.clear()
        this.userhashPermission = userhashPermission
        if (!userhashPermission.hasPermission) {

        }
        items.addAll(updated)
        notifyDataSetChanged()
    }

    inner class UnlockHolder(val viewBinding: ListItemUnlockFeatureBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


    fun setBlurTextView(userTitle: MaterialTextView) {
        userTitle.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val radius: Float = userTitle.textSize / 3
        val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        userTitle.paint.setMaskFilter(filter)

    }

    inner class SearchedUserHolder(val viewBinding: ListItemLockSearchedUserBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(user: User) {
            var title = ""
            var subTitle = ""
            if (user.fullName.isNotEmpty()) {
                val fullName = user.fullName
                val name = if (fullName != null && fullName.length > 15) {
                    fullName.substring(0, minOf(fullName.length, 15))
                } else {
                    fullName
                }
                title += name
            }
            if (user.age != null) {
                val age = defaultPicker.agePicker.getSelectedValueFromDefaultPicker(user.age)
                if (age.isNotEmpty()) title += ", $age"
            }
            if (!user.work.isNullOrEmpty()) subTitle += (user.work + "\n")
            if (!user.education.isNullOrEmpty()) subTitle += user.education

            viewBinding.userTitle.text = title
            setBlurTextView(viewBinding.userTitle)


            if (subTitle.isEmpty()) {
                viewBinding.userSubTitle.visibility = View.GONE
            } else {
                viewBinding.userSubTitle.text = subTitle
                viewBinding.userSubTitle.visibility = View.VISIBLE

                setBlurTextView(viewBinding.userSubTitle)

            }
            if (user.avatarPhotos != null) {
                if (user.avatarPhotos!!.size != 0) {

                    if (user.avatarIndex!! <= user.avatarPhotos!!.size - 1) {
                        val imageUrl = user.avatarPhotos!!.get(user.avatarIndex!!).url?.replace(
                            "http://95.216.208.1:8000/media/", "${BuildConfig.BASE_URL}media/"
                        )
                        viewBinding.photoCover.loadCircleImage(
                            imageUrl.toString()
                        )
                    }
                }

            }
        }
    }

    interface LockUserSearchListener {
        fun onLockItemClick(position: Int, user: User)
    }

}