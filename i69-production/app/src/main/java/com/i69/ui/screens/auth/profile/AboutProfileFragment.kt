package com.i69.ui.screens.auth.profile

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import com.i69.R
import com.i69.databinding.FragmentAboutProfileBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.viewModels.AuthViewModel
import com.i69.utils.snackbar

@AndroidEntryPoint
class AboutProfileFragment : BaseFragment<FragmentAboutProfileBinding>() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAboutProfileBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        binding?.toolbar?.inflateMenu(R.menu.next_menu)
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_keyboard_right_arrow)
    }

    override fun setupClickListeners() {
        binding?.toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_next) onNextClick()
            true
        }
        binding?.toolbar?.setNavigationOnClickListener { moveUp() }
        binding?.aboutNextBtn?.setOnClickListener { onNextClick() }

        binding?.aboutEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding?.tvAboutTitleCount?.text = "${s?.length}/250"
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun onNextClick() {
        val aboutText = binding?.aboutEdit?.text.toString()
        if (aboutText.isEmpty()) {
            binding?.root?.snackbar(getString(R.string.about_error_message))
            return
        }
        viewModel.getAuthUser()?.about = aboutText
        moveTo(R.id.action_about_setup_to_complete_profile)
    }

}