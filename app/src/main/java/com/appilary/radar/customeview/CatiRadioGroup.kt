package com.appilary.radar.customeview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.frag.BaseFragment
import com.appilary.radar.frag.SurveyFrag
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.OTHER
import java.util.*

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/28/16.
 */
class CatiRadioGroup : LinearLayout {
    lateinit var inflater: LayoutInflater
    var selectedRadio: RadioButton? = null
        private set
    var radioButtons = ArrayList<RadioButton>()
    var commentEntry = ArrayList<CatiTextEntry>()
    var otherTextEntry: CatiTextEntry? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
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

    private fun init(context: Context, attrs: AttributeSet?) {
        inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun addRadioView(
        title: String?,
        resAElement: ResAElement?,
        formField: FormField?,
        frag: BaseFragment?,
        isOtherText: Boolean = false
    ) {
        val view = inflater.inflate(R.layout.cati_radio_edit, null)
        //TODO:ankur creating a View programmatically will not have any layout params set, hence NPE!
//        if (getOrientation() == HORIZONTAL)
//            view.getLayoutParams().width = 150;
//        if (getOrientation() == HORIZONTAL){
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
//            view.setLayoutParams(params);
//        }


        if (isOtherText) {
            otherTextEntry = view.findViewById(R.id.other_cati_text_entry)
            otherTextEntry?.initTextEntry(formField, resAElement?.otherAns ?: "", "", View.GONE)
        }

        val radioButton = view.findViewById<View>(R.id.cati_radio) as RadioButton
        val catiTextEntry = view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        radioButton.isChecked = false
        radioButtons.add(radioButton)
        radioButton.setOnCheckedChangeListener(listener)
        radioButton.text = title
        radioButton.setTag(title)
        if (!TextUtils.isEmpty(formField?.firstRes)) {
            val lastTimeFirstResSaved = AppPreference.instance.lastTimeFirstResSaved
            if (!AppUtils.isSameDay(lastTimeFirstResSaved)) {
                if (frag is SurveyFrag)
                    frag.firstResFormField = formField
                radioButton.isEnabled = formField?.firstRes == title
            }
        }

        if (title == resAElement?.singleAns) radioButton.isChecked = true
        catiTextEntry.isEnabled = radioButton.isChecked
        addView(view)
    }

    var listener: CompoundButton.OnCheckedChangeListener =
        object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                for (radioButton in radioButtons) {
                    radioButton.setOnCheckedChangeListener(null)
                    radioButton.isChecked = false
                    //                View view = (View) radioButton.getTag(R.id.cati_edit);
//                if (view != null)
//                    view.setEnabled(false);
                    radioButton.setOnCheckedChangeListener(this)
                }
                selectedRadio = buttonView as RadioButton
                buttonView.setChecked(isChecked)

                if (buttonView.text.toString() == OTHER) {
                    otherTextEntry?.visibility = View.VISIBLE
                } else{
                    otherTextEntry?.visibility = View.GONE
                }
                //            View view = (View) buttonView.getTag(R.id.cati_edit);
//            if (view != null)
//                view.setEnabled(isChecked);
            }
        }

}