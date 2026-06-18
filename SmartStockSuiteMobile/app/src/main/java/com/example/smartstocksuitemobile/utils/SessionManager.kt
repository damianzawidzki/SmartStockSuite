package com.example.smartstocksuitemobile.utils

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        "SmartStockSuiteMobileSession",
        Context.MODE_PRIVATE
    )

    fun saveSession(
        token: String,
        userId: Int,
        fullName: String,
        email: String,
        role: String,
        canManageLocations: Boolean
    ) {
        sharedPreferences.edit()
            .putString("token", token)
            .putInt("userId", userId)
            .putString("fullName", fullName)
            .putString("email", email)
            .putString("role", role)
            .putBoolean("canManageLocations", canManageLocations)
            .apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("userId", 0)
    }

    fun getEmployeeId(): String {
        val userId = getUserId()

        if (userId > 0) {
            return userId.toString()
        }

        return ""
    }

    fun getFullName(): String {
        return sharedPreferences.getString("fullName", "") ?: ""
    }

    fun getEmail(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }

    fun getRole(): String {
        return sharedPreferences.getString("role", "") ?: ""
    }

    fun canManageLocations(): Boolean {
        return sharedPreferences.getBoolean("canManageLocations", false)
    }

    fun isLoggedIn(): Boolean {
        return getToken().isNotEmpty()
    }

    fun addMobileActivity(activityText: String) {
        val currentActivities = getMobileActivities().toMutableList()

        currentActivities.add(0, activityText)

        val latestActivities = currentActivities.take(5)

        sharedPreferences.edit()
            .putString("mobileActivities", latestActivities.joinToString("||"))
            .apply()
    }

    fun getMobileActivities(): List<String> {
        val savedActivities = sharedPreferences.getString("mobileActivities", "") ?: ""

        if (savedActivities.isBlank()) {
            return emptyList()
        }

        return savedActivities.split("||")
            .filter { activity -> activity.isNotBlank() }
    }

    fun clearMobileActivities() {
        sharedPreferences.edit()
            .remove("mobileActivities")
            .apply()
    }

    fun clearSession() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}