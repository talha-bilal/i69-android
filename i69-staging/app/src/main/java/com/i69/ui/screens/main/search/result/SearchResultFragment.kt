package com.i69.ui.screens.main.search.result

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.i69.GetNotificationCountQuery
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.remote.requests.SearchRequest
import com.i69.databinding.FragmentSearchResultBinding
import com.i69.ui.adapters.PageResultAdapter
import com.i69.ui.base.BaseFragment
import com.i69.ui.screens.main.MainActivity
import com.i69.ui.screens.main.notification.NotificationDialogFragment
import com.i69.ui.viewModels.SearchViewModel
import com.i69.utils.apolloClient
import com.i69.utils.hideKeyboard
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>() {
    private var userToken: String? = null
    private var userId: String? = null
    private var hasSkip: Boolean = false
    private val mViewModel: SearchViewModel by activityViewModels()
    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    private var TAG: String = SearchResultFragment::class.java.simpleName

    override fun getStatusBarColor() = R.color.toolbar_search_color

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchResultBinding.inflate(inflater, container, false).apply {
            stringConstant = AppStringConstant(requireContext())
        }

    override fun initObservers() {

    }

    override fun setupTheme() {
        viewStringConstModel.data.observe(this@SearchResultFragment) { data ->
            binding?.stringConstant = data
        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
        }

        lifecycleScope.launch {
            userToken = getCurrentUserToken()!!
            userId = getCurrentUserId()!!
            if (requireArguments().containsKey("hasSkip")) {
                hasSkip = requireArguments().getBoolean("hasSkip", false)
            }

            Log.e(TAG, "hasSkipgetted : $hasSkip")
            Log.e(TAG, "usertokenn $userToken")

            callSearchRandomPeopleQuery()

            getNotificationIndex()
        }

        initSearch()

        mViewModel.getupdateSearchResultWithCoin().observe(viewLifecycleOwner) {
            callSearchPopularUserQuery()
            callSearchMostActiveUserQuery()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupViewPagerData() {
        val tabs = arrayOf(
            AppStringConstant1.random, AppStringConstant1.popular, AppStringConstant1.most_active
        )
        val pagerAdapter = PageResultAdapter(this, tabs)
        binding?.searchPageViewPager?.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 3
        }

        binding?.searchPageViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
        })

        val tabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, pos: Int ->
                tab.text = tabs[pos]
            }
        TabLayoutMediator(
            binding!!.slidingTabs, binding!!.searchPageViewPager, true, tabConfigurationStrategy
        ).attach()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun callSearchRandomPeopleQuery() {
        if (!isAdded) {
            return
        }
        binding?.shimmer?.apply {
            startShimmer()
            setViewVisible()
        }
        val interestedIn = requireArguments().getInt("interestedIn")
        val searchKey = requireArguments().getString("searchKey")

        Log.e(TAG, "SearchResult: InterestedIn: $interestedIn")
        Log.e(TAG, "searchKey : $searchKey")

        val searchRequest = SearchRequest(
            interestedIn = interestedIn,
            id = userId!!,
            searchKey = searchKey,
        )

        if (!isLocationEnabled()) {

        } else {
            val locationService = LocationServices.getFusedLocationProviderClient(requireActivity())

            locationService.lastLocation.addOnSuccessListener { location: Location? ->

                var lat: Double? = null
                var lon: Double? = null
                if (searchKey!!.isEmpty()) {
                    lat = location?.latitude
                    lon = location?.longitude
                }
                SearchRequest(
                    interestedIn = interestedIn,
                    id = userId!!,
                    searchKey = searchKey,
                    lat = lat,
                    long = lon
                )
            }
        }

        Log.e(TAG, "search params : ${Gson().toJson(searchRequest)}")
        Log.e(TAG, "callSearchRandomPeopleQuery: ")
        mViewModel.getSearchUsers(
            _searchRequest = searchRequest,
            token = userToken!!,
            context = requireContext(),
            hasSkip = hasSkip,
        ) { error ->
            try {
                requireActivity().runOnUiThread {
                    TransitionManager.beginDelayedTransition(binding?.clRoot, AutoTransition())
                    binding?.shimmer?.apply {
                        stopShimmer()
                        setViewGone()
                    }
                    setupViewPagerData()
                    if (error == null) {
                        mViewModel.setUpdateUserListQuery("")
                        Log.e(TAG, "" + mViewModel.getRandomUsers().toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callSearchPopularUserQuery() {
        val interestedIn = requireArguments().getInt("interestedIn")
        val searchKey = requireArguments().getString("searchKey")
        Log.e(TAG, "interestedIn : $interestedIn")
        Log.e(TAG, "searchKey : $searchKey")

        mViewModel.getSearchPopularUsers(
            token = userToken!!,
            interestedIn = interestedIn,
            context = requireContext(),
            hasSkip = hasSkip
        ) { error ->
            if (error == null) {
                mViewModel.setUpdateUserListQuery("")
                Log.e(TAG, "" + mViewModel.getPopularUsers().size)
            } else {
                Log.e(TAG, "" + error.toString())
            }
        }
    }

    private fun callSearchMostActiveUserQuery() {
        val interestedIn = requireArguments().getInt("interestedIn")
        val searchKey = requireArguments().getString("searchKey")
        Log.e(TAG, "interestedIn : $interestedIn")
        Log.e(TAG, "searchKey : $searchKey")

        mViewModel.getSearchMostActiveUsers(
            token = userToken!!,
            interestedIn = interestedIn,
            context = requireContext(),
            hasSkip = hasSkip
        ) { error ->
            if (error == null) {
                Log.e(TAG, "calllUpdateQuery")
                mViewModel.setUpdateUserListQuery("")
                Log.e(TAG, "" + mViewModel.getMostActiveUsers().size)
            } else {
                Log.e(TAG, "" + error.toString())
            }
        }
    }

    private fun initSearch() {
        binding?.interestsIcon?.setOnClickListener {
            if (binding?.keyInput?.text.toString().isNotEmpty()) {
                binding?.keyInput?.hideKeyboard()
                mViewModel.setSearchUserQuery(binding?.keyInput?.text.toString())
            }
        }

        binding?.keyInput?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding?.keyInput?.hideKeyboard()
                mViewModel.setSearchUserQuery(binding?.keyInput?.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        binding?.keyInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e(TAG, binding?.keyInput?.text.toString())
                if (binding?.keyInput?.text?.length != 0) {
                    Log.e(TAG, binding?.keyInput?.text.toString())
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun setupClickListeners() {

        binding?.bell?.setOnClickListener {
            val dialog = NotificationDialogFragment(
                userToken, binding?.counter, userId, binding?.bell
            )

            if (activity is MainActivity) (activity as MainActivity).notificationDialog(
                dialog,
                childFragmentManager,
                "${requireActivity().resources.getString(R.string.notifications)}"
            )
        }

        binding?.search?.setOnClickListener {
            binding?.searchChiledContainer?.visibility = View.VISIBLE
            binding?.search?.visibility = View.GONE
            binding?.cross?.visibility = View.VISIBLE
            binding?.title?.visibility = View.GONE
        }

        binding?.cross?.setOnClickListener {
            binding?.searchChiledContainer?.visibility = View.GONE
            binding?.search?.visibility = View.VISIBLE
            binding?.cross?.visibility = View.GONE
            binding?.title?.visibility = View.VISIBLE
            binding?.keyInput?.text?.clear()
            binding?.keyInput?.hideKeyboard()
            mViewModel.setSearchUserQuery("")
            callSearchRandomPeopleQuery()
            callSearchPopularUserQuery()
            callSearchMostActiveUserQuery()
        }
    }

    override fun onDetach() {
        super.onDetach()
        binding?.keyInput?.text?.clear()
        mViewModel.setSearchUserQuery("")
    }

    private fun getNotificationIndex() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val res = try {
                    apolloClient(requireContext(), userToken!!).query(GetNotificationCountQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e(TAG, "apolloResponse Exception NotificationIndex  ${e.message}")
                    binding?.root?.snackbar("${e.message}")
                    return@repeatOnLifecycle
                }
                Log.e(TAG, "apolloResponse NotificationIndex ${res.hasErrors()}")

                val NotificationCount = res.data?.unseenCount
                if (NotificationCount == null || NotificationCount == 0) {
                    binding?.counter?.visibility = View.GONE
                } else {
                    binding?.counter?.visibility = View.VISIBLE

                    if (NotificationCount > 10) {
                        binding?.counter?.text = "9+"
                    } else {
                        binding?.counter?.text = "" + NotificationCount
                    }
                }
            }
        }
    }
}