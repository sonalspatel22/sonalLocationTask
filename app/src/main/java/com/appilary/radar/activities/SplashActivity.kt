package com.appilary.radar.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.appilary.radar.R
import com.appilary.radar.events.DialogCallbackEvent
import com.appilary.radar.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        findLocation(null)
        FragmentOpener.instance.addSplashFrag(this)
        lifecycleScope.launch {
            if (intent?.getBooleanExtra(FROM_INTERNAL, false) != true)
                delay(2000)
            if (AppPreference.instance.authToken.isEmpty())
                FragmentOpener.instance.addLoginFrag(mainActivity)
            else {
                Intent(this@SplashActivity, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }

        }
    }


    fun checkForNetwork(shouldFinish: Boolean = false): Boolean {
        val isAvail = isNetworkAvailable(mainActivity)
        if (!isAvail) {
            val msg = "Network Error, Please check and try again."
            FragmentOpener.instance.showDialog(mainActivity, msg, true)
            if (shouldFinish)
                EventBus.getDefault().register(this)
        }
        return isAvail
    }

    @Subscribe
    fun dialogCallback(event: DialogCallbackEvent) {
        if (EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().unregister(this)
        if (event.tag == VERSION_TAG) {
            if (event.isSuccess) {
                openAppInMarket(this)
            }
        } else finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    companion object {
        val FROM_INTERNAL = "from_internal"
        fun open(activity: Activity) {
            Intent(activity, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(FROM_INTERNAL, true)
                activity.startActivity(this)
            }
        }
    }

}