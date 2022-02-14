package com.appilary.radar.frag

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.appilary.radar.App
import com.appilary.radar.R
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.CAMERA_REQUEST
import com.appilary.radar.utils.GALLERY_REQUEST
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.whowinkedme.utilities.ImageCompression
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 7/12/17.
 */
abstract class BaseImageFragment : BaseFragment() {
    val PERMISSION_SETTING_REQUEST = 100
    lateinit var IMAGE_NAME: String
    var imageUri: Uri? = null

    fun getImage(titleMsg: String) {
        val dialog = BottomSheetDialog(mainActivity)
        dialog.setContentView(R.layout.dialog_upload)
        val title = dialog.findViewById<TextView>(R.id.header)
        val camera = dialog.findViewById<LinearLayout>(R.id.camera)
        val gallery = dialog.findViewById<LinearLayout>(R.id.gallery)
        title?.text = titleMsg
        camera?.setOnClickListener {
            getImageFromCameraOrGallery(CAMERA_REQUEST)
            dialog.dismiss()
        }
        gallery?.setOnClickListener {
            getImageFromCameraOrGallery(GALLERY_REQUEST)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun getCameraImage() {
        App.isCameraIntent = true
        getImageFromCameraOrGallery(CAMERA_REQUEST)
    }

    private fun getImageFromCameraOrGallery(requestCode: Int) {
        IMAGE_NAME = "${System.currentTimeMillis()}"
        if (requestCode == CAMERA_REQUEST) {
            if (checkCameraPermission(mainActivity)) {


//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//                val photo = File(Environment.getExternalStorageDirectory(), "Profile.jpg")
//                val uri = FileProvider.getUriForFile(mainActivity, BuildConfig.APPLICATION_ID + ".provider",photo)
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//                imageUri = Uri.fromFile(photo)
//                startActivityForResult(intent, CAMERA_REQUEST)


                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val photoURI = FileProvider.getUriForFile(
                    mainActivity,
                    "com.appilary.radar", File(AppUtils.getImagePath(mainActivity, IMAGE_NAME))
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    val resInfoList = mainActivity.packageManager.queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        mainActivity.grantUriPermission(
                            packageName,
                            photoURI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                }
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST)
                }
            } else {
                requestPermission(CAMERA_REQUEST)
            }
        } else {
            if (checkStoragePermission(mainActivity)) {
                val intent = getFileChooserIntent()
//                val intent = Intent("android.intent.action.GET_CONTENT")
//                intent.type = "image/*"
//                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY_REQUEST)
            } else {
                requestPermission(GALLERY_REQUEST)
            }
        }
    }

    private fun getFileChooserIntent(): Intent {
//        val mimeTypes = arrayOf("image/*", "application/pdf")
        val mimeTypes = arrayOf("image/*")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += mimeType + "|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        return intent
    }

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission(requestCode: Int) {
        if (requestCode == CAMERA_REQUEST)
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
        else if (requestCode == GALLERY_REQUEST)
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                GALLERY_REQUEST
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImageFromCameraOrGallery(requestCode);
        } else {
            showDenyAlert(requestCode);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showDenyAlert(requestCode: Int) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission required")
        if (requestCode == CAMERA_REQUEST)
            alertBuilder.setMessage("External storage permission is required")
        else
            alertBuilder.setMessage("Camera permission is required")

        alertBuilder.setPositiveButton("Access", DialogInterface.OnClickListener { dialog, _ ->
            if (requestCode == CAMERA_REQUEST && ActivityCompat.shouldShowRequestPermissionRationale(
                    mainActivity,
                    Manifest.permission.CAMERA
                ) ||
                requestCode == GALLERY_REQUEST && ActivityCompat.shouldShowRequestPermissionRationale(
                    mainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
                requestPermission(requestCode)
            else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", mainActivity.packageName, null)
                intent.setData(uri)
                startActivityForResult(intent, PERMISSION_SETTING_REQUEST)
            }
            dialog.dismiss()
        })
        val alert = alertBuilder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        App.isCameraIntent = false
        if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                var filePath: String? = null
                if (requestCode == GALLERY_REQUEST && data != null) {
                    data.data?.let {
                        val pictureInputStream = mainActivity.contentResolver.openInputStream(it)
                        val ext = getFileNameExt(it)
                        filePath = AppUtils.getImagePath(mainActivity, IMAGE_NAME, ext)
                        if (pictureInputStream != null)
                            copyFileToFile(pictureInputStream, File(filePath))
                    }
                }
                if (filePath == null)
                    filePath = AppUtils.getImagePath(mainActivity, IMAGE_NAME)


//                if (imageUri == null) {
//                    val photo = File(Environment.getExternalStorageDirectory(), "Profile.jpg")
//                    imageUri = Uri.fromFile(photo)
//                }

//                val filePath = FilePath.getPath(mainActivity, imageUri);
                val compress = object : ImageCompression(mainActivity) {
                    override fun onPostExecute(filePath: String?) {
                        onImageCapture(filePath)
                    }

                }


                compress.execute(filePath)
            }
        }
    }

    fun getFileNameExt(uri: Uri): String {
        val returnCursor = mainActivity.contentResolver.query(uri, null, null, null, null);
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            if (nameIndex >= 0 && sizeIndex >= 0) {
                val fileName = returnCursor.getString(nameIndex)
                val i = fileName.lastIndexOf('.')
                if (i > 0) {
                    return fileName.substring(i + 1)
                }
            }
        }
        return "jpg"
    }

    fun copyFileToFile(inStream: InputStream, file: File) {
        try {
            file.createNewFile()
            val out = FileOutputStream(file)
            val buf = ByteArray(1024)

            var len: Int = inStream.read(buf)
            while (len > 0) {
                out.write(buf, 0, len)
                len = inStream.read(buf)
            }

            out.close()
            inStream.close()
        } catch (e: IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    abstract fun onImageCapture(path: String?)
}