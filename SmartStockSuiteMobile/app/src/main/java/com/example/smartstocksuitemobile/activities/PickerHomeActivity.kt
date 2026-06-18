package com.example.smartstocksuitemobile.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstocksuitemobile.databinding.ActivityPickerHomeBinding
import com.example.smartstocksuitemobile.utils.SessionManager

class PickerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickerHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUserDetails()
        setupRecentActivity()

        binding.btnPickingTasks.setOnClickListener {
            val intent = Intent(this, PickingTasksActivity::class.java)
            startActivity(intent)
        }

        binding.btnInventoryScanner.setOnClickListener {
            val intent = Intent(this, InventoryScannerActivity::class.java)
            startActivity(intent)
        }

        binding.btnMobileSummary.setOnClickListener {
            val intent = Intent(this, MobileSummaryActivity::class.java)
            startActivity(intent)
        }

        binding.btnClearActivity.setOnClickListener {
            sessionManager.clearMobileActivities()
            setupRecentActivity()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecentActivity()
    }

    private fun setupUserDetails() {
        val employeeId = sessionManager.getEmployeeId()
        val fullName = sessionManager.getFullName()
        val role = sessionManager.getRole()

        val displayName = if (fullName.isNotBlank()) {
            fullName
        } else {
            "Warehouse User"
        }

        val displayEmployeeId = if (employeeId.isNotBlank()) {
            employeeId
        } else {
            "Demo"
        }

        val displayRole = if (role.isNotBlank()) {
            role
        } else {
            "USER"
        }

        binding.tvWelcome.text = "Welcome, $displayName"
        binding.tvEmployeeInfo.text = "Employee ID: $displayEmployeeId | Role: $displayRole"
    }

    private fun setupRecentActivity() {
        val activities = sessionManager.getMobileActivities()

        if (activities.isEmpty()) {
            binding.tvRecentActivityOne.text = "• No mobile activity recorded yet"
            binding.tvRecentActivityTwo.text = "• Complete picking or inventory actions to see them here"
            binding.tvRecentActivityThree.text = "• Activity is stored locally for demo traceability"
            return
        }

        binding.tvRecentActivityOne.text = "• ${activities.getOrNull(0) ?: "-"}"
        binding.tvRecentActivityTwo.text = "• ${activities.getOrNull(1) ?: "-"}"
        binding.tvRecentActivityThree.text = "• ${activities.getOrNull(2) ?: "-"}"
    }
}