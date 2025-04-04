package com.i69.ui.screens.main.profile.subitems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.models.User
import com.i69.databinding.FragmentUserProfileInterestsBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.views.TagsCloudView
import com.i69.utils.EXTRA_USER_MODEL
import com.i69.utils.setVisibility

class UserProfileInterestsFragment : BaseFragment<FragmentUserProfileInterestsBinding>() {

    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUserProfileInterestsBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        val user = Gson().fromJson(arguments?.getString(EXTRA_USER_MODEL), User::class.java) ?: return
        viewStringConstModel.data.observe(this@UserProfileInterestsFragment) { data ->

            binding?.stringConstant = data

        }

        viewStringConstModel.data?.also {
            binding?.stringConstant = it.value
//            Log.e("MydataBasesss", it.value!!.messages)
        }
        setupUserData(user)
    }

    override fun setupClickListeners() {
    }

    private fun setupUserData(user: User) {
        setupTagsLayout(binding?.userMusic, binding?.userMusicLayout, (if (!user.music.isNullOrEmpty()) user.music else null))
        setupTagsLayout(binding?.userMovies, binding?.userMoviesLayout, (if (!user.movies.isNullOrEmpty()) user.movies else null))
        setupTagsLayout(binding?.userTvShows, binding?.userTvShowsLayout, (if (!user.tvShows.isNullOrEmpty()) user.tvShows else null))
        setupTagsLayout(binding?.userSportTeams, binding?.userSportTeamsLayout, (if (!user.sportsTeams.isNullOrEmpty()) user.sportsTeams else null))
    }

    private fun setupTagsLayout(cloud: TagsCloudView?, layout: View?, items: List<String>?) {
        val musicVisibility = !items.isNullOrEmpty()
        if (musicVisibility) cloud?.setTags(items!!)
        layout?.setVisibility(musicVisibility)
    }

}