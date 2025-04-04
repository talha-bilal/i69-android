package com.i69.ui.screens.main.follower

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.i69.FollowSubscription
import com.i69.GetUserQuery
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.FragmentUserFollowFolllowersBinding
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.utils.SharedPref
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch


class UserFolloweFollowingFragment : BaseFragment<FragmentUserFollowFolllowersBinding>() {
    private var userId: String = ""
    private var userToken: String = ""

    private val viewModel: VMProfile by activityViewModels()
    private var TAG: String = UserFolloweFollowingFragment::class.java.simpleName
    lateinit var sharedPref: SharedPref
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserFollowFolllowersBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
        }
        val userFullName = requireArguments().getString("userFulNAme")

        val name = if (userFullName != null && userFullName.length > 15) {
            userFullName.substring(0, minOf(userFullName.length, 15))
        } else {
            userFullName
        }

        binding?.toolbarTitle?.text = "$name"

        binding?.userFollowTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding?.userFollowPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        binding?.userFollowTabs?.setupWithViewPager(binding?.userFollowPager)
        setupViewPager(binding?.userFollowPager)

        getUserFollowingData()
        subscribeForUserUpdate()

    }


    @SuppressLint("SuspiciousIndentation")
    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        val fragFollowers = FollowersFragment()
        val fragFollowing = FollowingFragment()
        val userid = requireArguments().getString("userId")

        val bundle = Bundle()
        bundle.putString("userId", userid)

        fragFollowers.arguments = bundle
        fragFollowing.arguments = bundle

        adapter.addFragItem(fragFollowers, AppStringConstant1.followers)
        adapter.addFragItem(fragFollowing, AppStringConstant1.following_tab)
        viewPager?.adapter = adapter
    }

    override fun setupClickListeners() {
        binding?.purchaseClose?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subscribeForUserUpdate() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), getCurrentUserToken()!!).subscription(
                    FollowSubscription()
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG,"reealltime exception= ${it.message}")
                }
                    .retryWhen { cause, attempt ->
                        Log.e(TAG,"reealltime retry $attempt ${cause.message}")
                        delay(attempt * 1000)
                        true
                    }.collect { newStory ->
                        if (newStory.hasErrors()) {
                            Log.e(TAG,"reealltime response error = ${newStory.errors?.get(0)?.message}")
                        } else {
                            //   Log.e("reealltime onNewMessage ${newMessage.data?.onNewMessage?.message?.timestamp}")
                            Log.e(TAG,
                                "followUserSubscript"+
                                "story realtime DeleteStory ${newStory.data}"
                            )
                            getUserFollowingData(false)
                        }
                    }


            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG,"UserMomentSubsc"+ "story realtime exception= ${e2.message}")
                Log.e(TAG,"reealltime exception= ${e2.message}")
            }
        }
    }

    private fun getUserFollowingData(isShowLoader: Boolean = true) {
        val userid = requireArguments().getString("userId")
        if (userid != null) {
            Log.e("getUserId", userid)
        }
        lifecycleScope.launch {
            if (isShowLoader) {
                showProgressView()
            }

            val res = try {
                apolloClient(
                    requireContext(),
                    getCurrentUserToken()!!
                )
                    .query(GetUserQuery(userid!!))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
                Log.e(TAG,"Error ApolloException : $e")
                hideProgressView()
                return@launch
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e(TAG,"Response Errors: $errorMessage")
                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                val response = Gson().toJson(res)
                Log.e(TAG,"Response: $response")
                val followingList = res.data!!.user!!.followingUsers
                val followerList = res.data!!.user!!.followerUsers
                Log.e(TAG,"FollowerList: ${Gson().toJson(followerList)}")
                Log.e(TAG,"FollowingList: ${Gson().toJson(followingList)}")
                val followingUsers = mutableListOf<GetUserQuery.FollowingUser?>().apply {
                    if (followingList != null) {
                        addAll(followingList)
                    }
                }

                val followerUsers = mutableListOf<GetUserQuery.FollowerUser?>().apply {
                    if (followerList != null) {
                        addAll(followerList)
                    }
                }

                viewModel.setupdateFollowingListResultWith(followingUsers)
                viewModel.updateFollowerListResultWith(followerUsers)
                hideProgressView()
            }
        }
    }
}
