package com.appilary.radar.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.appilary.radar.R
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.activities.LocationUpdated
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.res.DependentDropDownData
import com.appilary.radar.api.res.DropDownData
import com.appilary.radar.api.res.FormField
import com.appilary.radar.api.res.SurveyAppFormData
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.customeview.*
import com.appilary.radar.database.CreateSurveyBody
import com.appilary.radar.database.RealmControler
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.database.survey.ResPElement
import com.appilary.radar.database.survey.SurveyFileRealm
import com.appilary.radar.database.survey.SurveySingleResData
import com.appilary.radar.listners.AlertDialogCallBack
import com.appilary.radar.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_survey.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import kotlinx.coroutines.launch

class SurveyFrag : BaseFragment() {

    var isOpenFormAvailable = false
    var initialPageId = 1
    var firstResFormField: FormField? = null
    val userJourney = mutableListOf<Int>()
    var currentPageId = 0
    var currentPageNo = 0
    val singleSurveyData = SurveySingleResData()
    val pageResMap = HashMap<Int, ResPElement>()
    private val listOfPageView = mutableMapOf<Int, List<CatiCustomView>>()
    val createSurvey: CreateSurveyBody? = RealmControler.getCreateSurveyBody()
    var quesPadding = UiUtils.dpToPx(10)
    val quesLayoutParams by lazy {
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(quesPadding, quesPadding, quesPadding, 0)
        }
    }

    val formSurvey: List<SurveyAppFormData>? by lazy {
        createSurvey?.getAppFormData()
    }

    val totalPage: Int by lazy {
        formSurvey?.size ?: 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mainActivity is BaseActivity) {
            (mainActivity as BaseActivity).findLocation(object :
                LocationUpdated {
                override fun onLocCapture(lat: Double?, lang: Double?, address: String?) {
                    singleSurveyData.lt = lat
                    singleSurveyData.lg = lang
                }
            })
        }

        openFormId()
    }

    private fun openFormId() {
        val openFormId = arguments?.getInt(OPEN_FORM_ID, 0)
        if (openFormId != 0) {
            formSurvey?.firstOrNull { it.id == openFormId }?.let {
                initialPageId = it.id
                isOpenFormAvailable = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_survey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPage(initialPageId)
        showCompanyLogo()
        nextBtn.setOnClickListener {
            onNextClick()
        }

        previousBtn.setOnClickListener {
            onPreviousClick()
        }
    }

    fun addPage(pageId: Int) {
        currentPageId = pageId
        userJourney.add(pageId)
        currentPageNo = currentPageId
        addQuesToPage()
        scroll_view?.post { scroll_view?.scrollTo(0, 0) }
    }

    fun addQuesToPage() {
        val pageId = currentPageId
        val formdata = formSurvey?.filter { item -> item.id == pageId }?.single()
        quesLayout.removeAllViews()
        if (formdata == null)
            return
        titleTextView.text = formdata.title ?: "Form"
        var pageAnsObj = pageResMap[pageId]
        if (pageAnsObj == null) {
            pageAnsObj = ResPElement(pageId = pageId)
            pageResMap[pageId] = pageAnsObj
        }
        val customViewList = mutableListOf<CatiCustomView>()

        val controllList = mutableListOf<FormField>()
        if (!formdata.quesLimit.isNullOrEmpty()) {
            previousBtn.isEnabled = false
            val catMap =
                mutableMapOf<String, MutableList<FormField>>() //<QuesCat, List<Controller>>
            formdata.controls?.forEach {
                val quesCat = it.quesCategory
                if (!quesCat.isNullOrEmpty()) {
                    var list = catMap[quesCat]
                    if (list.isNullOrEmpty())
                        list = mutableListOf()

                    list.add(it)

                    catMap.put(quesCat, list)
                }
            }


            formdata.quesLimit.forEach {
                val list = catMap[it.key]
                if (!list.isNullOrEmpty() && list.size >= it.value) {
                    for (i in 0 until it.value) {
                        val formField = list.random()
                        controllList.add(formField)
                        list.remove(formField)
                    }
                }
            }
        } else if (!formdata.controls.isNullOrEmpty()) {
            controllList.addAll(formdata.controls)
        }



        controllList.forEach { formField ->
            formField.pageId = pageId
            var views: CatiCustomView? = null
            val opType = formField.optype
            if (opType == OP_TYPE_STATIC_LABEL) {
                views = CatiStatementView(formField)
            } else if (opType == OP_TYPE_DATE || opType == OP_TYPE_TIME || opType == OP_TYPE_DATE_TIME) {
                views = CatiDateTimeView(formField)
            } else if (opType == OP_TYPE_RADIO_BUTTON || opType == OP_TYPE_CHECKBOX || opType == OP_TYPE_DROPDOWN) {
                views = CatiMultiChoiceView(formField)
            } else if (opType == OP_TYPE_DEPENDENT_DROPDOWN) {
                if (formdata.id == 2) {
                    val selectedArea = pageResMap!![1]!!.quesList!![1].multipleAns!![0]
                    formField.dependentDropdownKey = selectedArea
                    views = CatiSubDependentDropdownView(formField)
                } else {
                    views = CatiDependentDropdownView(formField)
                }
            } else if (opType == OP_TYPE_INPUT_BOX) {
                if (formdata.otp == 1)
                    views = CatiOTPMobileEditTextView(
                        formField,
                        formdata.sendOtpBtn == 1,
                        formdata.otpBtnDisableTime
                    )
                else
                    views = CatiTextView(formField)
            } else if (opType == OP_TYPE_IMAGE_UPLOAD || opType == OP_TYPE_VIDEO_UPLOAD) {
                val surveyFile = SurveyFileRealm()
                surveyFile.surveyUniqId = singleSurveyData.uniqueId
                surveyFile.surveyId = singleSurveyData.surveyId
                surveyFile.pageId = currentPageId
                surveyFile.quesId = formField.id
                surveyFile.lt = singleSurveyData.lt
                surveyFile.lg = singleSurveyData.lg
                views = CatiFileView(formField, surveyFile)
            } else if (opType == OP_TYPE_GRID) {
                views = CatiMatrixTextBoxView(formField)
            } else if (opType == OP_TYPE_BARCODE_SCANNER || opType == OP_TYPE_QR_CODE_SCANNER) {
                views = CatiBarcodeView(formField)
            } else if (opType == OP_TYPE_DIGITAL_SIGN) {
                val surveyFile = SurveyFileRealm()
                surveyFile.surveyUniqId = singleSurveyData.uniqueId
                surveyFile.surveyId = singleSurveyData.surveyId
                surveyFile.pageId = currentPageId
                surveyFile.quesId = formField.id
                surveyFile.lt = singleSurveyData.lt
                surveyFile.lg = singleSurveyData.lg
                views = CatiSignatureView(formField, surveyFile)
            } else if (opType == OP_TYPE_VIDEO_PLAYBACK) {
                views = CatiVideoPlaybackView(formField)
            }

            if (views != null) {
                views.setFragment(this)
                val quesAns = pageAnsObj.quesList?.firstOrNull { it.quesId == formField.id }
                val quesView = views.init(mainActivity, quesAns)
                quesView?.layoutParams = quesLayoutParams
                quesLayout.addView(quesView)
                customViewList.add(views)
            }
        }

        listOfPageView[pageId] = customViewList
        setNavButton()
    }


    fun showCompanyLogo() {
        val data = AppPreference.instance.getLoginData()
        teamNameTextView1.text = data?.clientName
        val url = data?.logoImg
        if (!url.isNullOrEmpty())
            Picasso.with(mainActivity)
                .load(url)
                .into(teamLogoImageView1)
    }


    fun setNavButton() {
        if (totalPage == currentPageNo) {
            if (totalPage > 1)
                previousBtn.visibility = View.VISIBLE
            else
                previousBtn.visibility = View.GONE
            nextBtn.setText("Done")
        } else if (currentPageNo == 1) {
            previousBtn.visibility = View.GONE
            nextBtn.setText("Next")
        } else if (currentPageNo < totalPage) {
            previousBtn.visibility = View.VISIBLE
            nextBtn.setText("Next")
        }
    }

    fun onNextClick() {
        lifecycleScope.launch {
            try {
                val appFormData = formSurvey?.filter { it.id == currentPageId }?.single()
                val customListView = listOfPageView[currentPageId]
                val listResAElement = mutableMapOf<Int, ResAElement>()
                val resList = mutableListOf<ResAElement>()
                customListView?.forEach { views ->
                    val elementList = views.validateAndGetFillData()
//            val quesId = views.id // for index1

                    if (!views.isValidated) throw Exception()

//            if (elementList.size == 0) continue//Todo
                    elementList?.let {
                        listResAElement.put(views.id, elementList)
                    }

                }

                Log.e("####", Gson().toJson(listResAElement))

                val pageRes = pageResMap[currentPageId]
                pageRes?.let {
                    pageRes.quesList = listResAElement.values.toList()
                    pageResMap[currentPageId] = pageRes
                }

                var formField: FormField? = null
                appFormData?.controls?.forEach {
                    if (it.skipFlag == 1) {
                        formField = it;
                    }
                }

                var nextPageId = currentPageId
                formField.let {
                    if (it != null) {
                        val resItem = listResAElement.get(it.id)
                        if (it.optype == OP_TYPE_RADIO_BUTTON) {
                            singleSurveyData.totalDistance = (activity as BaseActivity).getTotalDistance()
                            val index = it.optList?.indexOf(resItem?.singleAns) ?: 0
                            if (resItem?.singleAns == "Attendance") {
                                    (activity as BaseActivity).startService()
                                Log.e("Click", "Attendance")
                            } else if (resItem?.singleAns == "Day End Selfie") {
                                    (activity as BaseActivity).StopService()
                                Log.e("Click", "Day End Selfie")
                            }
                            if (it.skipLogic != null && it.skipLogic.size > index) {
                                nextPageId = it.skipLogic.get(index)
                            } else
                                nextPageId++
                        } else
                            nextPageId++
                    } else {
                        nextPageId++
                    }
                }

                if (!appFormData?.quesLimit.isNullOrEmpty()) {
                    showCorrectAns(listResAElement)
                }

                if (totalPage == currentPageNo) {
                    showThankyouDialog()
                } else if (appFormData?.end == 1 || nextPageId == 0) {
                    showThankyouDialog()
                } else
                    addPage(nextPageId)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun showThankyouDialog(from: Int = 0) {

        singleSurveyData.setResPDataList(pageResMap.values.toList())
        singleSurveyData.surveyTime = System.currentTimeMillis() - singleSurveyData.dt

        AppUtils.showAlertDialog(mainActivity,
            true,
            false,
            "Thank You",
            "Confirm to Submit your transaction?",
            "Submit",
            "Cancel",
            object : AlertDialogCallBack {
                override fun onClick(isOkSelected: Boolean) {
                    if (isOkSelected) {
                        RealmControler.updateSurveyRes(singleSurveyData)
                        if (isOpenFormAvailable) {
                            val loginData = AppPreference.instance.getLoginData()?.apply {
                                agreementFormId = 0
                            }
                            AppPreference.instance.setLoginData(loginData)
                        }
                        previousBtn.isEnabled = true
                        firstFormResTaken()
                        mainActivity.finish()
                        ApiCallUtils.postForm()
//                        mainActivity.finish()
                    }
                }
            })
    }


    private fun firstFormResTaken() {
        firstResFormField?.let {
            AppPreference.instance.lastTimeFirstResSaved = System.currentTimeMillis()
        }

        firstResFormField = null
    }


    fun onPreviousClick() {
        if (userJourney.size <= 1) return
        userJourney.removeAt(userJourney.size - 1)
        val previousFormIndex: Int = userJourney.get(userJourney.size - 1)
        userJourney.removeAt(userJourney.size - 1)
        addPage(previousFormIndex)
    }

    fun getCustomViewList(): List<CatiCustomView>? {
        return listOfPageView[currentPageId]
    }

    fun showCorrectAns(listResAElement: Map<Int, ResAElement>) {
        var correctAns = 0
        val size = listResAElement.size
        listResAElement.forEach {
            if (it.value.ansMatched == true)
                correctAns++
        }

        AppUtils.showAlertDialog(
            mainActivity, false, false,
            "Your Score", "Correct Answers Given $correctAns out of $size", listener = null
        )
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    companion object {
        val TAG = SurveyFrag::class.java.simpleName
        val OPEN_FORM_ID = "open_form_id"

        @JvmStatic
        fun newInstance(openFormId: Int = 0): SurveyFrag {
            val fragment = SurveyFrag().apply {
                arguments = Bundle().apply {
                    putInt(OPEN_FORM_ID, openFormId)
                }
            }
            return fragment
        }
    }
}
