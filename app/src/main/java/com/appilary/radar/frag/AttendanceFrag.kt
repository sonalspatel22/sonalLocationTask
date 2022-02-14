package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.activities.LocationUpdated
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.FragmentOpener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_attendance.*
import java.io.File

/**
 * Created by vi.garg on 6/2/18.
 */
class AttendanceFrag : BaseImageFragment() {

    private var path: String? = null
    private var surveyId: String? = null
    private var lat: Double? = null
    private var lang: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.frag_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mainActivity is BaseActivity) {
            val curr = (mainActivity as BaseActivity).getCurrentLoc()
            lat = curr.lat
            lang = curr.lang
            (mainActivity as BaseActivity).findLocation(object :
                LocationUpdated {
                override fun onLocCapture(lat: Double?, lang: Double?, address: String?) {
                    this@AttendanceFrag.lat = lat
                    this@AttendanceFrag.lang = lang
                }
            })
        }


        arguments?.let {
            surveyId = it.getString("surveyId")
        }

        image.setOnClickListener {
            getCameraImage()
        }

        submit.setOnClickListener {
            if (validate()) {

                AppUtils.showToast("Submitted Successfully.")
            }
        }
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    private fun validate(): Boolean {
        if (path.isNullOrEmpty()) {
            AppUtils.showToast("Please Select Image")
            return false
        }

        return true
    }

    override fun onImageCapture(path: String?) {
        if (!path.isNullOrEmpty()) {
            this.path = path
            Picasso.with(mainActivity)
                .load(File(path))
                .into(image)
        } else
            AppUtils.showToast("Please try again")

    }

    companion object {
        val TAG = AttendanceFrag::class.java.simpleName
        @JvmStatic
        fun newInstance(surveyId: String?): AttendanceFrag {
            val fragment = AttendanceFrag()
            val bundle = Bundle()
            bundle.putString("surveyId", surveyId)
            fragment.arguments = bundle
            return fragment
        }
    }
}