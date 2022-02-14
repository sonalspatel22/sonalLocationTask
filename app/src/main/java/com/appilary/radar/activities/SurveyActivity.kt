package com.appilary.radar.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.appilary.radar.R
import com.appilary.radar.events.DialogCallbackEvent
import com.appilary.radar.frag.DialogFragment
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.FragmentOpener
import com.appilary.radar.utils.SURVEY_EXIT_MSG
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SurveyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchUi()
    }

    fun launchUi() {
        FragmentOpener.instance.addSurveyFrag(
            this,
            AppPreference.instance.getLoginData()?.agreementFormId ?: 0
        )
    }

    override fun onBackPressed() {
        val frags = supportFragmentManager.fragments
        if (frags.size > 0 && frags[frags.size - 1] is DialogFragment)
            supportFragmentManager.popBackStackImmediate()
        else {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this)
            FragmentOpener.instance.showDialog(mainActivity, SURVEY_EXIT_MSG, false)
        }
    }

    @Subscribe
    fun eventReceived(event: DialogCallbackEvent) {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        if (event.isSuccess)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    companion object {
        fun open(activity: Activity) {
            Intent(activity, SurveyActivity::class.java).apply {
                activity.startActivity(this)
            }
        }
    }

}
