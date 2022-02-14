package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.activities.SplashActivity
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.events.DialogCallbackEvent
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.CONFIRM_MSG
import com.appilary.radar.utils.FragmentOpener
import kotlinx.android.synthetic.main.frag_settings.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SettingsFrag : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle("Settings")
        setBackButton()
        setData()
        val pinfo = mainActivity.packageManager.getPackageInfo(mainActivity.packageName, 0)
        version_tv.text = "Version Name: ${pinfo.versionName}"
        logout_tv.setOnClickListener {
            EventBus.getDefault().register(this)
            FragmentOpener.instance.showDialog(mainActivity, CONFIRM_MSG, false)
        }

    }

    fun setData() {
        val data = AppPreference.instance.getLoginData()
        txt_userName.text = "${data?.clientName}"
    }


    @Subscribe
    fun eventReceived(event: DialogCallbackEvent) {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        if (event.isSuccess) {
            AppUtils.logout()
            SplashActivity.open(mainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }


    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    companion object {
        val TAG = SettingsFrag::class.java.simpleName

        @JvmStatic
        fun newInstance(): SettingsFrag {
            val fragment = SettingsFrag()
            return fragment
        }
    }
}