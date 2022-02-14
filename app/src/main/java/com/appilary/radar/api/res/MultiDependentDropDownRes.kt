package com.appilary.radar.api.res

import com.google.gson.annotations.SerializedName


data class MultiDependentDropDownRes(
    @SerializedName("label")
    val label: String = "",
    @SerializedName("value")
    val value: String = "",
    @SerializedName("options")
    val options: List<MultiDependentDropDownRes>? = null
) {
    override fun toString(): String {
        return label
    }
}


