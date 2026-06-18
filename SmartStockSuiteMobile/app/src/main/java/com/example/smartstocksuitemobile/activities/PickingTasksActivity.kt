package com.example.smartstocksuitemobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstocksuitemobile.api.ApiClient
import com.example.smartstocksuitemobile.databinding.ActivityPickingTasksBinding
import com.example.smartstocksuitemobile.models.PickingTaskItemResponse
import com.example.smartstocksuitemobile.models.PickingTaskResponse
import com.example.smartstocksuitemobile.models.ScanPickingItemRequest
import com.example.smartstocksuitemobile.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PickingTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickingTasksBinding
    private lateinit var sessionManager: SessionManager

    private var token = ""
    private var currentFullName = "Warehouse User"

    private var tasks: List<PickingTaskResponse> = emptyList()
    private var currentTaskIndex = 0
    private var currentItemIndex = 0

    private var currentTask: PickingTaskResponse? = null
    private var currentItem: PickingTaskItemResponse? = null

    private val locallyPickedItemIds = mutableSetOf<Int>()

    private val barcodeScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcodeValue = result.data?.getStringExtra("barcodeValue") ?: ""

            if (barcodeValue.isNotEmpty()) {
                scanItem(barcodeValue)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickingTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        token = "Bearer ${sessionManager.getToken()}"
        currentFullName = sessionManager.getFullName().ifBlank { "Warehouse User" }

        setupButtons()
        showLoading()
        loadPickingTasksFromApi()
    }

    private fun setupButtons() {
        binding.btnScanProduct.setOnClickListener {
            if (currentTask == null || currentItem == null) {
                Toast.makeText(this, "No picking item selected.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startTaskThenOpenScanner()
        }

        binding.btnCheckManualBarcode.setOnClickListener {
            val manualBarcode = binding.etManualBarcode.text.toString().trim()

            if (manualBarcode.isEmpty()) {
                Toast.makeText(this, "Please enter barcode.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startTaskThenScanManual(manualBarcode)
        }

        binding.btnGetNextTask.setOnClickListener {
            val task = currentTask

            if (task != null && areAllItemsPicked(task)) {
                completeCurrentTask(task)
            } else {
                currentTaskIndex++
                currentItemIndex = 0
                loadCurrentTask()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadPickingTasksFromApi() {
        val pickerName = sessionManager.getFullName().ifBlank {
            sessionManager.getUserId().toString()
        }

        ApiClient.apiService.getPickingTasksForPicker(token, pickerName)
            .enqueue(object : Callback<List<PickingTaskResponse>> {
                override fun onResponse(
                    call: Call<List<PickingTaskResponse>>,
                    response: Response<List<PickingTaskResponse>>
                ) {
                    if (response.isSuccessful) {
                        tasks = response.body()
                            ?.filter { task ->
                                val status = task.status?.uppercase() ?: ""
                                status != "COMPLETED"
                            }
                            ?: emptyList()

                        currentTaskIndex = 0
                        currentItemIndex = 0
                        loadCurrentTask()
                    } else {
                        showError("Could not load picking tasks. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<PickingTaskResponse>>, t: Throwable) {
                    showError("API connection failed: ${t.message}")
                }
            })
    }

    private fun loadCurrentTask() {
        if (tasks.isEmpty() || currentTaskIndex >= tasks.size) {
            showNoMoreTasks()
            return
        }

        val task = tasks[currentTaskIndex]
        currentTask = task

        val availableItems = task.items.filter { item ->
            !item.isPicked && !locallyPickedItemIds.contains(item.pickingTaskItemId)
        }

        if (availableItems.isEmpty()) {
            showReadyToComplete(task)
            return
        }

        if (currentItemIndex >= availableItems.size) {
            currentItemIndex = 0
        }

        currentItem = availableItems[currentItemIndex]
        showCurrentItem(task, currentItem!!, availableItems.size)
    }

    private fun showCurrentItem(
        task: PickingTaskResponse,
        item: PickingTaskItemResponse,
        remainingItems: Int
    ) {
        val productName = item.product?.productName ?: "Unknown product"
        val barcode = item.product?.barcode ?: "-"
        val location = buildLocationText(item)
        val status = task.status ?: "Pending"

        binding.tvScreenTitle.text = "Warehouse Picking"
        binding.tvTaskNumber.text = "Task ${currentTaskIndex + 1} of ${tasks.size} | Remaining items: $remainingItems"
        binding.tvTaskId.text = "PT-${task.pickingTaskId}"
        binding.tvProductName.text = productName
        binding.tvLocation.text = location
        binding.tvQuantity.text = "Quantity to pick: ${item.quantityToPick}"
        binding.tvExpectedBarcode.text = "Expected barcode: $barcode"

        binding.tvStatus.text = "Status: $status"
        binding.tvResult.text = "Scan this item, then continue with the next item in the same task."
        binding.tvResult.setTextColor(getColor(android.R.color.darker_gray))

        binding.etManualBarcode.setText("")
        binding.etManualBarcode.isEnabled = true

        binding.btnScanProduct.visibility = View.VISIBLE
        binding.etManualBarcode.visibility = View.VISIBLE
        binding.btnCheckManualBarcode.visibility = View.VISIBLE

        binding.btnScanProduct.isEnabled = true
        binding.btnCheckManualBarcode.isEnabled = true

        binding.btnGetNextTask.visibility = View.GONE
    }

    private fun startTaskThenOpenScanner() {
        val task = currentTask ?: return
        val status = task.status?.uppercase() ?: ""

        if (status == "PICKING" || status == "IN_PROGRESS") {
            openScanner()
            return
        }

        ApiClient.apiService.startPickingTask(token, task.pickingTaskId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        binding.tvStatus.text = "Status: Picking"
                        sessionManager.addMobileActivity(
                            "$currentFullName started picking task PT-${task.pickingTaskId}"
                        )
                        openScanner()
                    } else {
                        Toast.makeText(
                            this@PickingTasksActivity,
                            "Could not start task. Code: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@PickingTasksActivity,
                        "API connection failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun startTaskThenScanManual(barcode: String) {
        val task = currentTask ?: return
        val status = task.status?.uppercase() ?: ""

        if (status == "PICKING" || status == "IN_PROGRESS") {
            scanItem(barcode)
            return
        }

        ApiClient.apiService.startPickingTask(token, task.pickingTaskId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        binding.tvStatus.text = "Status: Picking"
                        sessionManager.addMobileActivity(
                            "$currentFullName started picking task PT-${task.pickingTaskId}"
                        )
                        scanItem(barcode)
                    } else {
                        Toast.makeText(
                            this@PickingTasksActivity,
                            "Could not start task. Code: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@PickingTasksActivity,
                        "API connection failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun openScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        barcodeScannerLauncher.launch(intent)
    }

    private fun scanItem(scannedBarcode: String) {
        val task = currentTask
        val item = currentItem

        if (task == null || item == null) {
            Toast.makeText(this, "No picking item selected.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvStatus.text = "Scanned barcode: $scannedBarcode"
        binding.tvResult.text = "Checking item with database..."

        val request = ScanPickingItemRequest(
            pickingTaskItemId = item.pickingTaskItemId,
            scannedBarcode = scannedBarcode,
            pickedQuantity = item.quantityToPick
        )

        ApiClient.apiService.scanPickingItem(token, request)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        locallyPickedItemIds.add(item.pickingTaskItemId)

                        binding.tvResult.text = "Item picked successfully"
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))

                        sessionManager.addMobileActivity(
                            "$currentFullName picked ${item.product?.productName ?: "item"} for PT-${task.pickingTaskId}"
                        )

                        if (areAllItemsPicked(task)) {
                            showReadyToComplete(task)
                        } else {
                            currentItemIndex++
                            loadCurrentTask()
                        }
                    } else {
                        binding.tvResult.text = "Wrong item or scan failed. Code: ${response.code()}"
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))

                        sessionManager.addMobileActivity(
                            "$currentFullName scanned wrong barcode for PT-${task.pickingTaskId}"
                        )
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    binding.tvResult.text = "API connection failed: ${t.message}"
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            })
    }

    private fun areAllItemsPicked(task: PickingTaskResponse): Boolean {
        if (task.items.isEmpty()) {
            return false
        }

        return task.items.all { item ->
            item.isPicked || locallyPickedItemIds.contains(item.pickingTaskItemId)
        }
    }

    private fun showReadyToComplete(task: PickingTaskResponse) {
        currentTask = task
        currentItem = null

        binding.tvScreenTitle.text = "Warehouse Picking"
        binding.tvTaskNumber.text = "Task ${currentTaskIndex + 1} of ${tasks.size}"
        binding.tvTaskId.text = "PT-${task.pickingTaskId}"
        binding.tvProductName.text = "All items picked"
        binding.tvLocation.text = "All products in this task have been scanned."
        binding.tvQuantity.text = "Ready to complete"
        binding.tvExpectedBarcode.text = "No more barcode scanning needed"

        binding.tvStatus.text = "Status: Ready to complete"
        binding.tvResult.text = "Press Complete Picking Task to update the task status in the database."
        binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))

        binding.btnScanProduct.visibility = View.GONE
        binding.etManualBarcode.visibility = View.GONE
        binding.btnCheckManualBarcode.visibility = View.GONE

        binding.btnGetNextTask.visibility = View.VISIBLE
        binding.btnGetNextTask.isEnabled = true
        binding.btnGetNextTask.text = "Complete Picking Task"
    }

    private fun completeCurrentTask(task: PickingTaskResponse) {
        ApiClient.apiService.completePickingTask(token, task.pickingTaskId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        binding.tvResult.text = "Task completed successfully"
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))

                        binding.tvStatus.text =
                            "PT-${task.pickingTaskId} completed. Frontend will show Completed after refresh."

                        sessionManager.addMobileActivity(
                            "$currentFullName completed picking task PT-${task.pickingTaskId}"
                        )

                        currentTaskIndex++
                        currentItemIndex = 0

                        if (currentTaskIndex < tasks.size) {
                            binding.btnGetNextTask.text = "Get Next Task"
                            binding.btnGetNextTask.visibility = View.VISIBLE
                            binding.btnGetNextTask.setOnClickListener {
                                loadCurrentTask()
                                setupButtons()
                            }
                        } else {
                            showNoMoreTasks()
                        }
                    } else {
                        binding.tvResult.text = "Task could not complete. Code: ${response.code()}"
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    binding.tvResult.text = "Complete task failed: ${t.message}"
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            })
    }

    private fun buildLocationText(item: PickingTaskItemResponse): String {
        val location = item.productLocation ?: return "Location: -"

        return "Location: ${location.warehouseName ?: "Warehouse"} / " +
                "Aisle ${location.aisle ?: "-"} / " +
                "Rack ${location.rack ?: "-"} / " +
                "Shelf ${location.shelf ?: "-"} / " +
                "Bin ${location.bin ?: "-"}"
    }

    private fun showLoading() {
        currentTask = null
        currentItem = null

        binding.tvScreenTitle.text = "Warehouse Picking"
        binding.tvTaskNumber.text = "Loading tasks..."
        binding.tvTaskId.text = "-"
        binding.tvProductName.text = "Loading..."
        binding.tvLocation.text = "-"
        binding.tvQuantity.text = "-"
        binding.tvExpectedBarcode.text = "-"

        binding.tvStatus.text = "Connecting to database..."
        binding.tvResult.text = "Please wait"
        binding.tvResult.setTextColor(getColor(android.R.color.darker_gray))

        binding.btnScanProduct.visibility = View.GONE
        binding.etManualBarcode.visibility = View.GONE
        binding.btnCheckManualBarcode.visibility = View.GONE
        binding.btnGetNextTask.visibility = View.GONE
    }

    private fun showError(message: String) {
        currentTask = null
        currentItem = null

        binding.tvScreenTitle.text = "Warehouse Picking"
        binding.tvTaskNumber.text = "Error"
        binding.tvTaskId.text = "-"
        binding.tvProductName.text = "Could not load tasks"
        binding.tvLocation.text = "-"
        binding.tvQuantity.text = "-"
        binding.tvExpectedBarcode.text = "-"

        binding.tvStatus.text = message
        binding.tvResult.text = "Check API, token, picker name, and database task assignment."
        binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))

        binding.btnScanProduct.visibility = View.GONE
        binding.etManualBarcode.visibility = View.GONE
        binding.btnCheckManualBarcode.visibility = View.GONE
        binding.btnGetNextTask.visibility = View.GONE
    }

    private fun showNoMoreTasks() {
        currentTask = null
        currentItem = null

        binding.tvScreenTitle.text = "Warehouse Picking"
        binding.tvTaskNumber.text = "All tasks completed"
        binding.tvTaskId.text = "-"
        binding.tvProductName.text = "No assigned task"
        binding.tvLocation.text = "-"
        binding.tvQuantity.text = "-"
        binding.tvExpectedBarcode.text = "-"

        binding.tvStatus.text = "There are no pending picking tasks assigned to you."
        binding.tvResult.text = "Completed"
        binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))

        binding.btnScanProduct.visibility = View.GONE
        binding.etManualBarcode.visibility = View.GONE
        binding.btnCheckManualBarcode.visibility = View.GONE
        binding.btnGetNextTask.visibility = View.GONE
    }
}