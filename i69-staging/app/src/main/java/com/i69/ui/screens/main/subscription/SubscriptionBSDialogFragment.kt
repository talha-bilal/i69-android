package com.i69.ui.screens.main.subscription

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.i69.*
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.BaseAllPackageModel
import com.i69.R
import com.i69.databinding.SheetSubscriptionFragmentBinding
import com.i69.ui.adapters.PurchasePlanMainAdapter
import com.i69.ui.viewModels.SearchViewModel
import com.i69.utils.AnimationTypes
import com.i69.utils.apolloClient
import com.i69.utils.navigate
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionBSDialogFragment : BottomSheetDialogFragment() {

    private val mViewModel: SearchViewModel by activityViewModels()
    private var TAG: String = SubscriptionBSDialogFragment::class.java.simpleName
    private lateinit var binding: SheetSubscriptionFragmentBinding
    var navController: NavController? = null

    var amount = 0.0

    private lateinit var purchasePlanMainAdapter: PurchasePlanMainAdapter

    var selectedPackageName = ""
    var selectedPackageId = 0

    var selectedPlanTitle = ""
    var selectedPlanID = 0
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    var userToken: String? = ""
    var userId: String? = ""
    var purchaseCoinMessage: String? = ""

    companion object {
        fun newInstance(
            userToken: String?, userId: String?, purchaseCoinMessage: String?
        ): SubscriptionBSDialogFragment {
            val args = Bundle()
            args.putString("userToken", userToken)
            args.putString("userId", userId)
            args.putString("purchaseCoinMessage", purchaseCoinMessage)
            val fragment = SubscriptionBSDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userToken = arguments?.getString("userToken")
        userId = arguments?.getString("userId")
        purchaseCoinMessage = arguments?.getString("purchaseCoinMessage")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = SheetSubscriptionFragmentBinding.inflate(inflater, container, false)
        viewStringConstModel.data.observe(this@SubscriptionBSDialogFragment) { data ->
            binding.stringConstant = data

        }
        viewStringConstModel.data.also {
            binding.stringConstant = it.value
        }
        setupTheme()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.MyBottomSheetDialogTheme)
    }

    fun setupTheme() {
        navController = findNavController()

        binding.icClose.setOnClickListener {
            dismiss()
        }

        binding.tvPurchaseUsingCoin.setOnClickListener {
            updateSearchResultWithCoin()
        }

        binding.purchaseUsingCoin.text = purchaseCoinMessage
        binding.llComparePlan.setOnClickListener {
            if (selectedPackageName.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putInt("selectedPlanID", selectedPlanID)
                bundle.putString("selectedPlanTitle", selectedPlanTitle)
                bundle.putInt("selectedPackageId", selectedPackageId)
                bundle.putString("selectedPackageName", selectedPackageName)
                bundle.putBoolean("itisFromSubScriptionDialog", true)

                findNavController().navigate(
                    destinationId = R.id.action_global_plan_detail,
                    popUpFragId = null,
                    animType = AnimationTypes.SLIDE_ANIM,
                    inclusive = true,
                    args = bundle

                )
            }
        }
        getAllPackage()
    }

    private fun updateSearchResultWithCoin() {
        val searchResultModel = mViewModel.getSearchRequest()

        if (searchResultModel != null) {
            mViewModel.getSearchUsers(
                _searchRequest = searchResultModel,
                token = userToken!!,
                autoDeductCoin = 1,
                context = requireContext()
            ) { error ->
                if (error == null) {
                    mViewModel.updateSearchResultWithCoin()
                    dismiss()
                } else {
                    if (error.contains(getString(R.string.no_enough_coins))) {
                        binding.root.snackbar(AppStringConstant1.dont_have_enough_coin_upgrade_plan,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(
                                    destinationId = R.id.actionGoToPurchaseFragment,
                                    popUpFragId = null,
                                    animType = AnimationTypes.SLIDE_ANIM,
                                    inclusive = false,

                                    )
                            })
                    } else {
                        binding.root.snackbar(error)
                    }
                }
            }
        }
    }

    private fun getAllPackage() {
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllPackagesQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                return@launchWhenResumed
            }

            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG,"getAllPackage: errorAllPackage: $errorMessage")
                if (errorMessage != null) {
                    binding.root.snackbar(errorMessage)
                }
            } else {
                selectedPackageName = response.data!!.allPackages!![0]!!.name
                selectedPackageId = response.data!!.allPackages!![0]!!.id.toInt()

                binding.recyclerViewPlan.layoutManager = LinearLayoutManager(context)
                val list = arrayListOf<BaseAllPackageModel>()
                response.data!!.allPackages!!.forEach {
                    Log.e(TAG,"Package: $it")
                    list.add(BaseAllPackageModel(false, it))
                }
                purchasePlanMainAdapter = PurchasePlanMainAdapter(
                    requireContext(),
                    list,
                    viewStringConstModel.data.value,
                    object : PurchasePlanMainAdapter.PlanInterface {

                        override fun onSubscribeClick(
                            selectedPackageId: Int,
                            selectedPackageName: String,
                            selectedPlanID: Int,
                            selectedPlanTitle: String
                        ) {
                            this@SubscriptionBSDialogFragment.selectedPackageId = selectedPackageId
                            this@SubscriptionBSDialogFragment.selectedPackageName =
                                selectedPackageName
                            this@SubscriptionBSDialogFragment.selectedPlanID = selectedPlanID
                            this@SubscriptionBSDialogFragment.selectedPlanTitle = selectedPlanTitle
                            purchaseSubscription(selectedPlanID)
                        }
                    })
                binding.recyclerViewPlan.adapter = purchasePlanMainAdapter
            }
        }
    }

    private fun purchaseSubscription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient(requireContext(), userToken!!).query(
                    UserSubscriptionQuery()
                ).execute()
            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                return@launchWhenResumed
            }
            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG,"purchaseSubscription: Error: $errorMessage")
                if (errorMessage != null) {
                    binding.root.snackbar(errorMessage)
                }
            } else {
                Log.e(TAG,"purchaseSubscription: Response: ${Gson().toJson(response.data)}")
                if (response.data!!.userSubscription!!.`package` != null) {
                    if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.silver,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            )
                        ) {
                            Log.e(TAG,"purchaseSubscription: silver package")
                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.gold, true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: gold package")
                            upgradeSubscription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.platinum, true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: platinum package")
                            upgradeSubscription(selectedPlanId)
                        } else {
                            Log.e(TAG,"purchaseSubscription: purchase subscription")
                            purchaseSubsription(selectedPlanId)
                        }
                    } else if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.gold,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            )
                        ) {
                            Log.e(TAG,"purchaseSubscription: gold package")
                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.silver, true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: silver package")
                            downgradeSubscription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.platinum, true)
