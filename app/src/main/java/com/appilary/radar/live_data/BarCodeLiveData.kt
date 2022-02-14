package com.appilary.radar.live_data

import androidx.lifecycle.MutableLiveData

object BarCodeLiveData : MutableLiveData<String>() {

    override fun onInactive() {
        super.onInactive()
        value = null
    }
}