package com.appilary.radar.customeview

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.appilary.radar.R
import com.appilary.radar.activities.BarCodeScannerActivity
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.live_data.BarCodeLiveData

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiBarcodeView(model: FormField) : CatiCustomView(model) {

    private var observer: Observer<String>? = null

    override fun setQuesLayout() {
        itemLayout.addView(addLayoutView())
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        observer?.let { BarCodeLiveData.removeObserver(it) }
        if (quesTv != null) {
            val count = itemLayout.childCount
            for (i in 0 until count) {
                val layout = itemLayout.getChildAt(i)
                val value = layout.findViewById<EditText>(R.id.edit_text)
                val text = value.text.toString()
                if (text.isEmpty() && formField.validations?.mn == 1) {
                    return null
                }

                if (text.trim().isNotEmpty()) {
                    val element = ResAElement(id)
                    element.singleAns = text
                    return element
                }
            }
        }
        return null
    }

    private fun addLayoutView(): View {
        val view = inflater.inflate(R.layout.cati_barcode, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText
        val editText = view.findViewById<View>(R.id.edit_text) as EditText
        editText.tag = formField.id
        if (formField.tooltip == 1)
            editText.setHint(formField.tooltipText)

        if (resData != null) {
            editText.setText(resData?.singleAns)
        }
        observer = Observer { result ->
            editText.setText(result)
        }


        view.findViewById<ImageView>(R.id.image_barcode).setOnClickListener {
            observer?.let {
                BarCodeLiveData.observeForever(it)
            }
            Intent(mContext, BarCodeScannerActivity::class.java).apply {
                mContext.startActivity(this)
            }
        }

        return view
    }

}