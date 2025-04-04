package com.i69.ui.screens.main.subscription

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.i69.*
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.PlanBnefits
import com.i69.R
import com.i69.databinding.FragmentPlanDetailBinding
import com.i69.ui.adapters.*
import com.i69.ui.base.BaseFragment
import com.i69.utils.*

class PlanDetailBuyFragment : BaseFragment<FragmentPlanDetailBinding>() {


    private var isSilverHidden = false
    private lateinit var adapterPlanBenefits: AdapterPlanDetailsBenefits
    private var TAG: String = PlanDetailBuyFragment::class.java.simpleName
    private var planBenefitsList = mutableListOf<PlanBnefits>()

    var selectedPackageId = 0
    var selectedPackageTitle = ""
    var selectedPlanName = ""
    var selectedPlanId = 0
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getStatusBarColor() = R.color.colorPrimary
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPlanDetailBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }


    override fun setupTheme() {
        navController = findNavController()
        viewStringConstModel.data.observe(this@PlanDetailBuyFragment) { data ->

            binding?.stringConstant = data

        }
        viewStringConstModel.data?.also {
            binding?.stringConstant = it.value
//            Log.e("MydataBasesss", it.value!!.messages)
        }

//        bundle.putInt("selectedPlanID",coinPrice!!.id.toInt())
//        bundle.putString("selectedPlanTitle",coinPrice!!.title)
//        bundle.putInt("selectedPackageId",selectedPlanId)
//        bundle.putString("selectedPackageName",selectedPlanName)


        selectedPackageId = requireArguments().getInt("selectedPackageId")
        selectedPackageTitle = requireArguments().getString("selectedPackageName").toString()
        selectedPlanName = requireArguments().getString("selectedPlanTitle").toString()
        selectedPlanId = requireArguments().getInt("selectedPlanID")
        isSilverHidden = requireArguments().getBoolean("isSilverHidden")

        if (isSilverHidden) {
            binding?.llSilver?.setViewGone()
            binding?.llGold?.background =
                ResourcesCompat.getDrawable(resources, R.drawable.bg_right_corners_curved, null)
        } else {
            binding?.llGold?.background =
                ResourcesCompat.getDrawable(resources, R.drawable.plan_gold_back_gradient, null)
            binding?.llSilver?.setViewVisible()
        }

        Log.e("MySelectedPackahgeType", selectedPackageTitle)
//        if (selectedPackageTitle.contains("silver", true)) {
        if (/*selectedPackageTitle.contains(AppStringConstant1.silver_package, true)*/ selectedPackageId == 1) {
            binding?.llSilver?.isSelected = true
            binding?.tvSilver?.isSelected = true
            //binding.viewSilver.visibility= View.VISIBLE
            //binding.tvSilver.setTextColor(resources.getColor(R.color.colorPrimary))
            binding?.purchasePackageTitle?.setText(AppStringConstant1.compare_silver_plan)

            binding?.purchaseDescription?.setText(AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included)
            binding?.purchaseDescription?.attributedString(
                AppStringConstant1.silver,
                requireActivity().resources.getColor(R.color.colorPrimary),
                StyleSpan(Typeface.BOLD)
            )
//        } else if (selectedPackageTitle.contains("gold", true)) {
        } else if (/*selectedPackageTitle.contains(AppStringConstant1.gold_package, true)*/ selectedPackageId == 2) {
            binding?.llGold?.isSelected = true
            binding?.tvGold?.isSelected = true
            //binding.tvGold.setTextColor(resources.getColor(R.color.colorPrimary))
            //binding.viewGold.visibility= View.VISIBLE
            binding?.purchasePackageTitle?.setText(AppStringConstant1.compare_gold_plan)

            binding?.purchaseDescription?.setText(AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included)
            binding?.purchaseDescription?.attributedString(
                AppStringConstant1.gold,
                requireActivity().resources.getColor(R.color.colorPrimary),
                StyleSpan(Typeface.BOLD)
            )

//        } else if (selectedPackageTitle.contains("platimum", true)) {
        } else if (/*selectedPackageTitle.contains(AppStringConstant1.platimum_package, true)*/ selectedPackageId == 3) {
            binding?.llPlatinum?.isSelected = true
            binding?.tvPlatinum?.isSelected = true
            //binding.tvPlatinum.setTextColor(resources.getColor(R.color.colorPrimary))
            //binding.viewPlatinum.visibility= View.VISIBLE
            binding?.purchasePackageTitle?.setText(AppStringConstant1.compare_platinum_plan)

            binding?.purchaseDescription?.setText(AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included)

            binding?.purchaseDescription?.attributedString(
                AppStringConstant1.platinum,
                requireActivity().resources.getColor(R.color.colorPrimary),
                StyleSpan(Typeface.BOLD)
            )


        }

//        tvPlatinum
//        tvGold
//        tvSilver

        getAllPackage()
        binding?.purchaseClose?.setOnClickListener {
            findNavController().popBackStack()

        }

        binding?.buyNowBtn?.setOnClickListener {
            purchaseSubScription()

        }

    }

    override fun setupClickListeners() {

    }


    fun purchaseSubScription() {

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
                Log.e("errorAllPackage", "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {


                Log.e("userCurentSubScription", Gson().toJson(response.data))

                Log.e("myCurrentPlanSelected", selectedPackageTitle)
                if (response.data!!.userSubscription!!.`package` != null) {
                    if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.silver,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageTitle,
                                true
                            )
                        ) {
                            Log.e("silver", "silver")
                            purchaseSubsription()
                        } else if (selectedPackageTitle.contains(AppStringConstant1.gold, true)) {
                            Log.e("silver", "gold")
                            upgradeSubsription(response.data!!.userSubscription!!.plan!!.id!!.toInt())
                        } else if (selectedPackageTitle.contains(
                                AppStringConstant1.platinum,
                                true
                            )
                        ) {
                            Log.e("silver", "platinum")
                            upgradeSubsription(response.data!!.userSubscription!!.plan!!.id!!.toInt())
                        } else {
                            Log.e("silver", "purchase")
                            purchaseSubsription()
                        }
                    } else if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.gold,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageTitle,
                                true
                            )
                        ) {
                            Log.e("gold", "gold")
                            purchaseSubsription()
                        } else if (selectedPackageTitle.contains(AppStringConstant1.silver, true)) {
                            Log.e("gold", "silver")
                            downGradeSubsription()
                        } else if (selectedPackageTitle.contains(
                                AppStringConstant1.platinum,
                                true
                            )
                        ) {
                            Log.e("gold", "platinum")
                            upgradeSubsription(response.data!!.userSubscription!!.plan!!.id!!.toInt())
                        } else {
                            Log.e("gold", "purchase")
                            purchaseSubsription()
                        }
                    } else if (response.data!!.userSubscription!!.`package`!!.name.contains(
                            AppStringConstant1.platinum,
                            true
                        )
                    ) {
                        if (response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageTitle,
                                true
                            )
                        ) {
                            Log.e("platinum", "platinum")
                            purchaseSubsription()
                        } else if (selectedPackageTitle.contains(AppStringConstant1.silver, true)) {
                            Log.e("platinum", "silver")
                            downGradeSubsription()
                        } else if (selectedPackageTitle.contains(AppStringConstant1.gold, true)) {
                            Log.e("platinum", "gold")
                            downGradeSubsription()
                        } else {
                            Log.e("platinum", "purchase")
                            purchaseSubsription()
                        }
                    } else {
                        Log.e("callPurchaseInElse", "callPurchaseInElse")
                        purchaseSubsription()
                    }
                } else {
                    Log.e("callPurchaseInElseElse", "callPurchaseInElseElse")
                    purchaseSubsription()
                }
            }

        }
    }

    private fun purchaseSubsription() {
        var itisFromSubScriptionDialog = false
        if (requireArguments().containsKey("itisFromSubScriptionDialog")) {
            itisFromSubScriptionDialog = requireArguments().getBoolean("itisFromSubScriptionDialog")
        }

        if (itisFromSubScriptionDialog) {

            findNavController().popBackStack()
        } else {


            lifecycleScope.launchWhenResumed {

                val userToken = getCurrentUserToken()!!
                val response = try {
                    apolloClient(requireContext(), userToken).mutation(
                        PurchasePackageMutation(selectedPackageId, selectedPlanId)
                    ).execute()

                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse ${e.message}")
                    hideProgressView()
                    return@launchWhenResumed
                }


                if (response.hasErrors()) {
                    hideProgressView()
                    val errorMessage = response.errors?.get(0)?.message
                    Log.e("errorAllPackagepurchase", "$errorMessage")

                    if (errorMessage != null) {

                        if (errorMessage.contains("Not enough coins")) {
                            binding?.root?.snackbar(
                                errorMessage,
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
                            binding?.root?.autoSnackbarOnTop(errorMessage, Snackbar.LENGTH_LONG) {
                                moveToSubscriptionDetailScreen()
                            }
                        }
                    }
                } else {


                    Log.e(
                        "subscriptionBuy",
                        Gson().toJson(response.data)
                    )
                    hideProgressView()

                    binding?.root?.autoSnackbarOnTop("Subscription active", Snackbar.LENGTH_LONG) {
                        moveToSubscriptionDetailScreen()
                    }
//                if (response.data!!.upgradePackage!!.success!!) {
//                    findNavController().popBackStack()
//                }
                }
            }
        }
    }

    fun moveToSubscriptionDetailScreen() {
        navController?.navigate(com.i69.R.id.action_global_subscription_detail)
    }

    fun downGradeSubsription() {
        lifecycleScope.launchWhenResumed {

            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).mutation(
                    DowngradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorPackagedownGrade", "$errorMessage")

                if (errorMessage != null) {
                    if (errorMessage.contains("Not enough coins")) {
                        binding?.root?.snackbar(
                            errorMessage,
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
                        binding?.root?.autoSnackbarOnTop(errorMessage, Snackbar.LENGTH_LONG) {
                            moveToSubscriptionDetailScreen()
                        }
                    }
                }
            } else {


                Log.e(
                    "subscriptionBuy",
                    Gson().toJson(response.data)
                )

                hideProgressView()
                response.data!!.downgradePackage!!.message?.let {
                    binding?.root?.autoSnackbarOnTop(it, Snackbar.LENGTH_LONG) {
                        moveToSubscriptionDetailScreen()
                    }
                }
//                if (response.data!!.upgradePackage!!.success!!) {
//                    findNavController().popBackStack()
//                }


            }

        }

    }

    fun upgradeSubsription(planId: Int) {
        lifecycleScope.launchWhenResumed {

            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).mutation(
                    UpgradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackageupgrade", "$errorMessage")

                if (errorMessage != null) {
                    if (errorMessage.contains("Not enough coins")) {
                        binding?.root?.snackbar(
                            errorMessage,
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
                        binding?.root?.autoSnackbarOnTop(errorMessage, Snackbar.LENGTH_LONG) {
                            moveToSubscriptionDetailScreen()
                        }
                    }
                }
            } else {


                Log.e(
                    "subscriptionBuy",
                    Gson().toJson(response.data)
                )
                hideProgressView()

                response.data!!.upgradePackage!!.message?.let {
                    binding?.root?.autoSnackbarOnTop(it, Snackbar.LENGTH_LONG) {
                        moveToSubscriptionDetailScreen()
                    }
                }
//                if (response.data!!.upgradePackage!!.success!!) {
//                    findNavController().popBackStack()
//                }


            }

        }

    }


    @SuppressLint("SuspiciousIndentation")
    fun getAllPackage() {

        showProgressView()
        lifecycleScope.launchWhenResumed {

            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).query(
                    GetAllPackagesQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG,"apolloResponse ${e.message}")
                hideProgressView()
                return@launchWhenResumed
            }

            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")

                if (errorMessage != null) {
                    binding?.root?.snackbar(errorMessage)
                }
            } else {


                Log.e(
                    "getAllPackage2",
                    Gson().toJson(response.data!!.allPackages!!.get(0)!!.permissions)
                )
                Log.e(
                    "getAllPackage3",
                    Gson().toJson(response.data!!.allPackages!!.get(0)!!.`package`)
                )
                Log.e(
                    "getAllPackage4", Gson().toJson(response.data!!.allPackages!!.get(0)!!.planSet)
                )
                Log.e("getAllPackage5", Gson().toJson(response.data!!.allPackages!!.get(0)!!.plans))

                planBenefitsList.clear()
                for (i in 0 until response.data!!.allPackages!!.size) {

                    for (j in 0 until response.data!!.allPackages!!.get(i)!!.permissions!!.size) {
                        var descriptions = ""
                        if (response.data!!.allPackages!!.get(i)!!.permissions!!.get(j)!!.description != null) {
                            descriptions =
                                response.data!!.allPackages!!.get(i)!!.permissions!!.get(j)!!.description!!
                        }
//                 if(selectedPackageId.equals(response.data!!.allPackages!!.get(i)!!.id))
                        var benefits = PlanBnefits(
//                            response.data!!.allPackages!!.get(i)!!.permissions!!.get(j)!!.description!!,

                            descriptions,
                            response.data!!.allPackages!!.get(i)!!.permissions!!.get(j)!!.id,

//                            response.data!!.allPackages!!.get(i)!!.name.contains("platimum", true),
                            true,
//                            response.data!!.allPackages!!.get(i)!!.name.contains("silver", true),
                            response.data!!.allPackages!!.get(i)!!.name.contains(
                                AppStringConstant1.silver,
                                true
                            ),

//                            response.data!!.allPackages!!.get(i)!!.name.contains("gold", true),
                            response.data!!.allPackages!!.get(i)!!.name.contains(
                                AppStringConstant1.gold,
                                true
                            ),

                            response.data!!.allPackages!!.get(i)!!.id,

                            )

                        if (planBenefitsList.size > 0) {
                            var isIdAdded = false
                            var localBenefits: PlanBnefits? = null
                            for (m in 0 until planBenefitsList.size) {

                                if (planBenefitsList.get(m).planId.equals(
                                        response.data!!.allPackages!!.get(
                                            i
                                        )!!.permissions!!.get(j)!!.id
                                    )
                                ) {

                                    localBenefits = planBenefitsList.get(m)
                                    planBenefitsList.removeAt(m)
                                    isIdAdded = true
                                    break
                                } else {
                                    isIdAdded = false
                                }
                            }

                            if (isIdAdded) {
                                var isGold = false
                                if (localBenefits!!.isGold) {
                                    isGold = true
                                } else {
                                    isGold = benefits.isGold
                                }

                                var isSilver = false
                                if (localBenefits!!.isSilver) {
                                    isSilver = true
                                } else {
                                    isSilver = benefits.isSilver
                                }


                                var benefits1 = PlanBnefits(
                                    benefits!!.name!!,
                                    benefits.planId,

//                            response.data!!.allPackages!!.get(i)!!.name.contains("platimum", true),
                                    true,
                                    isSilver,

                                    isGold,
                                    response.data!!.allPackages!!.get(i)!!.id
                                )

                                planBenefitsList.add(benefits1)

                            } else {
                                planBenefitsList.add(benefits)
                            }
//                            if (!isIdAdded) {
//                            planBenefitsList.add(benefits)
//                            }
                            //                            else {
//
//                            }
                        } else {
                            planBenefitsList.add(benefits)
                        }
                    }
                }
                adapterPlanBenefits = AdapterPlanDetailsBenefits(requireContext(), isSilverHidden)
                binding?.recyclerViewCoins?.adapter = adapterPlanBenefits
                adapterPlanBenefits.updateItemList(planBenefitsList, selectedPackageTitle)
                hideProgressView()
            }
        }
    }
}
