package com.i69.applocalization

import androidx.lifecycle.MutableLiveData
import com.i69.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AppStringConstantViewModel @Inject constructor(): BaseViewModel() {
    var  data = MutableLiveData<AppStringConstant>()
}