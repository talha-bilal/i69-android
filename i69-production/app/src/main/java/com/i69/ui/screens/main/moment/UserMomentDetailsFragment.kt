package com.i69.ui.screens.main.moment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.i69.databinding.FragmentUserMomentDetailsBinding
import com.i69.ui.base.BaseFragment

class UserMomentDetailsFragment: BaseFragment<FragmentUserMomentDetailsBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserMomentDetailsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
    }

    override fun setupClickListeners() {
    }

}