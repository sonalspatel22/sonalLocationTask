package com.appilary.radar.api.res

import com.google.gson.annotations.SerializedName

data class SurveyAppConfigData(
    @SerializedName("id")
    val surveyId: Int = 0,
    @SerializedName("buttons")
    val homeButtons: AppConfigHomeButtons? = null,
    @SerializedName("apiUrls")
    val apiUrls: AppConfigApiUrl? = null,
    @SerializedName("logoConfig")
    val logoConfig: AppConfigLogoUrl? = null
)

data class AppConfigApiUrl(
    @SerializedName("surveyUploadUrl")
    val surveyUploadUrl: String? = null,
    @SerializedName("imageUploadUrl")
    val imageUploadUrl: String? = null,
    @SerializedName("jsonUpdateUrl")
    val jsonUpdateUrl: String? = null,
    @SerializedName("statsUrl")
    val statsUrl: String? = null,
    @SerializedName("validateOtpUrl")
    val validateOtpUrl: String? = null,
    @SerializedName("sendOtpUrl")
    val sendOtpUrl: String? = null,
    @SerializedName("offlineDropdownApi")
    val offlineDropdownApi: String? = null
)

data class AppConfigLogoUrl(
    @SerializedName("splashScreenUrl")
    val splashScreenUrl: String? = null,
    @SerializedName("clientLogoUrl")
    val clientLogoUrl: String? = null,
    @SerializedName("projectLogoUrl")
    val projectLogoUrl: String? = null
)

data class AppConfigHomeButtons(
    @SerializedName("report")
    val report: AppConfigButtonStatus? = null,
    @SerializedName("upload")
    val upload: AppConfigButtonStatus? = null,
    @SerializedName("home")
    val home: AppConfigButtonStatus? = null,
    @SerializedName("settings")
    val settings: AppConfigButtonStatus? = null,
    @SerializedName("break")
    val breakButton: AppConfigButtonStatus? = null
)

data class AppConfigButtonStatus(
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("hide")
    val hide: Int = 0,
    @SerializedName("optList")
    val optList: List<String>? = null
)