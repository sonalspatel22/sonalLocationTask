package com.appilary.radar.database.survey

import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SurveySingleResData : RealmObject() {

    @PrimaryKey
    @SerializedName("uniqueId")
    var uniqueId = AppUtils.getUniqueId()

    @SerializedName("dt")
    var dt: Long = System.currentTimeMillis()

    @SerializedName("surveyId")
    var surveyId: Int = AppPreference.instance.surveyId

    @SerializedName("surveyTime")
    var surveyTime: Long = 0

    @SerializedName("lt")
    var lt: Double? = null

    @SerializedName("lg")
    var lg: Double? = null

    @SerializedName("appFormList")
    var appFormList: String? = null

    @SerializedName("totalDistance")
    var totalDistance: Float? = 0.0F

    fun setResPDataList(list: List<ResPElement>) {
        appFormList = Gson().toJson(list)
    }

    fun getResPList(): List<ResPElement>? {
        if (appFormList.isNullOrEmpty())
            return null

        return Gson().fromJson(appFormList, Array<ResPElement>::class.java).toList()
    }

}