package com.appilary.radar.customeview

import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.bean.RetainValueBean
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.utils.AppPreference

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiTextView(model: FormField) : CatiCustomView(model) {

    private var retainBean: RetainValueBean? = null
    override fun setQuesLayout() {
        retainBean = AppPreference.instance.getRetainValueData()
        val view = addLayoutView()
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            val count = itemLayout.childCount
            for (i in 0 until count) {
                val layout = itemLayout.getChildAt(i)
                val value = layout.findViewById<EditText>(R.id.edit_text)
                val catiTextEntry = layout.findViewById<CatiTextEntry>(R.id.cati_text_entry)
                var text = value.text.toString()

                if (catiTextEntry != null && catiTextEntry.visibility == View.VISIBLE) {

                    val isValid = catiTextEntry.checkValidation()
                    if (!isValid)
                        return null
                    text = catiTextEntry.textValue ?: ""
                }

                if (text.trim().isNotEmpty()) {
                    val element = ResAElement(id)
                    element.singleAns = text
                    setRetainVal(text)
                    return element
                }
//
//            for (ResAElement item : list) {
//                String text = item.getText();
//                if (TextUtils.isEmpty(text))
//                    text = item.getRowText();
//
//                if (TextUtils.isEmpty(text))
//                    text = item.getColText();
//
//                try {
//                    totalItemVal += Double.parseDouble(text);
//                } catch (Exception e) {
//
//                }
//            }
//
//            Validation valid = formField.getValidation();
//            if (valid != null) {
//                double min = Double.MIN_VALUE;
//                double max = Double.MAX_VALUE;
//                try {
//                    min = Double.parseDouble(valid.getMin());
//                    max = Double.parseDouble(valid.getMax());
//                } catch (Exception e) {
//                }
//
//                if (totalItemVal < min || totalItemVal > max) {
//                    formField.setQuesLevelNotValid("Total item count should be in a range of " + min + " to " + max);
//                    return null;
//                } else
//                    formField.setQuesLevelNotValid(null);
//            }
//
            }
        }
        return null
    }

    private fun addLayoutView(): View {
        val view = inflater.inflate(R.layout.cati_item_text, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val catiTextEntry = view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText
        val editText = view.findViewById<View>(R.id.edit_text) as EditText
        editText.tag = formField.id
        if (formField.tooltip == 1)
            editText.setHint(formField.tooltipText)
        cheKRetainVal()
        if (formField.keypadType == 1) {
            catiTextEntry.initTextEntry(formField, resData?.singleAns ?: "", null)
            editText.visibility = View.GONE
        } else if (resData != null) {
            editText.setText(resData?.singleAns)
        }
        return view
    }

    private fun cheKRetainVal() {
        if (formField.retainValue == 1 && resData == null) {
            retainBean?.let {
                val selAns = it.valaueMap?.get("${id}_${formField.pageId}")
                if (selAns != null) {
                    val data = ResAElement(id)
                    data.singleAns = selAns
                    resData = data
                }
            }
        }
    }

    private fun setRetainVal(ans: String?) {
        if (formField.retainValue == 1) {
            val bean = AppPreference.instance.getRetainValueData() ?: RetainValueBean(valaueMap = HashMap())
            if (bean.valaueMap == null)
                bean.valaueMap = HashMap()
            bean.valaueMap?.put("${id}_${formField.pageId}", ans)
            AppPreference.instance.setRetainValueData(bean)
        }
    }
}