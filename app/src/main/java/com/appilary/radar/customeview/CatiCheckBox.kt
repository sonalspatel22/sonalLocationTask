package com.appilary.radar.customeview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.utils.OTHER
import java.util.*

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/28/16.
 */
class CatiCheckBox : LinearLayout {
    lateinit var inflater: LayoutInflater
    var checkBoxs = ArrayList<CheckBox>()
    var selectedCheckBoxs = ArrayList<CheckBox?>()
    var commentEntry = ArrayList<CatiTextEntry>()
    var otherTextEntry: CatiTextEntry? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        inflater = getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun addCheckView(
        title: String?,
        resAElement: ResAElement?,
        formField: FormField,
        isOtherText: Boolean = false
    ) {
        val view = inflater.inflate(R.layout.cati_check_edit, null)
        //        if (getOrientation() == HORIZONTAL)
//            view.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));


        if (isOtherText) {
            otherTextEntry = view.findViewById(R.id.other_cati_text_entry)
            otherTextEntry?.initTextEntry(formField, resAElement?.otherAns ?: "", "", View.GONE)
        }

        val checkBox = view.findViewById<View>(R.id.cati_check) as CheckBox
        val catiTextEntry = view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        checkBox.isChecked = false
        checkBoxs.add(checkBox)
        //        if (row.isNA()) {
//            naCheckBox=checkBox;
//            checkBox.setOnCheckedChangeListener(naListener);
//        }
//        else
        checkBox.setOnCheckedChangeListener(listener)
        checkBox.text = title
        checkBox.tag = title
        if (resAElement?.multipleAns?.contains(title) == true) checkBox.isChecked = true
        addView(view)
    }

    var listener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            selectedCheckBoxs.remove(buttonView)
            //            View view = (View) buttonView.getTag(R.id.cati_edit);
//            if (view != null)
//                view.setEnabled(isChecked);
            if (isChecked) selectedCheckBoxs.add(buttonView as CheckBox)

            otherTextEntry?.visibility = View.GONE
            selectedCheckBoxs.forEach {
                if (it?.text?.toString() == OTHER) {
                    otherTextEntry?.visibility = View.VISIBLE
                    return@OnCheckedChangeListener
                }
            }
        }

    var naListener =
        CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                for (check in selectedCheckBoxs) {
                    check?.setOnCheckedChangeListener(null)
                    check?.isChecked = false
                    check?.setOnCheckedChangeListener(listener)
                }
                selectedCheckBoxs.clear()
            }
        }

    val selectedRadio: List<CheckBox?>
        get() = selectedCheckBoxs

}