package com.appilary.radar.api

/**
 * Created by manoj[dot]kumar[at]geminisolutions[dot]in on 11/21/17.
 */

import com.appilary.radar.BuildConfig
import com.appilary.radar.api.body.*
import com.appilary.radar.api.res.*
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppPreference
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object AppRestClientService {

    const val URL = "http://upimg.btlmonitor.com"

    interface Service {

        /**
         * @param body You have to provide full data in LoginBody
         */
        @POST(LOGIN)
        fun postLogin(@Body body: LoginBody): Call<LoginRes>

        @POST(VERIFY_PROMOTER)
        fun sendOtpPromoterVerifyLogin(
            @Query("type") type: Int,
            @Body body: PromoterVerifyBody,
            @Header("Authorization") token: String
        ): Call<BaseErrorResponse>

        @POST(VERIFY_PROMOTER)
        fun promoterVerifyLogin(
            @Query("type") type: Int,
            @Body body: PromoterVerifyBody,
            @Header("Authorization") token: String
        ): Call<BaseErrorResponse>

        @GET(VERSION_API)
        fun getAppVersionData(): Call<AppVersionRes>

        @GET
        fun getSummaryData(@Url url: String): Call<SummaryRes>

        @POST(LOCATION_UPDATE)
        fun locUpdate(@Body body: @JvmSuppressWildcards LocUpdateBody): Call<BaseErrorResponse>

        @GET
        suspend fun getSurvey(@Url url: String): CreateSurveyRes

        @GET
        suspend fun getDependentDropDown(@Url url: String): DependentDropDownRes

        @POST(BREAK_TIME)
        fun breakTime(
            @Query("start") start: Int,
            @Body body: @JvmSuppressWildcards List<BreakTimeBody>
        ): Call<BaseErrorResponse>

        @POST(BREAK_TIME)
        fun breakStartTime(
            @Query("start") start: Int,
            @Body body: @JvmSuppressWildcards BreakTimeBody
        ): Call<BaseErrorResponse>

        @POST
        suspend fun sendOtp(
            @Url url: String,
            @Body body: @JvmSuppressWildcards SendOtpBody
        ): BaseErrorResponse

        @POST
        suspend fun validateOtp(
            @Url url: String,
            @Body body: @JvmSuppressWildcards ValidateOtpBody
        ): BaseErrorResponse

        @POST(UPLOAD_SURVEY)
        fun postSurveyData(@Body body: @JvmSuppressWildcards List<SurveyPostReqBody>): Call<ImageRes>

        @Multipart
        @POST(IMAGE_UPLOAD)
        fun sendFormImage(
            @Part("uniqId") uniId: RequestBody,
            @Part("surveyUniqId") surveyUniqId: RequestBody,
            @Part("surveyId") surveyId: RequestBody,
            @Part("pageId") pageId: RequestBody,
            @Part("quesId") quesId: RequestBody,
            @Part("dt") dt: RequestBody,
            @Part("lt") lt: RequestBody,
            @Part("lg") lg: RequestBody,
            @Part("totalDistance") totalDistance: RequestBody,
            @Part file: MultipartBody.Part
        ): Call<ImageRes>

    }

    val okHttpClient = getOkHttp()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(Service::class.java)
}

fun getOkHttp(): OkHttpClient {
    val okHttpClientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
    }


    val interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
//                .addHeader("Content-Type", "application/json")
            .addHeader("pin", AppPreference.instance.pinNo)
            .addHeader("Authorization", "Basic ${AppPreference.instance.authToken}")
            .build()
        chain.proceed(request)
    }

    okHttpClientBuilder.networkInterceptors().add(interceptor)

    val okHttpClient = okHttpClientBuilder
        .retryOnConnectionFailure(true)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
    return okHttpClient
}
