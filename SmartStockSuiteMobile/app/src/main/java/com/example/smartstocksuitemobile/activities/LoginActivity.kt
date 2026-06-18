package com.example.smartstocksuitemobile.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstocksuitemobile.api.ApiClient
import com.example.smartstocksuitemobile.databinding.ActivityLoginBinding
import com.example.smartstocksuitemobile.models.LoginRequest
import com.example.smartstocksuitemobile.models.LoginResponse
import com.example.smartstocksuitemobile.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnLogin.setOnClickListener {
            loginWithBackend()
        }
    }

    private fun loginWithBackend() {
        val employeeIdText = binding.etEmployeeId.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (employeeIdText.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter employee ID and password.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val employeeId = employeeIdText.toIntOrNull()

        if (employeeId == null) {
            Toast.makeText(
                this,
                "Employee ID must be a number.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."

        val request = LoginRequest(
            userId = employeeId,
            password = password
        )

        ApiClient.apiService.loginById(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"

                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        if (loginResponse == null || loginResponse.token.isNullOrBlank()) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login response is empty.",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        sessionManager.saveSession(
                            token = loginResponse.token,
                            userId = loginResponse.userId ?: employeeId,
                            fullName = loginResponse.fullName ?: "Warehouse User",
                            email = loginResponse.email ?: "",
                            role = loginResponse.role ?: "USER",
                            canManageLocations = loginResponse.canManageLocations ?: false
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "Login successful.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@LoginActivity, PickerHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid employee ID or password.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"

                    Toast.makeText(
                        this@LoginActivity,
                        "Could not connect to API: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}