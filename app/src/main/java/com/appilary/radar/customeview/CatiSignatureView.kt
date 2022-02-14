package com.appilary.radar.customeview

import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.RealmControler
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.database.survey.SurveyFileRealm
import com.appilary.radar.utils.compressImage
import com.williamww.silkysignature.views.SignaturePad


/**
 * Created by vi.garg on 31/5/16.
 */
class CatiSignatureView(model: FormField, val surveyFile: SurveyFileRealm) : CatiCustomView(model) {

    var selectedFilePath: String? = null
    lateinit var signPad: SignaturePad

    override fun setQuesLayout() {
        val view = addLayoutView()
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            saveValue()
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
        val view: View = inflater.inflate(R.layout.cati_signature, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val answer = view.findViewById<View>(R.id.answer) as TextView
        signPad = view.findViewById(R.id.signature_pad)
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText

        resData?.filePath?.let {
            selectedFilePath = it
            try {
                if (Uri.parse(it) != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        mContext.contentResolver,
                        Uri.parse(it)
                    )
                    signPad.signatureBitmap = bitmap
                }
            } catch (e: Exception) {
                //handle exception
            }
        }

        return view
    }


    private suspend fun saveValue() {
        if (!signPad.isEmpty) {
            val mBitmap = signPad.signatureBitmap
            selectedFilePath = mContext.compressImage(bitmap = mBitmap) //Todo
        }
        selectedFilePath = null
    }

    private fun fileRemoved(
        answer: TextView,
        delete: ImageView,
        upload: ImageView
    ) { //Todo undo case
        RealmControler.removeSurveyFileRes(selectedFilePath)
        selectedFilePath = null
        answer.text = ""
        delete.visibility = View.GONE
        upload.setImageResource(R.drawable.image)
    }

}