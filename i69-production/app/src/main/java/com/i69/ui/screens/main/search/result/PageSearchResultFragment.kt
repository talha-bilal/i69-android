package com.i69.ui.screens.main.search.result


import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.MyPermission
import com.i69.data.models.User
import com.i69.data.remote.requests.SearchRequestNew
import com.i69.data.remote.responses.DefaultPicker
import com.i69.databinding.FragmentPageSearchResultBinding
import com.i69.ui.adapters.LockUsersSearchListAdapter
import com.i69.ui.adapters.UsersSearchListAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity.Companion.getMainActivity
import com.i69.ui.screens.main.search.FiltersDialogFragment
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.ui.screens.main.subscription.SubscriptionBSDialogFragment
import com.i69.ui.viewModels.SearchViewModel
import com.i69.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PageSearchResultFragment : BaseFragment<FragmentPageSearchResultBinding>(),
    UsersSearchListAdapter.UserSearchListener, LockUsersSearchListAdapter.LockUserSearchListener {
    private var userToken: String? = null
    private var userId: String? = null
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = PageSearchResultFragment::class.java.simpleName

    companion object {
        private const val ARG_DATA_BY_PAGE_ID = "ARG_PAGE_ID"

        fun newInstance(page: Int): PageSearchResultFragment {
            val args = Bundle()
            args.putInt(ARG_DATA_BY_PAGE_ID, page)
            val fragment = PageSearchResultFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var mPage: Int = 0
    private val mViewModel: SearchViewModel by activityViewModels()
    private lateinit var usersAdapter: UsersSearchListAdapter
    private lateinit var usersLockAdapter: LockUsersSearchListAdapter

    private var mHandler: Handler? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPageSearchResultBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }


    override fun setupTheme() {
        mHandler = Handler(Looper.getMainLooper())

        viewStringConstModel.data.observe(this@PageSearchResultFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }

        mViewModel.updateFilteredData.observe(this@PageSearchResultFragment) { _ ->
            val pageId = requireArguments().getInt(ARG_DATA_BY_PAGE_ID, 0)
            Log.e(TAG, "setupTheme: updateFilteredData Observe: $pageId")
            updateFilterdData()
        }

        mPage = requireArguments().getInt(ARG_DATA_BY_PAGE_ID)
        navController = findNavController()

        if (this@PageSearchResultFragment::usersAdapter.isInitialized) {
            binding?.usersRecyclerView?.adapter = usersAdapter
        }

        if (this@PageSearchResultFragment::usersLockAdapter.isInitialized) {
            binding?.usersLockRecyclerView?.adapter = usersLockAdapter
        }

        initSearch()
    }

    val userItems: ArrayList<User> = ArrayList()

    private fun initAdapterData(pickers: DefaultPicker) {

        if (!this@PageSearchResultFragment::usersAdapter.isInitialized) {
            usersAdapter = UsersSearchListAdapter(this@PageSearchResultFragment, pickers, userItems)
            usersAdapter.setHasStableIds(true)
            binding?.usersRecyclerView?.adapter = usersAdapter
        }

        if (!this@PageSearchResultFragment::usersLockAdapter.isInitialized) {
            usersLockAdapter =
                LockUsersSearchListAdapter(this@PageSearchResultFragment, pickers)
            binding?.usersLockRecyclerView?.adapter = usersLockAdapter
        }
    }

    private fun updateFilterdData() {
        val users: ArrayList<User> = when (mPage) {
            0 -> mViewModel.getRandomUsers()
            1 -> mViewModel.getPopularUsers()
            else -> mViewModel.getMostActiveUsers()
        }

        val myPermission: MyPermission = when (mPage) {
            0 -> mViewModel.getMyPermission()
            1 -> mViewModel.getPopularUserMyPermission()
            else -> mViewModel.getMostActiveUserMyPermission()
        }

        val unLockUsers: ArrayList<User> = ArrayList()
        val lockUsers: ArrayList<User> = ArrayList()
        if (myPermission.hasPermission) {
            unLockUsers.addAll(users)
            binding?.usersLockRecyclerView?.setViewGone()
            binding?.unlockLayout?.setViewGone()
        } else {
            users.indices.forEach { i ->
                if (i < myPermission.freeUserLimit) {
                    unLockUsers.add(users.get(i))
                } else {
                    lockUsers.add(users.get(i))
                }
            }

            binding?.usersLockRecyclerView?.setViewVisible()
            binding?.unlockLayout?.setViewVisible()
            binding?.usersRecyclerView?.setViewVisible()
        }

        if (users.isNullOrEmpty()) {
            binding?.noUsersLabel?.setViewVisible()
            binding?.usersLockRecyclerView?.setViewGone()
            binding?.unlockLayout?.setViewGone()
            binding?.usersRecyclerView?.setViewGone()

        } else {
            binding?.noUsersLabel?.setViewGone()
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    requireActivity().runOnUiThread {
                        usersLockAdapter.updateItems(lockUsers, myPermission)
                        userItems.clear()
                        userItems.addAll(unLockUsers)
                        usersAdapter.notifyDataSetChanged()

                        binding?.usersRecyclerView?.setViewVisible()
                        binding?.usersLockRecyclerView?.setViewVisible()
                        binding?.unlockLayout?.setViewVisible()

                    }
                }
            }, 100)
        }
    }

    private fun setupUserSearchAdapter() {
        lifecycleScope.launch {
            if (mViewModel.getSearchUserQuery() == null ||
                mViewModel.getSearchUserQuery().value == null ||
                mViewModel.getSearchUserQuery().value.toString().isEmpty()
            ) {

                val userToken = getCurrentUserToken()!!

                mViewModel.getDefaultPickers(userToken).observe(viewLifecycleOwner) { pickers ->
                    pickers?.let { _ ->

                        initAdapterData(pickers)

                        val users: ArrayList<User> = when (mPage) {
                            0 -> mViewModel.getRandomUsers()

                            1 -> mViewModel.getPopularUsers()

                            else -> mViewModel.getMostActiveUsers()
                        }

                        val myPermission: MyPermission = when (mPage) {
                            0 -> mViewModel.getMyPermission()

                            1 -> mViewModel.getPopularUserMyPermission()

                            else -> mViewModel.getMostActiveUserMyPermission()
                        }

                        val unLockUsers: ArrayList<User> = ArrayList()
                        val lockUsers: ArrayList<User> = ArrayList()
                        if (myPermission.hasPermission) {
                            unLockUsers.addAll(users)
                            binding?.usersLockRecyclerView?.setViewGone()
                            binding?.unlockLayout?.setViewGone()
                        } else {
                            users.indices.forEach { i ->
                                if (i < myPermission.freeUserLimit) {
                                    unLockUsers.add(users.get(i))
                                } else {
                                    lockUsers.add(users.get(i))
                                }
                            }

                            binding?.usersLockRecyclerView?.setViewVisible()
                            binding?.unlockLayout?.setViewVisible()
                            binding?.usersRecyclerView?.setViewVisible()
                        }
                        Log.e(TAG, "Lock users size: ${lockUsers.size}")
                        Log.e(TAG, "Unlock users size: ${unLockUsers.size}")
                        Log.e(TAG, "Users size: ${users.size}")
                        if (users.isEmpty()) {
                            binding?.noUsersLabel?.setViewVisible()
                            binding?.usersLockRecyclerView?.setViewGone()
                            binding?.unlockLayout?.setViewGone()
                            binding?.usersRecyclerView?.setViewGone()

                        } else {
                            mHandler?.post {
                                binding?.noUsersLabel?.setViewGone()
                                usersLockAdapter.updateItems(lockUsers, myPermission)
                                userItems.clear()
                                userItems.addAll(unLockUsers)
                                usersAdapter.notifyDataSetChanged()
                                binding?.usersRecyclerView?.setViewVisible()
                                binding?.usersLockRecyclerView?.setViewVisible()
                                binding?.unlockLayout?.setViewVisible()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun setupClickListeners() {

        binding?.llButtonUnLock?.setOnClickListener {

            val text: String =
                String.format(
                    AppStringConstant1.unlock_this_funtion,
                    "${this.mViewModel.getMyPermission().coinsToUnlock}"
                )

            val subscriptionBSDialogFragment =
                SubscriptionBSDialogFragment.newInstance(userToken, userId, text)
            subscriptionBSDialogFragment.show(
                childFragmentManager,
                "${AppStringConstant1.subscription}"
            )
        }

        binding?.filterButton?.setOnClickListener {
            val dialog = FiltersDialogFragment(userToken, userId)
            dialog.show(childFragmentManager, "${AppStringConstant1.filter}")
        }
    }

    private fun initSearch() {
        mViewModel.getUpdateUserListQuery().observe(viewLifecycleOwner) {
            Log.e(TAG, "initSearch: getUpdateListQuery: ${mViewModel.getRandomUsers().size}")
            setupUserSearchAdapter()
        }

        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            userId = getCurrentUserId()!!

            Log.e(TAG, "usertokenn $userToken")
        }

        mViewModel.getSearchUserQuery().observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                userToken = getCurrentUserToken()!!
                userId = getCurrentUserId()!!

                Log.e(TAG, "usertokenn $userToken")
            }
            if (it?.length != 0) {
                val searchRequest = SearchRequestNew(
                    name = it.toString()
                )

                Log.e(TAG, "search params : ${Gson().toJson(searchRequest)}")
                mViewModel.getSearchUsersTemp(
                    _searchRequest = searchRequest,
                    token = userToken!!,
                    context = requireContext()
                ) { error ->
                    if (error == null) {
                        hideProgressView()

                        val unLockUsers: ArrayList<User> = ArrayList()
                        val lockUsers: ArrayList<User> = ArrayList()
                        if (mViewModel.getMyPermission().hasPermission) {
                            unLockUsers.addAll(mViewModel.getRandomUsersSearched())
                            binding?.usersLockRecyclerView?.setViewGone()
                            binding?.unlockLayout?.setViewGone()
                        } else {
                            mViewModel.getRandomUsersSearched().indices.forEach { i ->
                                if (i < mViewModel.getMyPermission().freeUserLimit) {
                                    unLockUsers.add(mViewModel.getRandomUsersSearched()[i])
                                } else {
                                    lockUsers.add(mViewModel.getRandomUsersSearched()[i])
                                }
                            }
                            binding?.usersLockRecyclerView?.setViewVisible()
                            binding?.unlockLayout?.setViewVisible()
                        }

                        mHandler?.post {
                            Log.e(TAG, "manual search results: ")
                            usersLockAdapter.updateItems(lockUsers, mViewModel.getMyPermission())
                            userItems.clear()
                            userItems.addAll(unLockUsers)
                            usersAdapter.notifyDataSetChanged()
                            binding?.usersRecyclerView?.setViewVisible()
                            binding?.usersLockRecyclerView?.setViewVisible()
                            binding?.unlockLayout?.setViewVisible()

                            if (mViewModel.getRandomUsersSearched().isNullOrEmpty()) {
                                binding?.noUsersLabel?.setViewVisible()
                                binding?.usersLockRecyclerView?.setViewGone()
                                binding?.unlockLayout?.setViewGone()
                                binding?.usersRecyclerView?.setViewGone()
                            } else {
                                binding?.noUsersLabel?.setViewGone()
                                binding?.usersLockRecyclerView?.setViewVisible()
                                binding?.unlockLayout?.setViewVisible()
                                binding?.usersRecyclerView?.setViewVisible()
                            }
                        }
                    } else {
                        hideProgressView()
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int, user: User) {
        mViewModel.selectedUser.value = user
        val bundle = Bundle()
        bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
        bundle.putString("userId", user.id)
        bundle.putString("fullName", user.fullName)
        if (userId == user.id) {
            getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                R.id.nav_user_profile_graph
        } else {
            findNavController().navigate(
                destinationId = R.id.action_global_otherUserProfileFragment,
                popUpFragId = null,
                animType = AnimationTypes.SLIDE_ANIM,
                inclusive = false,
                args = bundle
            )
        }
    }

    override fun onUnlockFeatureClick() {
        userSearchAllrequestCall()
    }

    override fun onLockItemClick(position: Int, user: User) {
        userSearchAllrequestCall()
    }

    private fun userSearchAllrequestCall() {
        val text =
            "${AppStringConstant1.unlock_this_funtion_} ${AppStringConstant1.to_view_more_profile}"
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(text)
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { _, _ ->

                val text: String =
                    java.lang.String.format(
                        AppStringConstant1.unlock_this_funtion,
                        "${this.mViewModel.getMyPermission().coinsToUnlock}"
                    )

                val subscriptionBSDialogFragment =
                    SubscriptionBSDialogFragment.newInstance(userToken, userId, text)
                subscriptionBSDialogFragment.show(
                    childFragmentManager,
                    "${AppStringConstant1.subscription}"
                )
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(),R.color.black))
        alert.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
    }
}
