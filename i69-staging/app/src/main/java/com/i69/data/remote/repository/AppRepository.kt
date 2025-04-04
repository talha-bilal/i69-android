package com.i69.data.remote.repository

import androidx.lifecycle.MutableLiveData
import com.i69.data.models.LanguageCodeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.i69.data.remote.api.GraphqlApi
import com.i69.data.remote.responses.DefaultPicker
import com.i69.data.remote.responses.ResponseBody
import com.i69.utils.Resource
import com.i69.utils.getResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val api: GraphqlApi
) {
    private var _defaultPickers: DefaultPicker? = null
    private val defaultPickers: MutableLiveData<DefaultPicker> = MutableLiveData()

    private var _languageCode: LanguageCodeData? = null
    private val languageCode: MutableLiveData<LanguageCodeData> = MutableLiveData()

    fun getDefaultPickers(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<DefaultPicker> {
        if (_defaultPickers == null) {
            viewModelScope.launch(Dispatchers.IO) {
                loadDefaultPickers(token)

            }

        }
       // defaultPickers.value = _defaultPickers!!
        return defaultPickers
    }

    fun getLanguageCode(
        viewModelScope: CoroutineScope,
        token: String
    ): MutableLiveData<LanguageCodeData> {
        if (_languageCode == null) {
            viewModelScope.launch(Dispatchers.IO) {
                getUserSelectedLanguage(token)
                //languageCode.value = _languageCode!!
            }
        }

        return languageCode
    }

    /** New By suresh */
    suspend fun loadPickers(token: String?): Resource<ResponseBody<DefaultPicker>> {
        val queryName = "defaultPicker"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("genderPicker { id, value, valueFr }, ")
            .append("agePicker { id, value, valueFr }, ")
            .append("ethnicityPicker { id, value, valueFr }, ")
            .append("familyPicker { id, value, valueFr }, ")
            .append("heightsPicker { id, value, valueFr }, ")
            .append("politicsPicker { id, value, valueFr }, ")
            .append("religiousPicker { id, value, valueFr }, ")
            .append("tagsPicker { id, value, valueFr }, ")
            .append("zodiacSignPicker { id, value, valueFr } ")
            .append("}")
            .append("}")
            .toString()

        return api.getResponse<DefaultPicker>(query, queryName, token)
    }

    private suspend fun loadDefaultPickersOld(token: String) {
        val queryName = "defaultPicker"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("genderPicker { id, value, valueFr }, ")
            .append("agePicker { id, value, valueFr }, ")
            .append("ethnicityPicker { id, value, valueFr }, ")
            .append("familyPicker { id, value, valueFr }, ")
            .append("heightsPicker { id, value, valueFr }, ")
            .append("politicsPicker { id, value, valueFr }, ")
            .append("religiousPicker { id, value, valueFr }, ")
            .append("tagsPicker { id, value, valueFr }, ")
            .append("zodiacSignPicker { id, value, valueFr } ")
            .append("}")
            .append("}")
            .toString()

        api.getResponse<DefaultPicker>(query, queryName, token).data?.data?.let {
            _defaultPickers = it
            defaultPickers.postValue(_defaultPickers!!)
        }
    }

    private suspend fun loadDefaultPickers(token: String) {
        val queryName = "defaultPicker"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName { ")
            .append("genderPicker { id, value, valueFr }, ")
            .append("agePicker { id, value, valueFr }, ")
            .append("ethnicityPicker { id, value, valueFr }, ")
            .append("familyPicker { id, value, valueFr }, ")
            .append("heightsPicker { id, value, valueFr }, ")
            .append("politicsPicker { id, value, valueFr }, ")
            .append("religiousPicker { id, value, valueFr }, ")
            .append("tagsPicker { id, value, valueFr }, ")
            .append("zodiacSignPicker { id, value, valueFr } ")
            .append("}")
            .append("}")
            .toString()

        api.getResponse<DefaultPicker>(query, queryName, token).data?.data?.let {
            _defaultPickers = it
            defaultPickers.postValue(_defaultPickers!!)
        }

    }

    private suspend fun getUserSelectedLanguage(token: String) {
        val queryName = "user"
        val query = StringBuilder()
            .append("query {")
            .append("$queryName(id: 795e92df-1547-46fc-8b9f-83fb68844058) { ")
            .append("userLanguageCode")
            .append("}")
            .append("}")
            .toString()
        api.getResponse<LanguageCodeData>(query, queryName, token).data?.data?.let {
            _languageCode = it
            languageCode.postValue(_languageCode!!)
        }
    }

}