//                            selectedPackageTitle.contains("platimum", true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: platinum package")
                            upgradeSubscription(selectedPlanId)
                        } else {
                            Log.e(TAG,"purchaseSubscription: purchase subscription")
                            purchaseSubsription(selectedPlanId)
                        }
                    } else if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.platinum,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            )
                        ) {
                            Log.e(TAG,"purchaseSubscription: platinum package")
                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName, true
                            ) && selectedPackageName.contains(AppStringConstant1.silver, true)
//                            selectedPackageTitle.contains("silver", true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: silver package")
                            downgradeSubscription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.gold, true)
//                            selectedPackageTitle.contains("gold", true)
                        ) {
                            Log.e(TAG,"purchaseSubscription: gold package")
                            downgradeSubscription(selectedPlanId)
                        } else {
                            Log.e(TAG,"purchaseSubscription: purchase subscription")
                            purchaseSubsription(selectedPlanId)
                        }
                    } else {
                        Log.e(TAG,"purchaseSubscription: callPurchaseInElse")
                        purchaseSubsription(selectedPlanId)
                    }
                } else {
                    Log.e(TAG,"purchaseSubscription: callPurchaseInElseElse")
                    purchaseSubsription(selectedPlanId)
                }
            }
        }
    }

    private fun purchaseSubsription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {
//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    PurchasePackageMutation(selectedPackageId, selectedPlanId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }

            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG,"purchaseSubsription: errorAllPackagePurchase : $errorMessage")

                if (errorMessage != null) {
                    if (errorMessage.contains(getString(R.string.no_enough_coins))) {
                        binding.root.snackbar(AppStringConstant1.dont_have_enough_coin_upgrade_plan,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(
                                    destinationId = R.id.actionGoToPurchaseFragment,
                                    popUpFragId = null,
                                    animType = AnimationTypes.SLIDE_ANIM,
                                    inclusive = true,
                                )
                            })
                    } else {
                        binding.root.snackbar(errorMessage)
                    }
                }
            } else {
                Log.e(TAG,"purchaseSubsription: subscriptionBuy ${Gson().toJson(response.data)}")
//                hideProgressView()
                if (response.data!!.purchasePackage!!.success!!) {
                    dismiss()
//                    findNavController().popBackStack()
                }
            }

        }

    }

    private fun downgradeSubscription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {
//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    DowngradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG,"downgradeSubscription: erroragedownGrade: $errorMessage")

                if (errorMessage != null) {
                    if (errorMessage.contains(getString(R.string.no_enough_coins))) {
                        binding.root.snackbar(AppStringConstant1.dont_have_enough_coin_upgrade_plan,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(
                                    destinationId = R.id.actionGoToPurchaseFragment,
                                    popUpFragId = null,
                                    animType = AnimationTypes.SLIDE_ANIM,
                                    inclusive = true,
                                )
                            })
                    } else {
                        binding.root.snackbar(errorMessage)
                    }
                }
            } else {
                Log.e(TAG,"downgradeSubscription: subscriptionBuy: ${Gson().toJson(response.data)}")
//                hideProgressView()
                response.data!!.downgradePackage!!.message?.let { binding.root.snackbar(it) }
                if (response.data!!.downgradePackage!!.success!!) {
//                    findNavController().popBackStack()
                    dismiss()
                }

            }

        }

    }

    private fun upgradeSubscription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    UpgradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG,"upgradeSubscription: errorAllPackageupgrade: $errorMessage")

                if (errorMessage != null) {
                    if (errorMessage.contains(getString(R.string.no_enough_coins))) {

                        binding.root.snackbar(AppStringConstant1.dont_have_enough_coin_upgrade_plan,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {
                                findNavController().navigate(
                                    destinationId = R.id.actionGoToPurchaseFragment,
                                    popUpFragId = null,
                                    animType = AnimationTypes.SLIDE_ANIM,
                                    inclusive = true,
                                )
                            })
                    } else {
                        binding.root.snackbar(errorMessage)
                    }
                }
            } else {

                Log.e(TAG,"upgradeSubscription: subscriptionBuy : ${Gson().toJson(response.data)}")
//                hideProgressView()

                response.data!!.upgradePackage!!.message?.let { binding.root.snackbar(it) }
                if (response.data!!.upgradePackage!!.success!!) {
                    dismiss()
//                    findNavController().popBackStack()
                }

            }
        }
    }

}