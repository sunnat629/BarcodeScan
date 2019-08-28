package dev.sunnat629.barcodescan.api

import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.models.entities.RegDevice
import dev.sunnat629.barcodescan.models.entities.User
import retrofit2.Response
import retrofit2.http.*

interface BSApiService {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/device/register")
    suspend fun regDeviceByQRScanAsync(@Field("uniqueId") uniqueId: String, @Field("userCode") userCode: String): Response<RegDevice>

    @GET("/device/{deviceID}")
    suspend fun getDeviceByIDAsync(@Path("deviceID") deviceID: String): Response<DeviceDetails>

    @GET("/device/{qrCode}")
    suspend fun getDeviceByQRCodeAsync(@Path("qrCode") qrCode: String): Response<DeviceDetails>

    @GET("/device/all")
    suspend fun getAllDevicesAsync(): Response<List<DeviceDetails>>

    @GET("/user/{userCode}")
    suspend fun getUserDetailsByQrCodeAsync(@Path("userCode") userCode: String): Response<User>
}