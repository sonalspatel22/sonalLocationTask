package com.appilary.radar.dialog

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.appilary.radar.R
import com.appilary.radar.utils.UiUtils

abstract class BaseSurveyDialog : DialogFragment() {
    lateinit var activity: Activity
    lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Dialog)
    }


    fun bindView(view: View) {
        dialogView = view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    override fun onStart() {
        super.onStart()
        activity?.let { activity ->
            getDialog()?.let {
                val width: Int =
                    UiUtils.getScreenWidth(activity) - UiUtils.dpToPx(20) //ViewGroup.LayoutParams.MATCH_PARENT;
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.window?.setLayout(width, height)
            }
        }
    }

    fun setTitle(header: String?, subHeader: String?) {
        val titleView = dialogView.findViewById<TextView>(R.id.title)
        val subTitleView = dialogView.findViewById<TextView>(R.id.sub_title)
        if (TextUtils.isEmpty(header)) titleView?.visibility = View.GONE else titleView?.text =
            header
        if (TextUtils.isEmpty(subHeader)) subTitleView?.visibility =
            View.GONE else subTitleView?.text =
            subHeader
    }
}