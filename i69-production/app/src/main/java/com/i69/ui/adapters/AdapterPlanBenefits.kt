package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.i69.GetAllPackagesQuery
import com.i69.R

class AdapterPlanBenefits(
    var context: Context
) : RecyclerView.Adapter<AdapterPlanBenefits.CoinPriceViewHolder>() {

    private val itemList: ArrayList<GetAllPackagesQuery.Permission?> = ArrayList()

    inner class CoinPriceViewHolder(iView: View) : RecyclerView.ViewHolder(iView) {
        val tv_benefits = iView.findViewById<AppCompatTextView>(R.id.tv_benefits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinPriceViewHolder {
        return CoinPriceViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_purchase_plan_benifets, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinPriceViewHolder, position: Int) {
        val coinPrice = itemList.get(holder.adapterPosition)
        holder.tv_benefits.text = coinPrice?.description
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItemList(currentPlans: List<GetAllPackagesQuery.Permission?>?) {

        itemList.clear()
        if (currentPlans != null) {
            itemList.addAll(currentPlans)
        }
        notifyDataSetChanged()

    }
}