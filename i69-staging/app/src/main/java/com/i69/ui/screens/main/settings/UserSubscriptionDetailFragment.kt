package com.i69.ui.screens.main.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.UserSubscriptionQuery
import com.i69.applocalization.AppStringConstant
import com.i69.databinding.FragmentUserSubscriptionDetailBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.viewModels.UserViewModel
import com.i69.utils.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class UserSubscriptionDetailFragment : BaseFragment<FragmentUserSubscriptionDetailBinding>() {

    private val viewModel: UserViewModel by activityViewModels()
    private var userId: String = ""
    private var userToken: String = ""
    private var TAG: String = UserSubscriptionDetailFragment::class.java.simpleName

    private val formatterDateTimeUTC =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZ", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private val formatterDateOnly = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        //timeZone = TimeZone.getTimeZone("UTC")
        timeZone = TimeZone.getDefault()
    }

    lateinit var sharedPref: SharedPref
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserSubscriptionDetailBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {

        sharedPref = SharedPref(requireContext())
        sharedPref.setLanguage(false)
        sharedPref.setLanguageFromSettings(false)
        binding?.toolBarTitle?.text = AppStringConstant(requireContext()).subscription
        binding?.tvSubScription?.text = AppStringConstant(requireContext()).no_active_subscription
        navController = findNavController()

        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!
            //userChatID= getChatUserId()!!
        }

        userSubscription()

    }


    override fun setupClickListeners() {
        binding?.toolbarHamburger?.setOnClickListener {
            //activity?.onBackPressed()
            findNavController().popBackStack()
            //navController.navigate(R.id.nav_search_graph)
            //activity?.finish()
            //getActivity()?.getFragmentManager()?.popBackStack();
            //activity?.finishAfterTransition()
        }

//        binding.subScriptionLayout.setOnClickListener {
//
//        }
    }


    private fun userSubscription() {
        showProgressView()
        lifecycleScope.launchWhenResumed {

            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).query(
                    UserSubscriptionQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG, "userSubscription: Error: $errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {
                Log.e(TAG, "userSubscription: Response: ${Gson().toJson(response.data)}")
                hideProgressView()
                if (response.data!!.userSubscription!!.`package` != null) {
                    if (response.data?.userSubscription?.`package`?.id?.contains("1", true) == true
                        || response.data?.userSubscription?.`package`?.id?.contains(
                            "2",
                            true
                        ) == true
                    ) {

                        binding?.llActionsRoot?.setViewVisible()
                        binding?.llButtonSubscribe?.setViewGone()
                        binding?.llButtonUpgrade?.setViewVisible()
                        binding?.llButtonUpgrade?.setOnClickListener {
                            if (response.data?.userSubscription?.`package`?.id?.contains(
                                    "2",
                                    true
                                ) == true
                            )
                                navigatePlanPurchase("Gold")
                            else
                                navigatePlanPurchase("Silver")
                        }
                    }
                    val userSubscription = response.data?.userSubscription

                    binding?.tvSubscriptionPrice?.setViewVisible()
                    binding?.tvSubscriptionDescription?.setViewVisible()
                    binding?.tvSubscriptionDate?.setViewVisible()
                    binding?.tvSubscriptionLeft?.setViewVisible()

                    binding?.tvSubscriptionPrice?.text = requireContext().resources
                        .getString(com.i69.R.string.coins).plus(": ")
                        .plus(userSubscription?.plan?.priceInCoins.toString())
                    if (userSubscription?.plan?.isOnDiscount == true) {
                        binding?.tvSubscriptionDescription?.text =
                            userSubscription.plan.dicountedPriceInCoins.toString()
                    } else {
                        binding?.tvSubscriptionDescription?.setViewGone()
                    }

                    val startDate =
                        formatterDateTimeUTC.parse(userSubscription?.startsAt.toString())
                    val endDate = formatterDateTimeUTC.parse(userSubscription?.endsAt.toString())
                    if (startDate != null && endDate != null) {
                        binding?.tvSubscriptionDate?.text =
                            formatterDateOnly.format(startDate).plus(" - ")
                                .plus(formatterDateOnly.format(endDate))
                    } else binding?.tvSubscriptionDate?.setViewGone()

                    val diffInMillisec: Long = endDate.time - Date().time
                    val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diffInMillisec)
                    binding?.tvSubscriptionLeft?.text =
                        String.format(getString(com.i69.R.string.days_left), diffInDays)

                    binding?.tvSubScription?.text =
                        requireContext().resources.getString(com.i69.R.string.your_subscription)
                            .plus(response.data!!.userSubscription!!.`package`!!.name).plus("(")
                            .plus(response.data!!.userSubscription!!.plan!!.title).plus(")")
                }

                if (response.data?.userSubscription?.isActive == false) {
                    binding?.tvSubscriptionPrice?.setViewGone()
                    binding?.tvSubscriptionDescription?.setViewGone()
                    binding?.tvSubscriptionDate?.setViewGone()
                    binding?.tvSubscriptionLeft?.setViewGone()
                    binding?.llButtonUpgrade?.setViewGone()
                    binding?.llActionsRoot?.setViewVisible()
                    binding?.llButtonSubscribe?.setViewVisible()
                    binding?.llButtonSubscribe?.setOnClickListener {
                        navigatePlanPurchase("")
                    }
                }
            }

        }


    }

    private fun navigatePlanPurchase(type: String) {
        val bundle = Bundle()
        bundle.putString("type", type)
        findNavController().navigate(com.i69.R.id.action_global_plan, bundle)
    }
}