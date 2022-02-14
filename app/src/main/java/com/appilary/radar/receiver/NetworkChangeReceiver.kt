package com.appilary.radar.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.appilary.radar.utils.ApiCallUtils

/**
 * Created by vi.garg on 5/2/18.
 */
class NetworkChangeReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, p1: Intent?) {
        ApiCallUtils.postForm()
    }
}