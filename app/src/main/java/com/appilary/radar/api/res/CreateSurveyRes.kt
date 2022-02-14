package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class CreateSurveyRes(
    @SerializedName("appConfig")
    val appConfig: SurveyAppConfigData? = null,
    @SerializedName("appForms")
    val appForms: List<SurveyAppFormData>? = null
) : BaseErrorResponse()



