package com.persello.domotics.storage.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.persello.domotics.data.user.UserData
import com.persello.domotics.storage.DataStoreSingleton.dataStore
import kotlinx.coroutines.flow.firstOrNull
import java.util.ArrayList

class UserStorage(private val context: Context) {

    private var userDataKey = stringPreferencesKey("user_data");

    suspend fun write(userDataList: ArrayList<UserData>) {
        val json = Gson().toJson(userDataList);
        context.dataStore.edit { preferences ->
            preferences[userDataKey] = json;
        };
    }

    suspend fun read(): ArrayList<UserData> {
        val json = context.dataStore.data.firstOrNull()?.get(userDataKey) ?: return ArrayList();
        return Gson().fromJson(json, Array<UserData>::class.java).toCollection(ArrayList());
    }

    suspend fun clear() {
        write(ArrayList());
    }
}