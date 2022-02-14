package com.appilary.radar.api.body

import com.google.gson.annotations.SerializedName

/**
 * Created by vi.garg on 8/2/18.
 */
data class LocUpdateBody(@SerializedName("lt") val lt: Double,
                         @SerializedName("lg") val lg: Double) {
    @SerializedName("dt")
    val dt: Long = System.currentTimeMillis()
}