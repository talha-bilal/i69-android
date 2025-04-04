package com.i69.ui.screens.main.follower

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.*
import com.i69.applocalization.AppStringConstant1
import com.i69.R
import com.i69.databinding.FragmentFollowersBinding
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.AdaptersFollowing
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.utils.*
import kotlinx.coroutines.launch

class FollowingFragment : BaseFragment<FragmentFollowersBinding>(),
    AdaptersFollowing.FollowingListListener {
    var giftsAdapter: AdaptersFollowing? = null
    var list: MutableList<GetUserQuery.FollowingUser?> = mutableListOf()
    private var TAG: String = FollowingFragment::class.java.simpleName
    private val viewModel: VMProfile by activityViewModels()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFollowersBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        giftsAdapter = AdaptersFollowing(requireContext(), list, this@FollowingFragment)
        binding?.recyclerViewFolowers?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding?.recyclerViewFolowers?.setHasFixedSize(true)
        binding?.recyclerViewFolowers?.adapter = giftsAdapter

        viewModel.getupdateFollowingListResultWith()?.observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            giftsAdapter!!.notifyDataSetChanged()
        })
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
                Log.e(TAG, "apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
                Log.e("errorAApolloException", "$e")
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

                list.clear()
                val foolowerList = res.data!!.user!!.followingUsers

                Log.e("foolowerList", Gson().toJson(foolowerList))


                val x = mutableListOf<GetUserQuery.FollowingUser?>().apply {
                    if (foolowerList != null) {
                        addAll(foolowerList)
                    }
                }
                list.addAll(x)
                giftsAdapter!!.notifyDataSetChanged()
                hideProgressView()
            }
        }
    }

    override fun onItemClick(followinfUser: GetUserQuery.FollowingUser?) {
        Log.e("clickedOnItems", "clickedOnItems")
        userUnFollowMutationCall(followinfUser)
    }

    override fun onUserProfileClick(followinfUser: GetUserQuery.FollowingUser?) {
        lifecycleScope.launch {
            var bundle = Bundle()
            bundle.putBoolean(SearchUserProfileFragment.ARGS_FROM_CHAT, false)
            bundle.putString("userId", followinfUser!!.id.toString())

            if (getCurrentUserId()!! == followinfUser!!.id) {
                MainActivity.getMainActivity()?.binding?.bottomNavigation?.selectedItemId =
                    R.id.nav_user_profile_graph
            } else {
                findNavController().navigate(
                    destinationId = R.id.action_global_otherUserProfileFragment,
                    popUpFragId = null,
                    animType = AnimationTypes.SLIDE_ANIM,
                    inclusive = true,
                    args = bundle
                )
            }
        }
    }


    override fun setupClickListeners() {}


    private fun userUnFollowMutationCall(followinfUser: GetUserQuery.FollowingUser?) {
        if (followinfUser!!.isConnected != null && followinfUser!!.isConnected!!) {
            unFollowConfirmation(followinfUser)
        } else {
            userFollowMutationCall(followinfUser)
        }

    }

    private fun userFollowMutationCall(followinfUser: GetUserQuery.FollowingUser?) {
        Log.e("FollowUserIds", followinfUser!!.id.toString())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showProgressView()

                val res = try {
                    apolloClient(
                        requireContext(),
                        getCurrentUserToken()!!
                    ).mutation(UserFollowMutation(followinfUser!!.id!!.toString()))
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("Exception ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
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
                    binding?.root?.snackbar(AppStringConstant1.successfully)
                    getUserFollowingData()
                }
            }
        }
    }

    private fun unFollowConfirmation(followinfUser: GetUserQuery.FollowingUser?) {
        var message = AppStringConstant1.are_you_sure_you_want_to_unfollow_user
            .plus(followinfUser!!.fullName)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage("${message}")
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, which ->
                userUnFollowCall(followinfUser)
            }
            .setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss();
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black));
        alert.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.black));
    }

    private fun userUnFollowCall(followinfUser: GetUserQuery.FollowingUser?) {
        Log.e("FollowingUserIds", followinfUser!!.id.toString())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showProgressView()
                var unfollowMutation = UserUnfollowMutation(followinfUser!!.id!!.toString())
                Log.e("UnfollowMutationList", Gson().toJson(unfollowMutation))
                val res = try {
                    apolloClient(
                        requireContext(),
                        getCurrentUserToken()!!
                    ).mutation(unfollowMutation)
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    binding?.root?.snackbar("Exception ${e.message}")
                    hideProgressView()
                    return@repeatOnLifecycle
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
                    getUserFollowingData()
                }
            }
        }
    }
}