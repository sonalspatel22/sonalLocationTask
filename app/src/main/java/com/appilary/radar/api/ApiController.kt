package com.appilary.radar.api

import com.appilary.radar.api.body.*
import com.appilary.radar.api.res.AppVersionRes
import com.appilary.radar.api.res.ImageRes
import com.appilary.radar.api.res.LoginRes
import com.appilary.radar.api.res.SummaryRes
import com.appilary.radar.api.response.BaseErrorResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 11/21/17.
 */
//Controller
sealed class ApiAction

const val TAG: String = "API"

class LoginAction(val body: LoginBody) : ApiAction()
class PromoterVerifyAction(val body: PromoterVerifyBody) : ApiAction()
class PromoterSendOtpAction(val body: PromoterVerifyBody) : ApiAction()
class AppVersionAction : ApiAction()
class SummaryDataAction(val url: String) : ApiAction()
class SurveyGetAction(val url: String) : ApiAction()
class DependentDropDownAction(val url: String) : ApiAction()
class LocUpdateAction(val body: LocUpdateBody) : ApiAction()
class BreakStartTimeAction(val start: Int = 1, val body: BreakTimeBody) : ApiAction()
class BreakTimeAction(val start: Int, val body: List<BreakTimeBody>) : ApiAction()
class SurveyPostAction(val body: List<SurveyPostReqBody>) : ApiAction()
class ImageUploadAction(
    val path: String?,
    val uniqId: String,
    val surveyId: Int?,
    val pageId: Int?,
    val quesId: Int?,
    val surveyUniqId: String?,
    val dt: Long,
    val lt: Double?,
    val lg: Double?,
    val td: Float?
) : ApiAction()


interface ApiController {
    fun handle(apiAction: ApiAction): Boolean
}

class ApiControllerImpl(private val interactor: ApiPresenter) : ApiController {
    //Default The actor will not accept any message while being occupied. If we quickly click on a button twice, only the first click will be handled.
    //capacity = Channel.CONFLATED While being busy, the actor will only remember the last message that it received.
    //capacity = Channel.UNLIMITED Here, we set up the actor with an unlimited buffer (the channel is a linked list). This way, we store every message that are received during computation, and the actor will handle them all.
    //capacity = 5 Here, the buffer has a limited size: only the 5 last messages will be handled.

    private val actor = GlobalScope.actor<ApiAction>(capacity = 2) {
        for (event in channel) {
            try {
                when (event) {

                    is LoginAction ->
                        interactor.presentData(event, loginUser(event.body))

                    is PromoterSendOtpAction ->
                        interactor.presentData(event, promoterVerifySendOtp(event.body))

                    is PromoterVerifyAction ->
                        interactor.presentData(event, promoterVerify(event.body))
//
//                    is DependentDropDownAction ->
//                        interactor.presentData(event, getDependentDropDown(event.url))

                    is SummaryDataAction ->
                        interactor.presentData(event, getSummaryData(event.url))

                    is AppVersionAction ->
                        interactor.presentData(event, getAppVersion())

                    is SurveyPostAction ->
                        interactor.presentData(event, postSurveyData(event.body))

                    is LocUpdateAction ->
                        interactor.presentData(event, locUpdate(event.body))

                    is BreakTimeAction ->
                        interactor.presentData(event, breakTime(event.start, event.body))

                    is BreakStartTimeAction ->
                        interactor.presentData(event, breakStartTime(event.start, event.body))

                    is ImageUploadAction ->
                        interactor.presentData(
                            event,
                            sendFormImage(
                                event.path,
                                event.uniqId,
                                event.surveyId,
                                event.pageId,
                                event.quesId,
                                event.surveyUniqId,
                                event.dt,
                                event.lt,
                                event.lg,
                                event.td
                            )
                        )

                }
            } catch (ex: Exception) {
                interactor.presentError(event, ex.message)
            }
        }
    }

    override fun handle(apiAction: ApiAction): Boolean {
        return actor.offer(apiAction)
    }


    private fun loginUser(body: LoginBody): Response<LoginRes> =
        AppRestClientService.service.postLogin(body).execute()

    private fun promoterVerifySendOtp(body: PromoterVerifyBody): Response<BaseErrorResponse> =
        AppRestClientService.service.sendOtpPromoterVerifyLogin(1, body, "Basic ${body.auth}")
            .execute()

    private fun promoterVerify(body: PromoterVerifyBody): Response<BaseErrorResponse> =
        AppRestClientService.service.promoterVerifyLogin(2, body, "Basic ${body.auth}").execute()

    private fun getSummaryData(url: String): Response<SummaryRes> =
        AppRestClientService.service.getSummaryData(url).execute()

//    private fun getSurvey(url: String): Response<CreateSurveyRes> =
//        AppRestClientService.service.getSurvey(url).execute()
//
//    private fun getDependentDropDown(url: String): Response<DependentDropDownRes> =
//        AppRestClientService.service.getDependentDropDown(url).execute()

    private fun postSurveyData(body: List<SurveyPostReqBody>): Response<ImageRes> =
        AppRestClientService.service.postSurveyData(body).execute()

    private fun locUpdate(body: LocUpdateBody): Response<BaseErrorResponse> =
        AppRestClientService.service.locUpdate(body).execute()

    private fun breakTime(start: Int, body: List<BreakTimeBody>): Response<BaseErrorResponse> =
        AppRestClientService.service.breakTime(start, body).execute()

    private fun breakStartTime(start: Int, body: BreakTimeBody): Response<BaseErrorResponse> =
        AppRestClientService.service.breakStartTime(start, body).execute()

    private fun getAppVersion(): Response<AppVersionRes> =
        AppRestClientService.service.getAppVersionData().execute()

    private fun sendFormImage(
        path: String?,
        uniqId: String,
        surveyId: Int?,
        pageId: Int?,
        quesId: Int?,
        surveyUniqId: String?,
        dt: Long,
        lt: Double?,
        lg: Double?,
        td: Float?
    ): Response<ImageRes> {
        val uniqIdBody = uniqId.toRequestBody("text/plain".toMediaTypeOrNull())
        val surveyUniqIdBody = (surveyUniqId ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        val surveyIdBody = "$surveyId".toRequestBody("text/plain".toMediaTypeOrNull())
        val pageIdBody = "$pageId".toRequestBody("text/plain".toMediaTypeOrNull())
        val quesIdBody = "$quesId".toRequestBody("text/plain".toMediaTypeOrNull())
        val dtBody = "$dt".toRequestBody("text/plain".toMediaTypeOrNull())
        val ltBody = "${lt ?: 0}".toRequestBody("text/plain".toMediaTypeOrNull())
        val lgBody = "${lg ?: 0}".toRequestBody("text/plain".toMediaTypeOrNull())
        val totalDistanceBody ="${td ?: 0.0F}".toRequestBody("text/plain".toMediaTypeOrNull())
        val file = File(path)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return AppRestClientService.service.sendFormImage(
            uniqIdBody,
            surveyUniqIdBody,
            surveyIdBody,
            pageIdBody,
            quesIdBody,
            dtBody,
            ltBody,
            lgBody,
            totalDistanceBody,
            part
        ).execute()
    }

}

