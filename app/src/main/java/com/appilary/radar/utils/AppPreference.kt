package com.appilary.radar.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.appilary.radar.App
import com.appilary.radar.R
import com.appilary.radar.api.body.BreakTimeBody
import com.appilary.radar.api.res.*
import com.appilary.radar.bean.DependentDropdownBean
import com.appilary.radar.bean.RetainValueBean
import com.google.gson.Gson

/**
 * Created by vivek.
 */
class AppPreference private constructor() {
    private val sharedPref: SharedPreferences

    private val PREF_NAME = "msa_app"
    private val AUTH_TOKEN = "auth_token"
    private val LOGIN_DATA = "login_data"
    private val USER_ID = "user_id"
    private val SURVEY_ID = "survey_id"
    private val DEPENDENT_DROPDOWN = "dependent_dropdown"
    private val PIN_NO = "pin_no"
    private val ROUTE_DATA = "route_data"
    private val INVENTORY_DATA = "inventory_data"
    private val FCM_TOKEN = "fcm_token"
    private val VER_API_TIME = "ver_api_time"
    private val DROPDOWN_DATA = "dropdown_data"
    private val LAST_TIME_DATA_SAVED = "last_time_data_saved"
    private val LAST_TIME_FIRST_RES_SAVED = "last_time_first_res_saved"
    private val LAST_TIME_ATTENDANCE = "last_time_attendence"
    private val DAY_END_SUBITTED = "day_end_submitted"
    private val VERSION_DATA = "version_data"
    private val LAST_LOC_SEND = "last_loc_send"
    private val RETAIN_VALUE = "retain_value"
    private val BREAK_TIME = "break_time"
    private val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
    private val TOTAL_DISTANCE = "total_distance"
    init {
        sharedPref = App.mInstance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun clearData() {
        val mEditor = sharedPref.edit()
        mEditor.clear()
        mEditor.apply()
    }

    private fun saveData(key: String, value: String) {
        val mEditor = sharedPref.edit()
        mEditor.putString(key, value)
        mEditor.commit()
    }

    private fun saveData(key: String, value: Float) {
        val mEditor = sharedPref.edit()
        mEditor.putFloat(key, value)
        mEditor.apply()
    }

    private fun saveData(key: String, value: Int) {
        val mEditor = sharedPref.edit()
        mEditor.putInt(key, value)
        mEditor.apply()
    }

    private fun saveData(key: String, value: Boolean) {
        val mEditor = sharedPref.edit()
        mEditor.putBoolean(key, value)
        mEditor.apply()
    }

    private fun saveData(key: String, value: Long) {
        val mEditor = sharedPref.edit()
        mEditor.putLong(key, value)
        mEditor.apply()
    }

    private fun saveData(key: String, value: Set<String>) {
        val mEditor = sharedPref.edit()
        mEditor.putStringSet(key, value)
        mEditor.apply()
    }

    private fun setServiceRunning(key: String,value: Boolean){
        val mEditor = sharedPref.edit()
        mEditor.putBoolean(key, value)
        mEditor.apply()
    }

    private fun getStringData(key: String, defValue: String?): String {
        return sharedPref.getString(key, defValue) ?: ""
    }

    private fun getIntData(key: String, defValue: Int): Int {
        return sharedPref.getInt(key, defValue)
    }

    private fun getBooleanData(key: String, defValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defValue)
    }

    private fun getFloatData(key: String, defValue: Float): Float {
        return sharedPref.getFloat(key, defValue)
    }

    private fun getLongData(key: String, defValue: Long): Long {
        return sharedPref.getLong(key, defValue)
    }

    private fun getStringArrayData(key: String, defValue: Set<String>): MutableSet<String>? {
        return sharedPref.getStringSet(key, defValue)
    }

    //****************************************************************//

    var authToken: String
        get() = getStringData(AUTH_TOKEN, "")
        set(token) = saveData(AUTH_TOKEN, token ?: "")

    //****************************************************************//

