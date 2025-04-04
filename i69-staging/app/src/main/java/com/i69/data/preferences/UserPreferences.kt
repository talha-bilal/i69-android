package com.i69.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences constructor(private val dataStore: DataStore<Preferences>) {

    val userId: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_ID]
        }

    val userToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_TOKEN]
        }

    val chatUserId: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CHAT_USER_ID]
        }

    val userName: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_NAME]
        }

    val userEmail: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }

    suspend fun saveUserIdToken(userId: String, token: String, name: String, email: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_USER_ID] = userId
            mutablePreferences[KEY_USER_TOKEN] = token
            mutablePreferences[KEY_USER_NAME] = name
            mutablePreferences[KEY_USER_EMAIL] = email
        }
    }

    suspend fun saveChatUserId(chatUserId: Int) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_CHAT_USER_ID] = chatUserId
        }
    }

    suspend fun clear() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_TOKEN = stringPreferencesKey("user_token")
        private val KEY_CHAT_USER_ID = intPreferencesKey("chat_user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }
}