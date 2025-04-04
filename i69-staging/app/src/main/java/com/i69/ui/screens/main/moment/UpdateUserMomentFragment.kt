package com.i69.ui.screens.main.moment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.i69.UpdateMomentMutation
import com.i69.data.config.Constants.hideKeyboard
import com.i69.databinding.FragmentUpdateUserMomentBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.utils.apolloClient
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateUserMomentFragment : BaseFragment<FragmentUpdateUserMomentBinding>() {

    lateinit var desc: String
    var pk: Int = -1
    private var TAG: String = UpdateUserMomentFragment::class.java.simpleName
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateUserMomentBinding =
        FragmentUpdateUserMomentBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        desc = arguments?.getString("moment_desc") ?: ""
        pk = arguments?.getInt("moment_pk") ?: -1
        binding?.editWhatsGoing?.setText(desc)
    }

    override fun setupClickListeners() {

        binding?.btnShareMoment?.setOnClickListener {
            // call api
            Log.e(TAG, "UpdateUserMoment share button clicked")
            showProgressView()
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val response = apolloClient(
                        requireContext(),
                        getCurrentUserToken()!!
                    ).mutation(UpdateMomentMutation(pk, binding?.editWhatsGoing?.text.toString()))
                        .execute()

                    if (response.hasErrors()) {
                        val error = response.errors?.get(0)?.message
                        Log.e(TAG, "Exception momentUpdateDesc $error")
                        binding?.root?.snackbar(" $error")
                        hideProgressView()
                        return@launch
                    } else {
                        val responseData = response.data?.updateMoment
                        if (responseData?.success == true) {
                            hideProgressView()
                            moveUp()
                        } else {
                            binding?.root?.snackbar("${responseData?.message}")
                            hideProgressView()
                            return@launch
                        }

                    }

                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponsemomentUpdateDesc ${e.message}")
                    binding?.root?.snackbar(" ${e.message}")
                    hideProgressView()
                    return@launch
                }
            }
        }

        binding?.toolbarHamburger?.setOnClickListener {
            hideKeyboard(requireActivity())
            binding?.editWhatsGoing?.clearFocus()
            getMainActivity()?.drawerSwitchState()
        }

    }
}