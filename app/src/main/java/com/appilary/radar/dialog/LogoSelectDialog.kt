package com.appilary.radar.dialog

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.appilary.radar.R
import com.appilary.radar.listners.FileSelectedListner
import com.appilary.radar.utils.*
import com.appilary.radar.utils.AppUtils.showToast
import java.io.File

class LogoSelectDialog : DialogFragment() {
    private var fileType: Int = OP_TYPE_IMAGE_UPLOAD
    private var camType: Int = 0
    private var fileUri: Uri? = null
    private var filePath: String? = null
    private var listener: FileSelectedListner? = null
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!
    }

    private fun setListener(listener: FileSelectedListner) {
        this.listener = listener
    }

    private fun setFileType(fileType: Int) {
        this.fileType = fileType
    }

    private fun setCamType(camType: Int?) {
        this.camType = camType ?: 0
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val v = inflater.inflate(R.layout.dialog_logo_select, container, false)
//        bindView(v)
//        setTitle("Choose", null)

//        if (!(fileType == OP_TYPE_IMAGE_UPLOAD || fileType == OP_TYPE_VIDEO_UPLOAD)) {
//            photo()
//        }

        camera()

        return null
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        cancel.visibility = View.GONE
//        ok.text = "Cancel"
//
//        camera.setOnClickListener {
//            camera()
//        }
//
//        photos.setOnClickListener {
//            photo()
//        }
//
//        ok.setOnClickListener {
//            cancel()
//        }
//
//    }

    fun cancel() {
        dismiss()
    }

    fun camera() {
        if (PermissionCheck.checkCameraPermissionForFrag(
                activity,
                this@LogoSelectDialog
            )
        ) captureImage()
    }

    fun photo() {
        if (PermissionCheck.checkStoragePermissionForFrag(
                activity,
                this@LogoSelectDialog
            )
        ) openGallery()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0) when (requestCode) {
            REQUEST_PERMISSION_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else showToast(PERMISSION_STORAGE_DENIED_MSG)
            REQUEST_PERMISSION_CAMERA -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else showToast(PERMISSION_CAMERA_DENIED_MSG)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        if (fileType == OP_TYPE_IMAGE_UPLOAD) {
            intent.type = "image/*"
        } else if (fileType == OP_TYPE_VIDEO_UPLOAD) {
            val uri = Uri.parse(
                Environment.getExternalStorageDirectory().path
                        + "/"
            )
            intent.setDataAndType(uri, "video/*")
        } else
            intent.type = "*/*"
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun captureImage() { // Checking camera availability
        if (!isDeviceSupportCamera) {
            showToast("Sorry! Your device doesn't support camera")
            return
        }
        val intent: Intent
        val file: File
        if (fileType == OP_TYPE_VIDEO_UPLOAD) {
            intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            file = AppUtils.getVideoPath(activity)
        } else {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (camType == 1) {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            } else {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 0)
            }
            file = AppUtils.getLogoPath(activity)
        }
        filePath = file.absolutePath
        fileUri =
            FileProvider.getUriForFile(
                activity,
                getString(R.string.file_provider_authority),
                file
            )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_REQUEST)
    }// no camera on this device

    // this device has a camera
    private val isDeviceSupportCamera: Boolean
        private get() = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                listener?.onFileSelected(fileUri, filePath)
            } else {
                listener?.onFileSelected(data?.data, null)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            showToast("User cancelled")
        }
        dismiss()
    }

    companion object {
        val TAG = LogoSelectDialog::class.java.simpleName
        fun newInstance(
            listener: FileSelectedListner,
            fileType: Int,
            camType: Int?
        ): LogoSelectDialog {
            val dialog = LogoSelectDialog()
            dialog.setListener(listener)
            dialog.setFileType(fileType)
            dialog.setCamType(camType)
            return dialog
        }
    }
}