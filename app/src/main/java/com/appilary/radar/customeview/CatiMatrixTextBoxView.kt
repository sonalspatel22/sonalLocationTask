package com.appilary.radar.customeview

import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.database.survey.RowColTemp
import com.appilary.radar.utils.KEYPAD_TYPE_DECIMAL_NUMBER
import com.appilary.radar.utils.KEYPAD_TYPE_INT
import com.appilary.radar.utils.KEYPAD_TYPE_WHOLE_NUMBER
import java.util.*

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiMatrixTextBoxView(model: FormField) : CatiMatrixBase(model) {
    override fun setQuesLayout() {
        super.setQuesLayout()
    }

    override fun addMatrixBodyItem(col: String?, row: String?, rowNum: Int, colNum: Int): View {
        val edittext = EditText(mContext)
        edittext.layoutParams = itemLayoutParam
        edittext.setPadding(5, 5, 5, 5)
        edittext.textSize = 14f
        val drawable = mContext.resources.getDrawable(R.drawable.border_black)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            edittext.setBackgroundDrawable(drawable)
        } else {
            edittext.background = drawable
        }

        resData?.ansGrid?.forEach {
            if (it.colNo == colNum && it.rowNo == rowNum) {
                edittext.setText(it.ans)
            }
        }

        keyboardType(edittext)
        edittext.setTag(R.id.column_id, colNum)
        edittext.setTag(R.id.row_id, rowNum)
        return edittext
    }


    override suspend fun fillData(): ResAElement? {
        val list: MutableList<RowColTemp> = ArrayList()
        if (quesTv != null) {
            val count = itemLayout.childCount
            val rowList = formField.optRow ?: emptyList()
            val colList = formField.optCol ?: emptyList()
            for (i in 0 until count) {
                val tableRow = itemLayout.getChildAt(i) as LinearLayout
                for (j in 0 until tableRow.childCount) {
                    val view = tableRow.getChildAt(j)
                    if (view is EditText) {
                        val edittext = view
                        val isChecked = edittext.text.toString().isNotEmpty()
                        if (isChecked) {
                            val rowCol = RowColTemp()
                            val colTag = edittext.getTag(R.id.column_id) as Int
                            rowCol.colNo = colTag
                            val rowTag = edittext.getTag(R.id.row_id) as Int
                            rowCol.rowNo = rowTag
//                                if (rowTextEntryMap.containsKey(rowTag) && !TextUtils.isEmpty(rowTextEntryMap[rowTag]!!.textValue))
//                                element.setRowText(rowTextEntryMap[rowTag]!!.textValue)
//                                if (colTextEntryMap.containsKey(colTag) && !TextUtils.isEmpty(colTextEntryMap[colTag]!!.textValue))
//                                element.setColText(colTextEntryMap[colTag]!!.textValue)
                            rowCol.ans = edittext.text.toString()
                            list.add(rowCol)
                        }
                    } else if (view is CatiTextEntry) {
                        val catiTextEntry = view
                        val colTag = catiTextEntry.getTag(R.id.column_id) as Int
                        val rowTag = catiTextEntry.getTag(R.id.row_id) as Int
                        val isValid: Boolean = catiTextEntry.checkValidation()
                        if (!isValid) return null
                        if (!catiTextEntry.textValue.isNullOrEmpty()) {
                            val rowCol = RowColTemp()
                            rowCol.colNo = colTag
                            rowCol.rowNo = rowTag
                            rowCol.ans = catiTextEntry.textValue
                            list.add(rowCol)
                        }
                    } /*else if (view is LinearLayout) {
                            val catiTextEntry = view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
                            if (catiTextEntry != null) {
                                val rowTag = catiTextEntry.getTag(R.id.row_id) as Int
//                                if (currentItem != null) {
                                    val isValid: Boolean = catiTextEntry.checkValidation()
                                    if (!isValid) return null
//                                }
                                if (!catiTextEntry.textValue.isNullOrEmpty()) {
                                    val rowCol = RowColTemp()
                            rowCol.colNo = colTag
                            rowCol.rowNo = rowTag
//                                    element.setText(catiTextEntry.textValue)
//                                    list.add(element)
//                                }
                            }
                    }*/
                }
            }
        }

        if (list.isEmpty())
            return null
        else {
            return ResAElement(id).apply {
                ansGrid = list
            }
        }
    }


    fun keyboardType(editText: EditText) {
        val form = formField
        val valid = form.validations
        val subType = formField.keypadType
        if (subType == KEYPAD_TYPE_DECIMAL_NUMBER) {
            editText.setKeyListener(DigitsKeyListener.getInstance(true, true))
        } else if (subType == KEYPAD_TYPE_INT) {
            editText.setKeyListener(DigitsKeyListener.getInstance(true, false))
        } else if (subType == KEYPAD_TYPE_WHOLE_NUMBER) {
            editText.setKeyListener(DigitsKeyListener.getInstance(false, false))
        } else/* if (subType == EnumClass.ValidationType.email.name())*/ {
            editText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        }

        valid?.len?.let {
            val minMaxArr = it.split("-")
            var maxLen = Int.MAX_VALUE
            try {
                maxLen = minMaxArr[1].toInt()
            } catch (e: Exception) {
            }
            editText.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLen)))
        }

    }


}