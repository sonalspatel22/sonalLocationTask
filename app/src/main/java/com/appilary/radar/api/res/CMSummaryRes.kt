package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class CMSummaryRes(
        @SerializedName("response")
        val resData: CMSummaryData? = null) : BaseErrorResponse()

data class CMSummaryData(@SerializedName("today")
                         val today: CMSummaryItem? = null,
                         @SerializedName("mtd")
                         val mtd: CMSummaryItem? = null)

data class CMSummaryItem(
        @SerializedName("target")
        val target: Int = 0,
        @SerializedName("completed")
        val completed: Int = 0,
        @SerializedName("remaining")
        val remaining: Int = 0)
