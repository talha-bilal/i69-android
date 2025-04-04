package com.i69.ui.screens.main.search

import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import com.i69.R
import com.i69.applocalization.AppStringConstant
import com.i69.data.enums.InterestedInGender.*
import com.i69.ui.adapters.SearchInterestedAdapter
import com.i69.ui.base.search.BaseSearchBackFragment

@AndroidEntryPoint
class SearchGenderFragment : BaseSearchBackFragment() {

    override fun setScreenTitle() {
        binding?.title?.text = AppStringConstant(requireContext()).gender_label
    }

    override fun initObservers() {

    }

    override fun initDrawerStatus() {
        getMainActivity().disableNavigationDrawer()
    }

    override fun getItems(): List<SearchInterestedAdapter.MenuItem> = listOf(

        SearchInterestedAdapter.MenuItem(AppStringConstant(requireContext()).man, R.drawable.ic_man_unchecked),
        SearchInterestedAdapter.MenuItem(AppStringConstant(requireContext()).woman, R.drawable.ic_woman_unchecked),
        SearchInterestedAdapter.MenuItem(AppStringConstant(requireContext()).both, R.drawable.ic_both_unchecked)
    )

    override fun onAdapterItemClick(pos: Int) {
        val interestedIn = getInterestedIn(pos)
        navController?.navigate(R.id.action_searchGenderFragment_to_searchFiltersFragment,
            bundleOf("interested_in" to arguments?.getSerializable("interested_in"),
                "interested_in_gender" to interestedIn, "pos" to pos))
    }

    private fun getInterestedIn(pos: Int) = when (arguments?.getSerializable("interested_in") as com.i69.data.enums.InterestedInGender) {
        SERIOUS_RELATIONSHIP_ONLY_MALE -> when (pos) {
            0 -> SERIOUS_RELATIONSHIP_ONLY_MALE
            1 -> SERIOUS_RELATIONSHIP_ONLY_FEMALE
            else -> SERIOUS_RELATIONSHIP_BOTH
        }
        CAUSAL_DATING_ONLY_MALE -> when (pos) {
            0 -> CAUSAL_DATING_ONLY_MALE
            1 -> CAUSAL_DATING_ONLY_FEMALE
            else -> CAUSAL_DATING_BOTH
        }
        NEW_FRIENDS_ONLY_MALE -> when (pos) {
            0 -> NEW_FRIENDS_ONLY_MALE
            1 -> NEW_FRIENDS_ONLY_FEMALE
            else -> NEW_FRIENDS_BOTH
        }
        ROOM_MATES_ONLY_MALE -> when (pos) {
            0 -> ROOM_MATES_ONLY_MALE
            1 -> ROOM_MATES_ONLY_FEMALE
            else -> ROOM_MATES_BOTH
        }
        else -> when (pos) {
            0 -> BUSINESS_CONTACTS_ONLY_MALE
            1 -> BUSINESS_CONTACTS_ONLY_FEMALE
            else -> BUSINESS_CONTACTS_BOTH
        }
    }

}