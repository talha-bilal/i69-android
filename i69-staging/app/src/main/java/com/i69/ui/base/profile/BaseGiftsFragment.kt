package com.i69.ui.base.profile

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.i69.R
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.FragmentGiftsBinding
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.gifts.FragmentRealGifts
import com.i69.gifts.FragmentVirtualGifts

abstract class BaseGiftsFragment: BaseFragment<FragmentGiftsBinding>() {

    var fragVirtualGifts: FragmentVirtualGifts ?= null
    var fragRealGifts: FragmentRealGifts ?= null

    abstract fun setupScreen()

    override fun setupTheme() {
        binding?.userToolbarTitle?.text = AppStringConstant1.gifts
        binding?.signInGoogleButtonText?.text = AppStringConstant1.buy_gift

        binding?.giftsTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.giftsPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.giftsTabs?.setupWithViewPager(binding?.giftsPager)
        setupScreen()
        setupViewPager(binding?.giftsPager!!)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = UserItemsAdapter(childFragmentManager)
        fragRealGifts = FragmentRealGifts()
        fragVirtualGifts = FragmentVirtualGifts()

        adapter.addFragItem(fragRealGifts!!, getString(R.string.real_gifts))
        adapter.addFragItem(fragVirtualGifts!!, getString(R.string.virtual_gifts))
        viewPager.adapter = adapter
    }

}