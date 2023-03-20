package com.niceord.agent.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpModel: ViewModel() {
    var shopType = MutableLiveData<String>()
}