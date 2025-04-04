package com.i69.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.i69.GetAllUserMomentsQuery

class NearByMomentsAdapterDiff(
    private val oldList: List<GetAllUserMomentsQuery.Edge>,
    private val newList: List<GetAllUserMomentsQuery.Edge>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].node?.pk == newList[newItemPosition].node?.pk
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].node?.pk != newList[newItemPosition].node?.pk -> false
            else -> true
        }
    }
}