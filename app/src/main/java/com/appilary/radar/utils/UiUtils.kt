package com.appilary.radar.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.appilary.radar.App

object UiUtils {
    fun dpToPx(i: Int): Int {
        return Math.round(App.mInstance.resources.displayMetrics.xdpi / 160.0f * i.toFloat())
    }

    fun getScreenSize(context: Context): Point {
        val defaultDisplay =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        return point
    }

    fun getScreenWidth(context: Context): Int {
        return getScreenSize(context).x
    }

    fun getScreenHeight(context: Context): Int {
        return getScreenSize(context).y
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 2)
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        val identifier =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (identifier > 0) {
            context.resources.getDimensionPixelSize(identifier)
        } else 0
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (identifier > 0) {
            resources.getDimensionPixelSize(identifier)
        } else 0
    }

    fun getViewLocation(view: View): Point {
        val iArr = IntArray(2)
        view.getLocationInWindow(iArr)
        return Point(iArr[0], iArr[1])
    }

    fun getCenterLocation(view: View): Point {
        val iArr = IntArray(2)
        view.getLocationInWindow(iArr)
        return Point(
            iArr[0] + view.width / 2,
            iArr[1] + view.height / 2
        )
    }

    fun isTablet(context: Context): Boolean {
        return ((context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    fun lockOrientation(activity: Activity) {
        if (activity.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    fun unlockOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}