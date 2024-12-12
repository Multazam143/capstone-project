package com.example.plantgard.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Mode Gelap"
    }
    val text: LiveData<String> = _text
}