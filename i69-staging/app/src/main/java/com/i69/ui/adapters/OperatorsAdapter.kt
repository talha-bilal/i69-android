package com.i69.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.i69.databinding.ItemOperatorBinding


class OperatorsAdapter : RecyclerView.Adapter<OperatorsAdapter.OperatorsViewHolder>() {

    class OperatorsViewHolder(val binding: ItemOperatorBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: ((String?) -> Unit)? = null

    fun setOnItemClickListener(position: (String?) -> Unit) {
        onItemClickListener = position
    }


    private val diffCallback = object : DiffUtil.ItemCallback<String?>() {

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var operatorList: List<String?>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorsViewHolder {
        val binding = ItemOperatorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OperatorsViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return operatorList.size
    }

    override fun onBindViewHolder(holder: OperatorsViewHolder, position: Int) {
        val data = operatorList[position]
        holder.itemView.apply {

            with(holder) {
                binding.tvOperatorname.text = data
            }

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(data)
                }
            }
        }
    }

}
