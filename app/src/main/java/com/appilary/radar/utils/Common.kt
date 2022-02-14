package com.appilary.radar.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.util.DisplayMetrics
import androidx.fragment.app.FragmentActivity
import com.appilary.radar.App
import com.appilary.radar.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/1/17.
 */

const val CAMERA_REQUEST: Int = 101
const val GALLERY_REQUEST: Int = 102

const val REQUEST_READ_LOCATION_STATE_PERMISSION = 103
const val REQUEST_PERMISSION_STORAGE = 11
const val REQUEST_PERMISSION_CAMERA = 12

const val PERMISSION_STORAGE_DENIED_MSG = "Storage Permission Denied Please " +
        "enable to set the Profile image."

const val PERMISSION_CAMERA_DENIED_MSG = "Camera Permission Denied Please " +
        "enable to use Camera."

const val STATUS_PENDING = 0
const val STATUS_DONE = 1
const val STATUS_NOT_DONE = 2

const val TYPE_PRE = "pre"
const val TYPE_POST = "post"
const val TYPE_STOCK = "stock"
const val TYPE_DAMAGE = "damage"

const val VERSION_TAG = "version"

const val OTHER = "Other"

val CONFIRM_MSG_INVENTORY = App.mInstance.resources.getString(R.string.do_you_confirm_receipt_of_material)
val CONFIRM_MSG = App.mInstance.resources.getString(R.string.do_you_confirm_to_upload_data)
const val CONFIRM_MSG_UPLOAD = "Thanks. Your data is uploaded."


fun convertPixelsToDp(px: Int, context: Context): Int {
    val resources = context.getResources()
    val metrics = resources.getDisplayMetrics()
    val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return dp.toInt()
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    val resources = context.getResources()
    val metrics = resources.getDisplayMetrics()
    val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return dp
}

fun convertDpToPixel(dp: Int, context: Context): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return px.toInt()
}

fun validateMobile(mobile: String): Boolean {
    if (mobile.contains(" "))
        return false
    val regex = "\\d+"
    val pattern = Pattern.compile(regex)
    return (pattern.matcher(mobile).matches() && mobile.length == 10)
}


fun getAppVersion(context: Context): Int {
    try {
        val manager = context.packageManager
        val info = manager.getPackageInfo(
                context.packageName, 0)
        return info.versionCode
    } catch (e: Exception) {
    }

    return 0
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}


private fun getDataTime(time: Long?): String {
    val formater = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
    if (time == null)
        return formater.format(System.currentTimeMillis())
    return formater.format(time)
}

fun fullMonthDate(cal: Calendar): String {
    return android.text.format.DateFormat.format("EEE, MMM dd yyyy", cal).toString();
}

fun openAppInMarket(context: Context) {
    val uri = Uri.parse("market://details?id=" + context.applicationInfo.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    // To count with Play market backstack, After pressing back button,
    // to taken back to our application, we need to add following flags to intent.
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        context.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.applicationInfo.packageName)))
    }

}

fun launchNavigation(activity: FragmentActivity, lat: Double, lang: Double) {
    val intent = Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$lat,$lang"))
    activity.startActivity(intent)
}
