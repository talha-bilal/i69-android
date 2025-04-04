package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.BaseAllPackageModel
import com.i69.databinding.SubDialogPackageItemLayoutBinding

class PurchasePlanMainAdapter(
    private val ctx: Context,
    private var allusermoments: List<BaseAllPackageModel>,
    val appStringConstant: AppStringConstant?,
    var planInterface: PlanInterface
) : RecyclerView.Adapter<PurchasePlanMainAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: PurchasePlanMainAdapter.ViewHolder, position: Int) {
        val item = allusermoments.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PurchasePlanMainAdapter.ViewHolder = ViewHolder(
        SubDialogPackageItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int {
        return allusermoments.size
    }

    inner class ViewHolder(val viewBinding: SubDialogPackageItemLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.stringConstant = appStringConstant
            viewBinding.tvMoreDetail.setOnClickListener {
                collapseExpandView()
            }
            viewBinding.ivDown.setOnClickListener {
                collapseExpandView()
            }
            viewBinding.tvPurchaseUsingCoin.setOnClickListener {
                val item = allusermoments[bindingAdapterPosition]
                planInterface.onSubscribeClick(
                    item.allPackage!!.id.toInt(),
                    item.allPackage.name,
                    item.allPackage.plans!![1]?.id!!.toInt(),
                    item.allPackage.plans!![1]!!.title!!
                )
            }
        }

        fun collapseExpandView() {
            val item = allusermoments[bindingAdapterPosition]
            if (item.isExpanded) {
                item.isExpanded = false
                viewBinding.clDetailContent.visibility = View.GONE
                viewBinding.ivDown.rotation = 0.0f
            } else {
                item.isExpanded = true
                viewBinding.clDetailContent.visibility = View.VISIBLE
                viewBinding.ivDown.rotation = 180.0f
            }
        }

        fun bind(item: BaseAllPackageModel) {
            viewBinding.purchaseUsingCoin.text = item.allPackage!!.name
            if (item.allPackage.id == "1") {
                viewBinding.clMainItem.background =
                    ContextCompat.getDrawable(ctx, R.drawable.ic_silver_plane_drawable)
            } else if (item.allPackage.id == "2") {
                viewBinding.clMainItem.background =
                    ContextCompat.getDrawable(ctx, R.drawable.ic_gold_plane_drawable)
            } else if (item.allPackage.id == "3") {
                viewBinding.clMainItem.background =
                    ContextCompat.getDrawable(ctx, R.drawable.ic_platinum_plane_drawable)
            }
            viewBinding.price.text =
                item.allPackage.plans!![1]?.priceInCoins.toString() + " " + ctx.getString(R.string.coins) + "\n" + item.allPackage.plans!![1]?.title
            val adapterCoinPrice = PlanCoinPriceSubAdapter(ctx, planInterface)
            viewBinding.recyclerViewCoins.adapter = adapterCoinPrice
            adapterCoinPrice.updateItemList(
                item.allPackage.plans,
                item.allPackage.name,
                item.allPackage.id.toInt()
            )
            if (item.isExpanded) {
                viewBinding.clDetailContent.visibility = View.VISIBLE
            } else {
                viewBinding.clDetailContent.visibility = View.GONE
            }
        }
    }

    interface PlanInterface {
        fun onSubscribeClick(
            selectedPackageId: Int,
            selectedPackageName: String,
            selectedPlanID: Int,
            selectedPlanTitle: String
        )
    }
}