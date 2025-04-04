package com.i69.ui.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.i69.data.enums.InterestedInGender
import com.i69.data.models.IdWithValue
import com.i69.data.models.MyPermission
import com.i69.data.models.User
import com.i69.data.remote.repository.AppRepository
import com.i69.data.remote.repository.SearchRepository
import com.i69.data.remote.requests.SearchRequest
import com.i69.data.remote.requests.SearchRequestNew
import com.i69.data.remote.responses.DefaultPicker
import com.i69.utils.isCurrentLanguageFrench
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

private const val MAX_DIST_DEFAULT = 2500f

private const val AGE_RANGE_LEFT_DEFAULT = 18f
private const val AGE_RANGE_RIGHT_DEFAULT = 60f
private const val HEIGHT_RANGE_LEFT_DEFAULT = 55f
private const val HEIGHT_RANGE_RIGHT_DEFAULT = 97f

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val appRepository: AppRepository, private val searchRepository: SearchRepository
) : ViewModel() {

    var searchFromParent: MutableLiveData<String> = MutableLiveData()
    var searchUpdateFromParent: MutableLiveData<String> = MutableLiveData()
    var updateSearchResultWithCoin: MutableLiveData<String> = MutableLiveData()
    var interestedIn: MutableLiveData<InterestedInGender> = MutableLiveData()
    var interestedIngender: MutableLiveData<InterestedInGender> = MutableLiveData()

    var updateFilteredData: MutableLiveData<Boolean> = MutableLiveData()
    private var TAG: String = SearchViewModel::class.java.simpleName

    private var _randomUserPermission: MyPermission = MyPermission(false)
    private var _popularUserPermission: MyPermission = MyPermission(false)
    private var _mostActiveUserPermission: MyPermission = MyPermission(false)
    private val _randomUsers: ArrayList<User> = ArrayList()
    private val _randomUsersSearched: ArrayList<User> = ArrayList()
    private val _popularUsers: ArrayList<User> = ArrayList()
    private val _mostActiveUsers: ArrayList<User> = ArrayList()
    var selectedUser: MutableLiveData<User> = MutableLiveData()
    private lateinit var _searchRequest: SearchRequest

    val maxDistanceValue = ObservableFloat(MAX_DIST_DEFAULT)
    val ageRangeLeft = ObservableFloat(AGE_RANGE_LEFT_DEFAULT)
    val ageRangeRight = ObservableFloat(AGE_RANGE_RIGHT_DEFAULT)
    val heightRangeLeft = ObservableFloat(HEIGHT_RANGE_LEFT_DEFAULT)
    val heightRangeRight = ObservableFloat(HEIGHT_RANGE_RIGHT_DEFAULT)
    val tags: ArrayList<IdWithValue> = ArrayList()
    val genderPicker = SpinnerOptions()
    val familyPlans = SpinnerOptions()
    val politics = SpinnerOptions()
    val religious = SpinnerOptions()
    val zodiac = SpinnerOptions()

    var btnTagsAddListener = View.OnClickListener {}
    var searchBtnClickListener = View.OnClickListener {}

    val clearBtnClickListener = View.OnClickListener {
        clearItems()
    }

    fun updateTags(updated: List<IdWithValue>) {
        Log.e(TAG,"updateTags: updatedList: ${updated.size}")
        tags.clear()
        Log.e(TAG,"updateTags: updatedList: ${updated.size}")
        tags.addAll(updated)
        Log.e(TAG,"updateTags: newList: ${tags.size}")
    }

    private fun clearItems() {
        maxDistanceValue.set(MAX_DIST_DEFAULT)
        ageRangeLeft.set(AGE_RANGE_LEFT_DEFAULT)
        ageRangeRight.set(AGE_RANGE_RIGHT_DEFAULT)
        heightRangeLeft.set(HEIGHT_RANGE_LEFT_DEFAULT)
        heightRangeRight.set(HEIGHT_RANGE_RIGHT_DEFAULT)
        tags.clear()
        genderPicker.clear()
        familyPlans.clear()
        politics.clear()
        religious.clear()
        zodiac.clear()
    }

    fun updateSearchResultWithCoin() {
        updateSearchResultWithCoin.value = ""
    }

    fun getupdateSearchResultWithCoin(): LiveData<String?> {
        return updateSearchResultWithCoin
    }

    fun setUpdateUserListQuery(queryData: String) {
        searchUpdateFromParent.value = queryData
    }

    fun getUpdateUserListQuery(): LiveData<String?> {
        return searchUpdateFromParent
    }

    fun setSearchUserQuery(queryData: String) {
        searchFromParent.value = queryData
    }

    fun getSearchUserQuery(): LiveData<String?> {
        return searchFromParent
    }

    fun setFilteredSearchUserQuery(queryData: String) {
        searchFromParent.value = queryData
    }

    fun getFilteredSearchUserQuery(): LiveData<String?> {
        return searchFromParent
    }


    /// Default Pickers
    fun getDefaultPickers(token: String): LiveData<DefaultPicker> =
        appRepository.getDefaultPickers(viewModelScope, token)


    /// most active users
    fun getSearchMostActiveUsers(
        token: String,
        autoDeductCoin: Int = 0,
        interestedIn: Int,
        context: Context,
        hasSkip: Boolean = false,
        callback: (String?) -> Unit
    ) {

//        if(genderPicker!=null && genderPicker.getSelectedItem()!=null ) {
//            interestedIngender.value = getInterestedIn(this.genderPicker!!.getSelectedItem()!!.id)
//        }
        var searchRequest = SearchRequest(
            interestedIn = _searchRequest.interestedIn,
            id = _searchRequest.id,
            searchKey = _searchRequest.searchKey,
            minAge = ageRangeLeft.get().roundToInt(),
            maxAge = ageRangeRight.get().roundToInt(),
            minHeight = (heightRangeLeft.get().roundToInt() * 2.54).roundToInt(),
            maxHeight = (heightRangeRight.get().roundToInt() * 2.54).roundToInt(),
            lat = _searchRequest.lat,
            long = _searchRequest.long,
            tags = tags.map { it.id },
            gender = this.genderPicker.getSelectedItem()?.id,
            familyPlans = this.familyPlans.getSelectedItem()?.id,
            politics = this.politics.getSelectedItem()?.id,
            religious = this.religious.getSelectedItem()?.id,
            zodiacSign = this.zodiac.getSelectedItem()?.id,
            maxDistance = maxDistanceValue.get().roundToInt(),
        )

        searchRepository.getSearchMostActiveUsers(
            viewModelScope,
//            token = token,
//            autoDeductCoin, interestedIn,
            token = token,
            searchRequest = searchRequest,
            autoDeductCoin = autoDeductCoin,
            interestedIn = interestedIn,
            context = context,
            hasSkip = hasSkip,

            ) { _mostActiveUsers, error, myPermission ->
            this._mostActiveUsers.clear()
            this._mostActiveUsers.addAll(_mostActiveUsers)
            this._mostActiveUserPermission = myPermission
            callback.invoke(error)
        }
    }

    /// popular users
    fun getSearchPopularUsers(
        token: String,
        autoDeductCoin: Int = 0,
        interestedIn: Int,
        context: Context,
        hasSkip: Boolean = false,
        callback: (String?) -> Unit
    ) {

//        if(genderPicker!=null && genderPicker.getSelectedItem()!=null ) {
//            interestedIngender.value = getInterestedIn(this.genderPicker!!.getSelectedItem()!!.id)
//        }
//        interestedIn = if(genderPicker!=null && genderPicker.getSelectedItem()!=null )getInterestedIn(this.genderPicker!!.getSelectedItem()!!.id).id else interestedIngender.value!!.id,

        var searchRequest = SearchRequest(
            interestedIn = _searchRequest.interestedIn,
            id = _searchRequest.id,
            searchKey = _searchRequest.searchKey,
            minAge = ageRangeLeft.get().roundToInt(),
            maxAge = ageRangeRight.get().roundToInt(),
            minHeight = (heightRangeLeft.get().roundToInt() * 2.54).roundToInt(),
            maxHeight = (heightRangeRight.get().roundToInt() * 2.54).roundToInt(),
            lat = _searchRequest.lat,
            long = _searchRequest.long,
            tags = tags.map { it.id },
            gender = this.genderPicker.getSelectedItem()?.id,
            familyPlans = this.familyPlans.getSelectedItem()?.id,
            politics = this.politics.getSelectedItem()?.id,
            religious = this.religious.getSelectedItem()?.id,
            zodiacSign = this.zodiac.getSelectedItem()?.id,
            maxDistance = maxDistanceValue.get().roundToInt(),
        )

        Log.e(TAG,"MySearchRequestValues: ${Gson().toJson(this._searchRequest)}")

        searchRepository.getSearchPopularUsers(
            viewModelScope,
            token = token,
            searchRequest = searchRequest,
            autoDeductCoin = autoDeductCoin,
            interestedIn = interestedIn,
            context = context,
            hasSkip = hasSkip,
        ) { _popularUsers, error, myPermission ->
            this._popularUsers.clear()
            this._popularUsers.addAll(_popularUsers)
            this._popularUserPermission = myPermission
            callback.invoke(error)
        }
    }

    /// Search
    fun getSearchUsers(
        _searchRequest: SearchRequest,
        token: String,
        autoDeductCoin: Int = 0,
        context: Context,
        hasSkip: Boolean = false,
        callback: (String?) -> Unit
    ) {
//        if(genderPicker!=null && genderPicker.getSelectedItem()!=null ) {
//            interestedIngender.value = getInterestedIn(this.genderPicker!!.getSelectedItem()!!.id)
//        }
        this._searchRequest = SearchRequest(
            interestedIn = _searchRequest.interestedIn,
            id = _searchRequest.id,
            searchKey = _searchRequest.searchKey,
            minAge = ageRangeLeft.get().roundToInt(),
            maxAge = ageRangeRight.get().roundToInt(),
            minHeight = (heightRangeLeft.get().roundToInt() * 2.54).roundToInt(),
            maxHeight = (heightRangeRight.get().roundToInt() * 2.54).roundToInt(),
            lat = _searchRequest.lat,
            long = _searchRequest.long,
            tags = tags.map { it.id },
            gender = this.genderPicker.getSelectedItem()?.id,
            familyPlans = this.familyPlans.getSelectedItem()?.id,
            politics = this.politics.getSelectedItem()?.id,
            religious = this.religious.getSelectedItem()?.id,
            zodiacSign = this.zodiac.getSelectedItem()?.id,
            maxDistance = maxDistanceValue.get().roundToInt(),
        )

        Log.e(TAG,"SearchViewModel: InterestedIn: ${_searchRequest.interestedIn}")
        Log.e(TAG,"MySearchRequestValues : ${Gson().toJson(this._searchRequest)}")

        searchRepository.getSearchUsers(
            viewModelScope,
            token = token,
            this._searchRequest,
            autoDeductCoin,
            context = context,
            hasSkip = hasSkip
        ) { randomUsers, popularUsers, mostActiveUsers, error, randomPermission, popularPermission, mostActivePermission ->
            this._randomUsers.clear()
            this._randomUsers.addAll(randomUsers)
            Log.e(TAG,"users_random Size : ${randomUsers.size}")
            this._randomUserPermission = randomPermission

            this._popularUsers.clear()
            this._popularUsers.addAll(popularUsers)
            Log.e(TAG,"users_popular Size : ${_popularUsers.size}")
            this._popularUserPermission = popularPermission

            this._mostActiveUsers.clear()
            this._mostActiveUsers.addAll(mostActiveUsers)
            Log.e(TAG,"users_active Size : ${_mostActiveUsers.size}")
            this._mostActiveUserPermission = mostActivePermission

            callback.invoke(error)
        }
    }

    /// Search
    fun getSearchUsersTemp(
        _searchRequest: SearchRequestNew,
        token: String,
        context: Context,
        callback: (String?) -> Unit
    ) {
        val searchRequest = SearchRequestNew(name = _searchRequest.name)

        searchRepository.getSearchUsersNew(
            viewModelScope,
            token = token,
            searchRequest,
            context = context,
        ) { _randomUsers, _popularUsers, _mostActiveUsers, error ->
            this._randomUsersSearched.clear()
            Log.e(TAG,"getSearchUsersTemp :  ${Gson().toJson(_randomUsers)}")
            this._randomUsersSearched.addAll(_randomUsers)
//            this._randomUserPermission = mypermission

            this._popularUsers.clear()
            this._popularUsers.addAll(_popularUsers)

            this._mostActiveUsers.clear()
            this._mostActiveUsers.addAll(_mostActiveUsers)
            callback.invoke(error)
        }
    }

    fun getSearchRequest(): SearchRequest? = if (this::_searchRequest.isInitialized) {
        _searchRequest
    } else {
        null
    }

    fun getMyPermission(): MyPermission = _randomUserPermission

    fun getPopularUserMyPermission(): MyPermission = _popularUserPermission

    fun getMostActiveUserMyPermission(): MyPermission = _mostActiveUserPermission

    fun getRandomUsers(): ArrayList<User> = _randomUsers

    fun getRandomUsersSearched(): ArrayList<User> = _randomUsersSearched

    fun getPopularUsers(): ArrayList<User> = _popularUsers

    fun getMostActiveUsers(): ArrayList<User> = _mostActiveUsers

    fun getInterestedIn(pos: Int) = when (interestedIn.value) {
        InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE -> when (pos - 1) {
            0 -> InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE
            1 -> InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE
            else -> InterestedInGender.SERIOUS_RELATIONSHIP_BOTH
        }

        InterestedInGender.CAUSAL_DATING_ONLY_MALE -> when (pos - 1) {
            0 -> InterestedInGender.CAUSAL_DATING_ONLY_MALE
            1 -> InterestedInGender.CAUSAL_DATING_ONLY_FEMALE
            else -> InterestedInGender.CAUSAL_DATING_BOTH
        }

        InterestedInGender.NEW_FRIENDS_ONLY_MALE -> when (pos - 1) {
            0 -> InterestedInGender.NEW_FRIENDS_ONLY_MALE
            1 -> InterestedInGender.NEW_FRIENDS_ONLY_FEMALE
            else -> InterestedInGender.NEW_FRIENDS_BOTH
        }

        InterestedInGender.ROOM_MATES_ONLY_MALE -> when (pos - 1) {
            0 -> InterestedInGender.ROOM_MATES_ONLY_MALE
            1 -> InterestedInGender.ROOM_MATES_ONLY_FEMALE
            else -> InterestedInGender.ROOM_MATES_BOTH
        }

        else -> when (pos - 1) {
            0 -> InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE
            1 -> InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE
            else -> InterestedInGender.BUSINESS_CONTACTS_BOTH
        }
    }


    fun getInterestedInGender(pos: Int) = when (pos) {
        InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE.id, InterestedInGender.CAUSAL_DATING_ONLY_MALE.id, InterestedInGender.NEW_FRIENDS_ONLY_MALE.id, InterestedInGender.ROOM_MATES_ONLY_MALE.id, InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE.id -> 0

        InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE.id, InterestedInGender.NEW_FRIENDS_ONLY_FEMALE.id, InterestedInGender.CAUSAL_DATING_ONLY_FEMALE.id, InterestedInGender.ROOM_MATES_ONLY_FEMALE.id, InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE.id -> 1

        InterestedInGender.SERIOUS_RELATIONSHIP_BOTH.id, InterestedInGender.CAUSAL_DATING_BOTH.id, InterestedInGender.NEW_FRIENDS_BOTH.id, InterestedInGender.ROOM_MATES_BOTH.id, InterestedInGender.BUSINESS_CONTACTS_BOTH.id -> 2
        else -> -1
    }

    fun updateDefaultPicker(lookingFor: String, defaultPicker: DefaultPicker, pos: Int) {
        if (pos == -1) {
            genderPicker.update(lookingFor, defaultPicker.genderPicker)
        } else {
            genderPicker.update(lookingFor, defaultPicker.genderPicker, pos)
        }

        familyPlans.update(lookingFor, defaultPicker.familyPicker)
        politics.update(lookingFor, defaultPicker.politicsPicker)
        religious.update(lookingFor, defaultPicker.religiousPicker)
        zodiac.update(lookingFor, defaultPicker.zodiacSignPicker)
    }

    class SpinnerOptions {
        val prompt = ObservableField<String>()
        val position = ObservableInt(-1)
        val fullItems = ObservableField<List<IdWithValue>>()
        val items = ObservableField<List<String>>()

        private var prmt = ""
        private var fullItms = listOf<IdWithValue>()
        private var itms = listOf<String>()
        private var pos: Int? = -1

        fun update(prmt: String, itms: List<IdWithValue>, pos: Int? = null) {
            val strList = itms.map { if (isCurrentLanguageFrench()) it.valueFr else it.value }
            this.prmt = prmt
            this.fullItms = itms
            this.itms = strList
            this.pos = pos

            this.prompt.set(prmt)
            this.fullItems.set(itms)
            this.items.set(strList)
            if (pos != null) this.position.set(pos)
        }

        fun getSelectedItem(): IdWithValue? {
            return fullItems.get()?.getOrNull(position.get())
        }

        fun clear() = update(prmt, fullItms.toList(), if (pos == null) -1 else pos)
    }

}