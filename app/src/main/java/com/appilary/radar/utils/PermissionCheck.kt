package com.appilary.radar.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


object PermissionCheck {

    fun checkLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraPermissionForFrag(
        mContext: Context,
        frag: Fragment
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        val hasLocationPermission: Int = ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.CAMERA
        )
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            frag.requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CAMERA
            )
            return false
        }
        return true
    }

    fun checkStoragePermissionForFrag(
        mContext: Context,
        frag: Fragment
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            frag.requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_STORAGE
            )
            return false
        }
        return true
    }

}