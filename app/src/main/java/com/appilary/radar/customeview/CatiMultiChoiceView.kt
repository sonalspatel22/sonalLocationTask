package com.appilary.radar.customeview

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.bean.RetainValueBean
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.utils.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiMultiChoiceView(model: FormField) : CatiCustomView(model) {
    private var preFilledValue: String? = null
    private var retainBean: RetainValueBean? = null

    override fun setQuesLayout() {
        retainBean = AppPreference.instance.getRetainValueData()
        val view = addLayoutView(formField.optList?.toMutableList())
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            val subType = formField.optype
            if (subType == OP_TYPE_RADIO_BUTTON) {
                val radioGroup =
                    itemLayout.findViewById<View>(R.id.cati_radio_group) as CatiRadioGroup
                val radioButton = radioGroup.selectedRadio
                if (radioButton != null) {
                    if (radioButton.tag != null) {
                        var otherView: CatiTextEntry? = null
                        val element = ResAElement(id)
                        val rowTitle = radioButton.tag as String
                        if (formField.ot == 1 && rowTitle == OTHER) {
                            otherView = radioGroup.otherTextEntry
                            otherView?.formField = formField
                            element.otherAns = otherView?.textValue
                            otherView?.checkValidation() //Todo
                            if (formField.validations?.mn == 1 && element.otherAns.isNullOrEmpty()) {
                                return null
                            }
                        }

                        element.singleAns = rowTitle
                        formField.quesAnswer?.let {
                            element.ansMatched = it == rowTitle
                        }

                        setRetainVal(element.singleAns, otherView?.textValue)
                        return element
                    }
                }
            } else if (subType == OP_TYPE_CHECKBOX) {
                val catiCheckBox =
                    itemLayout.findViewById<View>(R.id.cati_check_box) as CatiCheckBox
                val checkBoxes = catiCheckBox.selectedRadio
                val selectedItem = mutableListOf<String>()
                val element = ResAElement(id)
                checkBoxes.forEach {
                    if (it != null) {
                        val rowTitle = it.tag as String
                        selectedItem.add(rowTitle)

                        if (formField.ot == 1 && rowTitle == OTHER) {
                            val otherView = catiCheckBox.otherTextEntry
                            otherView?.formField = formField
                            element.otherAns = otherView?.textValue
                            otherView?.checkValidation() //Todo
                            if (formField.validations?.mn == 1 && element.otherAns.isNullOrEmpty()) {
                                return null
                            }
                        }

                    }
                }
                if (selectedItem.size > 0) {
                    element.multipleAns = selectedItem
                    return element
                }
            } else if (subType == OP_TYPE_DROPDOWN) {
                val spinner = itemLayout.findViewById<View>(R.id.cati_spinner) as Spinner
                val catiTextEntry =
                    itemLayout.findViewById<CatiTextEntry>(R.id.cati_text_entry)
                val pos = spinner.selectedItemPosition
                if (pos > 0) {
                    val element = ResAElement(id)
                    element.singleAns = spinner.selectedItem as String

                    if (formField.ot == 1 && element.singleAns == OTHER) {
                        catiTextEntry?.formField = formField
                        element.otherAns = catiTextEntry?.textValue
                        catiTextEntry?.checkValidation() //Todo
                        if (formField.validations?.mn == 1 && element.otherAns.isNullOrEmpty()) {
                            return null
                        }
                    }



                    setRetainVal(element.singleAns, catiTextEntry?.textValue)

                    return element
                }
            }
        }
        return null
    }

    private fun addLayoutView(list: MutableList<String>?): View {
        val view: View
        if (formField.optype == OP_TYPE_RADIO_BUTTON) {
            view = inflater.inflate(R.layout.cati_item_radio, null)
            addRadioOptionView(view, list)
        } else if (formField.optype == OP_TYPE_DROPDOWN) {
            view = inflater.inflate(R.layout.cati_choice_dropdown, null)
            addDropDownOptionView(view, list)
        } else {
            view = inflater.inflate(R.layout.cati_item_checkbox, null)
            addCheckBoxOptionView(view, list)
        }
        return view
    }

    private fun addRadioOptionView(layout: View, rowColList: MutableList<String>?) {
        val group = layout.findViewById<View>(R.id.cati_radio_group) as CatiRadioGroup
        //        if (formField.getProperties() != null && formField.getProperties().getAlign() != null) {
//            if (formField.getProperties().getAlign().equals(EnumClass.AlignType.horizontal.name()))
//                group.setOrientation(LinearLayout.HORIZONTAL);
//        }


        if (formField.ot == 1) {
            rowColList?.add(OTHER)
        }

        cheKRetainVal()

        rowColList?.let {
            val size = rowColList.size
            for (i in 0 until size) {
                val row = rowColList[i]
                group.addRadioView(
                    row,
                    resData,
                    formField,
                    fragment,
                    i == size - 1 && formField.ot == 1
                )
            }
        }

    }

    private fun addCheckBoxOptionView(layout: View, rowColList: MutableList<String>?) {
        val catiCheckBox = layout.findViewById<View>(R.id.cati_check_box) as CatiCheckBox
        //        if (formField.getProperties() != null && formField.getProperties().getAlign() != null) {
//            if (formField.getProperties().getAlign().equals(EnumClass.AlignType.horizontal.name()))
//                catiCheckBox.setOrientation(LinearLayout.HORIZONTAL);
//        }

        if (formField.ot == 1) {
            rowColList?.add(OTHER)
        }

        rowColList?.let {
            val size = rowColList.size
            for (i in 0 until size) {
                val row = rowColList[i]
                catiCheckBox.addCheckView(
                    row,
                    resData,
                    formField,
                    i == size - 1 && formField.ot == 1
                )
            }
        }

    }

    private fun addDropDownOptionView(layout: View, rowColList: List<String>?) {
        val spinner = layout.findViewById<View>(R.id.cati_spinner) as Spinner
        cheKRetainVal()
        val catiTextEntry = layout.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        var listSize = 0
        rowColList?.let {
            val list = ArrayList<String>()
            list.add("Please Select Value")
            list.addAll(rowColList)
            if (formField.ot == 1) {
                listSize = list.size
                list.add(OTHER)
            }
            var selectedVal = -1
            list.forEachIndexed { index, s ->
                if (s == resData?.singleAns) {
                    selectedVal = index

                    if (formField.ot == 1 && s == OTHER) {
                        catiTextEntry.initTextEntry(formField, resData?.otherAns ?: "", "")
                    }
                }
            }

            val categoryAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, list)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = categoryAdapter
            preFilledValue = if (selectedVal != -1) {
                spinner.setSelection(selectedVal)
                resData?.singleAns
            } else null
        }

        if (formField.ot == 1) {
            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == listSize) {
                        catiTextEntry.visibility = View.VISIBLE
                    } else {
                        catiTextEntry.visibility = View.GONE
                    }
                }
            }
        }
    }

    var dropDownListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View,
            position: Int,
            id: Long
        ) {
            val layout = view.parent.parent as LinearLayout
            val catiTextEntry =
                layout.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
            if (parent.tag != null && parent.tag is String) {
                val itemText = parent.getItemAtPosition(position).toString()
                val spinnerVal = parent.tag as String
                if (!TextUtils.isEmpty(itemText) && !TextUtils.isEmpty(
                        spinnerVal
                    ) && itemText == spinnerVal
                ) catiTextEntry.initTextEntry(
                    formField,
                    preFilledValue ?: "",
                    null
                ) else catiTextEntry.visibility = View.GONE
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun cheKRetainVal() {
        if (formField.retainValue == 1 && resData == null) {
            retainBean?.let {
                val selAns = it.valaueMap?.get("${id}_${formField.pageId}")
                if (selAns != null) {
                    val data = ResAElement(id)
                    data.singleAns = selAns
                    data.otherAns = it.otherValMap?.get("${id}_${formField.pageId}")
                    resData = data
                }
            }
        }
    }

    private fun setRetainVal(ans: String?, otherString: String? = null) {
        if (formField.retainValue == 1) {
            val bean = AppPreference.instance.getRetainValueData()
                ?: RetainValueBean(valaueMap = HashMap())
            if (bean.valaueMap == null)
                bean.valaueMap = HashMap()
            bean.valaueMap?.put("${id}_${formField.pageId}", ans)
            if (!otherString.isNullOrEmpty()) {
                if (bean.otherValMap == null)
                    bean.otherValMap = HashMap()
                bean.otherValMap?.put("${id}_${formField.pageId}", otherString)
            }

            AppPreference.instance.setRetainValueData(bean)
        }
    }

    companion object {
        private const val ALPHA_ORDER_ASC = "asc"
    }
}