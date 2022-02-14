package com.appilary.radar.customeview

import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.view.get
import com.appilary.radar.R
import com.appilary.radar.api.res.DependentDropDownData
import com.appilary.radar.api.res.DependentDropDownItem
import com.appilary.radar.api.res.FormField
import com.appilary.radar.api.res.MultiDependentDropDownRes
import com.appilary.radar.bean.RetainValueBean
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.utils.AppPreference

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiDependentDropdownView(model: FormField) : CatiCustomView(model) {
    private var preFilledValue: List<String>? = null
    var dropDownData: DependentDropDownData? = null
    private var retainBean: RetainValueBean? = null
    val spinnerList = mutableListOf<Spinner>()
    override fun setQuesLayout() {
        retainBean = AppPreference.instance.getRetainValueData()
        cheKRetainVal()
        preFilledValue = resData?.multipleAns

        formField.dependentDropdownKey?.let { key ->
            dropDownData = AppPreference.instance.getDependentDropdown(key)
        }

        formField.dependentDropdownLabel?.forEachIndexed { index, s ->
            val view = addLayoutView(index, s)
            itemLayout.addView(view)
        }
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            val errorList = formField.dependentDropdownErrMsg ?: emptyList()
            val ansList = mutableListOf<String>()
            val count = itemLayout.childCount
            for (i in 0 until count) {
                val view = itemLayout.get(i)
                val spinner = view.findViewById<Spinner>(R.id.cati_spinner)
                if (view.visibility == View.VISIBLE && spinner.selectedItemPosition > 0) {
                    val selectedItem = spinner.selectedItem as DependentDropDownItem
                    ansList.add(selectedItem.value)
                } else if (formField.validations?.mn == 1) {
                    if (errorList.size > i) {
                        val res = ResAElement(id)
                        res.errorMsg = errorList[i]
                        return res
                    } else
                        return null
                }
            }

            val element = ResAElement(id)
            element.multipleAns = ansList
            setRetainVal(ansList)
            return element
        }
        return null
    }

    private fun addLayoutView(index: Int, label: String): View {
        val view = inflater.inflate(R.layout.cati_dependent_dropdown, null)
        view.findViewById<TextView>(R.id.cati_tv).text = label
        addDropDownOptionView(view, index)
        return view
    }


    private fun addDropDownOptionView(layout: View, index: Int) {
        val spinner = layout.findViewById<View>(R.id.cati_spinner) as Spinner
        spinner.tag = index
        spinnerList.add(spinner)
        var list = emptyList<DependentDropDownItem>()
        if (index == 0) {
            dropDownData?.dropDownItemList?.let {
                list = it
            }
        }

        val categoryAdapter = ArrayAdapter<DependentDropDownItem>(
            mContext,
            android.R.layout.simple_spinner_item,
            list
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = categoryAdapter

        val ansList = preFilledValue
        if (!ansList.isNullOrEmpty()) {
            if (index < ansList.size) {
                var selectedVal = -1
                list.forEachIndexed { i, dependentDropDownItem ->
                    if (dependentDropDownItem.value == ansList[index]) {
                        selectedVal = i
                    }
                }

                if (selectedVal != -1)
                    spinner.setSelection(selectedVal)
            } else if (index != 0)
                layout.visibility = View.GONE
        } else if (index != 0)
            layout.visibility = View.GONE

        spinner.onItemSelectedListener = dropDownListener
    }

    var dropDownListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val spinnerIndex = parent?.tag
            if (spinnerIndex is Int) {
                valueChanged(spinnerIndex)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    fun valueChanged(spinnerIndex: Int) {
        if (spinnerIndex < spinnerList.size) {
            spinnerList.forEachIndexed { index, spinner ->
                spinner.parent?.let {
                    if (it is LinearLayout) {
                        if (index > spinnerIndex + 1) {
                            it.visibility = View.GONE
                        } else {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
            }

            val selectedItem = spinnerList[spinnerIndex].selectedItem as DependentDropDownItem
            if (spinnerIndex + 1 < spinnerList.size) {
                updateSpinnerList(
                    spinnerIndex + 1,
                    spinnerList[spinnerIndex + 1],
                    selectedItem.options ?: emptyList()
                )
            }
        }
    }

    fun updateSpinnerList(index: Int, spinner: Spinner, list: List<DependentDropDownItem>) {
        val categoryAdapter = ArrayAdapter<DependentDropDownItem>(
            mContext,
            android.R.layout.simple_spinner_item,
            list
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = categoryAdapter

        val ansList = preFilledValue
        if (!ansList.isNullOrEmpty()) {
            if (index < ansList.size) {
                var selectedVal = -1
                list.forEachIndexed { i, dependentDropDownItem ->
                    if (dependentDropDownItem.value == ansList[index]) {
                        selectedVal = i
                    }
                }

                if (selectedVal != -1) spinner.setSelection(selectedVal)
            }
        }
    }


    private fun cheKRetainVal() {
        if (formField.retainValue == 1 && resData == null) {
            retainBean?.let {
                val selAns = it.multiValaueMap?.get("${id}_${formField.pageId}")
                if (selAns != null) {
                    val data = ResAElement(id)
                    data.multipleAns = selAns
                    resData = data
                }
            }
        }
    }

    private fun setRetainVal(ans: List<String>?) {
        if (formField.retainValue == 1) {
            val bean = AppPreference.instance.getRetainValueData()
                ?: RetainValueBean(multiValaueMap = HashMap())
            if (bean.multiValaueMap == null)
                bean.multiValaueMap = HashMap()
            bean.multiValaueMap?.put("${id}_${formField.pageId}", ans)
            AppPreference.instance.setRetainValueData(bean)
        }
    }

}