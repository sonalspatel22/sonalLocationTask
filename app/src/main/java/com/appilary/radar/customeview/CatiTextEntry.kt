package com.appilary.radar.customeview

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.utils.*
import kotlinx.android.synthetic.main.cati_text_entry.view.*
import java.util.*
import java.util.regex.Pattern

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/28/16.
 */
class CatiTextEntry : LinearLayout {
    var formField: FormField?= null
    private var timestamp: Long = 0

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
        View.inflate(context, R.layout.cati_text_entry, this)
        //        LayoutInflater inflater = (LayoutInflater) getContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.cati_text_entry, this, true);
//        ButterKnife.bind(this, view);
    }

    fun initTextEntry(
        form: FormField?,
        value: String,
        title: String?,
        visibilityView : Int = View.VISIBLE
    ) {
        visibility = visibilityView
        formField = form
        val valid = form?.validations
        if (!TextUtils.isEmpty(value)) catiEdit.setText(value)
        if (form?.tooltip == 1) catiEdit.setHint(form.tooltipText)
        if (!TextUtils.isEmpty(title)) {
            catiText.setVisibility(View.VISIBLE)
            catiText.setText(title)
        }
//        if (validation == null) return
        val subType = formField?.keypadType
        if (form?.optype == OP_TYPE_DATE || form?.optype == OP_TYPE_TIME) {
            catiEdit.setFocusable(false)
            if (!TextUtils.isEmpty(value)) {
                var timestamp: Long = 0
                try {
                    timestamp = value.toLong()
                } catch (e: Exception) {
                }
                if (timestamp != 0L) {
                    if (form.optype == OP_TYPE_DATE)
                        catiEdit.setText(
                            AppUtils.getDateInternational(timestamp)
                        ) else catiEdit.setText(AppUtils.getTime(timestamp))
                    this@CatiTextEntry.tag = timestamp
                }
            }
            dateTime.setVisibility(View.VISIBLE)
            dateTime.setOnClickListener {
                if (form.optype == OP_TYPE_DATE)
                    showDatePicker(false)
                else
                    showTimePicker("")
            }
        } else if (subType == KEYPAD_TYPE_DECIMAL_NUMBER) {
            catiEdit.setKeyListener(DigitsKeyListener.getInstance(true, true))
        } else if (subType == KEYPAD_TYPE_INT) {
            catiEdit.setKeyListener(DigitsKeyListener.getInstance(true, false))
        } else if (subType == KEYPAD_TYPE_WHOLE_NUMBER) {
            catiEdit.setKeyListener(DigitsKeyListener.getInstance(false, false))
        } else/* if (subType == EnumClass.ValidationType.email.name())*/ {
            catiEdit.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            catiEdit.maxLines = 1
        }

        valid?.len?.let {
            val minMaxArr = it.split("-")
            var maxLen = Int.MAX_VALUE
            try {
                maxLen = minMaxArr[1].toInt()
            } catch (e: Exception) {
            }
            catiEdit.filters = arrayOf<InputFilter>(LengthFilter(maxLen))
        }

        catiEdit.imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        catiEdit.setEnabled(enabled)
        dateTime.setEnabled(enabled)
    }

    fun checkValidation(): Boolean {
        catiTextError.setVisibility(View.GONE)
        if (visibility != View.VISIBLE && !isEnabled) return true
        if (TextUtils.isEmpty(catiEdit.getText().toString())) return true
        val validation = formField?.validations
        if (validation == null) return true

        if (!validation.len.isNullOrEmpty()) {
            val text = catiEdit.getText().toString()
            val lenSplit = validation.len.split("-")
            var min = -Int.MAX_VALUE
            var max = Int.MIN_VALUE
            try {
                min = lenSplit[0].toInt()
                max = lenSplit[1].toInt()
            } catch (e: Exception) {
            }

            if (!(text.length >= min && text.length <= max)) {
                catiTextError.setVisibility(View.VISIBLE)
                catiTextError.setText(formField?.validationsMsg?.len)
                return false
            }
        }

        if (validation.vtype != null && validation.vtype > 0) {
            val regexString = vtypeRegexMap[validation.vtype]
            val text = catiEdit.getText().toString()
            val isMatched = Pattern.compile(regexString).matcher(text).matches()
            if (!isMatched) {
                catiTextError.setVisibility(View.VISIBLE)
                catiTextError.setText(formField?.validationsMsg?.vtype)
                return false
            }
        }

        return true
    }

    val textValue: String?
        get() {
            if (visibility != View.VISIBLE) return null
            return if (formField?.validations != null &&
                (formField?.optype == OP_TYPE_DATE
                        || formField?.optype == OP_TYPE_TIME)
            ) {
                if (timestamp != 0L) timestamp.toString() else null
            } else catiEdit.getText().toString() //Todo check
        }

    private fun showDatePicker(isWithTime: Boolean) {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            context,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar[year, monthOfYear] = dayOfMonth
                timestamp = calendar.timeInMillis
                val value =
                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                if (isWithTime) showTimePicker(value) else catiEdit.setText(value)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
//        try {//Todo check
//            val minValue = validation!!.min!!.toLong()
//            val maxValue = validation!!.max!!.toLong()
//            val datePicker = datePickerDialog.datePicker
//            datePicker.minDate = minValue
//            datePicker.maxDate = maxValue
//        } catch (e: Exception) {
//        }
    }

    private fun showTimePicker(dateString: String) {
        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            context,
            OnTimeSetListener { view, hourOfDay, minute ->
                val time = " $hourOfDay:$minute"
                catiEdit.setText(dateString + time)
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }
}