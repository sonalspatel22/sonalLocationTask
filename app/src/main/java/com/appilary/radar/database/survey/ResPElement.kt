package com.appilary.radar.database.survey

import com.google.gson.annotations.SerializedName

class ResPElement(
    @SerializedName("pageId")
    var pageId: Int//page id - API
) {
    @SerializedName("quesList")
    var quesList: List<ResAElement>? = null

    override fun equals(other: Any?): Boolean {
        return other is Int && pageId == other
    }

}