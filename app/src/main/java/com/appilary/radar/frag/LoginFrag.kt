package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.activities.HomeActivity
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.LoginAction
import com.appilary.radar.api.body.LoginBody
import com.appilary.radar.api.res.LoginRes
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.FragmentOpener
import kotlinx.android.synthetic.main.frag_login.*


/**
 * Created by vi.garg on 8/2/18.
 */
class LoginFrag : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_verify.setOnClickListener {
            if (validate()) {
                callApi(
                    LoginAction(
                        LoginBody(
                            username_et.text.toString(),
                            password_et.text.toString()
                        )
                    )
                )
            }
        }
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
        if (event is LoginAction) {
            val res = t as LoginRes
            if (res.tokenClass != null) {
                if (res.tokenClass.showOtpScreen) {
                    FragmentOpener.instance.addVerifyPromoterFrag(mainActivity, res.tokenClass)
                } else {
                    AppPreference.instance.authToken = res.tokenClass.token
                    AppPreference.instance.setLoginData(res.tokenClass)
                    HomeActivity.open(mainActivity)
                }
            }
        }
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
        if (data.status == 401)
            data.message = "Wrong Credentials"
        AppUtils.showToast(data.message)
    }

    private fun validate(): Boolean {
        if (username_et.text.trim().isEmpty()) {
            username_et.requestFocus()
            username_et.error = "Please provide UserName"
            return false
        }
        if (password_et.text.trim().isEmpty()) {
            password_et.requestFocus()
            password_et.error = "Please provide Password"
            return false
        }
        return true
    }


    companion object {
        val TAG = LoginFrag::class.java.simpleName

        @JvmStatic
        fun newInstance(): LoginFrag {
            val fragment = LoginFrag()
            return fragment
        }
    }
}