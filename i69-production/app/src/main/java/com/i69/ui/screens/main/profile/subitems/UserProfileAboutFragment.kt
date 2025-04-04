package com.i69.ui.screens.main.profile.subitems

import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.i69.applocalization.AppStringConstantViewModel
import com.i69.data.enums.InterestedInGender
import com.i69.data.models.IdWithValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.i69.data.models.User
import com.i69.data.remote.responses.DefaultPicker
import com.i69.databinding.FragmentUserProfileAboutBinding
import com.i69.ui.base.BaseFragment
import com.i69.ui.views.TagsCloudView
import com.i69.ui.views.chipcloud.FlowLayout
import com.i69.utils.EXTRA_USER_MODEL
import com.i69.utils.isCurrentLanguageFrench
import com.i69.utils.setViewGone
import com.i69.utils.setViewVisible
import com.i69.utils.setVisibility

@AndroidEntryPoint
class UserProfileAboutFragment : BaseFragment<FragmentUserProfileAboutBinding>() {

    private val viewStringConstModel: AppStringConstantViewModel by activityViewModels()
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserProfileAboutBinding.inflate(inflater, container, false)

    override fun initObservers() {

    }

    override fun setupTheme() {
        val user = Gson().fromJson(arguments?.getString(EXTRA_USER_MODEL), User::class.java) ?: return
        val defaultPickers = Gson().fromJson(arguments?.getString("default_picker"), DefaultPicker::class.java)

        viewStringConstModel.data.observe(this@UserProfileAboutFragment) { data ->

            binding?.stringConstant = data

        }
        viewStringConstModel.data.also {
            binding?.stringConstant = it.value
    //            Log.e("MydataBasesss", it.value!!.messages)
        }
        val config: Configuration = resources.configuration
        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            binding?.userGenderChipsCloud?.setGravity(FlowLayout.Gravity.RIGHT)
            binding?.userfamilyChipsCloud?.setGravity(FlowLayout.Gravity.RIGHT)
            binding?.userworkChipsCloud?.setGravity(FlowLayout.Gravity.RIGHT)
            binding?.userLookingForChipsCloud?.setGravity(FlowLayout.Gravity.RIGHT)
            binding?.userTags?.setGravity(FlowLayout.Gravity.RIGHT)
        }
        setupUserData(user, defaultPickers)
    }

    override fun setupClickListeners() {
    }

    private fun setupUserData(user: User?, defaultPickers: DefaultPicker?) {
        setupUserAboutData(user)
        setupUserTagsData(user, defaultPickers)
        setupInterestData(user)
        setupUserGenderData(user)
        setupUserLookingFor(user)

        val str = "   "+ user!!.work!!.uppercase()+"   "//String.format(it.i_am_gender, it.male.uppercase())

        //used to check if text is greater then 14 then change the max length to 30
        if (user.work!!.length > 14){
            binding?.userworkChipsCloud?.chipMaxCountLimit(30)
        }else{
            binding?.userworkChipsCloud?.chipMaxCountLimit(14)
        }
        binding?.userworkChipsCloud?.addChip(IdWithValue(user.work!!.length!!, str, str), false, false)
        binding?.userworkChipsCloud?.setSelectedChip(user.work!!.length)

        if(user.work != null  && user.work!!.isNotEmpty()){
            binding?.relUserWork?.visibility = View.VISIBLE
        }else{
            binding?.relUserWork?.visibility = View.GONE
        }

//        val strFamily = "   "+ defaultPickers!!.familyPicker.get(user.familyPlans!!).value.uppercase()+"   "//String.format(it.i_am_gender, it.male.uppercase())

        defaultPickers?.familyPicker?.forEachIndexed { _, idWithValue ->
            Log.e("TAG", "setupUserData: ${idWithValue.id}")
            if (idWithValue.id == user.familyPlans) {
//                value = if (isCurrentLanguageFrench()) idWithValue.valueFr else idWithValue.value
                binding?.userfamilyChipsCloud?.addChip(IdWithValue(idWithValue.id, idWithValue.value, idWithValue.valueFr), false, false)
                binding?.userfamilyChipsCloud?.setSelectedChip(idWithValue.id)
                return@forEachIndexed
            }
        }
        if(user.familyPlans != null  && user.familyPlans!! > 0){
            binding?.relUserFamilyPlan?.setViewVisible()
        }else{
            binding?.relUserFamilyPlan?.setViewGone()
        }
//        binding.userfamilyChipsCloud.addChip(IdWithValue(strFamily!!.length!!, strFamily, strFamily), false)
//        binding.userfamilyChipsCloud.setSelectedChip(strFamily!!.length)


    }

    private fun setupUserAboutData(user: User?) {
        binding?.userAbout?.text = user?.about

        val fullName = user?.fullName
        val name = if (fullName != null && fullName.length > 15) {
            fullName.substring(0, minOf(fullName.length, 15))
        } else {
            fullName
        }

        binding?.userNameAboutSection?.text = name
        binding?.userNameIntrestSection?.text = name

        //binding.userIntrest.text = user?.about
        //binding.userAboutLayout.setVisibility(!aboutLayoutVisibility)
    }


    private fun setupUserGenderData(user: User?) {
        Log.e("der",user?.gender.toString())
        viewStringConstModel.data.value?.let {
            when (user?.gender) {
                1 -> {
                    val str = "   "+ it.male.uppercase()+"   "//String.format(it.i_am_gender, it.male.uppercase())
//                    val str = String.format( it.male.uppercase())
                    binding?.userGenderChipsCloud?.addChip(IdWithValue(user.gender!!, str, str), false, false)
                }
                2 -> {
//                    val str = String.format(it.i_am_gender,it.female.uppercase())
                    val str = "   "+ it.female.uppercase()+"   "
                    binding?.userGenderChipsCloud?.addChip(IdWithValue(user.gender!!, str, str), false, false)
                }
                3 -> {
//                    val str = String.format(it.i_am_gender,it.female.uppercase())
                    val str = "   "+ it.prefer_not_to_say
                    binding?.userGenderChipsCloud?.addChip(IdWithValue(user.gender!!, str, str), false, false)
                }
                else -> {
                    val str = it.male.uppercase()+"   "
                    binding?.userGenderChipsCloud?.addChip(IdWithValue(user?.gender!!, str, str), false, false)
                }
            }
            binding?.userGenderChipsCloud?.setSelectedChip(user?.gender!!)
        }
    }

    private fun getLookingForNameFromId(id:Int) : String {
        when (id) {
            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
            }
            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.serious_relationship,viewStringConstModel.data.value!!.woman.removeSurrounding("\""))
            }
            InterestedInGender.SERIOUS_RELATIONSHIP_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.serious_relationship} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.serious_relationship,viewStringConstModel.data.value!!.both.replace("\"", ""))
            }
            InterestedInGender.CAUSAL_DATING_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.casual_dating,viewStringConstModel.data.value!!.man.replace("\"", ""))
            }
            InterestedInGender.CAUSAL_DATING_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.casual_dating,viewStringConstModel.data.value!!.woman.replace("\"", ""))
            }
            InterestedInGender.CAUSAL_DATING_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.casual_dating} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.casual_dating,viewStringConstModel.data.value!!.both.replace("\"", ""))
            }
            InterestedInGender.NEW_FRIENDS_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.new_friends,viewStringConstModel.data.value!!.man.replace("\"", ""))
            }
            InterestedInGender.NEW_FRIENDS_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.new_friends,viewStringConstModel.data.value!!.woman.replace("\"", ""))
            }
            InterestedInGender.NEW_FRIENDS_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.new_friends} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.new_friends,viewStringConstModel.data.value!!.both.replace("\"", ""))
            }
            InterestedInGender.ROOM_MATES_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.roommates,viewStringConstModel.data.value!!.man.replace("\"", ""))
            }
            InterestedInGender.ROOM_MATES_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.roommates,viewStringConstModel.data.value!!.woman.replace("\"", ""))
            }
            InterestedInGender.ROOM_MATES_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.roommates} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.roommates,viewStringConstModel.data.value!!.both.replace("\"", ""))
            }
            InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.man}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.business_contacts,viewStringConstModel.data.value!!.man.replace("\"", ""))
            }
            InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.woman}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.business_contacts,viewStringConstModel.data.value!!.woman.replace("\"", ""))
            }
            InterestedInGender.BUSINESS_CONTACTS_BOTH.id -> {
                return "${viewStringConstModel.data.value!!.business_contacts} ${viewStringConstModel.data.value!!.with} : ${viewStringConstModel.data.value!!.both}"
//                return String.format(viewStringConstModel.data.value!!.with,viewStringConstModel.data.value!!.business_contacts,viewStringConstModel.data.value!!.both.replace("\"", ""))
            }
        }
        return ""
    }

    private fun setupUserLookingFor(user: User?) {
        if (user?.interestedIn?.isNotEmpty() == true) {
            lifecycleScope.launch(Dispatchers.Main) {
                user.interestedIn.forEach {
                    val value = getLookingForNameFromId(it)
                    binding?.userLookingForChipsCloudLabel?.addChip(IdWithValue(it, value, value), false)
//                    binding.userLookingForChipsCloud.addChip(IdWithValue(it, value, value), false, true)
                }
                user.interestedIn.forEach {
                    binding?.userLookingForChipsCloud?.setSelectedChip(it)
                }
            }
        }
    }

    private fun setupUserTagsData(user: User?, defaultPicker: DefaultPicker?) {
        if (user?.tags?.isNotEmpty() == true) {
            val list = ArrayList<String>()

            lifecycleScope.launch(Dispatchers.Main) {
                user.tags.forEach {
                    var value = ""
                    defaultPicker?.tagsPicker?.forEachIndexed { _, idWithValue ->
                        if (idWithValue.id == it) {
                            value = if (isCurrentLanguageFrench()) idWithValue.valueFr else idWithValue.value
                            binding?.userTags?.addChip(IdWithValue(idWithValue.id, idWithValue.value, idWithValue.valueFr), true, false)
                            return@forEachIndexed
                        }
                    }
                    if (value.isNotEmpty()) list.add(value)
                }

                withContext(Dispatchers.Main) {
                    user.tags.forEach {
                        binding?.userTags?.setSelectedChip(it)
                    }
                }
            }
        } else {
            //binding.userTagsLayout.visibility = View.GONE
        }
    }

    private fun setupInterestData(user: User?) {
        setupTagsLayout(binding?.userMusic, binding?.userMusicLayout, (if (!user?.music.isNullOrEmpty()) user?.music else null))
        setupTagsLayout(binding?.userMovies, binding?.userMoviesLayout, (if (!user?.movies.isNullOrEmpty()) user?.movies else null))
        setupTagsLayout(binding?.userTvShows, binding?.userTvShowsLayout, (if (!user?.tvShows.isNullOrEmpty()) user?.tvShows else null))
        setupTagsLayout(binding?.userSportTeams, binding?.userSportTeamsLayout, (if (!user?.sportsTeams.isNullOrEmpty()) user?.sportsTeams else null))
    }

    private fun setupTagsLayout(cloud: TagsCloudView?, layout: View?, items: List<String>?) {
        val musicVisibility = !items.isNullOrEmpty()
        if (musicVisibility) cloud?.setTags(items!!)
        layout?.setVisibility(musicVisibility)
    }
}