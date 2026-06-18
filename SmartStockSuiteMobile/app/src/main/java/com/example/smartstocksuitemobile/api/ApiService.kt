package com.example.smartstocksuitemobile.api

import com.example.smartstocksuitemobile.models.InventoryScanResponse
import com.example.smartstocksuitemobile.models.LoginRequest
import com.example.smartstocksuitemobile.models.LoginResponse
import com.example.smartstocksuitemobile.models.PickingTaskResponse
import com.example.smartstocksuitemobile.models.ScanPickingItemRequest
import com.example.smartstocksuitemobile.models.UpdateLocationQuantityRequest
import com.example.smartstocksuitemobile.models.UpdateLocationQuantityResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/Auth/login-by-id")
    fun loginById(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("api/PickingTasks/picker/{pickerName}")
    fun getPickingTasksForPicker(
        @Header("Authorization") token: String,
        @Path("pickerName") pickerName: String
    ): Call<List<PickingTaskResponse>>

    @PUT("api/PickingTasks/{id}/start")
    fun startPickingTask(
        @Header("Authorization") token: String,
        @Path("id") taskId: Int
    ): Call<Void>

    @POST("api/PickingTasks/scan")
    fun scanPickingItem(
        @Header("Authorization") token: String,
        @Body request: ScanPickingItemRequest
    ): Call<Any>

    @PUT("api/PickingTasks/{id}/complete")
    fun completePickingTask(
        @Header("Authorization") token: String,
        @Path("id") taskId: Int
    ): Call<Void>

    @GET("api/ProductLocations/scan/product/{code}")
    fun scanInventoryProduct(
        @Header("Authorization") token: String,
        @Path("code") code: String
    ): Call<InventoryScanResponse>

    @GET("api/ProductLocations/scan/location/{code}")
    fun scanInventoryLocation(
        @Header("Authorization") token: String,
        @Path("code") code: String
    ): Call<InventoryScanResponse>

    @PATCH("api/ProductLocations/{id}/quantity")
    fun updateLocationQuantity(
        @Header("Authorization") token: String,
        @Path("id") productLocationId: Int,
        @Body request: UpdateLocationQuantityRequest
    ): Call<UpdateLocationQuantityResponse>
}