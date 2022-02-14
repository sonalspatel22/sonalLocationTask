package com.appilary.radar.customeview

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.utils.AppUtils.getDateInternational
import com.appilary.radar.utils.AppUtils.getTime
import com.appilary.radar.utils.OP_TYPE_DATE
import com.appilary.radar.utils.OP_TYPE_DATE_TIME
import com.appilary.radar.utils.OP_TYPE_TIME
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiDateTimeView(model: FormField) : CatiCustomView(model) {
    override fun setQuesLayout() {
        val view = addLayoutView(0)
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        hideErrorText()
        if (quesTv != null) {
            val count = itemLayout.getChildCount();
            for (i in 0 until count) {
                val layout = itemLayout.getChildAt(i)
                val title = layout.findViewById<TextView>(R.id.selected_value)
                val catiTextEntry = layout.findViewById<CatiTextEntry>(R.id.cati_text_entry)
                if (title.getTag() != null) {
                    var element: ResAElement? = null
                    if (title.getTag(R.id.date_time_selected_val) != null) {
                        val selectedVal = "${title.getTag(R.id.date_time_selected_val)}"
                        element = ResAElement(id)
                        element.singleAns = selectedVal
                        return element
                    }
//                    if (element != null && rowCols != null && rowCols.size() > 0) {
//                        RowCol currentRow = rowCols.get(0);
//                        if (currentRow != null) {
//                            boolean isValid = catiTextEntry.checkItemValidation(currentRow.getAddnTextvalidation());
//                            if (!isValid)
//                                return null;
//                        }
//                        if (!TextUtils.isEmpty(catiTextEntry.getTextValue()))
//                            element.setRowText(catiTextEntry.getTextValue());
//                        element.setRowId(String.valueOf(title.getTag()));
//                        list.add(element);
//                    }
                }
            }
        }
        return null
    }

    private fun addLayoutView(pos: Int): View {
        val view = inflater.inflate(R.layout.cati_item_date_time, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val catiTextEntry =
            view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        val selectedTextView =
            view.findViewById<View>(R.id.selected_value) as TextView
        val dateTime =
            view.findViewById<View>(R.id.date_time) as ImageView
        if (formField.optype == OP_TYPE_TIME) dateTime.setImageResource(R.drawable.clock) else dateTime.setImageResource(
            R.drawable.calendar
        )
        selectedTextView.tag = formField.id
        dateTime.tag = pos
        dateTime.setOnClickListener(imageClickListener)
        val tit = formField.desc
        var preFilledVal: String? = null
        resData?.let {
            preFilledVal = it.singleAns
            preFilledVal?.let {
                setPrefilledDate(selectedTextView, it)
            }
        }
        if (!preFilledVal.isNullOrEmpty()) {
            title.visibility = View.GONE
            catiTextEntry.initTextEntry(formField, preFilledVal ?: "", tit)
        } else {
            if (TextUtils.isEmpty(tit)) title.visibility = View.GONE else title.text = tit
        }
        return view
    }

    var imageClickListener =
        View.OnClickListener { v ->
            if (v.tag is Int) {
                val pos = v.tag as Int
                if (formField.optype == OP_TYPE_DATE_TIME) showDatePicker(
                    true,
                    pos
                ) else if (formField.optype == OP_TYPE_TIME) showTimePicker(
                    pos,
                    null,
                    null
                ) else showDatePicker(false, pos)
            }
        }

    private fun showDatePicker(isDateAndTimePicker: Boolean, pos: Int) {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            mContext,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                if (itemLayout == null || !view.isShown) return@OnDateSetListener
                val layout = itemLayout.getChildAt(pos)
                val title =
                    layout.findViewById<View>(R.id.selected_value) as TextView
                val value =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                if (isDateAndTimePicker) showTimePicker(
                    pos,
                    value,
                    "$dayOfMonth-$monthOfYear-$year"
                ) else {
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    title.text = value
                    title.setTag(R.id.date_time_selected_val, calendar.timeInMillis)
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(
        pos: Int,
        shownDate: String?,
        actualDate: String?
    ) {
        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]
        // Launch Time Picker ForgotPasswordDialog
        val timePickerDialog = TimePickerDialog(
            mContext,
            OnTimeSetListener { view, hourOfDay, minute ->
                itemLayout?.let { itemLayout ->

                    val layout = itemLayout.getChildAt(pos)
                    val title = layout.findViewById<View>(R.id.selected_value) as TextView
                    var time = " $hourOfDay:$minute"
                    val sdf: SimpleDateFormat
                    val convertTime: String
                    if (!TextUtils.isEmpty(shownDate)) {
                        time = shownDate + time
                        convertTime = "$actualDate $hourOfDay:$minute"
                        sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    } else {
                        convertTime = "$hourOfDay:$minute"
                        sdf = SimpleDateFormat("HH:mm")
                    }
                    var currDate: Date? = null
                    try {
                        currDate = sdf.parse(convertTime)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    if (currDate != null) title.setTag(
                        R.id.date_time_selected_val,
                        currDate.time
                    )
                    title.text = time
                }
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun setPrefilledDate(selectedTV: TextView, userValue: String?) {
        if (TextUtils.isEmpty(userValue)) return
        var timestamp: Long = 0
        try {
            timestamp = userValue!!.toLong()
        } catch (e: Exception) {
        }
        if (timestamp == 0L) return
        if (formField.optype == OP_TYPE_DATE) {
            selectedTV.text = getDateInternational(timestamp)
            selectedTV.setTag(R.id.date_time_selected_val, timestamp)
        } else if (formField.optype == OP_TYPE_TIME) {
            selectedTV.text = getTime(timestamp)
            selectedTV.setTag(R.id.date_time_selected_val, timestamp)
        } else if (formField.optype == OP_TYPE_DATE_TIME) {
            var text = getDateInternational(timestamp)
            text += " " + getTime(timestamp)
            selectedTV.text = text
            selectedTV.setTag(R.id.date_time_selected_val, timestamp)
        }
    }
}