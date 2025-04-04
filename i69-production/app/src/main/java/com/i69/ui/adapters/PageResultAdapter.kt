package com.i69.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.i69.ui.screens.main.search.result.PageSearchResultFragment

class PageResultAdapter(fragment: Fragment, private val tabTitles: Array<String>) :
    FragmentStateAdapter(fragment) {

    private val listFrags = ArrayList<PageSearchResultFragment>()

    override fun createFragment(position: Int): Fragment {
        val frag = PageSearchResultFragment.newInstance(position)
        listFrags.add(frag)
        return frag
    }

    override fun getItemCount(): Int = tabTitles.size

}