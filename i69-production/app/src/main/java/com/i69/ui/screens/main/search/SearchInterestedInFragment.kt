package com.i69.ui.screens.main.search

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.i69.HideInterestedInSubscription
import com.i69.R
import com.i69.UserInterestsQuery
import com.i69.applocalization.AppStringConstant1
import com.i69.data.enums.InterestedInGender
import com.i69.type.UserInterestedInCategoryName
import com.i69.ui.adapters.SearchInterestedServerAdapter
import com.i69.ui.base.search.BaseSearchFragment
import com.i69.ui.interfaces.CallInterestedIn
import com.i69.ui.screens.SplashActivity
import com.i69.ui.screens.main.MainActivity
import com.i69.utils.apolloClient
import com.i69.utils.apolloClientSubscription
import com.i69.utils.hasLocationPermission
import com.i69.utils.snackbar
import com.i69.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchInterestedInFragment : BaseSearchFragment(), CallInterestedIn {

    companion object {
        private var TAG: String = SearchInterestedInFragment::class.java.simpleName
        fun setShowAnim(show: Boolean) {
            showAnim = show
        }
    }

    private var userId: String? = null
    private var userToken: String? = null

    private val locPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun setupChiledTheme() {
        getTypeActivity<MainActivity>()?.reloadNavigationMenu()
        showProgressView()
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!

            adapter.setItems(viewModel.listItemsFromViewModel)
            adapter.notifyDataSetChanged()
            hideProgressView()
            callInterestedInAPi()
            updateLocation()
            subscribeForUserUpdate()
        }
    }

    override fun initObservers() {
    }

    override fun setScreenTitle() {
        binding?.title?.text = AppStringConstant1.interested_in

        binding?.title?.setOnClickListener {
            getMainActivity().updateLanguageChanged()
            lifecycleScope.launch {
                delay(5000)
                activity?.recreate()
            }
        }
    }

    override fun callInterestedInApi() {
        callInterestedInAPi()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toast("onConfigurationChanged")
    }

    override fun initDrawerStatus() {
        try {
            getMainActivity().enableNavigationDrawer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun subscribeForUserUpdate() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                apolloClientSubscription(requireActivity(), getCurrentUserToken()!!).subscription(
                    HideInterestedInSubscription()
                ).toFlow().catch {
                    it.printStackTrace()
                    Log.e(TAG, "reealltime exception= ${it.message}")
                    binding?.root?.snackbar("Exception= ${it.message}")
                }.retryWhen { cause, attempt ->
                    Log.e(TAG, "reealltime retry $attempt ${cause.message}")
                    delay(attempt * 1000)
                    true
                }.collect { newStory ->
                    if (newStory.hasErrors()) {
                        Log.e(
                            TAG,
                            "reealltime response error = ${newStory.errors?.get(0)?.message}"
                        )
                    } else {
                        Log.e(
                            TAG,
                            "followUserSubscript:story realtime DeleteStory ${newStory.data}"
                        )
                        callInterestedInAPi()
                    }
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                Log.e(TAG, "story realtime exception= ${e2.message}")
            }
        }
    }

    private fun callInterestedInAPi() {
        lifecycleScope.launch {
            val userToken = getCurrentUserToken()!!
            val response = try {
                apolloClient(requireContext(), userToken).query(
                    UserInterestsQuery()
                ).execute()

            } catch (e: ApolloException) {
                Log.e(TAG, "apolloResponse ${e.message}")
                hideProgressView()
                return@launch
            }

            if (response.hasErrors()) {
                hideProgressView()
                val errorMessage = response.errors?.get(0)?.message
                Log.e(TAG, "errorInInterest : $errorMessage")

                if (errorMessage != null) {
                    try {
                        binding?.root?.snackbar(errorMessage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                val listItems = mutableListOf<SearchInterestedServerAdapter.MenuItemString>()
                response.data!!.userInterests!!.indices.forEach { i ->
                    if (response.data!!.userInterests!![i]!!.categoryName == UserInterestedInCategoryName.SERIOUS_RELATIONSHIP) {
                        listItems.add(
                            SearchInterestedServerAdapter.MenuItemString(
                                response.data!!.userInterests!![i]!!.strName.toString(),
                                R.drawable.ic_serious_relation
                            )
                        )
                    } else if (response.data!!.userInterests!![i]!!.categoryName == UserInterestedInCategoryName.CAUSAL_DATING) {
                        listItems.add(
                            SearchInterestedServerAdapter.MenuItemString(
                                response.data!!.userInterests!![i]!!.strName.toString(),
                                R.drawable.ic_casual_dating
                            )
                        )
                    } else if (response.data!!.userInterests!![i]!!.categoryName == UserInterestedInCategoryName.NEW_FRIENDS) {
                        listItems.add(
                            SearchInterestedServerAdapter.MenuItemString(
                                response.data!!.userInterests!![i]!!.strName.toString(),
                                R.drawable.ic_new_friend
                            )
                        )
                    } else if (response.data!!.userInterests!![i]!!.categoryName == UserInterestedInCategoryName.ROOM_MATES) {
                        listItems.add(
                            SearchInterestedServerAdapter.MenuItemString(
                                response.data!!.userInterests!![i]!!.strName.toString(),
                                R.drawable.ic_room_mates
                            )
                        )
                    } else if (response.data!!.userInterests!!.get(i)!!.categoryName == UserInterestedInCategoryName.BUSINESS_CONTACTS) {
                        listItems.add(
                            SearchInterestedServerAdapter.MenuItemString(
                                response.data!!.userInterests!![i]!!.strName.toString(),
                                R.drawable.ic_business_contact
                            )
                        )
                    }
                }

                viewModel.listItemsFromViewModel = listItems
                adapter.setItems(listItems)
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun updateLocation() {
        if (hasLocationPermission(requireContext(), locPermissions)) {
            showProgressView()
            if (isLocationEnabled()) {
                shareLocation()
            } else {
                enableLocation()
            }
        } else {
            (requireActivity() as MainActivity).permissionReqLauncher.launch(locPermissions)
        }
    }

    private fun shareLocation() {
        val locationService = LocationServices.getFusedLocationProviderClient(requireContext())
        locationService.lastLocation.addOnSuccessListener { location: Location? ->
            if (isAdded && !requireActivity().isFinishing) {
                requireActivity().runOnUiThread {
                    try {
                        hideProgressView()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
            val lat: Double? = location?.latitude
            val lon: Double? = location?.longitude
            if (lat != null && lon != null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    userToken = getCurrentUserToken()!!
                    try {
                        val res = mViewModel.updateLocation(
                            userId = userId!!, location = arrayOf(lat, lon), token = userToken!!
                        )

                        if (res.message.equals("User doesn't exist")) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                userPreferences?.clear()
                                val intent = Intent(requireContext(), SplashActivity::class.java)
                                startActivity(intent)
                                requireActivity().finishAffinity()
                            }
                        } else {
                            hideProgressView()
                        }
                        Log.e(TAG, "ResponseMessage: ${res.message}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating location: ${e.message}")
                    }
                }
            } else {
                shareLocation()
            }
        }.addOnFailureListener {
            shareLocation()
            Log.e(TAG, "Location fetch failed: ${it.message}")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun enableLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30 * 1000.toLong()
            fastestInterval = 5 * 1000.toLong()
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                println("location>>>>>>> ${response.locationSettingsStates?.isGpsPresent}")
                if (response.locationSettingsStates?.isGpsPresent == true) {
                    showProgressView()
                    shareLocation()
                }
                //do something
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest = e.status.resolution?.let { it1 ->
                            IntentSenderRequest.Builder(it1).build()
                        }
                        launcher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
        }.addOnCanceledListener {

        }
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showProgressView()
                shareLocation()
            } else {
                binding?.root?.snackbar(AppStringConstant1.location_enable_message,
                    Snackbar.LENGTH_INDEFINITE, callback = {
                        enableLocation()
                    })
            }
        }

    override fun getItems(): List<SearchInterestedServerAdapter.MenuItemString> = listOf(
    )

    override fun onAdapterItemClick(pos: Int) {
        val item = when (pos) {
            0 -> InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE
            1 -> InterestedInGender.CAUSAL_DATING_ONLY_MALE
            2 -> InterestedInGender.NEW_FRIENDS_ONLY_MALE
            3 -> InterestedInGender.ROOM_MATES_ONLY_MALE
            4 -> InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE
            else -> InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE
        }
        Log.e(TAG, "InterestedIn: ${item.id}")
        navController?.navigate(
            R.id.action_searchInterestedInFragment_to_searchGenderFragment,
            bundleOf("interested_in" to item)
        )
    }


    override fun onResume() {
        super.onResume()
        getMainActivity().setDrawerItemCheckedUnchecked(R.id.nav_search_graph)
    }
}
