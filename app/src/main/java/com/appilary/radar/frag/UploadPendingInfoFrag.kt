package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.adapter.PendingUploadAdapter
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.bean.UploadPendingBean
import com.appilary.radar.database.RealmControler
import com.appilary.radar.utils.ApiCallUtils
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.isNetworkAvailable
import com.appilary.radar.view.RecyclerViewMargin
import kotlinx.android.synthetic.main.dialog_upload_pending.*


/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 12/13/17.
 */

class UploadPendingInfoFrag : BaseFragment() {

    var formCount: Long = 0
    var imageCount: Long = 0
    val pendingList = mutableListOf<UploadPendingBean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_upload_pending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = PendingUploadAdapter(mainActivity, pendingList)
        recycler_view.addItemDecoration(RecyclerViewMargin(10))

        btnLeft.setOnClickListener {
            if (isNetworkAvailable(mainActivity)) {
                ApiCallUtils.postForm()
                AppUtils.showToast("Uploading...")
                btnLeft.visibility = View.GONE
                uploading_txt.visibility = View.VISIBLE
            } else
                AppUtils.showToast("No Internet Available")
        }
    }

    override fun onResume() {
        super.onResume()
        updateValues()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            updateValues()
    }

    fun updateValues() {
        formCount = RealmControler.getSurveyResCount()
        imageCount = RealmControler.getSurveyFileCount()
//
//        formNo.text =
//            "${activity?.resources?.getString(R.string.form_pending_to_upload)} : $formCount"
//        imageNo.text =
//            "${activity?.resources?.getString(R.string.image_pending_to_upload)} : $imageCount"

        pendingList.clear()

        RealmControler.getSurveyResList().forEach {
            pendingList.add(UploadPendingBean(it.dt, "Data"))
        }

        RealmControler.getSurveyFileList().forEach {
            pendingList.add(UploadPendingBean(it.dt, "File"))
        }

        pendingList.sortBy { it.time }
        recycler_view?.adapter?.notifyDataSetChanged()

        if (formCount == 0L && imageCount == 0L)
            empty_lay.visibility = View.VISIBLE
        else
            empty_lay.visibility = View.GONE
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }


    companion object {
        val TAG = UploadPendingInfoFrag::class.java.simpleName

        @JvmStatic
        fun newInstance(): UploadPendingInfoFrag {
            val fragment = UploadPendingInfoFrag()
            return fragment
        }
    }

}