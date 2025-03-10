package com.persello.domotics.storage.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.persello.domotics.storage.DataStoreSingleton.dataStore
import kotlinx.coroutines.flow.firstOrNull

class TokenStorage(private val context: Context) {

    private var tokenKey = stringPreferencesKey("token");

    suspend fun write(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token;
        }
    }

    suspend fun read(): String {
        return context.dataStore.data.firstOrNull()?.get(tokenKey) ?: ""
    }

    suspend fun clearToken() {
        write("");
    }
}