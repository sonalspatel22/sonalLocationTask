package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.activities.HomeActivity
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.PromoterSendOtpAction
import com.appilary.radar.api.PromoterVerifyAction
import com.appilary.radar.api.body.PromoterVerifyBody
import com.appilary.radar.api.res.LoginData
import com.appilary.radar.api.res.LoginRes
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import kotlinx.android.synthetic.main.frag_login.btn_verify
import kotlinx.android.synthetic.main.frag_login.password_et
import kotlinx.android.synthetic.main.frag_login.username_et
import kotlinx.android.synthetic.main.frag_verify_promotor.*


/**
 * Created by vi.garg on 8/2/18.
 */
class VerifyPromoterFrag : BaseFragment() {

    var loginData: LoginData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginData = arguments?.getParcelable(LOGIN_DATA_ARG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_verify_promotor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resend_otp.setOnClickListener {
            sendOtp()
        }

        btn_verify.setOnClickListener {
            if (opt_ll.visibility == View.VISIBLE) {
                if (validateMobile() && validateOtp()) {
                    callApi(
                        PromoterVerifyAction(
                            PromoterVerifyBody(
                                username_et.text.toString(),
                                password_et.text.toString(),
                                loginData?.token
                            )
                        )
                    )
                }
            } else {
                sendOtp()
            }
        }
    }

    fun sendOtp() {
        if (validateMobile()) {
            callApi(
                PromoterSendOtpAction(
                    PromoterVerifyBody(
                        username_et.text.toString(),
                        auth = loginData?.token
                    )
                )
            )
        }
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
        if (event is PromoterSendOtpAction) {
            opt_ll?.visibility = View.VISIBLE
            btn_verify?.text = "Verify"
        } else if (event is PromoterVerifyAction) {
                loginData?.let {
                    AppPreference.instance.authToken = it.token
                    AppPreference.instance.setLoginData(it)
                    HomeActivity.open(mainActivity)
            }
        }
//        FragmentOpener.instance.addOrderFrag(mainActivity)
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
        if (data.status == 401)
            data.message = "Wrong Credentials"
        AppUtils.showToast(data.message)
    }

    private fun validateMobile(): Boolean {
        if (username_et.text.trim().isEmpty()) {
            username_et.requestFocus()
            username_et.error = "Please provide Mobile No"
            return false
        }
        if (username_et.length() != 10) {
            password_et.requestFocus()
            password_et.error = "Please provide valid Mobile No"
            return false
        }
        return true
    }


    private fun validateOtp(): Boolean {
        if (password_et.text.trim().isEmpty()) {
            password_et.requestFocus()
            password_et.error = "Please provide Password"
            return false
        }
        return true
    }


    companion object {
        val TAG = VerifyPromoterFrag::class.java.simpleName

        const val LOGIN_DATA_ARG = "login_data_arg"

        @JvmStatic
        fun newInstance(loginData: LoginData): VerifyPromoterFrag {
            val fragment = VerifyPromoterFrag().apply {
                arguments = Bundle().apply {
                    putParcelable(LOGIN_DATA_ARG, loginData)
                }
            }
            return fragment
        }
    }
}