package com.i69.ui.screens.main.market.store

import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.i69.R
import com.i69.data.models.market.SkuInfo
import com.i69.databinding.FragmentBuyNowBinding
import com.i69.ui.adapters.PaymentMethodsAdapter
import com.i69.ui.adapters.SkuListAdapter
import com.i69.ui.base.BaseFragment
import com.i69.utils.displayDate

class BuyNowFragment : BaseFragment<FragmentBuyNowBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBuyNowBinding.inflate(inflater, container, false).apply { }

    override fun initObservers() {

    }

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        binding?.imgMorePaymentMethods?.setOnClickListener {
            val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.payment_methods_popup, null)
            dialog.setContentView(view)
            val imgClose = view.findViewById<AppCompatImageView>(R.id.imgClose)
            val listPaymentMethods = view.findViewById<ListView>(R.id.listPaymentMethods)
            val skuInfoList = mutableListOf<SkuInfo>()
            val adapter = PaymentMethodsAdapter(requireActivity(), skuInfoList)
            listPaymentMethods.adapter = adapter
            imgClose.setOnClickListener {
                if (dialog.isShowing)
                    dialog.dismiss()
            }
            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawableResource(android.R.color.transparent)

                // Adjust the position of the dialog to respect the top margin
                attributes = attributes.apply {
                    gravity = Gravity.CENTER
                }
            }
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.show()
        }
        binding?.imgPromoCode?.setOnClickListener {
            val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.promo_code_popup, null)
            dialog.setContentView(view)
            val imgClose = view.findViewById<AppCompatImageView>(R.id.imgClose)
            imgClose.setOnClickListener {
                if (dialog.isShowing)
                    dialog.dismiss()
            }
            dialog.window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawableResource(android.R.color.transparent)

                // Adjust the position of the dialog to respect the top margin
                attributes = attributes.apply {
                    gravity = Gravity.CENTER
                }
            }
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.show()
        }
    }
}