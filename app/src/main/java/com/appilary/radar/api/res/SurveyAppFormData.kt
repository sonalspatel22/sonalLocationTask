package com.appilary.radar.api.res

import com.google.gson.annotations.SerializedName

data class SurveyAppFormData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("end")
    val end: Int? = null,

    @SerializedName("otp")
    val otp: Int? = null,
    @SerializedName("sendOtpBtn")
    val sendOtpBtn: Int? = null,
    @SerializedName("otpBtnDisableTime")
    val otpBtnDisableTime: Long? = null,

    @SerializedName("controls")
    val controls: List<FormField>? = null,

    @SerializedName("quesLimit")
    val quesLimit: Map<String, Int>? = null
)

data class FormField(
    @SerializedName("id")
    val id: Int = 0,

    var pageId: Int = 0, //For internal use olny

    @SerializedName("label")
    val label: String? = null,
    @SerializedName("desc")
    val desc: String? = null,
    @SerializedName("optype")
    val optype: Int? = null,
    @SerializedName("dtype")
    val keypadType: Int? = null, // keypad 1- numeric, 2- Alpha numeric
    @SerializedName("isOtp")
    val isOtp: Int? = null,
    @SerializedName("isMobile")
    val isMobile: Int? = null,
    @SerializedName("subtype")
    val subtype: Int? = null,//This allows user to enter/select caption while capturing photo. This is required for optype = 6.
//    subtype = 1: Create radio options. (Options can be found in optList key).
//    subtype = 2: Create checkbox options. (Options can be found in optList key).
//    subtype = 3: Create a dropdown (Options can be found in optList key).
//    subtype = 4: Create a textbox

    @SerializedName("mul")
    val mul: Int? = null,//Allow/disallow multiple photos to capture. Default is 0. 0 means don’t allow, 1 means allow. This is required for optype = 6
    @SerializedName("marking")
    val marking: Int? = null,
    @SerializedName("cam_type")
    val camType: Int? = null,//Camera side. Default is 1. 1 means front camera, 2 means back camera. This is required for optype = 6 and 9.
    @SerializedName("ot")
    val ot: Int? = null, //For other option
    @SerializedName("max")
    val max: Int? = null,
    @SerializedName("optList")
    val optList: List<String>? = null, //Contains options list separated by semi-colon for the below controls:optype = 1, 2 ,5
    @SerializedName("optCol")
    val optCol: List<String>? = null,//Label of each column of a grid layout.
    @SerializedName("optRow")
    val optRow: List<String>? = null,//Label of each row of a grid layout.
    @SerializedName("skip_flag")
    val skipFlag: Int = 0,//0 means don’t skip, and 1 means jump to the form.
    @SerializedName("skip_logic")
    val skipLogic: List<Int>? = null,//Defines the list of form/page ID separated by semi-colon for above logic to work.
    @SerializedName("files")//Todo need to be fix from server
    val file: String? = null,//Url of file(s) to display or play (if optype = 17, 18, 19, 21).
    @SerializedName("enableforward")
    val enableforward: Int = 0, // 1-> can forward, 0-> video can not move forward
    @SerializedName("tooltip")
    val tooltip: Int? = null,
    @SerializedName("tooltipText")
    val tooltipText: String? = null,
    @SerializedName("firstRes")
    var firstRes: String? = null,
    @SerializedName("validations")
    val validations: Validation? = null,
    @SerializedName("validationsMsg")
    val validationsMsg: ValidationMsg? = null,
    @SerializedName("dependentDropdownKey")
    var dependentDropdownKey: String? = null,
    @SerializedName("dependentDropdownLabel")
    val dependentDropdownLabel: List<String>? = null,
    @SerializedName("dependentDropdownErrMsg")
    val dependentDropdownErrMsg: List<String>? = null,
    @SerializedName("quesCategory")
    val quesCategory: String? = null,
    @SerializedName("quesAnswer")
    var quesAnswer: String? = null,
    @SerializedName("retainValue")
    val retainValue: Int? = null,
    @SerializedName("imageMarking")
    val imageMarking: Int? = null
)

data class Validation(
    @SerializedName("mn")
    val mn: Int = 0,
    @SerializedName("len")
    val len: String? = null, //"8-10"
    @SerializedName("vtype")
    val vtype: Int? = null, //vtype map
    @SerializedName("min")
    val min: Int? = null,
    @SerializedName("max")
    val max: Int? = null
)

data class ValidationMsg(
    @SerializedName("mn")
    val mn: String? = null,
    @SerializedName("len")
    val len: String? = null,
    @SerializedName("vtype")
    val vtype: String? = null,
    @SerializedName("min")
    val min: String? = null,
    @SerializedName("max")
    val max: String? = null
)