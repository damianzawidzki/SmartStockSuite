package com.example.smartstocksuitemobile.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName(value = "token", alternate = ["Token"])
    val token: String? = null,

    @SerializedName(value = "userId", alternate = ["UserId"])
    val userId: Int? = null,

    @SerializedName(value = "fullName", alternate = ["FullName"])
    val fullName: String? = null,

    @SerializedName(value = "email", alternate = ["Email"])
    val email: String? = null,

    @SerializedName(value = "role", alternate = ["Role"])
    val role: String? = null,

    @SerializedName(value = "canManageLocations", alternate = ["CanManageLocations"])
    val canManageLocations: Boolean? = null,

    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null
)