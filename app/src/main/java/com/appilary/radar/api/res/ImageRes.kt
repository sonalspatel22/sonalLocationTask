package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

/**
 * Created by ma.kumar on 18/01/18.
 */
open class ImageRes(@SerializedName("response")
                    var imgId: String? = null) : BaseErrorResponse()