package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class AppVersionRes(
        @SerializedName("response")
        val versionData: AppVersionData? = null) : BaseErrorResponse()

data class AppVersionData(
        @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis(),
        @SerializedName("cm_min_version") val minVer: Int = 0,
        @SerializedName("cm_max_version") val currentVer: Int = 0,
        @SerializedName("message") val message: String? = null)