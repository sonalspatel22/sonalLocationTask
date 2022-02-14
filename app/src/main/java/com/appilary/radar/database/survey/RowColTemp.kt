package com.appilary.radar.database.survey

import com.google.gson.annotations.SerializedName

class RowColTemp {

    @SerializedName("rowNo")
    var rowNo: Int = 0

    @SerializedName("colNo")
    var colNo: Int = 0

    @SerializedName("ans")
    var ans: String? = null
}