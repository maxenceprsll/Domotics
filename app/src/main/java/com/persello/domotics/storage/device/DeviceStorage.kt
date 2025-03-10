package com.persello.domotics.storage.device

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.persello.domotics.data.device.DeviceData
import com.persello.domotics.storage.DataStoreSingleton.dataStore
import kotlinx.coroutines.flow.firstOrNull
import java.util.ArrayList

class DeviceStorage(private val context: Context) {

    private var deviceDataKey = stringPreferencesKey("device_data")

    suspend fun write(deviceDataList: ArrayList<DeviceData>) {
        val json = Gson().toJson(deviceDataList)
        context.dataStore.edit { preferences ->
            preferences[deviceDataKey] = json
        }
    }

    suspend fun read(): ArrayList<DeviceData> {
        val json = context.dataStore.data.firstOrNull()?.get(deviceDataKey) ?: return ArrayList()
        return Gson().fromJson(json, Array<DeviceData>::class.java).toCollection(ArrayList())
    }

    suspend fun clear() {
        write(ArrayList())
    }
}