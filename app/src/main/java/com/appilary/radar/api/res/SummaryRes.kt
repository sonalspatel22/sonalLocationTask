package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class SummaryRes(
    @SerializedName("response")
    val resData: List<SummaryResData>? = null
) : BaseErrorResponse()

data class SummaryResData(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("summaryList")
    val summaryList: List<SummaryListItem>? = null
)

data class SummaryListItem(
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("value")
    val value: String = "0"
)


