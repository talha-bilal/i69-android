package com.i69.ui.screens.main.visitors

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.GetUserQuery
import com.i69.R
import com.i69.UserFollowMutation
import com.i69.UserUnfollowMutation
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.BaseUserVisitingModel
import com.i69.databinding.FragmentFollowersBinding
import com.i69.profile.vm.VMProfile
import com.i69.ui.adapters.AdapterVisiting
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.search.userProfile.SearchUserProfileFragment
import com.i69.utils.AnimationTypes
import com.i69.utils.apolloClient
import com.i69.utils.navigate
import com.i69.utils.snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class VisitingFragment : BaseFragment<FragmentFollowersBinding>(),
    AdapterVisiting.FollowerListListener {
    var adapterVisiting: AdapterVisiting? = null

    //var list : MutableList<GetUserQuery.UserVisiting?> = mutableListOf()
    var listVisitors: MutableList<BaseUserVisitingModel> = mutableListOf()
    private val viewModel: VMProfile by activityViewModels()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    lateinit var stringConstant: AppStringConstant
    private var TAG: String = VisitingFragment::class.java.simpleName
    private val formatterDateTimeUTC =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val formatterDateOnly = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).apply {
        //timeZone = TimeZone.getTimeZone("UTC")
        timeZone = TimeZone.getDefault()
    }

    private val formatterTimeOnly = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        //timeZone = TimeZone.getTimeZone("UTC")
        timeZone = TimeZone.getDefault()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFollowersBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        viewStringConstModel.data.observe(this@VisitingFragment) { data ->
            stringConstant = data
        }
        viewStringConstModel.data.also {
            stringConstant = it.value ?: AppStringConstant(requireContext())
        }

        adapterVisiting =
            AdapterVisiting(requireContext(), listVisitors, stringConstant, this@VisitingFragment)
        binding?.recyclerViewFolowers?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding?.recyclerViewFolowers?.setHasFixedSize(true)
        binding?.recyclerViewFolowers?.adapter = adapterVisiting


        viewModel.getupdateVisitingListResultWith().observe(viewLifecycleOwner) {
            //list.clear()
            //list.addAll(it)
            listVisitors.clear()
            val tempList =
                it.filter { it1 -> !it1?.datetimeVisiting.isNullOrEmpty() }.toMutableList()
            tempList.sortWith { o1, o2 ->
                (formatterDateTimeUTC.parse(o2!!.datetimeVisiting) as Date).compareTo(
                    formatterDateTimeUTC.parse(o1!!.datetimeVisiting) as Date
                )
            }

            val distinctDates = tempList.flatMap { it1 ->
                listOf(
                    formatterDateOnly.format(
                        formatterDateTimeUTC.parse(it1?.datetimeVisiting) as Date
                    )
                )
            }.distinct()
            distinctDates.forEach { date ->
                //val visitorTime = formatterDateOnly.parse(date) as Date
                /*listVisitors.add(BaseUserVisitingModel(0, GetUserQuery.UserVisiting("","","","",0,0,"","","",false,null),
                    DateUtils.getRelativeTimeSpanString(visitorTime.time, Date().time, DateUtils.MINUTE_IN_MILLIS)))*/
                listVisitors.add(
                    BaseUserVisitingModel(
                        0, GetUserQuery.UserVisiting("", "", "", "", 0, 0, "", "", "", false, null),
                        date
                    )
                )
                for (i in 0 until tempList.size) {
                    val userVisitor = tempList[i]
                    userVisitor?.let { temp ->
                        /*if (temp.datetimeVisiting?.contains(date) == true) {

                        }*/
                        val visitorTimeNew =
                            formatterDateTimeUTC.parse(temp.datetimeVisiting) as Date
                        if (date == formatterDateOnly.format(visitorTimeNew)) {
                            listVisitors.add(
                                BaseUserVisitingModel(
                                    1, userVisitor,
                                    formatterTimeOnly.format(visitorTimeNew)
                                )
                            )
                        }
                    }
                }
            }
            adapterVisiting?.notifyDataSetChanged()

        }

