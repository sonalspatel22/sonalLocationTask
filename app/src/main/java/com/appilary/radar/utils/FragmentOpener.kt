package com.appilary.radar.utils

import androidx.fragment.app.FragmentActivity
import com.appilary.radar.R
import com.appilary.radar.api.res.LoginData
import com.appilary.radar.api.res.RouteData
import com.appilary.radar.frag.*


/**
 * Created by gvivgar on 31/3/16.
 */
class FragmentOpener {

//    fun addSplashFragment(activity: FragmentActivity) {
//        val fragment = SplashFragment.newInstance()
//        activity.supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.container, fragment,
//                        SplashFragment.TAG).addToBackStack("").commitAllowingStateLoss()
//    }

    //
    fun showDialog(
        activity: FragmentActivity,
        msg: String,
        singleButton: Boolean,
        okText: String = activity.getString(R.string.ok),
        cancelText: String = activity.getString(R.string.cancel),
        tag: String? = null
    ) {
        AppUtils.hideKeyboard(activity)
        val fragment = DialogFragment.newInstance(msg, singleButton, okText, cancelText, tag)
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment,
                DialogFragment.TAG
            ).addToBackStack("").commitAllowingStateLoss()
    }

    fun addSplashFrag(activity: FragmentActivity) {
        val fm = activity.supportFragmentManager
        for (i in 0 until fm.backStackEntryCount)
            fm.popBackStack()

        val fragment = SplashFrag.newInstance()
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, SplashFrag.TAG)
            .commitAllowingStateLoss()
    }

    fun addLoginFrag(activity: FragmentActivity) {
        val fm = activity.supportFragmentManager
        for (i in 0 until fm.backStackEntryCount)
            fm.popBackStack()

        val fragment = LoginFrag.newInstance()
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, LoginFrag.TAG)
            .commitAllowingStateLoss()
    }

    fun addSurveyFrag(activity: FragmentActivity, openFormId: Int = 0) {
        val fragment = SurveyFrag.newInstance(openFormId)
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, SurveyFrag.TAG)
            .commitAllowingStateLoss()
    }

    fun addSettingFrag(activity: FragmentActivity) {
        val fragment = SettingsFrag.newInstance()
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, SettingsFrag.TAG)
            .commitAllowingStateLoss()
    }

    fun addVerifyPromoterFrag(activity: FragmentActivity, loginData: LoginData) {
        val fragment = VerifyPromoterFrag.newInstance(loginData)
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, VerifyPromoterFrag.TAG)
            .commitAllowingStateLoss()
    }

    fun addCameraScreenFrag(
        activity: FragmentActivity,
        drawable: Int,
        text: String,
        surveyId: String?
    ) {
//        if (!(activity as MainActivity).checkForNetwork(false))
//            return

//        val fm = activity.getSupportFragmentManager()
//        for (i in 0 until fm.getBackStackEntryCount())
//            fm.popBackStack()

        val fragment = AttendanceFrag.newInstance(surveyId)
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, AttendanceFrag.TAG)
            .addToBackStack("").commitAllowingStateLoss()
    }

    fun addVideoPlayFrag(activity: FragmentActivity) {
        val fragment = VideoPlayFrag.newInstance()
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment, VideoPlayFrag.TAG)
            .addToBackStack("").commitAllowingStateLoss()
    }

    fun addImageViewFrag(activity: FragmentActivity, url: String) {
        val fragment = ImageViewFrag.newInstance(url)
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment, ImageViewFrag.TAG)
            .addToBackStack("").commitAllowingStateLoss()
    }

    fun addUploadPendingInfoFrag(activity: FragmentActivity) {
        val fragment = UploadPendingInfoFrag.newInstance()
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment, UploadPendingInfoFrag.TAG)
            .addToBackStack("").commitAllowingStateLoss()
    }



    companion object {
        private var mInstance: FragmentOpener? = null
        val instance: FragmentOpener
            get() {
                if (mInstance == null)
                    mInstance = FragmentOpener()
                return mInstance!!
            }
    }

}
