package com.appilary.radar.frag

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.appilary.radar.R
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.activities.LocationUpdated
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.BreakStartTimeAction
import com.appilary.radar.api.body.BreakTimeBody
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.database.RealmControler
import com.appilary.radar.utils.ApiCallUtils
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import kotlinx.android.synthetic.main.frag_break_time.*


class BreakTimeFrag : BaseFragment() {

    var breakStarted: Long = 0
    var lt: Double? = null
    var lg: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreference.instance.getBreakTimeData()?.firstOrNull { it.endDt == null }?.let {
            breakStarted = it.startDt
        }

        if (mainActivity is BaseActivity) {
            (mainActivity as BaseActivity).findLocation(object :
                LocationUpdated {
                override fun onLocCapture(lat: Double?, lang: Double?, address: String?) {
                    lt = lat
                    lg = lang
                }
            })
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_break_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateValues(false)
        if (breakStarted > 0) {
            val timeDiff = System.currentTimeMillis() - breakStarted
            chronometerTv.base = SystemClock.elapsedRealtime() - timeDiff
            chronometerTv.start()
            updateValues(true)
        }

        setSpinnerVal()
        startBtn.setOnClickListener {
            if (spin_reason.selectedItemPosition == 0) {
                AppUtils.showToast("Please select reason")
                return@setOnClickListener
            }
            breakStarted = System.currentTimeMillis()

            BreakTimeBody(
                startDt = breakStarted,
                reason = spin_reason.selectedItem as String,
                lt = lt,
                lg = lg
            ).apply {
                AppPreference.instance.saveBreakTimeData(this)
                callApi(BreakStartTimeAction(body = this), false)
            }
            updateValues(true)
            chronometerTv.base = SystemClock.elapsedRealtime()
            chronometerTv.start()
        }

        endBtn.setOnClickListener {
            val list = AppPreference.instance.getBreakTimeData()
            list?.firstOrNull { it.endDt == null }?.also {
                it.endDt = System.currentTimeMillis()
                it.lt = lt
                it.lg = lg
                AppPreference.instance.saveBreakTimeData(it)
            }
            ApiCallUtils.breakApiData()
            chronometerTv.stop()
            chronometerTv.base = SystemClock.elapsedRealtime()
            updateValues(false)
        }
    }

    private fun setSpinnerVal() {
        val reasonList = mutableListOf("Please Select Reason")

        val list = RealmControler.getCreateSurveyBody()
            ?.getAppConfigData()?.homeButtons?.breakButton?.optList ?: emptyList()

        reasonList.addAll(list)

        val reasonAdapter = ArrayAdapter(
            mainActivity,
            android.R.layout.simple_spinner_item,
            reasonList
        )
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_reason.adapter = reasonAdapter
    }

    fun updateValues(isInBreak: Boolean) {
        if (!isInBreak) {
            start_ll.visibility = View.VISIBLE
            end_ll.visibility = View.GONE
        } else {
            start_ll.visibility = View.GONE
            end_ll.visibility = View.VISIBLE
        }
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    fun isInBetweenBreak(): Boolean {
     return  AppPreference.instance.getBreakTimeData()?.firstOrNull { it.endDt == null } != null
    }

    companion object {
        val TAG = BreakTimeFrag::class.java.simpleName

        @JvmStatic
        fun newInstance(): BreakTimeFrag {
            val fragment = BreakTimeFrag()
            return fragment
        }
    }

}