//        getUserFollowingData()


    }


    private fun getUserFollowingData() {
        val userid = requireArguments().getString("userId")

        lifecycleScope.launch {
            showProgressView()

            val res = try {
                apolloClient(
                    requireContext(),
                    getCurrentUserToken()!!
                ).query(GetUserQuery(userid!!))
//                    .query(GetUserQuery(getCurrentUserId()!!))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                binding?.root?.snackbar("Exception ${e.message}")
//                    Toast.makeText(requireContext(), " ${e.message}", Toast.LENGTH_LONG).show()

                hideProgressView()
                return@launch
            }

            if (res.hasErrors()) {
                hideProgressView()
                val errorMessage = res.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {

                //list.clear()
                var foolowerList = res.data!!.user!!.userVisiting

                Log.e("followerList", Gson().toJson(foolowerList))


                val x = mutableListOf<GetUserQuery.UserVisiting?>().apply {
                    if (foolowerList != null) {
                        addAll(foolowerList)
                    }
                }
                //list.addAll(x)
                //adapterVisiting!!.notifyDataSetChanged()
//                Log.e("LIKE")

                hideProgressView()
            }
        }


    }


    override fun setupClickListeners() {}


//    private fun userUnFollowMutationCall(followinfUser: GetUserQuery.FollowingUser?){
//
//        lifecycleScope.launchWhenResumed {
//            showProgressView()
//           var unfollowMutation = UserUnfollowMutation(followinfUser!!.id!!.toString())
//            Log.e("UnfollowMutationList", Gson().toJson(unfollowMutation))
//            val res = try {
//                apolloClient(
//                    requireContext(),
//                    getCurrentUserToken()!!
//                ).mutation(unfollowMutation)
////                    .mutation(UserUnfollowMutation(followinfUser!!.id!!.toString()))
//                    .execute()
//            } catch (e: ApolloException) {
//                Log.e("apolloResponse ${e.message}")
//                binding.root.snackbar("Exception ${e.message}")
////                    Toast.makeText(requireContext(), " ${e.message}", Toast.LENGTH_LONG).show()
//
//                hideProgressView()
//                return@launchWhenResumed
//            }
//
//            if (res.hasErrors()) {
//                hideProgressView()
//                val errorMessage = res.errors?.get(0)?.message
//                Log.e("errorAllPackage", "$errorMessage")
//
//                if (errorMessage != null) {
//                    binding.root.snackbar(errorMessage)
//                }
//            } else {
//
//
//                binding.root.snackbar(getString(R.string.successfully))
//
//                getUserFollowingData()
//            }
//        }
//    }


    override fun onUserProfileClick(followinfUser: GetUserQuery.UserVisiting?) {
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


    override fun onItemClick(followinfUser: GetUserQuery.UserVisiting?) {
//        userFollowMutationCall(followinfUser)

//        viewModel.onVisitingItemClick.postValue(followinfUser)
        if (followinfUser!!.isConnected != null && followinfUser!!.isConnected!!) {
            unFollowConfirmation(followinfUser.fullName, followinfUser.id.toString())
        } else {
            userFollowMutationCall(followinfUser.id.toString())
        }
    }

    private fun unFollowConfirmation(followinfUser: String, userId: String) {

        var message = AppStringConstant1.are_you_sure_you_want_to_unfollow_user

//        var message = resources.getString(R.string.are_you_sure_you_want_to_unfollow_user)
            .plus(followinfUser!!)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage("${message}")
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, which ->
                userUnFollowCall(userId)
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


    private fun userFollowMutationCall(followinfUser: String) {
        Log.e("FollowUserIds", followinfUser)

        lifecycleScope.launchWhenResumed {
            showProgressView()

            val res = try {
                apolloClient(
                    requireContext(),
                    getCurrentUserToken()!!
                ).mutation(UserFollowMutation(followinfUser))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
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


    fun userUnFollowCall(followinfUser: String) {
        Log.e("FollowingUserIds", followinfUser)

        lifecycleScope.launchWhenResumed {
            showProgressView()
            var unfollowMutation = UserUnfollowMutation(followinfUser)
            Log.e("UnfollowMutationList", Gson().toJson(unfollowMutation))
            val res = try {
                apolloClient(
                    requireContext(),
                    getCurrentUserToken()!!
                ).mutation(unfollowMutation)
//                    .mutation(UserUnfollowMutation(followinfUser!!.id!!.toString()))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
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
                    requireContext(),
                    getCurrentUserToken()!!
                )
                    .query(GetUserQuery(userid!!))
//                    .query(GetUserQuery(getCurrentUserId()!!))
                    .execute()
            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
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

                viewModel.setupdateVisitingListResultWith(x)
                viewModel.setupdateVisitorListResultWith(xy)

                hideProgressView()
            }
        }


    }


}
