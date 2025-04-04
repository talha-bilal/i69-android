package com.i69.gifts

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.i69.data.models.ModelGifts
import com.i69.databinding.FragmentRealGiftsBinding
import com.i69.ui.adapters.AdapterGifts
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.SplashActivity
import com.i69.ui.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentRealGifts: BaseFragment<FragmentRealGiftsBinding>() {
    var giftsAdapter: AdapterGifts ?= null
    var list : MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()

    private val viewModel: UserViewModel by activityViewModels()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRealGiftsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        giftsAdapter = AdapterGifts(requireContext(), list)
        binding?.recyclerViewGifts?.layoutManager = GridLayoutManager(requireContext(), 3)
        binding?.recyclerViewGifts?.setHasFixedSize(true)
        binding?.recyclerViewGifts?.adapter = giftsAdapter
        lifecycleScope.launch {

            activity?.let { currentActivity ->
                showProgressView()
                viewModel.getRealGifts(getCurrentUserToken()!!).observe(currentActivity) {
                    list.clear()
                    list.addAll(it)

                    if (list.size > 0) {
                        if (list[0].giftName == "error") {
                            lifecycleScope.launch(Dispatchers.Main) {
                                userPreferences?.clear()
                                //App.userPreferences.saveUserIdToken("","","")
                                val intent = Intent(context, SplashActivity::class.java)
                                startActivity(intent)
                                requireActivity().finishAffinity()
                            }
                        }
                    }

                    giftsAdapter?.notifyDataSetChanged()
                    hideProgressView()
                }
            }
        }
    }

    override fun setupClickListeners() {}
}