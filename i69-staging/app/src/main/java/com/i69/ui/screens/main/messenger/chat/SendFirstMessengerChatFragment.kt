package com.i69.ui.screens.main.messenger.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.stfalcon.chatkit.messages.MessageInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.i69.R
import com.i69.data.models.User
import com.i69.databinding.FragmentMessengerChatBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.viewModels.SearchViewModel
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.Resource
import com.i69.utils.loadCircleImage
import com.i69.utils.snackbar

@AndroidEntryPoint
class SendFirstMessengerChatFragment : BaseFragment<FragmentMessengerChatBinding>(),
                                       MessageInput.InputListener {

    private val viewModel: SearchViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var userId: String? = null
    private var userToken: String? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMessengerChatBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
        }
        setupOtherUserData()
    }

    override fun setupClickListeners() {
        binding?.closeBtn?.setOnClickListener { requireActivity().onBackPressed() }
        binding?.input?.setInputListener(this)
    }

    private fun setupOtherUserData() {
        val otherUser = viewModel.selectedUser.value!!
        binding?.userName?.text = otherUser.fullName
        binding?.userProfileImg?.loadCircleImage(
            otherUser.avatarPhotos!!.get(otherUser.avatarIndex!!).url ?: ""
        )
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        showProgressView()
        return false
    }

    private fun updateLikes(currentUser: User, otherUser: User) {
        val currentUserLikes = ArrayList<String>()
        currentUserLikes.addAll(currentUser.likes.map { it.id })
        currentUserLikes.add(otherUser.id)

        val otherUserLikes = ArrayList<String>()
        otherUserLikes.addAll(otherUser.likes.map { it.id })
        otherUserLikes.add(currentUser.id)

        lifecycleScope.launch(Dispatchers.Main) {
            updateUserLikes(currentUser.id, userLikes = currentUserLikes) {
                lifecycleScope.launch(Dispatchers.Main) {
                    updateUserLikes(otherUser.id, userLikes = otherUserLikes) {
                    }
                }

            }
        }
    }

    private suspend fun updateUserLikes(
        userId: String,
        userLikes: ArrayList<String>,
        callback: () -> Unit
    ) {
        when (val response =
            userViewModel.updateUserLikes(userId, token = userToken!!, userLikes = userLikes)) {
            is Resource.Success -> callback()
            is Resource.Error -> {
                hideProgressView()
                binding?.root?.snackbar("${getString(R.string.something_went_wrong)} ${response.message}")
            }

            else -> {

            }
        }
    }
}