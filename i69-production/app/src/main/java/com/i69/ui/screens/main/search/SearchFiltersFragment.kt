package com.i69.ui.screens.main.search

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.enums.InterestedInGender
import com.i69.data.remote.requests.SearchRequest
import com.i69.databinding.FragmentSearchFiltersBinding
import com.i69.permissions.PermissionHandler
import com.i69.permissions.Permissions
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.viewModels.SearchViewModel
import com.i69.ui.views.ToggleImageView
import com.i69.utils.hasLocationPermission
import com.i69.utils.isCurrentLanguageFrench
import com.i69.utils.snackbar
import java.util.*

@AndroidEntryPoint
class SearchFiltersFragment : BaseFragment<FragmentSearchFiltersBinding>() {

    private val mViewModel: SearchViewModel by activityViewModels()
    private var userId: String? = null
    private var userToken: String? = null
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = SearchFiltersFragment::class.java.simpleName

    override fun getFragmentBinding(inflater: LayoutInflater, container : ViewGroup?) =
        FragmentSearchFiltersBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    override fun setupTheme() {
        handleViewModel()
    }

    override fun onResume() {
        super.onResume()
        handleViewModel()
    }

    private fun handleViewModel() {
        viewStringConstModel.data.observe(this@SearchFiltersFragment) { data ->

            binding?.stringConstant = data

        }
        viewStringConstModel.data?.also {
            binding?.stringConstant = it.value
//            Log.e("MydataBasesss", it.value!!.messages)
        }
        navController = findNavController()
        val interestedIngender = arguments?.getSerializable("interested_in_gender") as InterestedInGender
        val interestedIn = arguments?.getSerializable("interested_in") as InterestedInGender
        var pos = arguments?.getInt("pos") ?: -1
        mViewModel.interestedIn.value = interestedIn
        mViewModel.interestedIngender.value = interestedIngender
        binding?.model = mViewModel
        val lookingFor = AppStringConstant1.looking_for
//        val lookingFor = getString(R.string.looking_for)
        Log.e(TAG, "setupTheme: ${pos}")
        lifecycleScope.launch {
            userId = getCurrentUserId()!!
            userToken = getCurrentUserToken()!!

            mViewModel.getDefaultPickers(userToken!!).observe(viewLifecycleOwner) {
                it?.let { defaultPicker ->

                    if (pos != 0 && pos != 1) {
                        pos = -1
                    }
                    else {
                        pos += 1
                    }
                    mViewModel.updateDefaultPicker(lookingFor, defaultPicker, pos)
                    val agePicker = defaultPicker.agePicker


                    Log.e("MYrangePickerDataValues", "${agePicker[0].value.toFloat()}")
                    Log.e(
                        "MYrangePickerDataValues1",
                        "${agePicker[agePicker.size - 1].value.toFloat()}"
                    )
                    lifecycleScope.launchWhenResumed {

                        updateTags()
                        binding?.ageRangeSeekBar?.setRange(
                            agePicker[0].value.toFloat(),
                            agePicker[agePicker.size - 1].value.toFloat()
                        )
                    }
                }
            }
        }
        updateTags()

        binding?.tagsBtn?.setOnChipClickListener { tag, position ->
            if (mViewModel.tags.size - 1 >= position) {
                mViewModel.tags.removeAt(position)
                updateTags()
            }
        }

//        binding.personalLayoutItem.tagsBtn.setOnChipClickListener { tag, position ->
//            if (mViewModel.tags.size - 1 >= position) {
//                mViewModel.tags.removeAt(position)
//                updateTags()
//            }
//        }

        mViewModel.btnTagsAddListener = View.OnClickListener {
            navController?.navigate(R.id.action_searchFiltersFragment_to_selectTagsFragment)
        }


        binding?.tvSkip?.setOnClickListener {
//            showProgressView()
            val searchKey: String = ""
            var bundle = Bundle()
            bundle.putInt("interestedIn", interestedIn.id)
            bundle.putString("searchKey", searchKey)
            bundle.putBoolean("hasSkip", true)
            navController?.navigate(
                R.id.action_searchFiltersFragment_to_searchResultFragment,
                args = bundle
            )
            val searchRequest = SearchRequest(
                interestedIn = interestedIn.id,
                id = userId!!,
                searchKey = searchKey

            )

//            mViewModel.getSearchUsers(
//                _searchRequest = searchRequest,
//                token = userToken!!,
//                context = requireContext(),
//                hasSkip = true,
//            ) { error ->
//                if (error == null) {
//                    hideProgressView()
////                    var bundle = Bundle()
////                    bundle.putInt("interestedIn", interestedIn.id)
////                    bundle.putString("searchKey", searchKey)
//
//                    navController.navigate(
//                        R.id.action_searchFiltersFragment_to_searchResultFragment,
//                        args = bundle
//                    )
//                } else {
//                    hideProgressView()
//                    binding.root.snackbar(error)
//                }
//            }
        }

        mViewModel.searchBtnClickListener = View.OnClickListener {
            if (isLocationEnabled()) {
                val searchKey = ""
                val searchRequest = SearchRequest(
                    interestedIn = interestedIn.id,
                    id = userId!!,
                    searchKey = searchKey,
                )

                var bundle = Bundle()
                bundle.putInt("interestedIn", interestedIn.id)
                bundle.putString("searchKey", searchKey)

                navController?.navigate(
                    R.id.action_searchFiltersFragment_to_searchResultFragment,
                    args = bundle
                )
            }else {
                if (!hasLocationPermission(requireContext(), locPermissions)) {
                    (requireActivity() as MainActivity).permissionReqLauncher.launch(locPermissions)
                }
                else {
                    enableLocation()
                }
            }
        }
//        initGroups()
    }

    private fun enableLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30 * 1000.toLong()
            fastestInterval = 5 * 1000.toLong()
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                println("location>>>>>>> ${response.locationSettingsStates?.isGpsPresent}")
                if (response.locationSettingsStates?.isGpsPresent == true) {

                }
                //do something
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            e.status.resolution?.let { it1 ->
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

            } else {

                binding?.root?.snackbar(
                    AppStringConstant1.location_enable_message,
//                    getString(R.string.location_enable_message),
                    Snackbar.LENGTH_INDEFINITE,
                    callback = {
                        enableLocation()
                    })
//            requireContext().toast("Please Accept Location enable for use this App.")
            }
        }

    private val locPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun setupClickListeners() {}

    private fun updateTags() {
        Log.e("TagaddedinList","${mViewModel.tags.size}" )
        binding?.tagsBtn?.setInterests(mViewModel.tags.map { if (isCurrentLanguageFrench()) it.valueFr else it.value })
    }
//    private fun updateTags() {
//        binding.personalLayoutItem.tagsBtn.setInterests(mViewModel.tags.map { if (isCurrentLanguageFrench()) it.valueFr else it.value })
//    }

//    private fun initGroups() {
//        initExpandableLayout(binding.groupsExpand, binding.toggleGroupsExpand, binding.groups)
//        initExpandableLayout(binding.personalExpand, binding.togglePersonalExpand, binding.personal)
//    }

    private fun initExpandableLayout(
        button: View,
        toggleImageView: ToggleImageView,
        expandableLayout: ExpandableLayout
    ) {
        toggleImageView.onCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    expandableLayout.expand(false)
                } else {
                    expandableLayout.collapse()
                }
            }

        button.setOnClickListener {
            toggleImageView.toggle()
        }
    }

}