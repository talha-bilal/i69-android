package com.i69.profile.vm

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.i69.GetUserQuery
import com.i69.applocalization.AppStringConstant
import com.i69.applocalization.AppStringConstant1
import com.i69.base.BaseViewModel
import com.i69.data.enums.HttpStatusCode
import com.i69.data.models.User
import com.i69.data.remote.repository.AppRepository
import com.i69.data.remote.repository.UserDetailsRepository
import com.i69.data.remote.responses.DefaultPicker
import com.i69.profile.db.dao.UserDao
import com.i69.ui.adapters.UserItemsAdapter
import com.i69.ui.screens.main.profile.subitems.FeedsFragment
import com.i69.ui.screens.main.profile.subitems.MomentsFragment
import com.i69.ui.screens.main.profile.subitems.UserProfileAboutFragment
import com.i69.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMProfile @Inject constructor(
    private val appRepository: AppRepository,
    private val userDetailsRepository: UserDetailsRepository,
    var viewStringConstModel: AppStringConstant
) : BaseViewModel() {
    val TAG = this.javaClass.canonicalName
    private var _data: MutableLiveData<DataCombined> = MutableLiveData()
    val data: LiveData<DataCombined?>
        get() {
            return _data
        }

    @Inject
    lateinit var userDao: UserDao

    var isMyUser = false
    var isBackEnabled = MutableLiveData(false)
    var isDrawerEnabled = MutableLiveData(false)
    var isEditEnabled = MutableLiveData(false)
    var isSearchUserEditEnabled = MutableLiveData(false)
    var isReportEnabled = MutableLiveData(false)
    var isMessageEnabled = MutableLiveData(false)
    var isCoinsEnabled = MutableLiveData(false)
    var isEarnCoinsEnabled = MutableLiveData(false)
    var isGiftIconEnabled = MutableLiveData(false)
    var isLikesIconEnabled = MutableLiveData(false)

    var onSendMsg: (() -> Unit)? = null
    var onDrawer: (() -> Unit)? = null
    var onEditProfile: (() -> Unit)? = null
    var onGift: (() -> Unit)? = null
    var onReport: (() -> Unit)? = null
    var onCoins: ((coins: String) -> Unit)? = null
    var onBackPressed: (() -> Unit)? = null

    private var followerUserList: MutableLiveData<MutableList<GetUserQuery.FollowerUser?>> =
        MutableLiveData()
    private var followingUserList: MutableLiveData<MutableList<GetUserQuery.FollowingUser?>> =
        MutableLiveData()

    private var visitorUserList: MutableLiveData<MutableList<GetUserQuery.UserVisitor?>> = MutableLiveData()
    private var visitingUserList: MutableLiveData<MutableList<GetUserQuery.UserVisiting?>> =
        MutableLiveData()

    fun updateFollowerListResultWith(list: MutableList<GetUserQuery.FollowerUser?>) {
        followerUserList.postValue(list)
    }

    fun getupdateFollowerListResultWith(): MutableLiveData<MutableList<GetUserQuery.FollowerUser?>> {
        return followerUserList
    }

    fun setupdateFollowingListResultWith(list: MutableList<GetUserQuery.FollowingUser?>) {
        followingUserList.postValue(list)
    }

    fun getupdateFollowingListResultWith(): MutableLiveData<MutableList<GetUserQuery.FollowingUser?>> {
        return followingUserList
    }


    fun setupdateVisitorListResultWith(list: MutableList<GetUserQuery.UserVisitor?>) {
        visitorUserList.postValue(list)
    }

    fun getupdateVisitorListResultWith(): MutableLiveData<MutableList<GetUserQuery.UserVisitor?>> {
        return visitorUserList
    }

    fun setupdateVisitingListResultWith(list: MutableList<GetUserQuery.UserVisiting?>) {
        visitingUserList.postValue(list)
    }

    fun getupdateVisitingListResultWith(): MutableLiveData<MutableList<GetUserQuery.UserVisiting?>> {
        return visitingUserList
    }

    suspend fun addUserProfileVisit(userId: String, visitorId: String, token: String) =
        userDetailsRepository.addUserProfileVisit(userId, visitorId, token)

    fun onSendMsgClick() {
        onSendMsg?.invoke()
    }

    fun onBackPressed() {
        onBackPressed?.invoke()
    }

    fun onDrawerClick() {
        onDrawer?.invoke()
    }

    fun onEditProfile() {
        onEditProfile?.invoke()
    }

    fun onGiftClick() {
        onGift?.invoke()
    }

    fun onReport() {
        onReport?.invoke()
    }

    fun onCoinsClick() {


        onCoins?.invoke("${data.value?.user?.purchaseCoins}")
    }

    fun getProfile(userid: String? = "", onDataLoaded: () -> Unit) {
        _data.value?.user = null
        Log.e(TAG, "UserId: $userid")
        viewModelScope.launch(Dispatchers.Default) {
            _data.postValue(DataCombined(userDao.getUser(userid), userDao.getPicker()))
            val resOne = async {
                userDetailsRepository.getUserProfile(
                    if (userid?.isEmpty() == true) userId.first() else userid,
                    token.first()
                )
            }
            val resTwo = async { appRepository.loadPickers(token.first()) }
            if (resOne.await().status == Status.SUCCESS && resTwo.await().code == HttpStatusCode.OK) {
                resOne.await().data.let {
                    it?.id = userid ?: ""
                    Log.e(TAG, "UPFrag" + "getProfile: $it")
                    resTwo.await().data?.data.let { it2 ->
                        userDao.insertPicker(it2!!)
                        Log.e(TAG, "UPFrag" + "getProfile: $it2")
                        it?.ageValue =
                            "${it2.agePicker.getSelectedValueFromDefaultPicker(it?.age)} ${
                                AppStringConstant1.years
                            }"
                        it?.heightValue =
                            "${it2.heightsPicker.getSelectedValueFromDefaultPicker(it?.height)} ${
                                AppStringConstant1.cm
                            }"

                        if (it?.country.isNullOrEmpty() || it?.country.isNullOrBlank()) {
                            it?.country = ""
                        }

                        if (it?.state.isNullOrEmpty() || it?.state.isNullOrBlank()) {
                            it?.state = ""
                        }

                        if (it?.city.isNullOrEmpty() || it?.city.isNullOrBlank()) {
                            it?.city = ""
                        }

                        if (it?.countryCode.isNullOrEmpty() || it?.countryCode.isNullOrBlank()) {
                            it?.countryCode = ""
                        }
                        Log.e("insertedUserData", Gson().toJson(it))
                        userDao.insertUser(it!!)
                        _data.postValue(DataCombined(it, it2))
                        onDataLoaded.invoke()
                    }
                }
            } else {
                onDataLoaded.invoke()
                _data.postValue(null)
            }
        }
        setMyUI()
    }

    private fun setMyUI() {
        isBackEnabled.value = !isMyUser
        isDrawerEnabled.value = isMyUser
        isEditEnabled.value = isMyUser
        isSearchUserEditEnabled.value = !isMyUser
        isReportEnabled.value = !isMyUser
        isMessageEnabled.value = !isMyUser
        isCoinsEnabled.value = isMyUser
        isEarnCoinsEnabled.value = isMyUser
        isGiftIconEnabled.value = !isMyUser
        isLikesIconEnabled.value = !isMyUser

    }

    private var feed: FeedsFragment? = null
    private var moment: MomentsFragment? = null

    fun setupViewPager(
        childFragmentManager: FragmentManager,
        user: User?,
        defaultPicker: DefaultPicker?,
        mContext: Context,
        isUserHasMoments: Boolean
    ): UserItemsAdapter {

        val adapter = UserItemsAdapter(childFragmentManager)
        val about = UserProfileAboutFragment()
        //val interests = UserProfileInterestsFragment()
        feed = FeedsFragment()
        moment = MomentsFragment()
        moment?.setOnAllMomentsDeleted {
            if (it) updateViewPagerTabs()
        }
        val useriddata = Bundle()
        if (user != null) useriddata.putString(EXTRA_USER_MODEL, Gson().toJson(user))
        moment?.arguments = useriddata
        val userDataArgs = Bundle()
        if (user != null) userDataArgs.putString(EXTRA_USER_MODEL, Gson().toJson(user))
        userDataArgs.putString("default_picker", Gson().toJson(defaultPicker))
        about.arguments = userDataArgs
        //interests.arguments = userDataArgs

        Log.e("MydataBasesss", viewStringConstModel.messages)

        adapter.addFragItem(
            about,
            AppStringConstant(mContext).about
        )

        Log.e("callTranslation11111", "callTranslation==>" + viewStringConstModel.feed)
        if (feed != null) {
            adapter.addFragItem(
                feed!!,
                viewStringConstModel.feed!!
            )
        }

        if (isUserHasMoments && moment != null) {
            adapter.addFragItem(
                moment!!,
                viewStringConstModel.moment
            )
        }
        return adapter
    }

    fun pauseVideo() {
        feed?.pauseVideoOnSwipe()
        moment?.pauseVideoOnSwipe()
    }

    val removeMomentFromUserFeed = MutableLiveData(false)
    private fun updateViewPagerTabs() {
        removeMomentFromUserFeed.postValue(true)
    }

    @Keep
    data class DataCombined(var user: User?, var defaultPicker: DefaultPicker?)
}