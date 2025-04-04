package com.i69.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.i69.GetAllUserMomentsQuery
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.data.models.MomentData
import com.i69.data.models.OfflineStory
import com.i69.data.remote.repository.UserMomentsRepository
import com.i69.data.remote.responses.MomentLikes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserMomentsModelView @Inject constructor(private val userMomentsRepo: UserMomentsRepository) :
    ViewModel() {
    var TAG: String = UserMomentsModelView::class.java.simpleName
    val moments: LiveData<MomentData> get() = _moments
    private val _moments = MutableLiveData<MomentData>()

    val stories: LiveData<List<GetAllUserMultiStoriesQuery.AllUserMultiStory?>?> get() = _stories
    private val _stories = MutableLiveData<List<GetAllUserMultiStoriesQuery.AllUserMultiStory?>?>()

    val userMomentsList = ArrayList<GetAllUserMomentsQuery.Edge>()

    val errorMessage: LiveData<String> get() = _errorMessage
    private val _errorMessage = MutableLiveData<String>()

    val coinPrice = ArrayList<MomentLikes>()

//    fun getAllOfflineStories(callback: (List<OfflineStory>) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val stories = userMomentsRepo.getStoryList()
//            callback.invoke(stories)
//        }
//    }

    fun getAllOfflineStories(callback: (List<OfflineStory>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val stories = userMomentsRepo.getStoryList()
            withContext(Dispatchers.Main) { // Ensure updates happen on the UI thread
                callback.invoke(stories)
            }
        }
    }


    fun insertOfflineStories(stories: List<OfflineStory>) {
        viewModelScope.launch(Dispatchers.IO) {
            userMomentsRepo.insertStoryList(stories)
        }
    }

    fun deleteOfflineStories() {
        viewModelScope.launch(Dispatchers.IO) {
            userMomentsRepo.deleteAllStories()
        }
    }

    suspend fun getAllStories(context: Context, token: String) {
        userMomentsRepo.getMultiUserStories(context, token) { success, stories, message ->
            if (success == 0) {
                Log.e(TAG, "Some Error occurred: $message")
                _errorMessage.postValue(message)
            } else {
                _stories.postValue(stories)
            }
        }
    }

    suspend fun getAllMoments(
        context: Context,
        token: String,
        width: Int,
        size: Int,
        i: Int,
        endCursor: String
    ) {
        userMomentsRepo.getUserMoments(
            context,
            viewModelScope,
            token = token,
            width,
            size,
            i,
            endCursor,
            callback = { list, cursor, hasNext ->
                list.forEach {
                    Log.e(TAG, "ViewModel: $it")
                }
                val momentData = MomentData(list, cursor, hasNext)
                _moments.postValue(momentData)
            },
            onFailure = { errorMessage ->
                _errorMessage.postValue(errorMessage)
            }
        )
    }

//    suspend fun getAllMoments(
//        context: Context,
//        token: String,
//        width: Int,
//        size: Int,
//        i: Int,
//        endCursors: String,
//        callback: (String?) -> Unit
//    ) {
//        userMomentsRepo.getUserMoments(
//            context,
//            viewModelScope,
//            token = token,
//            width = width,
//            size = size,
//            i = i,
//            endCursors = endCursors
//        ) { allUserMoments, endCursor, hasNextPage ->
//
////            this.userMomentsList.clear()
//            this.userMomentsList.addAll(allUserMoments)
//
//            Log.e("getAllMoments: $i ${userMomentsList.size}")
//            if (i == 10) {
//                viewModelScope.launch {
//                    userMomentsRepo.deleteAllOfflineMoments()
//                }
//                userMomentsList.forEach {
//                    offlineUserMomentsList.add(Moment(node = it, image = null))
////                        saveImage(it.node?.file.toString()) { image ->
////                            allUserMomentsList.add(Moment(node = it, image = image))
////                        }
//                }
//
//                viewModelScope.launch {
//                    userMomentsRepo.insertMomentsList(offlineUserMomentsList)
//                }
//                Log.e("inserted: ${offlineUserMomentsList.size}")
//            }
//
//            endCursorN = endCursor
//            hasNextPageN = hasNextPage
//            //this.arrayListLiveData.postValue(allUserMoments)
//            callback.invoke(null)
//        }
//    }

//    private fun saveImage(url: String, imageSaved: (ByteArray) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val imageUrl = URL(url)
//                val ucon = imageUrl.openConnection()
//                val `is` = ucon.getInputStream()
//                val baos = ByteArrayOutputStream()
//                val buffer = ByteArray(1024)
//                var read: Int
//                while (`is`.read(buffer, 0, buffer.size).also { read = it } != -1) {
//                    baos.write(buffer, 0, read)
//                }
//                baos.flush()
//                imageSaved.invoke(baos.toByteArray())
//            } catch (e: Exception) {
//                Log.e("Error: $e")
//            }
//        }
//    }

    fun getMomentLikes(token: String, momentPk: String, callback: (String?) -> Unit) {
        Log.e(TAG, token)
        Log.e(TAG, momentPk)
        userMomentsRepo.getMomentLikes(
            viewModelScope, token = token, momentPk = momentPk
        ) { allUserMoments ->
            this.coinPrice.clear()
            this.coinPrice.addAll(allUserMoments)
            callback.invoke(null)
        }
    }
}