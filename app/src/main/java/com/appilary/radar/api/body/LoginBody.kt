package com.appilary.radar.api.body

import com.google.gson.annotations.SerializedName

/**
 * Created by vi.garg on 8/2/18.
 */
data class LoginBody(@SerializedName("username") val username: String,
                     @SerializedName("password") val password: String)