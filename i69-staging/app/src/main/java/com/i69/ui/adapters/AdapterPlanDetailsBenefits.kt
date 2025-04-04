package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.i69.R
import com.i69.data.models.PlanBnefits
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible

class AdapterPlanDetailsBenefits(
    var context: Context, private val isSilverHidden: Boolean
) : RecyclerView.Adapter<AdapterPlanDetailsBenefits.CoinPriceViewHolder>() {

    private val itemList: ArrayList<PlanBnefits> = ArrayList()

    private var selectedPlanName = ""

    inner class CoinPriceViewHolder(iView: View) : RecyclerView.ViewHolder(iView) {
        val txtName = iView.findViewById<MaterialTextView>(R.id.purchasePackageTitle)
        val imgPlatnium = iView.findViewById<ImageView>(R.id.imgPlatnium)
        val imgGold = iView.findViewById<ImageView>(R.id.imgGold)
        val imgSilver = iView.findViewById<ImageView>(R.id.imgSilver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinPriceViewHolder {
        return CoinPriceViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_plan_details_benefits, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinPriceViewHolder, position: Int) {
        val coinPrice = itemList.get(holder.adapterPosition)
        holder.txtName.text = coinPrice.name

        if (isSilverHidden) holder.imgSilver.setViewGone()
        else holder.imgSilver.setViewVisible()
        if (coinPrice.isPlatnium) {
            holder.imgPlatnium.setImageResource(R.drawable.check_mark_right_round)
        } else {
            holder.imgPlatnium.setImageResource(R.drawable.delete_round)
        }

        if (coinPrice.isGold == true) {
            holder.imgGold.setImageResource(R.drawable.check_mark_right_round)

        } else {
            holder.imgGold.setImageResource(R.drawable.delete_round)
        }

        if (coinPrice.isSilver == true) {
            holder.imgSilver.setImageResource(R.drawable.check_mark_right_round)
        } else {
            holder.imgSilver.setImageResource(R.drawable.delete_round)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItemList(currentPlans: List<PlanBnefits>, selectedPlanName: String) {
        this.selectedPlanName = selectedPlanName
        itemList.clear()
        if (currentPlans != null) {
            itemList.addAll(currentPlans)
        }
        notifyDataSetChanged()

    }
}