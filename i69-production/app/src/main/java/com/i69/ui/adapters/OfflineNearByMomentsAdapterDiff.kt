package com.i69.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.i69.data.models.Moment

class OfflineNearByMomentsAdapterDiff(
    private val oldList: List<Moment>, private val newList: List<Moment>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].node?.node?.pk == newList[newItemPosition].node?.node?.pk
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].node?.node?.pk != newList[newItemPosition].node?.node?.pk -> false
            else -> true
        }
    }
}