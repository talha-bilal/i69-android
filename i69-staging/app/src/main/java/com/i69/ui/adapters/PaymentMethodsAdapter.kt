package com.i69.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.i69.R
import com.i69.data.models.market.SkuInfo

class PaymentMethodsAdapter(val activity: Activity, private val skuInfoList: List<SkuInfo>) :
    BaseAdapter() {
    override fun getCount(): Int = 5

    override fun getItem(position: Int): Any = skuInfoList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.payment_method_item, parent, false)

        return view
    }
}