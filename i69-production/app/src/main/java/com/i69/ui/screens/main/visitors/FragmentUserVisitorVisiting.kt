package com.i69.ui.screens.main.visitors

import android.app.AlertDialog
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
import com.i69.GetUserQuery
import com.i69.R
import com.i69.UserFollowMutation
import com.i69.UserUnfollowMutation
import com.i69.applocalization.AppStringConstant1
import com.i69.databinding.FragmentUserFollowFolllowersBinding
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.base.BaseFragment
import com.i69.utils.SharedPref
import com.i69.utils.apolloClient
import com.i69.utils.snackbar
import kotlinx.coroutines.launch

class FragmentUserVisitorVisiting : BaseFragment<FragmentUserFollowFolllowersBinding>() {

    private var userId: String = ""
    private var userToken: String = ""
    private val viewModel: VMProfile by activityViewModels()
    lateinit var sharedPref: SharedPref
    private var TAG: String = FragmentUserVisitorVisiting::class.java.simpleName

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserFollowFolllowersBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        navController = findNavController()

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            //userChatID= getChatUserId()!!
        }

        val userFullName = requireArguments().getString("userFulNAme")

//
//        viewModel.getProfile(specificUserid)

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

//        viewModel.onvisitorItemClick.observe(viewLifecycleOwner) {
//
//            if (it!!.isConnected != null && it!!.isConnected!!) {
//                unFollowConfirmation(it.fullName, it.id.toString())
//            } else {
//                userFollowMutationCall(it.id.toString())
//            }
//        }


//        viewModel.onVisitingItemClick.observe(viewLifecycleOwner) {
//
//            if (it!!.isConnected != null && it!!.isConnected!!) {
//                unFollowConfirmation(it.fullName, it.id.toString())
//            } else {
//                userFollowMutationCall(it.id.toString())
//            }
//        }
    }


    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = UserItemsAdapter(childFragmentManager)
        val fragFollowers = VisitorsFragment()
        val fragFollowing = VisitingFragment()

        val userid = requireArguments().getString("userId")

        val bundle = Bundle()
        bundle.putString("userId", userid)

        fragFollowers.arguments = bundle
        fragFollowing.arguments = bundle

        adapter.addFragItem(fragFollowers, AppStringConstant1.visitors)
        adapter.addFragItem(fragFollowing, AppStringConstant1.visited)

        viewPager?.adapter = adapter
    }

    override fun setupClickListeners() {
        binding?.purchaseClose?.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun unFollowConfirmation(followinfUser: String, userId: String) {
        val message = AppStringConstant1.are_you_sure_you_want_to_unfollow_user
//        var message = resources.getString(R.string.are_you_sure_you_want_to_unfollow_user)
            .plus(followinfUser)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage("${message}").setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, which ->
                userUnfollowCall(userId)
            }.setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))

    }


    private fun userFollowMutationCall(followinfUser: String) {
        Log.e("FollowUserIds", followinfUser)

        lifecycleScope.launchWhenResumed {
            showProgressView()

            val res = try {
                apolloClient(
                    requireContext(), getCurrentUserToken()!!
                ).mutation(UserFollowMutation(followinfUser)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
//                    Toast.makeText(requireContext(), " ${e.message}", Toast.LENGTH_LONG).show()

                hideProgressView()
                return@launchWhenResumed
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")
                Log.e("res.errorsFollowers", "${res.errors}")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {


//                binding.root.snackbar(getString(R.string.successfully))

                getUserFollowingData(false)
            }
        }
    }


    private fun userUnfollowCall(followingUser: String) {
        Log.e("FollowingUserIds", followingUser)

        lifecycleScope.launchWhenResumed {
            showProgressView()
            var unfollowMutation = UserUnfollowMutation(followingUser)
            Log.e("UnfollowMutationList", Gson().toJson(unfollowMutation))
            val res = try {
                apolloClient(
                    requireContext(), getCurrentUserToken()!!
                ).mutation(unfollowMutation)
//                    .mutation(UserUnfollowMutation(followinfUser!!.id!!.toString()))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
//                    Toast.makeText(requireContext(), " ${e.message}", Toast.LENGTH_LONG).show()

                hideProgressView()
                return@launchWhenResumed
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")
                Log.e("res.errorsFollowig", "${res.errors}")
                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {

                binding?.root?.snackbar(AppStringConstant1.successfully)

                getUserFollowingData(false)
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
                    requireContext(), getCurrentUserToken()!!
                ).query(GetUserQuery(userid!!)).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
                Log.e("errorAApolloException", "$e")
//                    Toast.makeText(requireContext(), " ${e.message}", Toast.LENGTH_LONG).show()

                hideProgressView()
                return@launch
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e("FollowingFragmntError", "${res.errors}")
                Log.e("FollowingFragmntError1", "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {

//                list.clear()
                Log.e("WEdewwe", GetUserQuery(userid).toString())
                var userVisitingList = res.data!!.user!!.userVisiting
                var userVisitorsList = res.data!!.user!!.userVisitors

                Log.e("userVisitingList", Gson().toJson(userVisitingList))


                val x = mutableListOf<GetUserQuery.UserVisiting?>().apply {

                    if (userVisitingList != null) {
                        addAll(userVisitingList)
                    }

                }

                val xy = mutableListOf<GetUserQuery.UserVisitor?>().apply {

                    if (userVisitorsList != null) {
                        addAll(userVisitorsList)
                    }

                }

                Log.e(TAG,"VFrag"+ "getUserFollowingData: $xy, $x")
                viewModel.setupdateVisitingListResultWith(x)
                viewModel.setupdateVisitorListResultWith(xy)

                hideProgressView()
            }
        }


    }

}