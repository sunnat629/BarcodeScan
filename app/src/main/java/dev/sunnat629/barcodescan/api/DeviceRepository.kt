package dev.sunnat629.barcodescan.api

import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.models.entities.RegDevice
import dev.sunnat629.barcodescan.models.entities.User
import dev.sunnat629.barcodescan.network.NetworkResult
import javax.inject.Inject


class DeviceRepository @Inject constructor(private val deviceShelfApiService: BSApiService) : BaseRepository() {

    suspend fun fetchDeviceList(): NetworkResult<List<DeviceDetails>> {
        return safeApiCall(call = { deviceShelfApiService.getAllDevicesAsync() })
    }

    suspend fun fetchOneDeviceByQRCode(qrCode: String): NetworkResult<DeviceDetails> {
        return safeApiCall(call = { deviceShelfApiService.getDeviceByQRCodeAsync(qrCode) })
    }

    suspend fun fetchDeviceDetailsByID(deviceId: String): NetworkResult<DeviceDetails> {
        return safeApiCall(call = { deviceShelfApiService.getDeviceByIDAsync(deviceId)})
    }

    suspend fun fetchUserDetailsByID(userCode: String): NetworkResult<User> {
        return safeApiCall(call = { deviceShelfApiService.getUserDetailsByQrCodeAsync(userCode) })
    }

    suspend fun regNewDevice(uniqueId: String, userCode: String): NetworkResult<RegDevice> {
        return safeApiCall(call = { deviceShelfApiService.regDeviceByQRScanAsync(uniqueId, userCode) })
    }
}