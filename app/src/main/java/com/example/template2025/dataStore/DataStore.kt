package com.example.template2025.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object PrefsKeys { val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in") }

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
}