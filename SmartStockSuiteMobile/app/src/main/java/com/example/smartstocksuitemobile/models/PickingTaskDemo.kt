package com.example.smartstocksuitemobile.models

data class PickingTaskDemo(
    val taskId: String,
    val productName: String,
    val location: String,
    val quantityToPick: Int,
    val expectedBarcode: String,
    val status: String
)