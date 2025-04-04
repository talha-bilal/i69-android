package com.i69.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.i69.applocalization.AppStringConstant
import javax.inject.Inject

class SharedPref @Inject constructor(context: Context) {

    val sharedpref: SharedPreferences =
        context.getSharedPreferences("i69languagechange_pref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedpref.edit()

//    private val ROOM_ID = "ROOM_ID"
//    private val ROOM_ID_NOTIFY = "ROOM_ID_NOTIFY"
//    private val LANGUAGE_NAME = "LANGUAGE_NAME"
//    private val TYPE_VIEW = "TYPE_VIEW"
//    private val SHOW_NOTIFICATION = "ShowNotification"
//    private val NEW_CHAT = "NEW_CHAT"
//    private val CHAT_LIST_REFRESH = "CHAT_LIST_REFRESH"
//    private val READ_COUNT = "READ_COUNT"


    private val LANGUAGE_CHANGE = "LANGUAGECHANGE"
    private val IS_FROM_SETTINGS = "ISFROMSETTINGS"
    private val OPERATOR_CODE = "OPERATORCODE"
    private val OPERATION_REFERENCE = "OPERATIONREFERENCE"
    private val FIRST_TIME_LANGUAGE_SET = "FIRTTIMELANGUAGESET"
    private val ATTR_TRANSLATER = "ATTRTRANSLATER"


    fun setAttrTranslater(appConst: AppStringConstant) {
        val gson = Gson()
        val json: String = gson.toJson(appConst)
        editor.apply {
            putString(ATTR_TRANSLATER, json)
            apply()
        }
    }


    fun attrTranslater(): AppStringConstant? {
        val gson = Gson()
        val cb: String = sharedpref.getString(ATTR_TRANSLATER, null) ?: return null
        return gson.fromJson(cb, AppStringConstant::class.java)

    }

    fun setLanguage(language: Boolean) {
        editor.apply {
            putBoolean(LANGUAGE_CHANGE, language)
            apply()
        }
    }

    fun getLanguage(): Boolean {
        return sharedpref.getBoolean(LANGUAGE_CHANGE, false)
    }

    fun setLanguageFromSettings(isFromSettings: Boolean) {
        editor.apply {
            putBoolean(IS_FROM_SETTINGS, isFromSettings)
            apply()
        }
    }

    fun getLanguageFromSettings(): Boolean {
        return sharedpref.getBoolean(IS_FROM_SETTINGS, false)
    }

    fun setOperatorCode(operatorCode: String) {
        editor.apply {
            putString(OPERATOR_CODE, operatorCode)
            apply()
        }
    }

    fun getOperatorCode(): String {
        return sharedpref.getString(OPERATOR_CODE, "null") ?: "null"
    }

    fun setOperatorReference(operationReference: String) {
        editor.apply {
            putString(OPERATION_REFERENCE, operationReference)
            apply()
        }
    }

    fun getOperatorReference(): String {
        return sharedpref.getString(OPERATION_REFERENCE, "null") ?: "null"
    }

    fun setFirtsTimeLanguage(langageSet: Boolean) {
        editor.apply {
            putBoolean(FIRST_TIME_LANGUAGE_SET, langageSet)
            apply()
        }
    }

    fun getFirstTimeLanguage(): Boolean {
        return sharedpref.getBoolean(FIRST_TIME_LANGUAGE_SET, false)
    }

//    fun setLanguageName(languageName: String) {
//        editor.apply {
//            putString(LANGUAGE_NAME, languageName)
//            apply()
//        }
//    }
//
//    fun getLanguageName(defaultLang: String): String {
//        return sharedpref.getString(LANGUAGE_NAME, defaultLang) ?: ""
//    }
//
//
//    fun setRoomID(roomID: String) {
//        editor.apply {
//            putString(ROOM_ID, roomID)
//            apply()
//        }
//    }
//
//    fun getRoomID(): String {
//        return sharedpref.getString(ROOM_ID, "") ?: "null"
//    }
//
//
//    fun setRoomIDNotify(roomIDNotify: String) {
//        editor.apply {
//            putString(ROOM_ID_NOTIFY, roomIDNotify)
//            apply()
//        }
//    }
//
//    fun getRoomIDNotify(): String {
//        return sharedpref.getString(ROOM_ID_NOTIFY, "false") ?: "null"
//    }
//
//    fun setTypeView(typeView: String) {
//        editor.apply {
//            putString(TYPE_VIEW, typeView)
//            apply()
//        }
//    }
//
//    fun getTypeView(): String {
//        return sharedpref.getString(TYPE_VIEW, "") ?: "null"
//    }
//
//
//    fun setShowNotification(typeView: String) {
//        editor.apply {
//            putString(SHOW_NOTIFICATION, typeView)
//            apply()
//        }
//    }
//
//    fun getShowNotification(): String {
//        return sharedpref.getString(SHOW_NOTIFICATION, "") ?: "null"
//    }
//
//
//    fun setNewChat(newChat: String) {
//        editor.apply {
//            putString(NEW_CHAT, newChat)
//            apply()
//        }
//    }
//
//    fun getNewChat(): String {
//        return sharedpref.getString(NEW_CHAT, "") ?: "null"
//    }
//
//    fun setChatListRefresh(chatRefreshList: String) {
//        editor.apply {
//            putString(CHAT_LIST_REFRESH, chatRefreshList)
//            apply()
//        }
//    }
//
//    fun getChatListRefresh(): String {
//        return sharedpref.getString(CHAT_LIST_REFRESH, "false") ?: "null"
//    }
//
//
//    fun setReadCount(readCount: String) {
//        editor.apply {
//            putString(READ_COUNT, readCount)
//            apply()
//        }
//    }
//
//    fun getReadCount(): String {
//        return sharedpref.getString(READ_COUNT, "") ?: "null"
//    }
}