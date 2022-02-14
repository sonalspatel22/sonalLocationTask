package com.appilary.radar.database.survey

import com.google.gson.annotations.SerializedName

class ResAElement(
    @SerializedName("quesId")
    var quesId: Int

) {
    @SerializedName("singleAns")
    var singleAns: String? = null

    @SerializedName("ansMatched")
    var ansMatched: Boolean? = null

    @SerializedName("ansMultiChoice")
    var multipleAns: List<String>? = null //index from 1

    @SerializedName("ansGrid")
    var ansGrid: List<RowColTemp>? = null //"1-1": 100,	// row 1, column 1 value

    @SerializedName("fileUniqueId")
    var fileUniqueId: String? = null

    @SerializedName("filePath")
    var filePath: String? = null

    @SerializedName("errorMsg")
    var errorMsg: String? = null

    @SerializedName("otherAns")
    var otherAns: String? = null
}