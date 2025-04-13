package com.adam.buru.timemanager.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object AuthManager {

    private val JWT_TOKEN = stringPreferencesKey("jwt_token")
    private val USER_ID = intPreferencesKey("user_id")

    suspend fun saveToken(token: String, userId: Int, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
            preferences[USER_ID] = userId
        }
    }

    suspend fun getToken(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[JWT_TOKEN]
    }

    suspend fun getUserId(context: Context): Int {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID] ?: -1
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN)
            preferences.remove(USER_ID)
        }
    }
}
