package com.appilary.radar.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.appilary.radar.App
import com.appilary.radar.R
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.api.body.SurveyPostReqBody
import com.appilary.radar.database.RealmControler
import com.appilary.radar.database.survey.SurveySingleResData
import com.appilary.radar.listners.AlertDialogCallBack
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 7/4/17.
 */
object AppUtils {

    private var progressDialog: ProgressDialog? = null

    fun getUniqueId(): String {
        return "${System.currentTimeMillis()}"
    }

    fun showToast(msg: String?) {
        if (!msg.isNullOrEmpty())
            Toast.makeText(App.mInstance, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToast(msg: Int?) {
        if (msg != 0 && msg != null)
            Toast.makeText(App.mInstance, msg, Toast.LENGTH_SHORT).show()
    }

    fun showProgressDialog(context: Context, msg: String = "Please wait...") {
        if (progressDialog != null)
            return
        progressDialog = ProgressDialog(context)
        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog?.setMessage(msg)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing())
            progressDialog?.dismiss()
        progressDialog = null
    }

    fun getImagePath(context: Context, name: String, ext: String = "jpg"): String {
        val mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            val mediaStorageDir = Environment.getExternalStorageDirectory()
        if (!mediaStorageDir!!.exists()) {
            mediaStorageDir.mkdirs()
        }

        return mediaStorageDir.absolutePath + File.separator + name + ".$ext"
    }

    fun removeImagePath(fullpath: String?) {
        if (!fullpath.isNullOrEmpty())
            File(fullpath).delete()
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }


    fun showDatePicker(activity: Activity, editText: EditText) {
        val calander = Calendar.getInstance()
        if (editText.getTag() != null && editText.getTag() is Long)
            calander.timeInMillis = editText.tag as Long
        val mYear = calander.get(Calendar.YEAR)
        val mMonth = calander.get(Calendar.MONTH)
        val mDay = calander.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val formatter = DecimalFormat("00")
                val value =
                    formatter.format(dayOfMonth) + "/" + formatter.format(monthOfYear + 1) + "/" + year
                calander.set(year, monthOfYear, dayOfMonth)
                if (editText != null) {
                    editText.setText(fullMonthDate(calander))
                    editText.setTag(calander.timeInMillis)
                }
            }, mYear, mMonth, mDay
        )

        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -2)
        datePickerDialog.datePicker.minDate = cal.timeInMillis
        cal.add(Calendar.MONTH, 6)
        datePickerDialog.datePicker.maxDate = cal.timeInMillis
        datePickerDialog.show()
    }

    fun surveyPostCopy(list: List<SurveySingleResData>): List<SurveyPostReqBody> {
        val formList = ArrayList<SurveyPostReqBody>()
        for (item in list) {
            val formItem = SurveyPostReqBody(
                uniqueId = item.uniqueId,
                surveyId = item.surveyId,
                dt = item.dt,
                surveyTime = item.surveyTime,
                lt = item.lt,
                lg = item.lg,
                totalDistance=item.totalDistance,
                appFormList = item.getResPList()
            )

            formList.add(formItem)
        }

        return formList
    }

    fun logout() {
        AppPreference.instance.clearData()
        RealmControler.clearRealmData()
    }

    fun error401() {
        logout()
        Intent(App.mInstance, BaseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            App.mInstance.startActivity(this)
        }
    }


    fun isNeedToShowAttendance(): Boolean {
        val lastAttendance = AppPreference.instance.lastTimeAttendence
        val cal = Calendar.getInstance()
        val currDay = cal.get(Calendar.DAY_OF_YEAR)
        val currYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = lastAttendance
        val preDay = cal.get(Calendar.DAY_OF_YEAR)
        val preYear = cal.get(Calendar.YEAR)
        return preYear != currYear || preDay != currDay
    }

    fun isSameDay(longTime: Long): Boolean {
        val cal = Calendar.getInstance()
        val currDay = cal.get(Calendar.DAY_OF_YEAR)
        val currYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = longTime
        val preDay = cal.get(Calendar.DAY_OF_YEAR)
        val preYear = cal.get(Calendar.YEAR)
        return preYear == currYear && preDay == currDay
    }

    fun getDateInternational(timeStamp: Long): String? {
        val date = Date(timeStamp)
        return SimpleDateFormat("dd-MM-yyyy").format(date)
    }

    fun getTime(timeStamp: Long): String? {
        val date = Date(timeStamp)
        return SimpleDateFormat("hh:mm").format(date)
    }

    fun getTimeWithSec(timeStamp: Long): String? {
        val date = Date(timeStamp)
        return SimpleDateFormat("hh:mm:ss").format(date)
    }

    fun getDateTime(timeStamp: Long): String? {
        val date = Date(timeStamp)
        return SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(date)
    }

    private const val IMAGE_FILE_NAME = "IMG_"
    private const val VIDEO_FILE_NAME = "VID_"

    fun getLogoPath(context: Context): File {
        return File(getImagePath(context, IMAGE_FILE_NAME + System.currentTimeMillis()))
    }

    fun getVideoPath(activity: Activity): File {
        return File(getImagePath(activity, VIDEO_FILE_NAME + System.currentTimeMillis(), "mp4"))
    }

    fun showAlertDialog(
        context: Context,
        hasTwoButtons: Boolean,
        isCancelable: Boolean,
        title: String?,
        msg: String?,
        listener: AlertDialogCallBack?
    ) {
        showAlertDialog(
            context, hasTwoButtons, isCancelable,
            title, msg, "Yes", "No", listener
        )
    }

    fun showAlertDialog(
        context: Context,
        hasTwoButtons: Boolean,
        isCancelable: Boolean,
        title: String?,
        msg: String?,
        positiveText: String?,
        negativeText: String?,
        listener: AlertDialogCallBack?
    ) {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(R.layout.dialog_general_alert)
        val header = dialog.findViewById<View>(R.id.title) as TextView
        val subHeader = dialog.findViewById<View>(R.id.sub_title) as TextView
        val message = dialog.findViewById<View>(R.id.message) as TextView
        val cancel = dialog.findViewById<View>(R.id.cancel) as TextView
        val ok = dialog.findViewById<View>(R.id.ok) as TextView
        header.text = title
        subHeader.visibility = View.GONE
        if (!hasTwoButtons) cancel.visibility = View.GONE
        message.text = msg
        ok.text = positiveText
        cancel.text = negativeText
        ok.setOnClickListener {
            dialog.dismiss()
            if (listener != null) listener.onClick(true)
        }
        cancel.setOnClickListener {
            dialog.dismiss()
            if (listener != null) listener.onClick(false)
        }
        dialog.show()
    }


}

