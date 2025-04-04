package com.i69.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.data.models.market.FilterSizeSkus

class SkuSizeAdapter(
    val activity: Activity?,
    private val filterSizeSkus: List<FilterSizeSkus>,
    private val clickSizeItemListener: ClickSizeItemListener
) :
    RecyclerView.Adapter<SkuSizeAdapter.ViewHolder>() {
    private var selctedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sku_size_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtSizeName.text = filterSizeSkus[position].size

        if (selctedPosition == position) {
            holder.cardTxtSizeName.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.black
                )
            )
            clickSizeItemListener.onClickSizeItemListener(filterSizeSkus[position])
        } else holder.cardTxtSizeName.setCardBackgroundColor(
            ContextCompat.getColor(
                activity!!,
                R.color.white
            )
        )
        holder.txtSizeName.setOnClickListener {
            selctedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return filterSizeSkus.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTxtSizeName: CardView = itemView.findViewById(R.id.cardTxtSizeName)
        val txtSizeName: TextView = itemView.findViewById(R.id.txtSizeName)
    }

    interface ClickSizeItemListener {
        fun onClickSizeItemListener(filterSizeSkus: FilterSizeSkus)
    }
}

