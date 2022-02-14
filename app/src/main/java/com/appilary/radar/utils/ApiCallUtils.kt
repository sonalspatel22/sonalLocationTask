package com.appilary.radar.utils

import android.text.TextUtils
import com.appilary.radar.App
import com.appilary.radar.api.*
import com.appilary.radar.api.body.LocUpdateBody
import com.appilary.radar.api.res.ImageRes
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.database.RealmControler

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 7/4/17.
 */
class ApiCallUtils {
    companion object {
        var isFormUploading = false
        var isImageUploading = false
        var isBreakUploading = false

        fun postForm() {
            postSurveyData()
            postImage()
            breakApiData()
        }

        fun postSurveyData() {
            val context = App.mInstance
            if (TextUtils.isEmpty(AppPreference.instance.authToken) || !isNetworkAvailable(context))
                return

            val formList = RealmControler.getSurveyResList()
            if (formList.isEmpty())
                return

            if (!isFormUploading) {
                isFormUploading = true
                val apiController: ApiController =
                    ApiControllerImpl(ApiPresenterImpl(object : ApiResponseDisplay {
                        override fun <T> onResponse(event: ApiAction, t: T) {
                            val body = event as SurveyPostAction
                            RealmControler.removePostSurveyList(body.body)
                            isFormUploading = false
                        }

                        override fun onError(event: ApiAction, error: BaseErrorResponse) {
                            isFormUploading = false
                        }

                        override fun onFailure(event: ApiAction, msg: String?) {
                            isFormUploading = false
                        }
                    }))

                apiController.handle(SurveyPostAction(AppUtils.surveyPostCopy(formList)))
            }

        }

        fun postImage() {
            val context = App.mInstance
            if (TextUtils.isEmpty(AppPreference.instance.authToken) || !isNetworkAvailable(context))
                return

            val docList = RealmControler.getSurveyFileList()
            if (docList.size == 0)
                return

            val emptyPath = mutableListOf<String>() //uniqueId

            if (!isImageUploading) {
                for (item in docList) {
                    if (item.filePath.isNullOrEmpty()) {
                        emptyPath.add(item.uniqId)
                        continue
                    }

                    isImageUploading = true
                    val apiController: ApiController =
                        ApiControllerImpl(ApiPresenterImpl(object : ApiResponseDisplay {
                            override fun <T> onResponse(event: ApiAction, t: T) {
                                val res = t as ImageRes
                                RealmControler.removeSurveyFile(res.imgId)
                                isImageUploading = false
                            }

                            override fun onError(event: ApiAction, error: BaseErrorResponse) {
                                isImageUploading = false
                            }

                            override fun onFailure(event: ApiAction, msg: String?) {
                                isImageUploading = false
                            }
                        }))

                    apiController.handle(
                        ImageUploadAction(
                            item.filePath,
                            item.uniqId,
                            item.surveyId,
                            item.pageId,
                            item.quesId,
                            item.surveyUniqId,
                            item.dt,
                            item.lt,
                            item.lg,
                            item.totalDistance
                        )
                    )
                }

                emptyPath.forEach {
                    RealmControler.removeSurveyFile(it)
                }
            }
        }


        fun sendLocation(lt: Double, lg: Double) {

            if (TextUtils.isEmpty(AppPreference.instance.authToken) || !isNetworkAvailable(App.mInstance))
                return
            val apiController: ApiController =
                ApiControllerImpl(ApiPresenterImpl(object : ApiResponseDisplay {
                    override fun <T> onResponse(event: ApiAction, t: T) {
                        AppPreference.instance.lastLocSend = System.currentTimeMillis()
                    }

                    override fun onError(event: ApiAction, error: BaseErrorResponse) {
                    }

                    override fun onFailure(event: ApiAction, msg: String?) {
                    }
                }))

            apiController.handle(LocUpdateAction(LocUpdateBody(lt, lg)))

        }


        fun breakApiData() {

            val list = AppPreference.instance.getBreakTimeData()?.filter { it.endDt != null }
            if (list.isNullOrEmpty())
                return

            if (TextUtils.isEmpty(AppPreference.instance.authToken) || !isNetworkAvailable(App.mInstance))
                return

            if (!isBreakUploading) {
                isBreakUploading = true
                val apiController: ApiController =
                    ApiControllerImpl(ApiPresenterImpl(object : ApiResponseDisplay {
                        override fun <T> onResponse(event: ApiAction, t: T) {
                            AppPreference.instance.removeBreakTime(list)
                            isBreakUploading = false
                        }

                        override fun onError(event: ApiAction, error: BaseErrorResponse) {
                            isBreakUploading = false
                        }

                        override fun onFailure(event: ApiAction, msg: String?) {
                            isBreakUploading = false
                        }
                    }))

                apiController.handle(BreakTimeAction(2, list))
            }
        }

    }

}