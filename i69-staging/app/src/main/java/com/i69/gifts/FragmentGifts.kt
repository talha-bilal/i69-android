package com.i69.gifts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.i69.GiftPurchaseMutation
import com.i69.data.models.ModelGifts
import com.i69.R
import com.i69.databinding.FragmentGiftsBinding
import com.i69.ui.base.profile.BaseGiftsFragment
import com.i69.utils.apolloClient
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentGifts : BaseGiftsFragment() {

    private var purchaseGiftFor: String? = ""
    private var TAG: String = FragmentGifts::class.java.simpleName

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGiftsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupClickListeners() {
        binding?.purchaseButton?.setOnClickListener {
            val items: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
            fragVirtualGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }
            fragRealGifts?.giftsAdapter?.getSelected()?.let { it1 -> items.addAll(it1) }

            lifecycleScope.launchWhenCreated() {
                if (items.size > 0) {
                    showProgressView()
                    items.forEach { gift ->

                        var res: ApolloResponse<GiftPurchaseMutation.Data>? = null
                        try {
                            res = apolloClient(
                                requireContext(),
                                getCurrentUserToken()!!
                            ).mutation(
                                GiftPurchaseMutation(
                                    gift.id,
                                    purchaseGiftFor!!,
                                    getCurrentUserId()!!
                                )
                            ).execute()
                        } catch (e: ApolloException) {
                            Log.e(TAG, "apolloResponse Exception ${e.message}")
                            binding?.root?.snackbar(" ${e.message}")
                        }
                        if (res?.hasErrors() == false) {
                            binding?.root?.snackbar(
                                context?.resources?.getString(R.string.you_bought) + " ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName} " + context?.resources?.getString(
                                    R.string.successfully
                                )
                            )
                        }
                        if (res!!.hasErrors()) {
                            binding?.root?.snackbar("" + res.errors!![0].message)

                        }
                        Log.e(
                            TAG,
                            "apolloResponse ${res.hasErrors()} ${res.data?.giftPurchase?.giftPurchase?.gift?.giftName}"
                        )
                    }
                    hideProgressView()
                }
            }
        }
    }

    override fun setupScreen() {
        purchaseGiftFor = requireArguments().getString("userId")
    }
}