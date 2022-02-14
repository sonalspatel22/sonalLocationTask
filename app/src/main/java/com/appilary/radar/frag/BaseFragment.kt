package com.appilary.radar.frag

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.appilary.radar.R
import com.appilary.radar.api.*
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.FragmentOpener
import com.appilary.radar.utils.isNetworkAvailable
import kotlinx.android.synthetic.main.toolbar.*


abstract class BaseFragment : Fragment(), ApiResponseDisplay {
    lateinit var mainActivity: FragmentActivity
    var actionBar: ActionBar? = null
    private lateinit var apiController: ApiController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity!!
        apiController = ApiControllerImpl(ApiPresenterImpl(this))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val logo = view?.findViewById<View>(R.id.logo)
//        logo?.setOnClickListener {
//            FragmentOpener.instance.addDashBoardFrag(mainActivity)
//        }
    }

//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if (toolbar != null) {
//            (mainActivity as AppCompatActivity).setSupportActionBar(toolbar)
//            actionBar = (mainActivity as AppCompatActivity).supportActionBar
//            (mainActivity as MainActivity).disableBack = false
//        }
//
//        view?.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
//                AppUtils.hideKeyboard(mainActivity)
//                return false
//            }
//        })
//    }

    fun setToolbarTitle(title: String) {
        toolbar?.title = title
    }

    fun setBackButton() {
        toolbar?.navigationIcon = resources.getDrawable(R.drawable.back)
        toolbar?.setNavigationOnClickListener { mainActivity.onBackPressed() }
    }


    fun checkForNetwork(shouldFinish: Boolean = false): Boolean {
        val isAvail = isNetworkAvailable(mainActivity)
        if (!isAvail) {
            val msg = "Network Error, Please check and try again."
            FragmentOpener.instance.showDialog(mainActivity, msg, true)
//            if (shouldFinish)
//                EventBus.getDefault().register(this)
        }
        return isAvail
    }

    fun checkCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkReadSMSPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkReciveSMSPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun callApi(action: ApiAction, showProgress: Boolean = true) {
        apiController.handle(action)
        if (showProgress)
            showProgressBar()
    }

    override fun onFailure(event: ApiAction, msg: String?) {
        hideProgressBar()
        var msg = msg
        if (TextUtils.isEmpty(msg))
            msg = "Something went wrong, Please try again."
        else
            msg = "Error $msg"
        if (!isNetworkAvailable(mainActivity))
            msg = "Network Not Available."

        AppUtils.showToast(msg)
//        showDialog(msg)
    }

    override fun onError(event: ApiAction, data: BaseErrorResponse) {
        hideProgressBar()
        onErrorCall(event, data)
    }

    override fun <T> onResponse(event: ApiAction, t: T) {
        hideProgressBar()
        onResponseCall(event, t)
    }

    fun showProgressBar() {
        AppUtils.showProgressDialog(mainActivity)
    }

    fun hideProgressBar() {
        AppUtils.hideProgressDialog()
    }

    abstract fun onErrorCall(event: ApiAction, data: BaseErrorResponse)
    abstract fun <T> onResponseCall(event: ApiAction, t: T)
}
