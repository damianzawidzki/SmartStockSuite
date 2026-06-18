package com.example.smartstocksuitemobile.models

data class ScanPickingItemRequest(
    val pickingTaskItemId: Int,
    val scannedBarcode: String,
    val pickedQuantity: Int
)