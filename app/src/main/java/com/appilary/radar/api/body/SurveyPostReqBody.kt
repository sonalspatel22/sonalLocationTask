package com.appilary.radar.api.body

import com.appilary.radar.database.survey.ResPElement
import com.google.gson.annotations.SerializedName

data class SurveyPostReqBody(

    @SerializedName("uniqueId")
    var uniqueId: String,

    @SerializedName("surveyId")
    var surveyId: Int,

    @SerializedName("dt")
    var dt: Long,

    @SerializedName("surveyTime")
    var surveyTime: Long,

    @SerializedName("lt")
    var lt: Double? = null,

    @SerializedName("lg")
    var lg: Double? = null,

    @SerializedName("totalDistance")
    var totalDistance: Float? = 0.0F,

    @SerializedName("appFormList")
    var appFormList: List<ResPElement>? = null

)