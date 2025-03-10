package com.persello.domotics.storage

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

object DataStoreSingleton {
    val Context.dataStore by preferencesDataStore(name = "domotics");
}