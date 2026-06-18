package com.example.smartstocksuitemobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstocksuitemobile.api.ApiClient
import com.example.smartstocksuitemobile.databinding.ActivityInventoryScannerBinding
import com.example.smartstocksuitemobile.models.InventoryScanResponse
import com.example.smartstocksuitemobile.models.UpdateLocationQuantityRequest
import com.example.smartstocksuitemobile.models.UpdateLocationQuantityResponse
import com.example.smartstocksuitemobile.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventoryScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryScannerBinding
    private lateinit var sessionManager: SessionManager

    private var token = ""
    private var selectedScanMode = ""

    private var currentProductLocationId = 0
    private var currentProductId = 0
    private var currentProductName = "-"
    private var currentProductCategory = "-"
    private var currentBarcode = "-"
    private var currentLocationCode = "-"
    private var currentLocationDetails = "-"
    private var currentQuantityInLocation = 0
    private var isProductLocationVerified = false

    private val barcodeScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcodeValue = result.data?.getStringExtra("barcodeValue")?.trim() ?: ""

            if (barcodeValue.isNotEmpty()) {
                if (selectedScanMode == "PRODUCT") {
                    binding.tvProductBarcode.text = "Product barcode: $barcodeValue"
                    binding.tvInventoryStatus.text = "Status: Checking product in database..."
                    scanProduct(barcodeValue)
                }

                if (selectedScanMode == "LOCATION") {
                    binding.tvLocationBarcode.text = "Location barcode: $barcodeValue"
                    binding.tvInventoryStatus.text = "Status: Checking location in database..."
                    scanLocation(barcodeValue)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        token = "Bearer ${sessionManager.getToken()}"

        setupInitialScreen()
        setupMovementButtons()

        binding.btnScanProductBarcode.setOnClickListener {
            selectedScanMode = "PRODUCT"
            val intent = Intent(this, BarcodeScannerActivity::class.java)
            barcodeScannerLauncher.launch(intent)
        }

        binding.btnCheckManualProductBarcode.setOnClickListener {
            val manualBarcode = binding.etProductBarcode.text.toString().trim()

            if (manualBarcode.isEmpty()) {
                Toast.makeText(this, "Please enter product barcode.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.tvProductBarcode.text = "Product barcode: $manualBarcode"
            binding.tvInventoryStatus.text = "Status: Checking product in database..."
            scanProduct(manualBarcode)
        }

        binding.btnScanLocationBarcode.setOnClickListener {
            selectedScanMode = "LOCATION"
            val intent = Intent(this, BarcodeScannerActivity::class.java)
            barcodeScannerLauncher.launch(intent)
        }

        binding.btnCheckManualLocationBarcode.setOnClickListener {
            val manualLocation = binding.etLocationBarcode.text.toString().trim()

            if (manualLocation.isEmpty()) {
                Toast.makeText(this, "Please enter location barcode.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.tvLocationBarcode.text = "Location barcode: $manualLocation"
            binding.tvInventoryStatus.text = "Status: Checking location in database..."
            scanLocation(manualLocation)
        }

        binding.btnSaveMovement.setOnClickListener {
            saveStockMovement()
        }

        binding.btnClear.setOnClickListener {
            setupInitialScreen()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupInitialScreen() {
        selectedScanMode = ""

        currentProductLocationId = 0
        currentProductId = 0
        currentProductName = "-"
        currentProductCategory = "-"
        currentBarcode = "-"
        currentLocationCode = "-"
        currentLocationDetails = "-"
        currentQuantityInLocation = 0
        isProductLocationVerified = false

        binding.etProductBarcode.setText("")
        binding.etLocationBarcode.setText("")
        binding.etMovementAmount.setText("")

        binding.tvProductBarcode.text = "Product barcode: Not scanned yet"
        binding.tvLocationBarcode.text = "Location barcode: Not scanned yet"
        binding.tvProductName.text = "Product name: -"
        binding.tvProductCategory.text = "Category: -"
        binding.tvLocationDetails.text = "Location details: -"
        binding.tvQuantityInLocation.text = "Quantity in location: -"
        binding.tvStockStatus.text = "Stock status: -"
        binding.tvStockStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        binding.tvSelectedMovementType.text = "Selected movement type: -"
        binding.tvMovementResult.text = "Movement result: Waiting for product or location scan"
        binding.tvInventoryStatus.text = "Status: Scan product barcode or location barcode."

        setMovementEnabled(false)
    }

    private fun setupMovementButtons() {
        binding.btnMovementIn.setOnClickListener {
            binding.tvSelectedMovementType.text = "Selected movement type: IN"
        }

        binding.btnMovementOut.setOnClickListener {
            binding.tvSelectedMovementType.text = "Selected movement type: OUT"
        }

        binding.btnMovementAdjustment.setOnClickListener {
            binding.tvSelectedMovementType.text = "Selected movement type: ADJUSTMENT"
        }
    }

    private fun setMovementEnabled(enabled: Boolean) {
        binding.btnMovementIn.isEnabled = enabled
        binding.btnMovementOut.isEnabled = enabled
        binding.btnMovementAdjustment.isEnabled = enabled
        binding.etMovementAmount.isEnabled = enabled
        binding.btnSaveMovement.isEnabled = enabled
    }

    private fun scanProduct(code: String) {
        ApiClient.apiService.scanInventoryProduct(token, code)
            .enqueue(object : Callback<InventoryScanResponse> {
                override fun onResponse(
                    call: Call<InventoryScanResponse>,
                    response: Response<InventoryScanResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        showInventoryDetails(
                            response.body()!!,
                            "Status: Product found. Location and quantity are displayed."
                        )
                    } else {
                        showNotFound("Product was not found. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<InventoryScanResponse>, t: Throwable) {
                    showNotFound("API connection failed: ${t.message}")
                }
            })
    }

    private fun scanLocation(code: String) {
        ApiClient.apiService.scanInventoryLocation(token, code)
            .enqueue(object : Callback<InventoryScanResponse> {
                override fun onResponse(
                    call: Call<InventoryScanResponse>,
                    response: Response<InventoryScanResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        showInventoryDetails(
                            response.body()!!,
                            "Status: Location found. Product and quantity are displayed."
                        )
                    } else {
                        showNotFound("Location was not found. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<InventoryScanResponse>, t: Throwable) {
                    showNotFound("API connection failed: ${t.message}")
                }
            })
    }

    private fun showInventoryDetails(item: InventoryScanResponse, statusMessage: String) {
        currentProductLocationId = item.productLocationId
        currentProductId = item.productId
        currentProductName = item.productName ?: "Unknown product"
        currentProductCategory = item.category ?: "-"
        currentBarcode = item.barcode ?: currentProductId.toString()
        currentLocationCode = item.locationCode ?: "-"
        currentLocationDetails = item.locationDetails ?: "-"
        currentQuantityInLocation = item.quantityInLocation

        binding.tvProductBarcode.text = "Product barcode: $currentBarcode"
        binding.tvLocationBarcode.text = "Location barcode: $currentLocationCode"
        binding.tvProductName.text = "Product name: $currentProductName"
        binding.tvProductCategory.text = "Category: $currentProductCategory"
        binding.tvLocationDetails.text = "Location details: $currentLocationDetails"
        binding.tvQuantityInLocation.text = "Quantity in location: $currentQuantityInLocation"
        binding.tvStockStatus.text = "Stock status: Product found in warehouse location"
        binding.tvStockStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        binding.tvInventoryStatus.text = statusMessage
        binding.tvMovementResult.text = "Movement result: Ready to record stock movement"

        isProductLocationVerified = currentProductLocationId > 0
        setMovementEnabled(isProductLocationVerified)
    }

    private fun showNotFound(message: String) {
        isProductLocationVerified = false
        currentProductLocationId = 0
        currentProductId = 0
        currentProductName = "-"
        currentProductCategory = "-"
        currentBarcode = "-"
        currentLocationCode = "-"
        currentLocationDetails = "-"
        currentQuantityInLocation = 0

        binding.tvProductName.text = "Product name: Not found"
        binding.tvProductCategory.text = "Category: -"
        binding.tvLocationDetails.text = "Location details: Not verified"
        binding.tvQuantityInLocation.text = "Quantity in location: -"
        binding.tvStockStatus.text = "Stock status: Not verified"
        binding.tvStockStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        binding.tvInventoryStatus.text = "Status: $message"
        binding.tvMovementResult.text = "Movement result: Waiting for valid product or location"

        setMovementEnabled(false)
    }

    private fun saveStockMovement() {
        if (!isProductLocationVerified || currentProductLocationId <= 0) {
            Toast.makeText(this, "Scan valid product or location first.", Toast.LENGTH_SHORT).show()
            return
        }

        val movementTypeText = binding.tvSelectedMovementType.text.toString()

        val movementType = when {
            movementTypeText.contains("IN") -> "IN"
            movementTypeText.contains("OUT") -> "OUT"
            movementTypeText.contains("ADJUSTMENT") -> "ADJUSTMENT"
            else -> ""
        }

        if (movementType.isEmpty()) {
            Toast.makeText(this, "Please select movement type.", Toast.LENGTH_SHORT).show()
            return
        }

        val amountText = binding.etMovementAmount.text.toString().trim()
        val amount = amountText.toIntOrNull()

        if (amount == null || amount < 0) {
            Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show()
            return
        }

        val newQuantity = when (movementType) {
            "IN" -> currentQuantityInLocation + amount
            "OUT" -> {
                if (amount > currentQuantityInLocation) {
                    Toast.makeText(
                        this,
                        "OUT amount cannot be greater than current quantity.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                currentQuantityInLocation - amount
            }
            "ADJUSTMENT" -> amount
            else -> currentQuantityInLocation
        }

        ApiClient.apiService.updateLocationQuantity(
            token,
            currentProductLocationId,
            UpdateLocationQuantityRequest(newQuantity)
        ).enqueue(object : Callback<UpdateLocationQuantityResponse> {
            override fun onResponse(
                call: Call<UpdateLocationQuantityResponse>,
                response: Response<UpdateLocationQuantityResponse>
            ) {
                if (response.isSuccessful) {
                    currentQuantityInLocation = newQuantity

                    binding.tvQuantityInLocation.text =
                        "Quantity in location: $currentQuantityInLocation"

                    binding.tvMovementResult.text =
                        "Movement result: $movementType saved. New quantity is $currentQuantityInLocation."

                    binding.tvInventoryStatus.text =
                        "Status: Stock movement saved to database."

                    binding.etMovementAmount.setText("")

                    val fullName = sessionManager.getFullName().ifBlank { "Warehouse User" }

                    sessionManager.addMobileActivity(
                        "$fullName recorded $movementType for $currentProductName at $currentLocationCode"
                    )
                } else {
                    binding.tvMovementResult.text =
                        "Movement result: Failed to update quantity. Code: ${response.code()}"

                    Toast.makeText(
                        this@InventoryScannerActivity,
                        "Failed to update quantity.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdateLocationQuantityResponse>, t: Throwable) {
                binding.tvMovementResult.text =
                    "Movement result: API connection failed: ${t.message}"

                Toast.makeText(
                    this@InventoryScannerActivity,
                    "API connection failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}