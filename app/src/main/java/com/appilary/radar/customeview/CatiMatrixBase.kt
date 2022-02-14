package com.appilary.radar.customeview

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.appilary.radar.api.res.FormField
import com.appilary.radar.utils.UiUtils

/**
 * Created by vi.garg on 31/5/16.
 */
abstract class CatiMatrixBase(model: FormField) : CatiCustomView(model) {
    protected var rowTextEntryMap = mutableMapOf<String, CatiTextEntry>()
    protected var colTextEntryMap = mutableMapOf<String, CatiTextEntry>()
    protected var itemLayoutParam: LinearLayout.LayoutParams? = null
    protected var rowWidth = 0
    protected var colSize = 0
    protected var isRowEnable = true
    protected var isColEnable = true
    protected var isColTop = true
    protected var displayAreaWidth = 0
    abstract fun addMatrixBodyItem(col: String?, row: String?, rowNum: Int, colNum: Int): View
    override fun setQuesLayout() {
        val screenWidth: Int = UiUtils.getScreenWidth(mContext)
        displayAreaWidth = screenWidth
        rowWidth = UiUtils.dpToPx(130)
        val row = formField.optRow ?: emptyList()
        val col = formField.optCol ?: emptyList()
        val rowSize = row.size
        colSize = col.size
        var columnSize = colSize
        if (isRowEnable) columnSize += 1
        val dpToPx10: Int = UiUtils.dpToPx(10)
        val dpToPx50: Int = UiUtils.dpToPx(50)
        displayAreaWidth = screenWidth - 2 * dpToPx10
        rowWidth = screenWidth / columnSize - 2 * dpToPx10
        if (rowWidth < dpToPx50) rowWidth = dpToPx50
        itemLayoutParam =
            LinearLayout.LayoutParams(rowWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
        for (i in 0..rowSize) {
            val linearLayout = LinearLayout(mContext)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER_VERTICAL
            if (i == 0) {
                if (isColEnable) addFirstRow(linearLayout, col) else continue
            } else {
                var isRowAdded = false
                addOtherRow(linearLayout, col, row[i - 1], i, isRowAdded)
            }
            itemLayout.addView(linearLayout)
        }
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    protected fun addFirstRow(
        linearLayout: LinearLayout,
        col: List<String>
    ) {
        for (i in 0..col.size) {
            if (i == 0) {
                if (isRowEnable) linearLayout.addView(getRowColHeading(null, true)) else continue
            } else {
                val rowCol = col[i - 1]
                linearLayout.addView(getRowColHeading(rowCol, true))
            }
        }
    }

    protected fun addOtherRowFirstCol(linearLayout: LinearLayout, row: String) {
        val view: View
        var title: String? = null
//        if (resData != null && resData!!.contains(row)) {
//            for (resAE in resData!!) if (resAE.equals(row)) {
//                if (resAE.getRowId() == null) continue
//                if (resAE.getRowId().equals(row.getId())) {
//                    title = resAE.getText()
//                    if (TextUtils.isEmpty(title)) title = resAE.getRowText()
//                    if (TextUtils.isEmpty(title)) title = resAE.getColText()
//                    break
//                }
//            }
//        }

        val textView = getRowColHeading(row, false)
        view = textView
        linearLayout.addView(view)
    }

    private fun addOtherRow(
        linearLayout: LinearLayout,
        col: List<String>,
        row: String,
        rowNum: Int,
        isRowAdded: Boolean
    ) {
        for (i in 0..col.size) {
            if (i == 0) {
                if (isRowEnable) {
                    if (!isRowAdded) addOtherRowFirstCol(linearLayout, row)
                } else continue
            } else {
                val colLastItem = col[i - 1]
                linearLayout.addView(addMatrixBodyItem(colLastItem, row, rowNum, i))
            }
        }
    }

    private fun getRowColHeading(row: String?, isFirst: Boolean): TextView {
        val textView = TextView(mContext)
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (row == null) textView.text = ""
        textView.setText(row)
        textView.layoutParams = itemLayoutParam
        return textView
    }


    fun addMatrixBodyNAItem(col: String?, row: String?, rowNum: Int, colNum: Int): View {
        return addMatrixBodyItem(col, row, rowNum, colNum)
    }
}