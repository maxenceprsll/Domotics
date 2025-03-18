package com.persello.domotics.storage.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.ArrayList
import com.google.gson.Gson
import com.persello.domotics.data.home.HomeData
import com.persello.domotics.storage.DataStoreSingleton.dataStore
import kotlinx.coroutines.flow.firstOrNull

class HomeStorage(private val context: Context) {

    private var homeDataKey = stringPreferencesKey("home_data");
    private var selectedHouseIdKey = stringPreferencesKey("selected_house_id");

    suspend fun write(homeDataList: ArrayList<HomeData>) {
        val json = Gson().toJson(homeDataList);
        context.dataStore.edit { preferences ->
            preferences[homeDataKey] = json;
        };
    }

    suspend fun read(): ArrayList<HomeData> {
        val json = context.dataStore.data.firstOrNull()?.get(homeDataKey) ?: return ArrayList();
        return Gson().fromJson(json, Array<HomeData>::class.java).toCollection(ArrayList());
    }

    suspend fun saveSelectedHouseId(selectedHouseId: String) {
        context.dataStore.edit { preferences ->
            preferences[selectedHouseIdKey] = selectedHouseId;
        };
    }

    suspend fun clear() {
        write(ArrayList());
    }

    suspend fun readSelectedHouseId(): String {
        return context.dataStore.data.firstOrNull()?.get(selectedHouseIdKey) ?: "";
    }
}