    var pinNo: String
        get() = getStringData(PIN_NO, "")
        set(pinNo) = saveData(PIN_NO, pinNo ?: "")

    //****************************************************************//

    var userId: String
        get() = getStringData(USER_ID, "")
        set(userId) = saveData(USER_ID, userId ?: "")

    //****************************************************************//

    var surveyId: Int
        get() = getIntData(SURVEY_ID, 0)
        set(surveyId) = saveData(SURVEY_ID, surveyId)

    //****************************************************************//

    var fcmToken: String
        get() = getStringData(FCM_TOKEN, "")
        set(token) = saveData(FCM_TOKEN, token ?: "")

    //****************************************************************//

    var verApiTime: Long
        get() = getLongData(VER_API_TIME, 0)
        set(time) = saveData(VER_API_TIME, time)

    //****************************************************************//

    var lastTimeDataSaved: Long
        get() = getLongData(LAST_TIME_DATA_SAVED, 0)
        set(lastTimeDataSaved) = saveData(LAST_TIME_DATA_SAVED, lastTimeDataSaved)


    //****************************************************************//

    var lastLocSend: Long
        get() = getLongData(LAST_LOC_SEND, 0)
        set(lastLocSend) = saveData(LAST_LOC_SEND, lastLocSend)

    //****************************************************************//

    var lastTimeAttendence: Long
        get() = getLongData(LAST_TIME_ATTENDANCE, 0)
        set(time) {
            isDayEndSubimitted = false
            saveData(LAST_TIME_ATTENDANCE, time)
        }

    //****************************************************************//

    var lastTimeFirstResSaved: Long
        get() = getLongData(LAST_TIME_FIRST_RES_SAVED, 0)
        set(time) {
            saveData(LAST_TIME_FIRST_RES_SAVED, time)
        }

    //****************************************************************//

    var isDayEndSubimitted: Boolean
        get() = getBooleanData(DAY_END_SUBITTED, false)
        set(isDayEndSubimitted) = saveData(DAY_END_SUBITTED, isDayEndSubimitted)

    //****************************************************************//

    fun setLoginData(data: LoginData?) {
        if (data != null)
            saveData(LOGIN_DATA, Gson().toJson(data))
    }

    fun getLoginData(): LoginData? {
        val data = getStringData(LOGIN_DATA, "")
        return Gson().fromJson<LoginData>(data, LoginData::class.java)
    }
    //****************************************************************//

    fun setRouteRes(data: RouteResData?) {
        if (data != null)
            lastTimeDataSaved = System.currentTimeMillis()
        saveData(ROUTE_DATA, Gson().toJson(data))
    }

    fun getRouteResData(): RouteResData? {
        val data = getStringData(ROUTE_DATA, "")
        return Gson().fromJson<RouteResData>(data, RouteResData::class.java)
    }

    //****************************************************************//

    fun setInventoryData(data: InventoryData?) {
        saveData(INVENTORY_DATA, Gson().toJson(data))
    }

    fun getInventoryData(): InventoryData? {
        val data = getStringData(INVENTORY_DATA, "")
        return Gson().fromJson<InventoryData>(data, InventoryData::class.java)
    }

    //****************************************************************//

    fun setDropDownData(data: DropDownRes) {
        if (data.resData != null)
            saveData(DROPDOWN_DATA, Gson().toJson(data))
    }

    fun getDropDownData(): DropDownRes? {
        val data = getStringData(DROPDOWN_DATA, "")
        if (data.isNullOrEmpty())
            return null

        return Gson().fromJson<DropDownRes>(data, DropDownRes::class.java)
    }

    //****************************************************************//

    fun setDependentDropdown(data: List<DependentDropDownData>) {
        val beanString = getStringData(DEPENDENT_DROPDOWN, null)
        val bean: DependentDropdownBean
        if (beanString.isNullOrEmpty())
            bean = DependentDropdownBean()
        else
            bean = Gson().fromJson(beanString, DependentDropdownBean::class.java)

        val map = bean.dependentMap ?: HashMap()
        data.forEach {
            map.put(it.key, it)
        }
        bean.dependentMap = map
        saveData(DEPENDENT_DROPDOWN, Gson().toJson(bean))
    }

