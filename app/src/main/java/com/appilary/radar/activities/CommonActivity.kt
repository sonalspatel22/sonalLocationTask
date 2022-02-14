package com.appilary.radar.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.appilary.radar.R
import com.appilary.radar.utils.FragmentOpener
import com.appilary.radar.utils.SETTING_SCREEN_OPEN

class CommonActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchUi()
    }

    private fun launchUi() {
        val screenOpen = intent.getIntExtra(SCREEN_OPENED_ARG, 0)
        if (screenOpen == 0)
            finish()

        if (screenOpen == SETTING_SCREEN_OPEN) {
            FragmentOpener.instance.addSettingFrag(this)
        }
    }

    override fun onBackPressed() {
        val frags = supportFragmentManager.fragments
        if (frags.size > 1)
            supportFragmentManager.popBackStackImmediate()
        else {
            finish()
        }
    }

    companion object {
        const val SCREEN_OPENED_ARG = "screen_opened_arg"

        fun open(activity: Activity, screenOpen: Int) {
            Intent(activity, CommonActivity::class.java).apply {
                putExtra(SCREEN_OPENED_ARG, screenOpen)
                activity.startActivity(this)
            }
        }
    }
}