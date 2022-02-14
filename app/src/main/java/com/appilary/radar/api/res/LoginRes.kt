package com.appilary.radar.api.res

import android.os.Parcelable
import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class LoginRes(
    @SerializedName("response")
    val tokenClass: LoginData? = null
) : BaseErrorResponse()

@Parcelize
data class LoginData(
    @SerializedName("token") val token: String = "",
    @SerializedName("json_link") val jsonLink: String = "",
    @SerializedName("splash_img") val splashImg: String = "",
    @SerializedName("logo_img") val logoImg: String = "",
    @SerializedName("client_name") val clientName: String = "",
    @SerializedName("agreementFormId") var agreementFormId: Int = 0,
    @SerializedName("showOtpScreen") val showOtpScreen: Boolean = false
) : Parcelable