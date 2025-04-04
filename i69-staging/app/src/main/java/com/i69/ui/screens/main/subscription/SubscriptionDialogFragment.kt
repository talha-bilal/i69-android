package com.i69.ui.screens.main.subscription

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.i69.*
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.R
import dagger.hilt.android.AndroidEntryPoint
import com.i69.databinding.DialogSubscriptionFragmentBinding
import com.i69.ui.adapters.AdapterPlanCoinPrice
import com.i69.ui.adapters.AdapterPurchasePlanType
import com.i69.ui.viewModels.SearchViewModel
import com.i69.utils.AnimationTypes
import com.i69.utils.apolloClient
import com.i69.utils.navigate

import com.i69.utils.snackbar

@AndroidEntryPoint
class SubscriptionDialogFragment(
    var userToken: String?,
    var userId: String?,
    var purchaseCoinMessage: String?
) : DialogFragment() {
    private var TAG: String = SubscriptionDialogFragment::class.java.simpleName
    private val mViewModel: SearchViewModel by activityViewModels()

    private lateinit var binding: DialogSubscriptionFragmentBinding
    var navController: NavController? = null

    private lateinit var adapterCoinPrice: AdapterPlanCoinPrice

    //    private lateinit var adapterPlanBenefits : AdapterPlanBenefits
    var amount = 0.0
    private lateinit var adapterPurchasePlanType: AdapterPurchasePlanType

    var selectedPackageName = ""
    var selectedPackageId = 0

    var selectedPlanTitle = ""
    var selectedPlanID = 0
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSubscriptionFragmentBinding.inflate(inflater, container, false)
        viewStringConstModel.data.observe(this@SubscriptionDialogFragment) { data ->

            binding.stringConstant = data

        }
        viewStringConstModel.data.also {
            binding.stringConstant = it.value
            //            Log.e("MydataBasesss", it.value!!.messages)
        }
        setupTheme()
//        binding.btnSkip.setOnClickListener {
//            dialog?.dismiss()
//        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        dialog?.window?.setLayout(
            width - 50,
            height - 50
        )

        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
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
        getAllPackage()

//        val lookingFor = getString(R.string.looking_for)

//        lifecycleScope.launch {
//
//            mViewModel.getDefaultPickers(userToken!!).observe(viewLifecycleOwner) {
//                it?.let { defaultPicker ->
//                    mViewModel.updateDefaultPicker(lookingFor, defaultPicker)
//                    val agePicker = defaultPicker.agePicker
//                    binding.ageRangeSeekBar.setRange(
//                        agePicker[0].value.toFloat(),
//                        agePicker[agePicker.size - 1].value.toFloat()
//                    )
//                }
//            }
//        }


//        binding.personalLayoutItem.tagsBtn.setOnChipClickListener { tag, position ->
//            if (mViewModel.tags.size - 1 >= position) {
//                mViewModel.tags.removeAt(position)
//                updateTags()
//            }
//        }

//        mViewModel.btnTagsAddListener = View.OnClickListener {
//            navController!!.navigate(R.id.action_searchFiltersFragment_to_selectTagsFragment)
//        }

