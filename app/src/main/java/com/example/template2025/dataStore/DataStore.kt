package com.example.template2025.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.template2025.dataStore.PrefsKeys.TOKEN_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object PrefsKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val USER_ROLE = stringPreferencesKey("user_role")
    val TOKEN_KEY = stringPreferencesKey("token")
}

class AppDataStore(private val context: Context) {
    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[PrefsKeys.IS_LOGGED_IN] ?: false
        }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.IS_LOGGED_IN] = value
        }
    }

    val userTypeFlow: Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[PrefsKeys.USER_ROLE]
        }

    suspend fun setUserType(value: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.USER_ROLE] = value
        }
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[PrefsKeys.TOKEN_KEY]
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}