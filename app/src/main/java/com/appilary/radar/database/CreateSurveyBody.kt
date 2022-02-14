package com.appilary.radar.database

import android.text.TextUtils
import com.appilary.radar.api.res.SurveyAppConfigData
import com.appilary.radar.api.res.SurveyAppFormData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CreateSurveyBody(
    @PrimaryKey
    @SerializedName("formUrl")
    private var formUrl: String = "formUrl",

    @SerializedName("appConfig")
    private var appConfig: String? = null,

    @SerializedName("appForms")
    private var appForms: String? = null

) : RealmObject() {

    fun getAppConfigData(): SurveyAppConfigData? {
        if (TextUtils.isEmpty(appConfig))
            return null
        else
            return Gson().fromJson(appConfig, SurveyAppConfigData::class.java)
    }

    fun getAppFormData(): List<SurveyAppFormData>? {
        if (TextUtils.isEmpty(appForms))
            return null
        else
            return Gson().fromJson(appForms, Array<SurveyAppFormData>::class.java)?.asList()
    }

    fun setAppConfigData(appConfigObj : SurveyAppConfigData) {
        appConfig = Gson().toJson(appConfigObj)
    }

    fun setFormDataList(appformList : List<SurveyAppFormData>) {
        appForms = Gson().toJson(appformList)
    }
}