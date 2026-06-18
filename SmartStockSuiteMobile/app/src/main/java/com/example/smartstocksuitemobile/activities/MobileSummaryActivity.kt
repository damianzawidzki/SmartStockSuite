package com.example.smartstocksuitemobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstocksuitemobile.databinding.ActivityMobileSummaryBinding
import com.example.smartstocksuitemobile.utils.SessionManager

class MobileSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMobileSummaryBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMobileSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUserSummary()
        setupFeatureSummary()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupUserSummary() {
        val fullName = sessionManager.getFullName().ifBlank { "Warehouse User" }
        val employeeId = sessionManager.getEmployeeId().ifBlank { "Demo" }
        val role = sessionManager.getRole().ifBlank { "USER" }

        binding.tvUserSummary.text =
            "Logged in as: $fullName\nEmployee ID: $employeeId\nRole: $role"
    }

    private fun setupFeatureSummary() {
        binding.tvPickingSummary.text =
            "Picking Tasks module allows the picker to view assigned tasks, start a task, scan product barcodes using the phone camera, enter barcodes manually if needed, verify the correct product and complete the task."

        binding.tvInventorySummary.text =
            "Inventory Scanner module allows the user to scan or manually enter product and location barcodes, verify stock in a warehouse location and record stock movements such as IN, OUT and ADJUSTMENT."

        binding.tvTraceabilitySummary.text =
            "Recent Mobile Activity provides local traceability by recording important mobile actions such as started picking task, verified barcode, completed task and saved stock movement."

        binding.tvFutureSummary.text =
            "Future backend integration will connect these mobile actions to live SmartStockSuite API endpoints, PostgreSQL database records, stock movements and audit logs."
    }
}