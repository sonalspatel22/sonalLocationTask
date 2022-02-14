package com.appilary.radar.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.appilary.radar.R
import com.appilary.radar.api.AppRestClientService
import com.appilary.radar.database.CreateSurveyBody
import com.appilary.radar.database.RealmControler
import com.appilary.radar.dialog.SettingBottomSheet
import com.appilary.radar.frag.BaseFragment
import com.appilary.radar.frag.BreakTimeFrag
import com.appilary.radar.frag.DashBoardFrag
import com.appilary.radar.frag.UploadPendingInfoFrag
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity() {

    val summarFrag = DashBoardFrag.newInstance()
    val uploadPendingFrag = UploadPendingInfoFrag.newInstance()
    val breakFrag = BreakTimeFrag.newInstance()
    var activeFragment: BaseFragment = summarFrag
    var isAllDataSaved = false
    private var surveyQues: CreateSurveyBody? = null
    private var isBreakFeatureAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        checkBreakFeatureAvailable()
        saveLocalData()
        fab.setOnClickListener {
            if (isBreakFeatureAvailable && breakFrag.isInBetweenBreak())
                return@setOnClickListener

            if (isAllDataSaved)
                SurveyActivity.open(mainActivity)
            else
                dataNotSaved()
        }

        bottomAppBar.replaceMenu(R.menu.home_menu)
        bottomAppBar.setNavigationOnClickListener {

            if (activeFragment != summarFrag) {
                supportFragmentManager.beginTransaction().hide(activeFragment).show(summarFrag).commit()
                activeFragment = summarFrag
            } else {
                summarFrag.callSummaryApi()
            }
        }

        bottomAppBar.menu.findItem(R.id.break_menu)?.isVisible = isBreakFeatureAvailable

        bottomAppBar.setOnMenuItemClickListener { item ->
            if (summarFrag.isApiCalling) {
                return@setOnMenuItemClickListener false
            }

            if (item.itemId != R.id.break_menu && isBreakFeatureAvailable && breakFrag.isInBetweenBreak()) {
                return@setOnMenuItemClickListener false
            }

            when (item.itemId) {
                R.id.break_menu -> {
                    if (activeFragment != breakFrag) {
                        supportFragmentManager.beginTransaction().hide(activeFragment)
                            .show(breakFrag)
                            .commit()
                        activeFragment = breakFrag
                    }
                    true
                }
                R.id.upload_menu -> {
                    if (activeFragment != uploadPendingFrag) {
                        supportFragmentManager.beginTransaction().hide(activeFragment)
                            .show(uploadPendingFrag)
                            .commit()
                        activeFragment = uploadPendingFrag
                    }
                    true
                }
                R.id.setting_menu -> {
                    SettingBottomSheet.getInstance()
                        .show(supportFragmentManager, SettingBottomSheet.TAG)

                    true
                }

                else -> false


                // do something interesting on navigation click
            }
        }

        addFragment()
    }

    fun addFragment() {
        supportFragmentManager.beginTransaction().apply {
            add(
                R.id.container,
                uploadPendingFrag,
                UploadPendingInfoFrag.TAG
            ).hide(uploadPendingFrag)

            if (isBreakFeatureAvailable) {
                add(
                    R.id.container,
                    breakFrag,
                    BreakTimeFrag.TAG
                ).hide(breakFrag)
            }
//            add(R.id.container, summarFrag, DashBoardFrag.TAG).hide(summarFrag)
            add(R.id.container, summarFrag, DashBoardFrag.TAG) //Main
        }.commit()
    }

    private fun saveLocalData() {
        lifecycleScope.launch {
            surveyQues = RealmControler.getCreateSurveyBody()
            var surveySaved: Boolean? = null
            var dropdownSaved: Boolean? = null

            showProgressBar()

            if (!AppUtils.isSameDay(AppPreference.instance.lastTimeDataSaved) || surveyQues == null) {
                AppPreference.instance.setRetainValueData(null)
                AppPreference.instance.getLoginData()?.let {
                    if (it.jsonLink.isNotEmpty()) {
                        try {
                            val surveyData =
                                withContext(Dispatchers.Default) {
                                    AppRestClientService.service.getSurvey(
                                        it.jsonLink
                                    )
                                }
                            AppPreference.instance.surveyId = surveyData.appConfig?.surveyId ?: 0
                            RealmControler.saveCreateSurveyRes(surveyData)
                            surveySaved = true
                            summarFrag.updateValue()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            surveySaved = false
                        }
                    }
                }
            }

            val dependentDropDownBean = AppPreference.instance.isDependentDropdownBeanExist()
            if (dependentDropDownBean == null || !AppUtils.isSameDay(dependentDropDownBean.time)) {
                RealmControler.getCreateSurveyBody()?.getAppConfigData()
                    ?.apiUrls?.offlineDropdownApi?.let {
                        if (it.isNotEmpty()) {
                            try {
                                val dropdownData =
                                    withContext(Dispatchers.Default) {
                                        AppRestClientService.service.getDependentDropDown(
                                            it
                                        )
                                    }
                                dropdownData.resData?.let {
                                    AppPreference.instance.setDependentDropdown(it)
                                }
                                dropdownSaved = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                                dropdownSaved = false
                            }
                        }
                    }
            }


            hideProgressBar()
            if (surveySaved == false || dropdownSaved == false) {
                AppUtils.showToast("Some files not able to download. Please try after some time")
            } else
                isAllDataSaved = true
        }
    }

    private fun dataNotSaved() {
        AppUtils.showToast("Some files not able to download. Please try after some time")
    }

    fun checkBreakFeatureAvailable() {
        RealmControler.getCreateSurveyBody()
            ?.getAppConfigData()?.homeButtons?.breakButton?.hide?.let {
                isBreakFeatureAvailable = it == 1
            }
    }

    companion object {
        fun open(activity: Activity) {
            Intent(activity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(this)
            }
        }
    }
}