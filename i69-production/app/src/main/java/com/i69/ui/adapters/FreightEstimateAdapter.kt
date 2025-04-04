package com.i69.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.i69.R
import com.i69.data.models.market.FreightEstimateResponse

class FreightEstimateAdapter(
    private val activity: Activity,
    private val freightEstimateResponse: MutableList<FreightEstimateResponse>,
    private val selectSingleFreightEstimate: SelectSingleFreightEstimate
) : BaseAdapter() {
    override fun getCount(): Int = freightEstimateResponse.size

    override fun getItem(position: Int): Any = freightEstimateResponse[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(activity)
        val view = convertView ?: inflater.inflate(R.layout.freight_estimate_item, parent, false)

        val item = getItem(position) as FreightEstimateResponse

        val tvShippingFees = view.findViewById<TextView>(R.id.tvShippingFees)
        val tvDeliveryDates = view.findViewById<TextView>(R.id.tvDeliveryDates)
        val tvCompany = view.findViewById<TextView>(R.id.tvCompany)
        val tvTracking = view.findViewById<TextView>(R.id.tvTracking)
        val layMain = view.findViewById<LinearLayout>(R.id.layMain)

        if (item.freeShipping.equals("true"))
            tvShippingFees?.text = "Free Shipping"
        else tvShippingFees?.text =
            "Shipping: " + item.shippingFeeFormat

        tvDeliveryDates?.text =
            "Delivery: " + item.deliveryDateDesc

        tvCompany.text = "via " + item.company

        if (item.tracking.equals("true"))
            tvTracking.text = "Tracking available"
        else tvTracking.text = "Tracking not available"

        layMain.setOnClickListener {
            selectSingleFreightEstimate.onSelectSingleFreightEstimate(item)
        }

        return view
    }

    interface SelectSingleFreightEstimate {
        fun onSelectSingleFreightEstimate(item: FreightEstimateResponse)
    }
}