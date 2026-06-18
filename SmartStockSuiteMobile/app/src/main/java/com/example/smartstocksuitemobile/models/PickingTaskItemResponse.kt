package com.example.smartstocksuitemobile.models

import com.google.gson.annotations.SerializedName

data class PickingTaskItemResponse(
    @SerializedName(value = "pickingTaskItemId", alternate = ["PickingTaskItemId"])
    val pickingTaskItemId: Int = 0,

    @SerializedName(value = "productId", alternate = ["ProductId"])
    val productId: Int = 0,

    @SerializedName(value = "productLocationId", alternate = ["ProductLocationId"])
    val productLocationId: Int = 0,

    @SerializedName(value = "quantityToPick", alternate = ["QuantityToPick"])
    val quantityToPick: Int = 0,

    @SerializedName(value = "pickedQuantity", alternate = ["PickedQuantity"])
    val pickedQuantity: Int = 0,

    @SerializedName(value = "isPicked", alternate = ["IsPicked"])
    val isPicked: Boolean = false,

    @SerializedName(value = "scannedBarcode", alternate = ["ScannedBarcode"])
    val scannedBarcode: String? = null,

    @SerializedName(value = "product", alternate = ["Product"])
    val product: ProductInPickingResponse? = null,

    @SerializedName(value = "productLocation", alternate = ["ProductLocation"])
    val productLocation: ProductLocationInPickingResponse? = null
)

data class ProductInPickingResponse(
    @SerializedName(value = "productId", alternate = ["ProductId"])
    val productId: Int = 0,

    @SerializedName(value = "productName", alternate = ["ProductName"])
    val productName: String? = null,

    @SerializedName(value = "category", alternate = ["Category"])
    val category: String? = null,

    @SerializedName(value = "barcode", alternate = ["Barcode"])
    val barcode: String? = null,

    @SerializedName(value = "quantity", alternate = ["Quantity"])
    val quantity: Int = 0
)

data class ProductLocationInPickingResponse(
    @SerializedName(value = "productLocationId", alternate = ["ProductLocationId"])
    val productLocationId: Int = 0,

    @SerializedName(value = "warehouseName", alternate = ["WarehouseName"])
    val warehouseName: String? = null,

    @SerializedName(value = "aisle", alternate = ["Aisle"])
    val aisle: String? = null,

    @SerializedName(value = "rack", alternate = ["Rack"])
    val rack: String? = null,

    @SerializedName(value = "shelf", alternate = ["Shelf"])
    val shelf: String? = null,

    @SerializedName(value = "bin", alternate = ["Bin"])
    val bin: String? = null,

    @SerializedName(value = "quantity", alternate = ["Quantity"])
    val quantity: Int = 0
)