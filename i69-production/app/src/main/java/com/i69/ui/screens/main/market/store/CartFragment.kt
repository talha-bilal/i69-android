package com.i69.ui.screens.main.market.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.i69.databinding.FragmentCartBinding
import com.i69.ui.adapters.CartAdapter
import com.i69.ui.base.BaseFragment

class CartFragment : BaseFragment<FragmentCartBinding>(), CartAdapter.OnRemoveItemFromCart {

    private lateinit var cartAdapter: CartAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCartBinding.inflate(inflater, container, false).apply { }

    override fun initObservers() {
        cartAdapter = CartAdapter(activity, this)
        binding?.recyclerCartItems?.adapter = cartAdapter
    }

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        binding?.actionBack1?.setOnClickListener {
            findNavController().navigateUp()
        }
        binding?.proceedToPayment?.setOnClickListener {

        }
    }

    override fun removeItemFromCart() {

    }
}