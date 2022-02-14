package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

//if activity_type = 1, enable Campaign icon/button
//if activity_type = 2, enable Retention icon/button
//if activity_type = 3, enable TSR icon/button
//if activity_type = 4, enable CR icon/button
//if activity_type = 5, enable PMM icon/button

data class RouteRes(
        @SerializedName("response")
        val routeResData: RouteResData? = null) : BaseErrorResponse()

data class RouteResData(
        @SerializedName("activity_name")
        val activityName: String? = null,
        @SerializedName("activity_type")
        val activityType: Int = 0,
        @SerializedName("mm_guide")
        val mm_guide: String? = null,
        @SerializedName("target_today")
        val target_today: Int = 0,
        @SerializedName("routes_assigned_today")
        val routes_assigned_today: String? = null,
        @SerializedName("route_list")
        var routeDataList: List<RouteData>? = null
)

data class RouteData(
        @SerializedName("route_name") val routeName: String = "",
        @SerializedName("scope_outlets") val scopeOutlets: Long = 0,
        @SerializedName("total_outlets") val totalOutlets: Long = 0,
        @SerializedName("is_assigned") val isAssigned: Boolean = false,
        @SerializedName("campaign_scope_outlet") val campaignScopeOutlet: Long = 0,
        @SerializedName("outlets") var outlets: List<OutletData>? = null) {
    override fun toString(): String {
        return routeName
    }
}


// status == 0 Panding
// status == 1 Done
// status ==

data class OutletData(
        @SerializedName("outlet_id") val outlet_id: Long = 0,
        @SerializedName("scope_outlet") val scopeOutlet: String? = null,
        @SerializedName("owner_name") val ownerName: String? = null,
        @SerializedName("retailer_name") val retailerName: String? = null,
        @SerializedName("mobile_no") val mobileNo: String? = null,
        @SerializedName("outlet_details") val outletDetail: String? = null,
        @SerializedName("activity_done_by") val doneByOwn: Boolean = false,
        @SerializedName("lt") val lt: Double = 0.0,
        @SerializedName("lg") val lg: Double = 0.0,
        @SerializedName("activity_done") var status: Int = 0
)

