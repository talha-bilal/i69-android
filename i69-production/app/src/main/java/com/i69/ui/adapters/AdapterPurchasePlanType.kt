package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.i69.GetAllPackagesQuery
import com.i69.R
import com.i69.databinding.ListItemPlanNameBinding
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible

class AdapterPurchasePlanType(
    private val ctx: Context,

    private var allusermoments: List<GetAllPackagesQuery.AllPackage?>,
    var planInterface: PlanInterface
) : RecyclerView.Adapter<AdapterPurchasePlanType.ViewHolder>() {
    override fun onBindViewHolder(holder: AdapterPurchasePlanType.ViewHolder, position: Int) {

        val item = allusermoments.get(position)
        holder.bind(position, item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AdapterPurchasePlanType.ViewHolder = ViewHolder(
        ListItemPlanNameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )


    override fun getItemCount(): Int {
        return if (allusermoments == null) 0 else allusermoments.size
    }

    val hidePackagesList = mutableListOf<String>()

    fun hidePackages(hidePackagesList: MutableList<String>) {
        this.hidePackagesList.addAll(hidePackagesList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val viewBinding: ListItemPlanNameBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int, item: GetAllPackagesQuery.AllPackage?) {

            viewBinding.planTitle.text = item!!.name
            if (item.id == "1") {
                if (hidePackagesList.contains(item.id)) {
                    viewBinding.root.setViewGone()
                    planInterface.onClick(1, allusermoments[1])
                } else {
                    viewBinding.clPlanType.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.silver_plan
                        )
                    )
                }
            } else if (item.id == "2") {
                if (hidePackagesList.contains(item.id)) {
                    viewBinding.root.setViewGone()
                    planInterface.onClick(1, allusermoments[1])
                } else {
                    viewBinding.root.setViewVisible()
                    viewBinding.clPlanType.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx, R.color.gold_plan
                        )
                    )
                }
            } else if (item.id == "3") {
                viewBinding.clPlanType.setBackgroundColor(
                    ContextCompat.getColor(
                        ctx, R.color.platinum_plan
                    )
                )
            }

            viewBinding.cvPlanType.setOnClickListener(View.OnClickListener {
                planInterface.onClick(bindingAdapterPosition, item)
            })
        }
    }

    interface PlanInterface {
        fun onClick(index: Int, coinPrice: GetAllPackagesQuery.AllPackage?)
    }
}
