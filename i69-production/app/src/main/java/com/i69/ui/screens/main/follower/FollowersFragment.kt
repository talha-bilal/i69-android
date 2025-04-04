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
import com.i69.ui.adapters.AdapterFollowers
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.utils.*
import kotlinx.coroutines.launch

class FollowersFragment : BaseFragment<FragmentFollowersBinding>(),
    AdapterFollowers.FollowerListListener {
    var giftsAdapter: AdapterFollowers? = null
    var list: MutableList<GetUserQuery.FollowerUser?> = mutableListOf()
    private var TAG: String = FollowersFragment::class.java.simpleName

    private val viewModel: VMProfile by activityViewModels()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFollowersBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {

        giftsAdapter = AdapterFollowers(requireContext(), list, this@FollowersFragment)
        binding?.recyclerViewFolowers?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding?.recyclerViewFolowers?.setHasFixedSize(true)
        binding?.recyclerViewFolowers?.adapter = giftsAdapter
        val observe = viewModel.getupdateFollowerListResultWith()?.observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            giftsAdapter!!.notifyDataSetChanged()
        })
    }

    private fun getUserFollowersData(isShowLoader: Boolean = true) {

        val userid = requireArguments().getString("userId")
        if (userid != null) {
            Log.e("getUserId1111", userid)
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
                Log.e("FollowersFragmntError", "${res.errors}")
                Log.e("FollowersFragmntError1", "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                list.clear()
                var foolowerList = res.data!!.user!!.followerUsers
                Log.e("followerUsers", Gson().toJson(foolowerList))
                val x = mutableListOf<GetUserQuery.FollowerUser?>().apply {
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


    override fun onUserProfileClick(followinfUser: GetUserQuery.FollowerUser?) {
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


    override fun onItemClick(followinfUser: GetUserQuery.FollowerUser?, isRemove: Boolean) {
        Log.e("clickedOnItems", "clickedOnItems")

        if (isRemove) {
            userRemoveFollowMutationCall(followinfUser)

        } else {

            if (followinfUser!!.isConnected != null && followinfUser!!.isConnected!!) {
                unFollowConfirmation(followinfUser)
            } else {
                userFollowMutationCall(followinfUser)
            }
        }
    }


    private fun unFollowConfirmation(followinfUser: GetUserQuery.FollowerUser?) {
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

    private fun userUnFollowCall(followinfUser: GetUserQuery.FollowerUser?) {
        Log.e("FollowingUserIds", followinfUser!!.id.toString())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showProgressView()
                var unfollowMutation = UserUnfollowMutation(followinfUser.id.toString())
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
                    getUserFollowersData()
                }
            }
        }
    }

    private fun userFollowMutationCall(followinfUser: GetUserQuery.FollowerUser?) {
        Log.e("FollowUserIds", followinfUser!!.id.toString())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showProgressView()

                val res = try {
                    apolloClient(
                        requireContext(),
                        getCurrentUserToken()!!
                    ).mutation(UserFollowMutation(followinfUser.id.toString()))
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
                    getUserFollowersData()
                }
            }
        }
    }


    private fun userRemoveFollowMutationCall(followinfUser: GetUserQuery.FollowerUser?) {
        Log.e("FollowUserIds", followinfUser!!.id.toString())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showProgressView()

                val res = try {
                    apolloClient(
                        requireContext(),
                        getCurrentUserToken()!!
                    ).mutation(UserRemoveFollowerMutation(followinfUser.id.toString()))
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
                    getUserFollowersData()
                }
            }
        }
    }

    override fun setupClickListeners() {}
}