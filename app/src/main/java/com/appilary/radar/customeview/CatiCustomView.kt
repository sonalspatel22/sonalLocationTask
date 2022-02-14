package com.appilary.radar.customeview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.appilary.radar.R
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement
import com.appilary.radar.frag.BaseFragment
import com.appilary.radar.frag.SurveyFrag

/**
 * Created by vi.garg on 20/7/16.
 */
abstract class CatiCustomView(var formField: FormField) {
    var errorText: TextView? = null
    var quesTv: TextView? = null
    lateinit var mContext: Context
    lateinit var inflater: LayoutInflater
    lateinit var itemLayout: LinearLayout
    var resData: ResAElement? = null
    var optionList: List<String>? = null
    var fragment: BaseFragment? = null
    var isValidated = false
    abstract fun setQuesLayout()
    fun init(
        activity: FragmentActivity,
        resData: ResAElement?
    ): View? {
        this.resData = resData
        mContext = activity
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var layoutView: View? = null
        val questionFamily = formField.optype
        //        if (questionFamily.equals(EnumClass.QuestionFamilyEnum.matrix.name())) {
//            String subType = formField.getFieldType().getSubtype();
//            if (subType.equals(EnumClass.QuesFamilySubtypeEnum.dropdown.name()) ||
//                    subType.equals(EnumClass.QuesFamilySubtypeEnum.checkbox.name()) ||
//                    subType.equals(EnumClass.QuesFamilySubtypeEnum.radio.name()) ||
//                    subType.equals(EnumClass.QuesFamilySubtypeEnum.graphical.name()) ||
//                    subType.equals(EnumClass.QuesFamilySubtypeEnum.textBox.name()))
//                layoutView = inflater.inflate(R.layout.cati_common_matrix_view, null);
//            else
//                layoutView = inflater.inflate(R.layout.cati_common_view, null);
//        } else
        layoutView = inflater.inflate(R.layout.cati_common_view, null)
        quesTv = layoutView.findViewById<View>(R.id.ques) as TextView
        errorText = layoutView.findViewById<View>(R.id.cati_error_tv) as TextView
        itemLayout = layoutView.findViewById<View>(R.id.item_ll) as LinearLayout
        if (formField != null) setQuesLayout()
        setQuestionText(quesTv)
        return layoutView
    }

    val id: Int
        get() = formField.id

    fun setQuestionText(quesTv: TextView?) {
        val required = formField.validations?.mn == 1
        val questionText = formField.label
        var prefix: String
        if (required) {
            hideErrorText()
            errorText?.text = formField.validationsMsg?.mn
            //            if (fragment.showAsterisks())
//                questionText = "*" + questionText;
        }
        quesTv?.text = questionText
        //        quesTv.setTextColor(Color.parseColor(fragment.getQuestionTextColor())); //Todo
    }

    suspend fun validateAndGetFillData(): ResAElement? {
        isValidated = true
        var res = fillData()

        if (!res?.errorMsg.isNullOrEmpty() || (res == null && formField.validations?.mn == 1)) {
            isValidated = false
            showErrorText(res?.errorMsg ?: formField.validationsMsg?.mn)
            res = null
        } else
            hideErrorText()
        return res
    }

    fun setFragment(fragment: SurveyFrag) {
        this.fragment = fragment
    }


    suspend abstract fun fillData(): ResAElement?

    fun showErrorText(str: String? = null) {
        if (errorText != null) {
            val scrollView = fragment?.view?.findViewById<ScrollView>(R.id.scroll_view)
            if (str.isNullOrEmpty()) {
                scrollView?.post {
                    errorText?.parent?.requestChildFocus(errorText, errorText)
                }
            } else {
                scrollView?.post { scrollView.scrollTo(0, errorText!!.bottom) }
                errorText?.text = str
            }
            errorText?.visibility = View.VISIBLE
        }
    }

    fun hideErrorText() {
        errorText?.visibility = View.GONE
    }

}