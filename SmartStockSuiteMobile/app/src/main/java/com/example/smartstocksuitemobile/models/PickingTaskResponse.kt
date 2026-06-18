package com.example.smartstocksuitemobile.models

import com.google.gson.annotations.SerializedName

data class PickingTaskResponse(
    @SerializedName(value = "pickingTaskId", alternate = ["PickingTaskId"])
    val pickingTaskId: Int = 0,

    @SerializedName(value = "pickerName", alternate = ["PickerName"])
    val pickerName: String? = null,

    @SerializedName(value = "status", alternate = ["Status"])
    val status: String? = null,

    @SerializedName(value = "createdAt", alternate = ["CreatedAt"])
    val createdAt: String? = null,

    @SerializedName(value = "completedAt", alternate = ["CompletedAt"])
    val completedAt: String? = null,

    @SerializedName(value = "items", alternate = ["Items"])
    val items: List<PickingTaskItemResponse> = emptyList()
)