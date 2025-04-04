package com.i69.ui.viewModels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.i69.utils.EXTRA_FIELD_VALUE
import javax.inject.Inject

@HiltViewModel
class InterestsListViewModel @Inject constructor(

) : ViewModel() {

    val interests = ArrayList<String>()
    private val interestsLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var interestType: Int? = null

    fun getInterests(): LiveData<ArrayList<String>> {
        interestsLiveData.value = interests
        return interestsLiveData
    }

    fun loadInterests(interestType: Int?, interestedInValues: ArrayList<String>?) {
        this.interestType = interestType
        this.interests.clear()
        interestedInValues?.forEach {
            this.interests.add(it)
        }
        this.interestsLiveData.value = this.interests
    }

    fun removeInterest(pos: Int) {
        interests.removeAt(pos)
        interestsLiveData.value = this.interests
    }

    fun handleOnActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            interests.add(data?.getStringExtra(EXTRA_FIELD_VALUE)!!)
            interestsLiveData.value = interests
        }
    }
}