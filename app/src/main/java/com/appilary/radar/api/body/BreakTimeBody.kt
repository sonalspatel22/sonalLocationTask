package com.appilary.radar.api.body

import com.appilary.radar.utils.AppUtils
import com.google.gson.annotations.SerializedName

/**
 * Created by vi.garg on 8/2/18.
 */
data class BreakTimeBody(
    @SerializedName("id") val id: String = AppUtils.getUniqueId(),
    @SerializedName("start_dt") val startDt: Long,
    @SerializedName("end_dt") var endDt: Long? = null,
    @SerializedName("reason") var reason: String,
    @SerializedName("lt") var lt: Double? = null,
    @SerializedName("lg") var lg: Double? = null
) {
    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (other is BreakTimeBody) {
            return id == other.id
        }
        return super.equals(other)
    }
}