package com.example.smartstocksuitemobile.models

import com.google.gson.annotations.SerializedName

data class InventoryScanResponse(
    @SerializedName(value = "productLocationId", alternate = ["ProductLocationId"])
    val productLocationId: Int = 0,

    @SerializedName(value = "productId", alternate = ["ProductId"])
    val productId: Int = 0,

    @SerializedName(value = "productName", alternate = ["ProductName"])
    val productName: String? = null,

    @SerializedName(value = "category", alternate = ["Category"])
    val category: String? = null,

    @SerializedName(value = "barcode", alternate = ["Barcode"])
    val barcode: String? = null,

    @SerializedName(value = "qrCode", alternate = ["QrCode", "QRCode"])
    val qrCode: String? = null,

    @SerializedName(value = "locationCode", alternate = ["LocationCode"])
    val locationCode: String? = null,

    @SerializedName(value = "locationDetails", alternate = ["LocationDetails"])
    val locationDetails: String? = null,

    @SerializedName(value = "quantityInLocation", alternate = ["QuantityInLocation"])
    val quantityInLocation: Int = 0
)

data class UpdateLocationQuantityRequest(
    val quantity: Int
)

data class UpdateLocationQuantityResponse(
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null,

    @SerializedName(value = "productLocationId", alternate = ["ProductLocationId"])
    val productLocationId: Int = 0,

    @SerializedName(value = "productId", alternate = ["ProductId"])
    val productId: Int = 0,

    @SerializedName(value = "quantity", alternate = ["Quantity"])
    val quantity: Int = 0
)