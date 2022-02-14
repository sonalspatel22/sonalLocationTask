package com.appilary.radar.database.survey

import com.appilary.radar.utils.AppUtils
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class SurveyFileRealm : RealmObject() {

    @PrimaryKey
    var filePath: String? = null

    var uniqId: String = AppUtils.getUniqueId()

    var dt = System.currentTimeMillis()

    var surveyUniqId: String? = null

    var surveyId: Int? = null

    var pageId: Int? = null

    var quesId: Int? = null

    var lt: Double? = null

    var lg: Double? = null

    var totalDistance: Float? = null

}