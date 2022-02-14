package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.appilary.radar.R
import com.appilary.radar.activities.BaseActivity
import com.appilary.radar.activities.LocationUpdated
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.SummaryDataAction
import com.appilary.radar.api.res.SummaryRes
import com.appilary.radar.api.res.SummaryResData
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.database.RealmControler
import com.appilary.radar.utils.ApiCallUtils
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_dashboard.*
import java.util.*
import kotlin.random.Random


/**
 * Created by vi.garg on 6/2/18.
 */
class DashBoardFrag : BaseFragment() {

    private lateinit var uniqueFormId: String
    private var lat: Double? = null
    private var lang: Double? = null
    private var summaryRes: List<SummaryResData>? = null
    private lateinit var inflater: LayoutInflater
    lateinit var bgcolorList: List<Int>
    var isApiCalling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bgcolorList = listOf(
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_1, null),
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_2, null),
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_3, null),
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_4, null),
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_5, null),
            ResourcesCompat.getColor(mainActivity.resources, R.color.das_6, null)
        )
        inflater = LayoutInflater.from(mainActivity)
        uniqueFormId = UUID.randomUUID().toString()
        ApiCallUtils.postForm()
        callSummaryApi()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.appilary.radar.R.layout.frag_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCompanyLogo()
        if (mainActivity is BaseActivity) {
            (mainActivity as BaseActivity).findLocation(object :
                LocationUpdated {
                override fun onLocCapture(lat: Double?, lang: Double?, address: String?) {
                    this@DashBoardFrag.lat = lat
                    this@DashBoardFrag.lang = lang
                }
            })
        }


//        guideline_bt.setOnClickListener {
//            AppPreference.instance.getRouteResData()?.let {
//                val mmGuide = it.mm_guide
//                if (!mmGuide.isNullOrEmpty()) {
//                    if (mmGuide.contains("pdf")) {
//                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mmGuide))
//                        startActivity(browserIntent)
////                        val intent = Intent()
////                        intent.setDataAndType(Uri.parse(mmGuide), "application/pdf")
////                        startActivity(intent)
//                    } else
//                        FragmentOpener.instance.addImageViewFrag(mainActivity, mmGuide)
//                } else
//                    AppUtils.showToast("Nothing to show")
//            }
//        }


    }

//    @Subscribe
//    fun eventCallback(event: DialogCallbackEvent) {
//        unregisterBus()
//        if (event.isSuccess) {
//            AppUtils.logout()
//            FragmentOpener.instance.addLoginFrag(mainActivity)
//        }
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        unregisterBus()
//    }
//
//    private fun unregisterBus() {
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this)
//        }
//    }


    override fun onFailure(event: ApiAction, msg: String?) {
        super.onFailure(event, msg)
        progress_bar?.visibility = View.GONE
        isApiCalling = false
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
        AppUtils.showToast(data.message)
        progress_bar?.visibility = View.GONE
        isApiCalling = false
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
        if (event is SummaryDataAction) {
            isApiCalling = false
            summaryRes = (t as SummaryRes).resData
            showUI()
        }
    }

    fun updateValue() {
        if (!isApiCalling && summaryRes == null) {
            callSummaryApi()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateValue()
        }
    }

    override fun onResume() {
        super.onResume()
        updateValue()
    }

    private fun showUI() {
        main_ll?.removeAllViews()
        summaryRes?.forEach { summaryRes ->
            val summaryView = inflater.inflate(R.layout.item_summary, null)
            val item = Random.nextInt(bgcolorList.size - 1)
            val cardView = summaryView.findViewById<MaterialCardView>(R.id.card_view)
            cardView.setCardBackgroundColor(bgcolorList[item])
            summaryView.findViewById<TextView>(R.id.title_tv).text = summaryRes.title
            val itemLl = summaryView.findViewById<LinearLayout>(R.id.item_ll)
            summaryRes.summaryList?.forEach {
                val itemView = inflater.inflate(R.layout.item_summary_item, null)
                itemView.findViewById<TextView>(R.id.key_tv).text = it.label
                itemView.findViewById<TextView>(R.id.value_tv).text = it.value
                itemLl.addView(itemView)
            }
            main_ll?.addView(summaryView)
        }

        progress_bar?.visibility = View.GONE
    }


    fun showCompanyLogo() {
        val data = AppPreference.instance.getLoginData()
        userName.text = "Welcome ${data?.clientName}"
        val url = data?.logoImg
        if (url.isNullOrEmpty())
            logo.visibility = View.GONE
        else {
            logo.visibility = View.VISIBLE
            Picasso.with(mainActivity)
                .load(url)
                .error(R.drawable.app_logo)
                .into(logo)
        }
    }

    fun callSummaryApi() {
        if (isApiCalling)
            return
        isApiCalling = true
        RealmControler.getCreateSurveyBody()?.getAppConfigData()?.apiUrls?.statsUrl.let {
            if (it.isNullOrEmpty())
                isApiCalling = false
            else {
                progress_bar?.visibility = View.VISIBLE
                callApi(SummaryDataAction(it), false)
            }
        }
    }

    companion object {
        val TAG = DashBoardFrag::class.java.simpleName

        @JvmStatic
        fun newInstance(): DashBoardFrag {
            val fragment = DashBoardFrag()
            return fragment
        }
    }
}