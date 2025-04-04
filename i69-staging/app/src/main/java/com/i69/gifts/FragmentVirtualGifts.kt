package com.i69.gifts

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.i69.GetAllVirtualGiftsQuery
import com.i69.data.models.ModelGifts
import com.i69.databinding.FragmentVirtualGiftsBinding
import com.i69.ui.adapters.AdapterGifts
import com.i69.ui.base.BaseFragment
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.apolloClient
import kotlinx.coroutines.launch

class FragmentVirtualGifts : BaseFragment<FragmentVirtualGiftsBinding>() {
    var giftsAdapter: AdapterGifts? = null
    var list: MutableList<ModelGifts.Data.AllRealGift> = mutableListOf()
    private var TAG: String = FragmentVirtualGifts::class.java.simpleName

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVirtualGiftsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setupTheme() {

        giftsAdapter = AdapterGifts(requireContext(), list)
        binding?.recyclerViewGifts?.layoutManager = GridLayoutManager(requireContext(), 3)
        binding?.recyclerViewGifts?.setHasFixedSize(true)
        binding?.recyclerViewGifts?.adapter = giftsAdapter

        lifecycleScope.launch {

            try {
                val response = apolloClient(requireContext(), getCurrentUserToken()!!).query(
                    GetAllVirtualGiftsQuery()
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors
                    Toast.makeText(
                        requireContext(),
                        error?.get(0)?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val virtualGifts = response.data?.allVirtualGift
                    val virtualGiftList = virtualGifts?.map {
                        Log.e(TAG, "FragmentVirtualGifts" + "Image :  ${it?.picture!!}")
                        Log.e(TAG, "FragmentVirtualGifts" + "price :  ${it.cost}")
                        ModelGifts.Data.AllRealGift(
                            it.cost,
                            it.giftName,
                            it.id,
                            it.picture,
                            "",
                            false
                        )
                    }
                    list.clear()
                    if (virtualGiftList != null) {
                        list.addAll(
                            virtualGiftList
                        )
                    }
                    giftsAdapter!!.notifyDataSetChanged()
                }
            } catch (e: ApolloException) {
                //  Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                //    Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun setupClickListeners() {}
}