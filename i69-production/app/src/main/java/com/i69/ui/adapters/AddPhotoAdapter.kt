package com.i69.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.databinding.ButtonAddPhotoLayoutBinding

class AddPhotoAdapter(private val listener: AddPhotoAdapterListener) :
    RecyclerView.Adapter<AddPhotoAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        ButtonAddPhotoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            root.setOnClickListener {
                listener.onAddButtonClick(it)
            }
        }
    }

    override fun getItemCount() = 1

    class MyViewHolder(val viewBinding: ButtonAddPhotoLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    fun interface AddPhotoAdapterListener {
        fun onAddButtonClick(v: View)
    }
}