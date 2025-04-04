package com.i69.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.DefaultUpload
import com.google.gson.JsonObject
import com.i69.ChatRoomSubscription
import com.i69.GetAllRoomsQuery
import com.i69.data.models.*
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.repository.*
import com.i69.data.remote.requests.ReportRequest
import com.i69.data.remote.responses.CoinsResponse
import com.i69.data.remote.responses.DefaultPicker
import com.i69.data.remote.responses.ResponseBody
import com.i69.ui.adapters.SearchInterestedServerAdapter
import com.i69.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val userDetailsRepository: UserDetailsRepository,
    private val userUpdateRepository: UserUpdateRepository,
    private val coinRepository: CoinRepository,
    private val giftsRepository: GiftsRepository,
    private val api: GraphqlApi
) : ViewModel() {

    private var selectedMsgPreview: GetAllRoomsQuery.Edge? = null

    private val _shouldUpdateAdapter: MutableSharedFlow<Boolean> = MutableSharedFlow()
    private val _currentUserLiveData: MutableLiveData<User> = MutableLiveData()
    val shouldUpdateAdapter: Flow<Boolean>
        get() = _shouldUpdateAdapter

    val currentUserLiveData: MutableLiveData<User>
        get() = _currentUserLiveData

    /// Mutable Flow
    fun updateAdapterFlow() {
        viewModelScope.launch {
            _shouldUpdateAdapter.emit(true)
        }
    }

    fun scheduleStory(file: DefaultUpload, publishAt: String, token: String) {
        viewModelScope.launch {
            userDetailsRepository.scheduleStory(file, publishAt, token)
        }
    }

    fun deleteChatRoom(
        roomId: Int,
        token: String,
        callback: (Resource<ResponseBody<DeleteChatRoom>>) -> Unit
    ) {
        viewModelScope.launch {
            val response = userDetailsRepository.deleteChatRoom(roomId, token)
            callback.invoke(response)
        }
    }

    /// Selected Preview Model
    fun getSelectedMessagePreview(): GetAllRoomsQuery.Edge? = selectedMsgPreview

    fun setSelectedMessagePreview(updated: GetAllRoomsQuery.Edge) {
        selectedMsgPreview = updated
    }

    /// Default Pickers
    fun getDefaultPickers(token: String): LiveData<DefaultPicker> =
        appRepository.getDefaultPickers(viewModelScope, token)

    /// LanguageCode
    fun getLanguageCode(token: String): LiveData<LanguageCodeData> =
        appRepository.getLanguageCode(viewModelScope, token)

    /// Coin Settings
    fun getCoinSettings(token: String): LiveData<ArrayList<CoinSettings>> =
        coinRepository.getCoinSettings(viewModelScope, token)

    fun getCoinSettingsByRegion(token: String, method: String): LiveData<CoinSettings> =
        coinRepository.getCoinSettingByRegion(viewModelScope, token, method)

    fun getRealGifts(token: String): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> =
        giftsRepository.getRealGifts(viewModelScope, token)

    fun getVirtualGifts(token: String): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> =
        giftsRepository.getVirtualGifts(viewModelScope, token)

    fun getAllGifts(token: String): MutableLiveData<ArrayList<ModelGifts.Data.AllRealGift>> =
        giftsRepository.getAllGifts(viewModelScope, token)

    suspend fun purchaseGift(
        token: String?,
        userId: String?,
        giftId: Int?
    ): Resource<ResponseBody<Id>> = giftsRepository.purchaseGift(token = token, userId, giftId)

    /// Current User
    fun getCurrentUser(userId: String, token: String, reload: Boolean): LiveData<User> =
        userDetailsRepository.getCurrentUser(viewModelScope, userId, token = token, reload)

    fun getCurrentUserUpdate(userId: String, token: String, reload: Boolean) =
        userDetailsRepository.getCurrentUser(
            viewModelScope,
            userId,
            token = token,
            reload,
            _currentUserLiveData
        )

    suspend fun getLanguage(userId: String, token: String) =
        userDetailsRepository.getLanguage(userId, token).data

    suspend fun updateLanguage(
        languageCode: String,
        userid: String,
        token: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateLanguage(
            languageCode = languageCode,
            userid = userid,
            token = token
        )

    suspend fun updateLanguageId(
        languageCode: Int,
        userid: String,
        token: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateLanguageId(
            languageCode = languageCode,
            userid = userid,
            token = token
        )

    /// Coin
    suspend fun deductCoin(
        userId: String,
        token: String,
        deductCoinMethod: com.i69.data.enums.DeductCoinMethod
    ): Resource<ResponseBody<CoinsResponse>> =
        coinRepository.deductCoin(userId, token = token, deductCoinMethod)

    /// Update
    suspend fun updateProfile(user: User, token: String): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateProfile(user, token = token)

    suspend fun uploadImage(
        userId: String,
        token: String,
        filePath: String,
        type: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.uploadImage(userId = userId, token = token, filePath, type)

    suspend fun uploadImage2(
        userId: String,
        token: String,
        file: File,
        type: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.uploadImage2(userId = userId, token = token, file, type)

    suspend fun deleteUserPhotos(token: String, photoId: String): Resource<ResponseBody<Id>> =
        userUpdateRepository.deleteUserPhotos(token = token, photoId = photoId)


    suspend fun deleteUserPublicPhotos(token: String, photoId: String): Resource<ResponseBody<Id>> =
        userUpdateRepository.deleteUserPublicPhotos(token = token, photoId = photoId)

    suspend fun updateUserLikes(
        userId: String,
        userLikes: ArrayList<String>,
        token: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateUserLikes(userId, userLikes, token = token)

    suspend fun updateLocation(
        userId: String,
        location: Array<Double>,
        token: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.updateLocation(userId, token = token, location)

    //Report
    suspend fun reportUser(
        reportRequest: ReportRequest,
        token: String?
    ): Resource<ResponseBody<Id>> = userUpdateRepository.reportUser(reportRequest, token = token)

    suspend fun blockUser(
        userId: String?,
        blockedId: String?,
        token: String?
    ): Resource<ResponseBody<Id>> = userUpdateRepository.blockUser(token = token, userId, blockedId)

    suspend fun unblockUser(
        userId: String,
        blockedId: String,
        token: String
    ): Resource<ResponseBody<Id>> =
        userUpdateRepository.unblockUser(token = token, userId, blockedId)

    suspend fun deleteProfile(
        userId: String,
        token: String,
        code: Int
    ): Resource<ResponseBody<JsonObject>> =
        userUpdateRepository.deleteProfile(userId, token = token, code)

    suspend fun verifyUser(userEmail: String, token: String): Resource<ResponseBody<JsonObject>> =
        userUpdateRepository.verifyUser(userEmail, token = token)

    suspend fun getLanguages(): Response<LanguageNewModel> = userUpdateRepository.getLanguages()

    //Log Out
    fun logOut(userId: String, token: String, callback: () -> Unit) =
        userDetailsRepository.logOut(viewModelScope, userId = userId, token = token, callback)

    private val _newMessageFlow: MutableSharedFlow<ChatRoomSubscription.Message?> =
        MutableSharedFlow()
    val newMessageFlow: SharedFlow<ChatRoomSubscription.Message?>
        get() = _newMessageFlow

    suspend fun onNewMessage(newMessage: ChatRoomSubscription.Message?) {
        _newMessageFlow.emit(newMessage)
    }

    //Share location
    suspend fun shareLocation(
        messageStr: String,
        roomId: Int,
        moderatorId: String,
        token: String
    ): Resource<ResponseBody<JsonObject>> =
        userUpdateRepository.shareLocation(messageStr, roomId, moderatorId, token)

    suspend fun audioChat(
        roomId: Int,
        requestName: String,
        moderatorId: String,
        callStrType: String,
        data: String, token:String
    ): Resource<ResponseBody<JsonObject>> =
        userDetailsRepository.audioChat(roomId, requestName, moderatorId, callStrType, data, token)

    var listItemsFromViewModel = mutableListOf<SearchInterestedServerAdapter.MenuItemString>()
}
