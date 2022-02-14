package com.appilary.radar.api.res

import com.appilary.radar.api.response.BaseErrorResponse
import com.google.gson.annotations.SerializedName

data class InventoryRes(
        @SerializedName("response")
        val inventoryData: InventoryData? = null) : BaseErrorResponse()

data class InventoryData(
        @SerializedName("stock_confirmed")
        var stockConfirmed: Int = 0,
        @SerializedName("material")
        var inventoryItemList: List<InventoryDataItem>? = null
) {
    fun getCountableList(): List<InventoryDataItem>? {
        return inventoryItemList?.filter {
            it.isCountable
        }
    }
}

data class InventoryDataItem(
        @SerializedName("material_type") val materialType: String = "",
        @SerializedName("isCountable") val isCountable: Boolean = true,
        @SerializedName("isPosm") val isPosm: Boolean = false,
        @SerializedName("limit") var limit: Int = 0,
        @SerializedName("material_list") var material_list: List<MaterialInventoryData>? = null)

data class MaterialInventoryData(
        @SerializedName("material_id") val matId: Long = 0,
        @SerializedName("material_name") val materialName: String = "",
        @SerializedName("allocated_qty") val allocated_qty: Int = 0,
        @SerializedName("inhand_qty") val inhand_qty: Int = 0,
        @SerializedName("total_qty") val total_qty: Int = 0,
        @SerializedName("blank_mat_list") val blankMatList: List<BlankMaterialData>? = null,
        @SerializedName("used_qty") var usedQty: Int = 0
) {
    fun getAvailableQty(): Int {
        var avlQty = total_qty - usedQty
        if (avlQty < 0)
            avlQty = 0
        return avlQty
    }
}


data class BlankMaterialData(
        @SerializedName("material_id") val matId: Long = 0,
        @SerializedName("material_name") val materialName: String = "",
        @SerializedName("allowed_max_qty") val allowed_max_qty: Int = 0
)