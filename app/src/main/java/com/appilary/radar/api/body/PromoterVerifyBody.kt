package com.appilary.radar.api.body

import com.google.gson.annotations.SerializedName

/**
 * Created by vi.garg on 8/2/18.
 */
data class PromoterVerifyBody(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("code") val otpCode: String? = null,
    val auth: String? = null
)