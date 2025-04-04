package com.i69.ui.screens.main.search

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.i69.applocalization.AppStringConstant
import com.i69.data.models.IdWithValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.i69.ui.base.profile.BaseSelectedTagsFragment
import com.i69.ui.viewModels.SearchViewModel

@AndroidEntryPoint
class SearchSelectTagsFragment : BaseSelectedTagsFragment() {

    private val viewModel: SearchViewModel by activityViewModels()

    override fun loadTags() {
        lifecycleScope.launch {
            binding?.title?.text= AppStringConstant(requireContext()).tags

            val userToken = getCurrentUserToken()!!
            viewModel.getDefaultPickers(userToken).observe(viewLifecycleOwner, { defaultPicker ->
                defaultPicker?.let {
                    hideProgressView()
                    binding?.chips?.addChips(it.tagsPicker)
                    selectedTags = viewModel.tags

                    restoreTags(viewModel.tags.map { tag -> tag.id })
                }
            })
        }
    }

    override fun onNextClick() {
        if (isTagsValid()) {

            lifecycleScope.launch {
                var localTagsadded: ArrayList<IdWithValue> = arrayListOf()
             localTagsadded .addAll(selectedTags)
//                viewModel.tags.clear()
//                viewModel.tags.addAll(selectedTags)
//                Log.e("selecteddataadasd", "${selectedTags.size}")
                Log.e("selecteddataadasd1", "${viewModel.tags.size}")
                Log.e("selecteddataadasd1", "${localTagsadded.size}")
                viewModel.updateTags(localTagsadded)
            }

//            viewModel.updateTags(selectedTags)
            moveUp()
        }
    }

    override fun initObservers() {

    }
}