//        mViewModel.searchBtnClickListener = View.OnClickListener {
//            //  showProgressView()
//
//            Permissions.check(
//                requireActivity(),
//                permission.ACCESS_COARSE_LOCATION,
//                null,
//                object : PermissionHandler() {
//                    @SuppressLint("MissingPermission")
//                    override fun onGranted() {
//                        val locationService =
//                            LocationServices.getFusedLocationProviderClient(activity!!)
//
//                        locationService.lastLocation.addOnSuccessListener { location: Location? ->
//                            val searchKey: String = "" //binding.keyInput.text.toString()
//                            var lat: Double? = null
//                            var lon: Double? = null
//                            if (searchKey.isEmpty()) {
//                                lat = location?.latitude
//                                lon = location?.longitude
//                            }
//                            val searchRequest = SearchRequest(
//                                //   interestedIn = interestedIn.id,
//                                id = userId!!,
//                                searchKey = searchKey,
//                                lat = lat,
//                                long = lon
//                            )
//                            Log.e("search params", Gson().toJson(searchRequest))
//                            mViewModel.getSearchUsers(
//                                _searchRequest = searchRequest,
//                                token = userToken!!
//                            ) { error ->
//                                if (error == null) {
//
//                                    dismiss()
////                                    moveUp()
//                                    //hideProgressView()
////                                    navController!!.navigate(R.id.action_searchFiltersFragment_to_searchResultFragment)
//                                } else {
//                                    //   hideProgressView()
//                                    binding.root.snackbar(error)
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onDenied(
//                        context: Context?,
//                        deniedPermissions: ArrayList<String>?
//                    ) {
//                        binding.root.snackbar(getString(R.string.search_permission))
//                        // hideProgressView()
//                    }
//                })
//        }

    }


    fun updateSearchResultWithCoin() {
        var searchResutModel = mViewModel.getSearchRequest()

        if (searchResutModel != null) {
//            searchResutModel.autoDeductCoin = 1
//            lifecycleScope.launch {
//                userToken = getCurrentUserToken()!!
//                userId = getCurrentUserId()!!
//
//                Log.e("usertokenn $userToken")
//            }
            Log.e(TAG,"ExtraSearchCalls"+ "updateSearchResultWithCoin: ")
            mViewModel.getSearchUsers(
                _searchRequest = searchResutModel,
                token = userToken!!,
                autoDeductCoin = 1,
                context = requireContext()
            ) { error ->
                if (error == null) {
//                    hideProgressView()
                    mViewModel.updateSearchResultWithCoin()

                    dismiss()
//                    findNavController().navigate(
//                        destinationId = R.id.actionGoToPurchaseFragment,
//                        popUpFragId = null,
//                        animType = AnimationTypes.SLIDE_ANIM,
//                        inclusive = true,
//
//                        )

                } else {
//                    hideProgressView()

                    if (error!!.contains(getString(R.string.no_enough_coins))) {

                        binding.root.snackbar(
                            AppStringConstant1.dont_have_enough_coin_upgrade_plan,
                            Snackbar.LENGTH_INDEFINITE,
                            callback = {

//                                navController?.navigate(
//                                    R.id.action_searchFiltersFragment_to_searchResultFragment,
//                                )
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

    fun getAllPackage() {
//        showProgressView()
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).query(
                    GetAllPackagesQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")

                if (errorMessage != null) {
                    binding.root.snackbar(errorMessage)
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


                var ivArrayDotsPager = arrayOfNulls<ImageView>(response.data!!.allPackages!!.size)
                for (i in 0 until ivArrayDotsPager.size) {
                    Log.e("MyDotCreatd", "$i")
                    ivArrayDotsPager[i] = ImageView(requireContext())
                    val params = LinearLayout.LayoutParams(
                        25, 25
                    )
                    params.setMargins(10, 0, 10, 0)
                    ivArrayDotsPager[i]!!.layoutParams = params
                    ivArrayDotsPager[i]!!.setImageResource(R.drawable.default_dot)
                    ivArrayDotsPager[i]!!.setOnClickListener { view ->
                        view.alpha = 1f
                    }
                    binding.pagerDots.addView(ivArrayDotsPager[i])
                    binding.pagerDots.bringToFront()
                }
                ivArrayDotsPager[0]!!.setImageResource(R.drawable.selected_dot)

                selectedPackageName = response.data!!.allPackages!!.get(0)!!.name
                selectedPackageId = response.data!!.allPackages!!.get(0)!!.id.toInt()

                adapterCoinPrice = AdapterPlanCoinPrice(requireContext(),
                    object : AdapterPlanCoinPrice.PlanCoinPriceInterface {
                        override fun onClick(index: Int, coinPrice: GetAllPackagesQuery.Plan?) {
                            Log.e("paymentCurrency", "${coinPrice?.title}")
                            selectedPlanTitle = coinPrice!!.title.toString()
                            selectedPlanID = coinPrice!!.id.toInt()
//                            amount = coinPrice.discountedPrice.toDouble()
//                            val bundle = Bundle()
//                            bundle.putInt("selectedPlanID",coinPrice!!.id.toInt())
//                            bundle.putString("selectedPlanTitle",coinPrice!!.title)
//                            bundle.putInt("selectedPackageId",selectedPackageId)
//                            bundle.putString("selectedPackageName",selectedPackageName)


                            purchaseSubScription(coinPrice!!.id.toInt())
//                            findNavController().navigate(
//                                destinationId = R.id.action_global_plan_detail,
//                                popUpFragId = null,
//                                animType = AnimationTypes.SLIDE_ANIM,
//                                inclusive = true,
//                                args = bundle
//
//                            )
                        }
                    })
//                adapterCoinPrice.updateItemList(response.data!!.allPackages!!.get(0)!!.plans)
                adapterCoinPrice.updateItemList(
                    response.data!!.allPackages!!.get(0)!!.plans,
                    selectedPackageName
                )

                binding.recyclerViewCoins.adapter = adapterCoinPrice

//
//                adapterPlanBenefits   = AdapterPlanBenefits(requireContext())
//                binding. recyclerViewBenefits.adapter = adapterPlanBenefits
//                adapterPlanBenefits.updateItemList(response.data!!.allPackages!!.get(0)!!.permissions)

                binding.recyclerViewPlan.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapterPurchasePlanType = AdapterPurchasePlanType(
                    requireContext(), response.data!!.allPackages!!,
                    object : AdapterPurchasePlanType.PlanInterface {
                        override fun onClick(
                            index: Int,
                            coinPrice: GetAllPackagesQuery.AllPackage?
                        ) {
                            Log.e("paymentCurrency", "${coinPrice?.name}")

                            selectedPackageName = coinPrice?.name.toString()
                            selectedPackageId = coinPrice?.id!!.toInt()
                            for (i in 0 until ivArrayDotsPager.size) {
                                ivArrayDotsPager.get(i)?.setImageResource(R.drawable.default_dot)
                            }
                            ivArrayDotsPager[index]!!.setImageResource(R.drawable.selected_dot)

                            adapterCoinPrice.updateItemList(
                                response.data!!.allPackages!!.get(index)!!.plans,
                                selectedPackageName
                            )
//                            adapterPlanBenefits.updateItemList(response.data!!.allPackages!!.get(index)!!.permissions)

                        }
                    }
                )
                binding.recyclerViewPlan.adapter = adapterPurchasePlanType
//                hideProgressView()
            }
        }
    }


    fun purchaseSubScription(selectedPlanId: Int) {

//        showProgressView()
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).query(
                    UserSubscriptionQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackage", "$errorMessage")

                if (errorMessage != null) {
                    binding.root.snackbar(errorMessage)
                }
            } else {


                Log.e("userCurentSubScription", Gson().toJson(response.data))

//                Log.e("myCurrentPlanSelected", selectedPackageTitle)
                if (response.data!!.userSubscription!!.`package` != null) {
//                    if (response.data!!.userSubscription!!.`package`!!.name.contains(
//                            "silver",
//                            true
//                        )
//                    )
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
                            Log.e("silver", "silver")

                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) &&
                            selectedPackageName.contains(AppStringConstant1.gold, true)
//                            selectedPackageTitle.contains("gold", true)
                        ) {
                            Log.e("silver", "gold")
                            upgradeSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) &&
                            selectedPackageName.contains(AppStringConstant1.platinum, true)
//                            selectedPackageTitle.contains("platimum", true)
                        ) {
                            Log.e("silver", "platinum")
                            upgradeSubsription(selectedPlanId)
                        } else {
                            Log.e("silver", "purchase")
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
                            Log.e("gold", "gold")
                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) &&
//                            selectedPackageTitle.contains("silver", true)
                            selectedPackageName.contains(AppStringConstant1.silver, true)
                        ) {


                            Log.e("gold", "silver")
                            downGradeSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) &&
                            selectedPackageName.contains(AppStringConstant1.platinum, true)
//                            selectedPackageTitle.contains("platimum", true)
                        ) {

                            Log.e("gold", "platinum")
                            upgradeSubsription(selectedPlanId)
                        } else {
                            Log.e("gold", "purchase")
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
                            Log.e("platinum", "platinum")
                            purchaseSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) && selectedPackageName.contains(AppStringConstant1.silver, true)
//                            selectedPackageTitle.contains("silver", true)
                        ) {
                            Log.e("platinum", "silver")
                            downGradeSubsription(selectedPlanId)
                        } else if (!response.data!!.userSubscription!!.`package`!!.name.contains(
                                selectedPackageName,
                                true
                            ) &&
                            selectedPackageName.contains(AppStringConstant1.gold, true)
//                            selectedPackageTitle.contains("gold", true)
                        ) {

                            Log.e("platinum", "gold")
                            downGradeSubsription(selectedPlanId)
                        } else {
                            Log.e("platinum", "purchase")
                            purchaseSubsription(selectedPlanId)
                        }
                    } else {
                        Log.e("callPurchaseInElse", "callPurchaseInElse")
                        purchaseSubsription(selectedPlanId)
                    }
                } else {
                    Log.e("callPurchaseInElseElse", "callPurchaseInElseElse")
                    purchaseSubsription(selectedPlanId)
                }
            }

        }


    }


    fun purchaseSubsription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    PurchasePackageMutation(selectedPackageId, selectedPlanId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackagepurchase", "$errorMessage")

                if (errorMessage != null) {

                    if (errorMessage!!.contains(getString(R.string.no_enough_coins))) {

                        binding.root.snackbar(
                            AppStringConstant1.dont_have_enough_coin_upgrade_plan,
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


                Log.e(
                    "subscriptionBuy",
                    Gson().toJson(response.data)
                )
//                hideProgressView()

                if (response.data!!.purchasePackage!!.success!!) {
                    dismiss()
//                    findNavController().popBackStack()
                }

            }

        }

    }

    fun downGradeSubsription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    DowngradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("erroragedownGrade", "$errorMessage")

                if (errorMessage != null) {
                    if (errorMessage!!.contains(getString(R.string.no_enough_coins))) {

                        binding.root.snackbar(
                            AppStringConstant1.dont_have_enough_coin_upgrade_plan,
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


                Log.e(
                    "subscriptionBuy",
                    Gson().toJson(response.data)
                )

//                hideProgressView()
                response.data!!.downgradePackage!!.message?.let { binding.root.snackbar(it) }
                if (response.data!!.downgradePackage!!.success!!) {
//                    findNavController().popBackStack()
                    dismiss()
                }

            }

        }

    }

    fun upgradeSubsription(selectedPlanId: Int) {
        lifecycleScope.launchWhenResumed {

//            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken!!).mutation(
                    UpgradePackageMutation(selectedPackageId)
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
//                hideProgressView()
                return@launchWhenResumed
            }


            if (response.hasErrors()) {
//                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e("errorAllPackageupgrade", "$errorMessage")

                if (errorMessage != null) {
                    if (errorMessage!!.contains(getString(R.string.no_enough_coins))) {

                        binding.root.snackbar(
                            AppStringConstant1.dont_have_enough_coin_upgrade_plan,
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


                Log.e(
                    "subscriptionBuy",
                    Gson().toJson(response.data)
                )
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
