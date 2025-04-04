package com.i69.ui.screens.main.subscription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.google.gson.Gson
import com.i69.GetAllPackagesQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.databinding.FragmentPurchasePlanBinding
import com.i69.ui.adapters.AdapterPlanBenefits
import com.i69.ui.adapters.AdapterPlanCoinPrice
import com.i69.ui.adapters.AdapterPurchasePlanType
import com.i69.ui.base.BaseFragment
import com.i69.utils.*


class PlanDetailFragment : BaseFragment<FragmentPurchasePlanBinding>() {

    private var isSilverHidden = false

    //    private lateinit var adapterCoinPrice: AdapterUserPlanCoinPrice
private lateinit var adapterCoinPrice: AdapterPlanCoinPrice
    private lateinit var adapterPlanBenefits : AdapterPlanBenefits
    var amount = 0.0
    private lateinit var adapterPurchasePlanType: AdapterPurchasePlanType
    private var TAG: String = PlanDetailFragment::class.java.simpleName
    var selectedPackageName = ""
    var selectedPackageId = 0

    var selectedPlanName = ""
    var selectedPlanId = 0

    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getStatusBarColor() = R.color.colorPrimary
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPurchasePlanBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    private val hidePackagesList = mutableListOf<String>()

    override fun setupTheme() {
        val bundle = arguments
        if (bundle?.containsKey("type") == true){
            val type = bundle.getString("type")
            if (type == "Silver") {
                hidePackagesList.add("1")
            }
            else if (type == "Gold") {
                isSilverHidden = true
                hidePackagesList.add("1")
                hidePackagesList.add("2")
            }
        }

        viewStringConstModel.data.observe(this@PlanDetailFragment) { data ->

            binding?.stringConstant = data

        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
    //            Log.e("MydataBasesss", it.value!!.messages)
        }
        binding?.purchaseClose?.setOnClickListener {
            findNavController().popBackStack()

        }
        getAllPackage()

    }

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
                    "getAllPackage",
                    Gson().toJson(response)
                )
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
                        25,25
                    )
                    params.setMargins(10, 0, 10, 0)
                    ivArrayDotsPager[i]!!.layoutParams = params
                    ivArrayDotsPager[i]!!.setImageResource(R.drawable.default_dot)
                    ivArrayDotsPager[i]!!.setOnClickListener { view ->
                        view.alpha = 1f
                    }
                   binding?.pagerDots?.addView(ivArrayDotsPager[i])
                    binding?.pagerDots?.bringToFront()
                }
                ivArrayDotsPager[0]!!.setImageResource(R.drawable.selected_dot)

                selectedPackageName = response.data!!.allPackages!!.get(0)!!.name
                selectedPackageId = response.data!!.allPackages!!.get(0)!!.id.toInt()

                selectedPlanName = response.data!!.allPackages!![0]!!.plans!![0]!!.title!!
                selectedPlanId = response.data!!.allPackages!![0]!!.plans!![0]!!.id.toInt()

//                adapterCoinPrice = AdapterUserPlanCoinPrice(requireContext(),
//                    object : AdapterUserPlanCoinPrice.PlanCoinPriceInterface {
                adapterCoinPrice = AdapterPlanCoinPrice(requireContext(),
                    object : AdapterPlanCoinPrice.PlanCoinPriceInterface {
                        override fun onClick(index: Int, coinPrice: GetAllPackagesQuery.Plan?) {
                            Log.e("paymentCurrency", "${coinPrice?.title}")
//                            amount = coinPrice.discountedPrice.toDouble()
                            selectedPlanName = coinPrice!!.title!!
                            selectedPlanId = coinPrice!!.id.toInt()
                            openPlanDetailScreen()
                        }
                    })
//                adapterCoinPrice.updateItemList(response.data!!.allPackages!!.get(0)!!.plans)
                adapterCoinPrice.updateItemList(response.data!!.allPackages!!.get(0)!!.plans, selectedPackageId.toString())
                binding?.tvCompare?.setVisibility(View.VISIBLE)
                binding?.recyclerViewCoins?.adapter = adapterCoinPrice


                adapterPlanBenefits = AdapterPlanBenefits(requireContext())
                binding?.recyclerViewBenefits?.adapter = adapterPlanBenefits
                adapterPlanBenefits.updateItemList(response.data!!.allPackages!!.get(0)!!.permissions)

                binding?.recyclerViewPlan?.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapterPurchasePlanType = AdapterPurchasePlanType(
                    requireContext(), response.data!!.allPackages!!,
                    object : AdapterPurchasePlanType.PlanInterface {
                        override fun onClick(index: Int, coinPrice: GetAllPackagesQuery.AllPackage?) {
                            Log.e("paymentCurrency", "${coinPrice?.name}")

                            selectedPackageName =coinPrice?.name.toString()
                            selectedPackageId = coinPrice?.id!!.toInt()
                            selectedPlanName = coinPrice.plans!![0]!!.title!!
                            selectedPlanId = coinPrice.plans!![0]!!.id.toInt()
                            for (i in 0 until ivArrayDotsPager.size) {
                                ivArrayDotsPager.get(i)?.setImageResource(R.drawable.default_dot)
                            }
                            ivArrayDotsPager[index]!!.setImageResource(R.drawable.selected_dot)

                            adapterCoinPrice.updateItemList(response.data!!.allPackages!!.get(index)!!.plans,selectedPackageId.toString())
                            adapterPlanBenefits.updateItemList(response.data!!.allPackages!!.get(index)!!.permissions)

                        }
                    }
                )
                adapterPurchasePlanType.hidePackages(hidePackagesList)
                binding?.recyclerViewPlan?.adapter = adapterPurchasePlanType
                hideProgressView()
            }
        }
    }


    override fun setupClickListeners() {
        binding?.purchaseClose?.setOnClickListener {
            //activity?.onBackPressed()
            findNavController().popBackStack()
        }

        binding?.tvCompare?.setOnClickListener {
            openPlanDetailScreen()
        }
    }

    private fun openPlanDetailScreen() {
        val bundle = Bundle()
        bundle.putInt("selectedPlanID",selectedPlanId)
        bundle.putString("selectedPlanTitle",selectedPlanName)
        bundle.putInt("selectedPackageId",selectedPackageId)
        bundle.putString("selectedPackageName",selectedPackageName)
        bundle.putBoolean("isSilverHidden",isSilverHidden)
        findNavController().navigate(
            destinationId = R.id.action_global_plan_detail,
            popUpFragId = null,
            animType = AnimationTypes.SLIDE_ANIM,
            inclusive = true,
            args = bundle
        )
    }
}
