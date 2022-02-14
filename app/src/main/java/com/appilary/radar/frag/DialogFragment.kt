package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.R
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.events.DialogCallbackEvent
import kotlinx.android.synthetic.main.dialog_base.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/13/17.
 */

class DialogFragment : BaseFragment() {

    var tagString: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mainActivity as BaseActivity).disableBack = true

        val args = arguments!!
        msg.text = args.getString("msg")
        tagString = args.getString("tag")

        if (args.getBoolean("singleButton", true))
            btnLeft.visibility = View.GONE
        else
            btnLeft.text = args.getString("cancelText")
        btnRight.text = args.getString("okText")

        btnRight.setOnClickListener {
            (mainActivity as BaseActivity).disableBack = false
            mainActivity.onBackPressed()
            EventBus.getDefault().post(DialogCallbackEvent(true, tagString))
        }
        btnLeft.setOnClickListener {
            (mainActivity as BaseActivity).disableBack = false
            mainActivity.onBackPressed()
            EventBus.getDefault().post(DialogCallbackEvent(false, tagString))
        }
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }


    companion object {
        val TAG = DialogFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(msg: String, singleButton: Boolean, okText: String, cancelText: String, tag: String? = null): DialogFragment {
            val fragment = DialogFragment()
            val bundle = Bundle()
            bundle.putString("msg", msg)
            bundle.putBoolean("singleButton", singleButton)
            bundle.putString("okText", okText)
            bundle.putString("cancelText", cancelText)
            bundle.putString("tag", tag)
            fragment.arguments = bundle
            return fragment
        }
    }

}