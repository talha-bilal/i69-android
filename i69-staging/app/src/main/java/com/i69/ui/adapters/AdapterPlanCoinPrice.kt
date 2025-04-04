package com.i69.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.i69.GetAllPackagesQuery
import com.i69.R

class AdapterPlanCoinPrice(
    var context: Context, var coinPriceInterface: PlanCoinPriceInterface
) : RecyclerView.Adapter<AdapterPlanCoinPrice.CoinPriceViewHolder>() {

    private val itemList: ArrayList<GetAllPackagesQuery.Plan?> = ArrayList()
    private var selectedPlanId = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinPriceViewHolder {
        return CoinPriceViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_purchase_plan_comper, parent, false)
        )
    }

    inner class CoinPriceViewHolder(iView: View) : RecyclerView.ViewHolder(iView) {
        val constarinetPurchasePlan =
            iView.findViewById<ConstraintLayout>(R.id.constarinet_purchase_plan)
        val numberOfCoins = iView.findViewById<TextView>(R.id.numberOfCoins)
        val price = iView.findViewById<TextView>(R.id.price)
        val txtPersentag = iView.findViewById<TextView>(R.id.txt_persentag)
        val description = iView.findViewById<TextView>(R.id.description)
        val subScribePlan = iView.findViewById<LinearLayout>(R.id.subScribePlan)
        val llComparePrice = iView.findViewById<LinearLayout>(R.id.llComparePrice)

        init {
            subScribePlan.setOnClickListener {
                val coinPrice = itemList.get(adapterPosition)
                coinPriceInterface.onClick(adapterPosition, coinPrice)
            }
        }
    }

    override fun onBindViewHolder(holder: CoinPriceViewHolder, position: Int) {
        val coinPrice = itemList.get(holder.adapterPosition)
        holder.numberOfCoins.text = coinPrice?.title
        holder.description.text = coinPrice?.`package`!!.name
        if (coinPrice.isOnDiscount) {
            holder.txtPersentag.visibility = View.VISIBLE
            holder.txtPersentag.text = "${coinPrice.dicountedPriceInCoins}"
            Log.e("MyCoinPriseDiscounts", "${coinPrice.dicountedPriceInCoins}")
        } else {
            holder.txtPersentag.visibility = View.GONE
        }
        val value = coinPrice.priceInCoins?.toFloat()
        holder.price.text = value.toString()
        if (selectedPlanId == "1") {
            holder.llComparePrice.background =
                ContextCompat.getDrawable(context, R.drawable.linyer_comperprice_subcribe)
            holder.constarinetPurchasePlan.background =
                ContextCompat.getDrawable(context, R.drawable.ic_silver_plane_drawable)
        } else if (selectedPlanId == "2") {
            holder.llComparePrice.background =
                ContextCompat.getDrawable(context, R.drawable.linear_comper_price_silver)
            holder.constarinetPurchasePlan.background =
                ContextCompat.getDrawable(context, R.drawable.ic_gold_plane_drawable)
        } else if (selectedPlanId == "3") {
            holder.llComparePrice.background =
                ContextCompat.getDrawable(context, R.drawable.linyer_comperprice_subcribe)
            holder.constarinetPurchasePlan.background =
                ContextCompat.getDrawable(context, R.drawable.ic_platinum_plane_drawable)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItemList(currentPlans: List<GetAllPackagesQuery.Plan?>?, selectedPlanId: String) {
        this.selectedPlanId = selectedPlanId
        itemList.clear()
        if (currentPlans != null) {
            itemList.addAll(currentPlans)
        }
        notifyDataSetChanged()

    }

    interface PlanCoinPriceInterface {
        fun onClick(index: Int, coinPrice: GetAllPackagesQuery.Plan?)
    }
}
