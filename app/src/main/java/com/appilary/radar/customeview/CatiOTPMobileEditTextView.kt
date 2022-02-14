package com.appilary.radar.customeview

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.appilary.radar.R
import com.appilary.radar.api.AppRestClientService
import com.appilary.radar.api.body.SendOtpBody
import com.appilary.radar.api.body.ValidateOtpBody
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.RealmControler
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.frag.SurveyFrag
import com.appilary.radar.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiOTPMobileEditTextView(
    model: FormField,
    val isSendOtp: Boolean = false,
    val otpDisableTime: Long? = 0
) : CatiCustomView(model) {
    var otpSent = false

    override fun setQuesLayout() {
        val view = addLayoutView()
        itemLayout.addView(view)
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (quesTv != null) {
            val count = itemLayout.childCount
            for (i in 0 until count) {
                val layout = itemLayout.getChildAt(i)
                val catiTextEntry = layout.findViewById<CatiTextEntry>(R.id.cati_text_entry)

                val isValid = catiTextEntry.checkValidation()
                if (!isValid)
                    return null

                if (formField.isMobile == 1 && !otpSent) {
                    val element = ResAElement(id)
                    element.errorMsg = "Please send Otp"
                    return element
                }

                val text = catiTextEntry.textValue ?: ""

                if (text.trim().isNotEmpty()) {
                    val element = ResAElement(id)
                    element.singleAns = text
                    if (formField.isOtp == 1) {
                        fragment?.showProgressBar()
                        withContext(Dispatchers.IO) {
                            val msg = withContext(Dispatchers.IO) {
                                verifyOtp(text)
                            }
                            element.errorMsg = msg
                        }
                        fragment?.hideProgressBar()
                    }
                    return element
                }
            }
        }
        return null
    }

    private fun addLayoutView(): View {
        val view = inflater.inflate(R.layout.cati_otp_mobile_text, null)
        val title = view.findViewById<TextView>(R.id.title)
        val optBtn = view.findViewById<View>(R.id.btn_sendotp)
        val catiTextEntry = view.findViewById<View>(R.id.cati_text_entry) as CatiTextEntry
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText
        catiTextEntry.initTextEntry(formField, resData?.singleAns ?: "", null)
        if (formField.isMobile == 1 && isSendOtp) {
            optBtn.visibility = View.VISIBLE
            optBtn.setOnClickListener {
                val isValid = catiTextEntry.checkValidation()
                if (isValid) {
                    if (otpDisableTime != null && otpDisableTime > 0) {
                        fragment?.lifecycleScope?.launch {
                            optBtn.isEnabled = false
                            delay(otpDisableTime)
                            optBtn.isEnabled = true
                        }

                        fragment?.lifecycleScope?.launch {
                            callSendOtp(catiTextEntry.textValue)
                        }
                    }
                } else {
                    showErrorText(formField.validationsMsg?.mn)
                }
            }
        }


        return view
    }

    suspend fun callSendOtp(textValue: String?) {
        fragment?.showProgressBar()
        try {
            val result = withContext(Dispatchers.IO) {
                RealmControler.getCreateSurveyBody()?.getAppConfigData()
                    ?.apiUrls?.sendOtpUrl?.let {
                        AppRestClientService.service.sendOtp(
                            it,
                            SendOtpBody(textValue ?: "")
                        )
                    }
            }
            otpSent = true
            AppUtils.showToast(result?.message)
        } catch (e: Exception) {
            if (e is IOException)
                AppUtils.showToast("No Internet Available")
            else
                AppUtils.showToast("Please try after some time")
        }
        fragment?.hideProgressBar()
    }

    suspend fun verifyOtp(otp: String): String? {
        fragment?.let {
            var mobileNo: String? = null
            run loop@{
                (it as SurveyFrag).getCustomViewList()?.forEach {
                    if (it.isValidated && it.formField.isMobile == 1) {
                        mobileNo = it.validateAndGetFillData()?.singleAns
                        return@loop
                    }
                }
            }
            return callVerifyOtp(mobileNo, otp)
        }

        return null
    }


    suspend fun callVerifyOtp(mobile: String?, otp: String?): String? {
        try {
            val result = withContext(Dispatchers.IO) {
                RealmControler.getCreateSurveyBody()?.getAppConfigData()
                    ?.apiUrls?.validateOtpUrl?.let {
                        AppRestClientService.service.validateOtp(
                            it,
                            ValidateOtpBody(otp ?: "", mobile)
                        )
                    }
            }
            if (result?.status == 200)
                return null
            else
                return result?.message
        } catch (e: Exception) {
            return null
        }
    }
}