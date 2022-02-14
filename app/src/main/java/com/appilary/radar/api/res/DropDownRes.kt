package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName


//State -> Zone -> City -> Area
data class DropDownRes(
        @SerializedName("response")
        val resData: ResData? = null) : BaseErrorResponse()

data class ResData(@SerializedName("address")
                   val stateList: List<DropDownData>? = null,
                   @SerializedName("doctorType")
                   val docTypeList: List<DropDownData>? = null,
                   @SerializedName("doctorQualification")
                   val docQualiList: List<DropDownData>? = null,
                   @SerializedName("doctorDrink")
                   val docDrinkList: List<DropDownData>? = null,
                   @SerializedName("doctorsList")
                   val doctorsList: List<DoctorData>? = null,
                   @SerializedName("chemistsList")
                   val chemistsList: List<ChemistData>? = null,
                   @SerializedName("range")
                   val range: Int = 800)

data class DropDownData(
        @SerializedName("label")
        val label: String = "",
        @SerializedName("value")
        val value: String = "",
        @SerializedName("isDoctor")
        val isDoctor: Boolean? = null,
        @SerializedName("options")
        val options: List<DropDownData>? = null) {
    override fun toString(): String {
        return label
    }
}

data class DoctorData(
        @SerializedName("id")
        val id: String = "",
        @SerializedName("state")
        val state: String = "",
        @SerializedName("zone")
        val zone: String? = null,
        @SerializedName("city")
        val city: String = "",
        @SerializedName("area")
        val area: String = "",
        @SerializedName("address")
        val address: String? = null,
        @SerializedName("landmark")
        val landmark: String = "",
        @SerializedName("pincode")
        val pincode: String? = null,
        @SerializedName("doctorName")
        val doctorName: String? = null,
        @SerializedName("qualification")
        val qualification: String? = null,
        @SerializedName("contact")
        val contact: String = "",
        @SerializedName("category")
        val category: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("visitingHrPatients")
        val visitingHrPatients: String? = null,
        @SerializedName("visitingHrMR")
        val visitingHrMR: String? = null,
        @SerializedName("patientsDaily")
        val patientsDaily: String? = null,
        @SerializedName("lt")
        val lt: Double? = null,
        @SerializedName("lg")
        val lg: Double? = null) {

    fun getFullAddress() :String {
        return "$doctorName $address, $area, near $landmark, $city -$pincode, $state"
    }

    override fun toString(): String {
        return "$doctorName"
    }
}

data class ChemistData(
        @SerializedName("id")
        val id: String = "",
        @SerializedName("state")
        val state: String = "",
        @SerializedName("zone")
        val zone: String? = null,
        @SerializedName("city")
        val city: String = "",
        @SerializedName("area")
        val area: String = "",
        @SerializedName("address")
        val address: String? = null,
        @SerializedName("landmark")
        val landmark: String = "",
        @SerializedName("pincode")
        val pincode: String? = null,
        @SerializedName("shopName")
        val shopName: String = "",
        @SerializedName("shopOwnerName")
        val shopOwnerName: String = "",
        @SerializedName("contact")
        val contact: String = "",
        @SerializedName("shopHr")
        val shopHr: String? = null,
        @SerializedName("stockiestName")
        val stockiestName: String = "",
        @SerializedName("nearbyDoctor")
        val nearbyDoctor: String? = null,
        @SerializedName("lt")
        val lt: Double? = null,
        @SerializedName("lg")
        val lg: Double? = null) {

    fun getFullAddress() :String {
        return "$shopName $address, $area, near $landmark, $city -$pincode, $state"
    }

    override fun toString(): String {
        return "$shopName"
    }
}
