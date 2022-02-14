package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class DependentDropDownRes(
    @SerializedName("response")
    val resData: List<DependentDropDownData>? = null
) : BaseErrorResponse()

data class DependentDropDownData(
    @SerializedName("key")
    val key: String = "",
    @SerializedName("dropDownItemList")
    val dropDownItemList: List<DependentDropDownItem>? = null
)

data class DependentDropDownItem(
    @SerializedName("label")
    val label: String = "",
    @SerializedName("value")
    val value: String = "",
    @SerializedName("options")
    val options: List<DependentDropDownItem>? = null,
    @SerializedName("otherDetails")
    val otherDetails: OtherDetails? = null
) {
    override fun toString(): String {
        return label
    }
}

data class OtherDetails(
    val contactNo: String,
    val htmlText: String,
    val lg: Double,
    val lt: Double,
    val showMapIcon: Boolean
)
