package com.appilary.radar.customeview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appilary.radar.App
import com.appilary.radar.R
import com.appilary.radar.activities.SurveyActivity
import com.appilary.radar.api.res.FormField
import com.appilary.radar.camera.CameraConfiguration
import com.appilary.radar.camera.PhotoActivity
import com.appilary.radar.database.RealmControler
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.database.survey.SurveyFileRealm
import com.appilary.radar.dialog.FreeDrawImageFragment
import com.appilary.radar.dialog.LogoSelectDialog
import com.appilary.radar.listners.FileSelectedListner
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.FilePath
import com.appilary.radar.utils.OP_TYPE_IMAGE_UPLOAD
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiFileView(model: FormField, val surveyFile: SurveyFileRealm) : CatiCustomView(model) {

    var selectedFilePath: String? = null

    override fun setQuesLayout() {
        val view = addLayoutView()
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            if (selectedFilePath.isNullOrEmpty())
                return null
            else {
                val element = ResAElement(id)
                element.filePath = selectedFilePath
                surveyFile.filePath = selectedFilePath
                element.fileUniqueId = surveyFile.uniqId
                RealmControler.updateSurveyFileRes(surveyFile)
                return element
            }
        }
        return null
    }

    private fun addLayoutView(): View {
        val view: View = inflater.inflate(R.layout.cati_item_file, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val answer = view.findViewById<View>(R.id.answer) as TextView
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText
        val upload = view.findViewById<View>(R.id.upload) as ImageView
        val delete = view.findViewById<View>(R.id.delete) as ImageView
        delete.setOnClickListener {
            fileRemoved(answer, delete, upload)
        }

        resData?.filePath?.let {
            selectedFilePath = it
            showImage(it, answer, delete, upload)
        }

        upload.setOnClickListener {

            val callback = object : FileSelectedListner {
                override fun onFileSelected(uri: Uri?, path: String?) {
//                        var filePath = path
//                        if (TextUtils.isEmpty(filePath)) filePath = FilePath.getPath(mContext, uri)
                    if (!path.isNullOrEmpty()) {
                        if (formField.optype == OP_TYPE_IMAGE_UPLOAD) {
                            AppUtils.showProgressDialog(mContext)
                            GlobalScope.launch(Dispatchers.IO) { //Todo do it later
                                val compressedImageFile =
                                    Compressor.compress(mContext, File(path)) {
                                        default()
                                        val savepath = AppUtils.getImagePath(mContext, "${System.currentTimeMillis()}COMPRESS_PRE")
                                        destination(File(savepath))
                                    }



                                launch(Dispatchers.Main) {
                                    File(path).delete()
                                    AppUtils.hideProgressDialog()
                                    fileSelected(
                                        compressedImageFile.path,
                                        answer,
                                        delete,
                                        upload
                                    )
                                }
                            }
//
//                                GlobalScope.launch(Dispatchers.IO) {
//                                    val imagePath =
//                                        mContext.compressImage(imagePath = filePath) ?: "Not found"
//                                    AppUtils.hideProgressDialog()
//                                    launch(Dispatchers.Main) {
//                                        fileSelected(imagePath, answer, delete, upload)
//                                    }
//                                }
                            return
                        } else fileSelected(path, answer, delete, upload)
                    } else {
                        answer.text = "fileName"
                        val filePath = AppUtils.getImagePath(
                            App.mInstance,
                            "${System.currentTimeMillis()}"
                        )
                        fileSelected(filePath, answer, delete, upload)
                    }
                }
            }


            PhotoActivity.open(callbackListener = callback, callingActivity = (mContext as Activity), isFront = formField.camType == 1)
//            val dialog: LogoSelectDialog = LogoSelectDialog.newInstance(callback, formField.optype ?: OP_TYPE_IMAGE_UPLOAD, formField.camType)
//            fragment?.activity?.supportFragmentManager?.let {
//                dialog.show(it, LogoSelectDialog.TAG)
//            }

        }
        upload.tag = formField.id
        return view
    }


    private fun fileSelected(
        filePath: String,
        answer: TextView,
        delete: ImageView,
        upload: ImageView
    ) {

        if (formField.optype == OP_TYPE_IMAGE_UPLOAD && formField.imageMarking == 1)
            editCaptureImage(filePath, answer, delete, upload)
        else {
            selectedFilePath = filePath
            showImage(filePath, answer, delete, upload)
        }
    }

    fun editCaptureImage(
        filePath: String,
        answer: TextView,
        delete: ImageView,
        upload: ImageView
    ) {
        val dialog: FreeDrawImageFragment =
            FreeDrawImageFragment.newInstance(object : FileSelectedListner {
                override fun onFileSelected(uri: Uri?, path: String?) {
                    selectedFilePath = filePath
                    showImage(filePath, answer, delete, upload)
                }
            }, filePath)

        fragment?.activity?.supportFragmentManager?.beginTransaction()
            ?.add(R.id.container, dialog, FreeDrawImageFragment.TAG)?.addToBackStack("")
            ?.commitAllowingStateLoss()
    }


    fun showImage(
        filePath: String,
        answer: TextView,
        delete: ImageView,
        upload: ImageView
    ) {
        Picasso.with(fragment?.mainActivity)
            .load(File(filePath))
            .into(upload)
        answer.text = filePath.substring(filePath.lastIndexOf("/") + 1)
        delete.visibility = View.VISIBLE
    }


    private fun fileRemoved(answer: TextView, delete: ImageView, upload: ImageView) {
        RealmControler.removeSurveyFileRes(selectedFilePath)
        selectedFilePath = null
        answer.text = ""
        delete.visibility = View.GONE
        upload.setImageResource(R.drawable.image)
    }


    @Throws(IOException::class)
    private fun copy(src: File, dst: File) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    }
}