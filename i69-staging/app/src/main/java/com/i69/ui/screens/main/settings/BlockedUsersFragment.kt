package com.i69.ui.screens.main.settings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.i69.R
import com.i69.data.models.BlockedUser
import com.i69.data.models.User
import com.i69.databinding.FragmentBlockedUsersSettingsBinding
import com.i69.ui.adapters.BlockUsersListAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.Resource
import com.i69.utils.snackbar

class BlockedUsersFragment : BaseFragment<FragmentBlockedUsersSettingsBinding>(), BlockUsersListAdapter.BlockListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: BlockUsersListAdapter
    private var currentUser: User? = null
    private var TAG: String = BlockedUsersFragment::class.java.simpleName
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentBlockedUsersSettingsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {

        adapter = BlockUsersListAdapter(this)
        binding?.blockedUsers?.adapter = adapter

        lifecycleScope.launch(Dispatchers.Main) {
            val currentUserId = getCurrentUserId()!!
            val token = getCurrentUserToken()!!

            viewModel.getCurrentUser(currentUserId, token = token, false).observe(viewLifecycleOwner) { user ->
                user?.let {
                    currentUser = it
                    adapter.updateList(currentUser!!.blockedUsers)
                }
            }
        }
    }

    override fun setupClickListeners() {

        binding?.toolbarHamburger?.setOnClickListener {
            //activity?.onBackPressed()
            findNavController().popBackStack()
        }

    }

    override fun unblock(user: BlockedUser) {

        showProgressView()
        lifecycleScope.launch(Dispatchers.Main) {
            val currentUserId = getCurrentUserId()!!
            val token = getCurrentUserToken()!!

            when (val response = viewModel.unblockUser(userId = currentUserId, token = token, blockedId = user.id)) {
                is Resource.Success -> {
                    viewModel.getCurrentUser(currentUserId, token = token, true)
                    //getMainActivity()?.mViewModelUser?.userMomentsList.clear()
                    getMainActivity()?.pref?.edit()?.putString("chatListRefresh","true")?.putString("readCount","false")?.apply()

                    hideProgressView()

                }
                is Resource.Error -> {
                    hideProgressView()
                    Log.e(TAG,"${getString(R.string.something_went_wrong)} ${response.message}")
                    binding?.root?.snackbar("${getString(R.string.something_went_wrong)} ${response.message}")
                }else -> {

                }
            }
        }
    }
}