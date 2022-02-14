package com.appilary.radar.api

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.AppPreference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 11/21/17.
 */

//Presenter
interface ApiPresenter {
    fun <T> presentData(event: ApiAction, t: retrofit2.Response<T>)
    fun presentError(event: ApiAction, msg: String?)
}

class ApiPresenterImpl(private val apiResponseDisplay: ApiResponseDisplay) : ApiPresenter, CoroutineScope {
    override val coroutineContext = Job() + Dispatchers.Main

    override fun <T> presentData(event: ApiAction, t: retrofit2.Response<T>) {
        launch(Dispatchers.Main) {
            AppUtils.hideProgressDialog()
            var resCode = 0
            val res = t.body()
            if (res is BaseErrorResponse)
                resCode = res.status

            if (t.isSuccessful && (resCode == 200 || event is SurveyGetAction)) {
//                if (event is LoginAction) {
//                    val auth = t.headers().get("Authorization")
//                    if (auth != null)
//                        AppPreference.instance.authToken = auth
//                }
                apiResponseDisplay.onResponse(event, t.body())
            }else if (t.code() == 401) {
                AppUtils.error401()
            } else if (resCode == 400 && res is BaseErrorResponse)
                apiResponseDisplay.onFailure(event, res.message)
            else {
                try {
                    val error = t.errorBody()?.string()
                    if (!TextUtils.isEmpty(error)) {
                        val gson = Gson()
                        var data: BaseErrorResponse
                        try {
                            data = gson.fromJson<BaseErrorResponse>(error, object : TypeToken<BaseErrorResponse>() {}.type)
                        } catch (e: Exception) {
                            data = gson.fromJson<List<BaseErrorResponse>>(error, object : TypeToken<List<BaseErrorResponse>>() {}.type)[0]
                        }
                        apiResponseDisplay.onError(event, data)
                    } else {
                        val data = BaseErrorResponse()
                        data.status = t.code()
                        data.message = t.message()
                        apiResponseDisplay.onError(event, data)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppUtils.showToast("Something went wrong, Try again")
                }
            }
        }

    }

    override fun presentError(event: ApiAction, msg: String?) {
        launch(Dispatchers.Main) {
            AppUtils.hideProgressDialog()
            Log.e("Error Response ${event.toString()}", "Message $msg")
            apiResponseDisplay.onFailure(event, msg)
        }
    }
}

interface ApiResponseDisplay {
    fun <T> onResponse(event: ApiAction, t: T)
    fun onError(event: ApiAction, error: BaseErrorResponse)
    fun onFailure(event: ApiAction, msg: String?)
}
