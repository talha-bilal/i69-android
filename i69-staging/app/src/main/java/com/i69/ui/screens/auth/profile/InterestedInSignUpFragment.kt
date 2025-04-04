package com.i69.ui.screens.auth.profile

import androidx.fragment.app.activityViewModels
import com.i69.R
import com.i69.ui.base.profile.BaseInterestedInFragment
import com.i69.ui.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterestedInSignUpFragment : BaseInterestedInFragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun initObservers() {

    }

    override fun onSaveClick() {
        if (checkInterestedInputs()) {
            viewModel.getAuthUser()!!.interestedIn = getInterestedInValues()
            moveTo(R.id.action_interested_in_to_select_tags)
        }
    }

}