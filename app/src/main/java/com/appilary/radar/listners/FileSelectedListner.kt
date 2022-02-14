package com.appilary.radar.listners

import android.net.Uri

interface FileSelectedListner {
    fun onFileSelected(uri: Uri?, path: String?)
}