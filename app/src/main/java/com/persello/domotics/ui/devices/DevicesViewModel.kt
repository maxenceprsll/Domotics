package com.persello.domotics.ui.devices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DevicesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Liste de vos appareils"
    }
    val text: LiveData<String> = _text
}