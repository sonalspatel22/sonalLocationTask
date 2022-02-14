package com.appilary.radar.customeview

import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import java.util.*

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiStatementView(model: FormField) : CatiCustomView(model) {
    override fun setQuesLayout() {}
    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    val value: FormField?
        get() = null

    override suspend fun fillData(): ResAElement? {
        return null
    }
}