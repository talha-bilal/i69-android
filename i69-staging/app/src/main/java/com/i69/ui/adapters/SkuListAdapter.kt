package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.i69.R
import com.i69.data.models.market.SkuInfo

class SkuListAdapter(val context: Context, private val skuInfoList: List<SkuInfo>) : BaseAdapter() {
    override fun getCount(): Int = skuInfoList.size

    override fun getItem(position: Int): Any = skuInfoList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.sku_list_item, parent, false)

        val item = getItem(position) as SkuInfo

        val parts = item.skuAttr?.split("#")
        val skuName = view.findViewById<TextView>(R.id.skuName)
        skuName.text = parts?.last()
        return view
    }
}