    fun getDependentDropdown(key: String): DependentDropDownData? {
        val data = getStringData(DEPENDENT_DROPDOWN, null)
        if (data.isNullOrEmpty())
            return null

        val bean = Gson().fromJson(data, DependentDropdownBean::class.java)

        return bean.dependentMap?.get(key)
    }

    fun isDependentDropdownBeanExist(): DependentDropdownBean? {
        val data = getStringData(DEPENDENT_DROPDOWN, null)
        if (data.isNullOrEmpty())
            return null

        return Gson().fromJson(data, DependentDropdownBean::class.java)
    }

    fun emptyDependentDropdown() {
        saveData(DEPENDENT_DROPDOWN, "")
    }

    //****************************************************************//

    fun setVersionData(data: AppVersionData?) {
        verApiTime = System.currentTimeMillis()
        if (data != null)
            saveData(VERSION_DATA, Gson().toJson(data))
    }

    fun getVersionData(): AppVersionData? {
        val data = getStringData(VERSION_DATA, "")
        if (data.isNullOrEmpty())
            return null

        return Gson().fromJson<AppVersionData>(data, AppVersionData::class.java)
    }


    //****************************************************************//

    fun setRetainValueData(data: RetainValueBean?) {
        if (data == null) {
            saveData(RETAIN_VALUE, Gson().toJson(data))
            return
        }
        var bean = getRetainValueData()
        if (bean == null)
            bean = data
        else {
            bean.multiValaueMap?.putAll(data.multiValaueMap ?: emptyMap())
            bean.valaueMap?.putAll(data.valaueMap ?: emptyMap())
            bean.otherValMap?.putAll(data.otherValMap ?: emptyMap())
        }
        saveData(RETAIN_VALUE, Gson().toJson(data))
    }

    fun getRetainValueData(): RetainValueBean? {
        val data = getStringData(RETAIN_VALUE, "")
        if (data.isEmpty())
            return null

        return Gson().fromJson(data, RetainValueBean::class.java)
    }

    //****************************************************************//

    fun saveBreakTimeData(body: BreakTimeBody) {
        val dataList = getBreakTimeData()

        val mutableList = mutableListOf<BreakTimeBody>()
        if (dataList != null) {
            mutableList.addAll(dataList)
        }

        mutableList.remove(body)
        mutableList.add(body)

        saveBreakTimeData(mutableList)
    }

    fun getBreakTimeData(): List<BreakTimeBody>? {
        val data = getStringArrayData(BREAK_TIME, emptySet())

        if (data.isNullOrEmpty())
            return null

        return data.map { Gson().fromJson(it, BreakTimeBody::class.java) }
    }

    fun removeBreakTime(list: List<BreakTimeBody>) {
        val data = getBreakTimeData()
        val newList = mutableListOf<BreakTimeBody>()
        data?.forEach {
            if (!list.contains(it))
                newList.add(it)
        }

        saveBreakTimeData(newList)
    }

    private fun saveBreakTimeData(body: List<BreakTimeBody>?) {
        val set = mutableSetOf<String>()
        body?.forEach {
            set.add(Gson().toJson(it))
        }

        saveData(BREAK_TIME, set)
    }

    //****************************************************************//

    companion object {
        val instance: AppPreference
            get() {
                return AppPreference()
            }

    }

    //****************************************************************//

    var isServiceIsRunnig: Boolean
        get() = getBooleanData(KEY_FOREGROUND_ENABLED, false)
        set(isServiceIsRunnig) = saveData(KEY_FOREGROUND_ENABLED, isServiceIsRunnig)

    //****************************************************************//

    var totalDistance: Float
        get() = getFloatData(TOTAL_DISTANCE, 0.0F)
        set(totalDistance) = saveData(TOTAL_DISTANCE, totalDistance)

}

