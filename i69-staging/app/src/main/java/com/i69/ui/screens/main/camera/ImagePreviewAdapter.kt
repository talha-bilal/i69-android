package com.i69.ui.screens.main.camera

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.databinding.ItemCapturePreviewBinding
import com.otaliastudios.cameraview.filter.Filters

class ImagePreviewAdapter(
    private val context: Context,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCapturePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var binding: ItemCapturePreviewBinding
    private var data: Array<Filters> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCapturePreviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.root.setOnClickListener {
            onClick.invoke(position)
        }
        binding.filterName.text = data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshItems(data: Array<Filters>) {
        this.data = data
        notifyDataSetChanged()
    }

}