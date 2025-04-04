package com.i69.ui.screens.main.search

import android.Manifest.permission
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.i69.R
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.remote.requests.SearchRequest
import com.i69.databinding.DialogFilterBinding
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.viewModels.SearchViewModel
import com.i69.ui.views.ToggleImageView
import com.i69.utils.createLoadingDialog
import com.i69.utils.hasLocationPermission
import com.i69.utils.isCurrentLanguageFrench
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout

@AndroidEntryPoint
class FiltersDialogFragment(var userToken: String?, var userId: String?) : DialogFragment() {

    private val mViewModel: SearchViewModel by activityViewModels()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = FiltersDialogFragment::class.java.simpleName

    fun moveUp() = view?.findNavController()?.navigateUp()
    private lateinit var binding: DialogFilterBinding
    var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogFilterBinding.inflate(inflater, container, false)
        viewStringConstModel.data.observe(this@FiltersDialogFragment) { data ->
            binding.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding.stringConstant = it.value
//            Log.e("MydataBasesss", it.value!!.messages)
        }

        binding.btnSkip.setOnClickListener {
            dialog?.dismiss()
        }
        loadingDialog = requireActivity().createLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTheme()

        val items = mViewModel.genderPicker.items.get()
        items?.forEach {
            Log.e(TAG,"GenderPickerItem: $it")
        }
        val currentPosition = mViewModel.genderPicker.position.get()
        Log.e(TAG,"CurrentPosition: $currentPosition")
        val selectedItem = mViewModel.genderPicker.getSelectedItem()
        Log.e(TAG,"selectedItem: $selectedItem")
    }


    override fun onStart() {
        super.onStart()

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        dialog?.window?.setLayout(width - 50, height - 50)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }

    lateinit var loadingDialog: Dialog
    protected fun showProgressView() {
        if (loadingDialog != null && !loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    protected fun hideProgressView() {
        if (loadingDialog != null) {
            loadingDialog.dismiss()
        }
    }

    fun setupTheme() {
        navController = findNavController()
        // val interestedIn = arguments?.getSerializable("interested_in") as InterestedInGender
        binding.model = mViewModel
        val lookingFor = getString(R.string.looking_for)

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

        updateTags()
//        binding.personalLayoutItem.tagsBtn.setOnChipClickListener { tag, position ->
//            if (mViewModel.tags.size - 1 >= position) {
//                mViewModel.tags.removeAt(position)
//                updateTags()
//            }
//        }

        mViewModel.btnTagsAddListener = View.OnClickListener {
            navController!!.navigate(R.id.action_searchFiltersFragment_to_selectTagsFragment)
        }

        mViewModel.searchBtnClickListener = View.OnClickListener {
            if (!hasLocationPermission(requireContext(), locPermissions)) {
                (requireActivity() as MainActivity).permissionReqLauncher.launch(locPermissions)
            } else {
//                showProgressView()
//                binding.btnSkip.setViewGone()
//                binding.clearBtn.setViewGone()
                binding.progress.setViewVisible()
                binding.searchBtn.setViewGone()

                val locationService =
                    LocationServices.getFusedLocationProviderClient(requireActivity())

                locationService.lastLocation.addOnSuccessListener { location: Location? ->
                    val searchKey: String = ""
                    var lat: Double? = null
                    var lon: Double? = null
                    if (searchKey.isEmpty()) {
                        lat = location?.latitude
                        lon = location?.longitude
                    }
                    val searchRequest = SearchRequest(
                        //   interestedIn = interestedIn.id,
                        id = userId!!, searchKey = searchKey, lat = lat, long = lon)
                    Log.e(TAG,Gson().toJson(searchRequest))
                    Log.e(TAG,"ExtraSearchCalls: onGranted")
                    mViewModel.getSearchUsers(
                        _searchRequest = searchRequest,
                        token = userToken!!,
                        context = requireContext()
                    ) { error ->
                        if (error == null) {
                            if (view != null) {
                                lifecycleScope.launch {
                                    mViewModel.getDefaultPickers(userToken!!).observe(viewLifecycleOwner) {
                                            it?.let { defaultPicker ->
                                                mViewModel.updateDefaultPicker(lookingFor, defaultPicker, -1)
                                                val agePicker = defaultPicker.agePicker
                                                binding.ageRangeSeekBar.setRange(agePicker[0].value.toFloat(), agePicker[agePicker.size - 1].value.toFloat())
                                                mViewModel.updateFilteredData.value = true
                                                binding.btnSkip.setViewVisible()
                                                binding.clearBtn.setViewVisible()
                                                binding.progress.setViewGone()
                                                binding.searchBtn.setViewVisible()
                                                dismiss()
                                            }
                                        }
                                }
                            }
                        } else {
                            hideProgressView()
                            binding.root.snackbar(error)
                        }

                    }
                }
            }
        }
        initGroups()
    }

    private val locPermissions = arrayOf(
        permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION
    )

    private fun updateTags() {
        binding.tagsBtn.setInterests(mViewModel.tags.map { if (isCurrentLanguageFrench()) it.valueFr else it.value })
    }

    private fun initGroups() {
        // initExpandableLayout(binding.groupsExpand, binding.toggleGroupsExpand, binding.groups)
        //initExpandableLayout(binding.personalExpand, binding.togglePersonalExpand, binding.personal)
    }

    private fun initExpandableLayout(
        button: View, toggleImageView: ToggleImageView, expandableLayout: ExpandableLayout
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