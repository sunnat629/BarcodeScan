package dev.sunnat629.barcodescan.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import dev.sunnat629.barcodescan.BSApplication
import dev.sunnat629.barcodescan.NonNullMediatorLiveData
import dev.sunnat629.barcodescan.api.DeviceRepository
import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.models.entities.RegDevice
import dev.sunnat629.barcodescan.network.NetworkResult
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class BarcodeCViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coRoutinesContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coRoutinesContext)

    private val _regNewDevice: MutableLiveData<RegDevice> = MutableLiveData()
    val regNewDevice: MutableLiveData<RegDevice>
        get() = _regNewDevice

    private val _deviceDetails = MediatorLiveData<DeviceDetails>()
    val deviceDetails: LiveData<DeviceDetails>
        get() = _deviceDetails

    private val _errorScan = NonNullMediatorLiveData<String>()
    val errorScan: LiveData<String>
        get() = _errorScan

    @Inject
    lateinit var deviceRepository: DeviceRepository

    init {
        BSApplication.appComponent(application).inject(this)
    }

    fun getDeviceDetailsByID(deviceID: String) {
        scope.launch {
            when (val result = deviceRepository.fetchDeviceDetailsByID(deviceID)) {
                is NetworkResult.Success -> _deviceDetails.postValue(result.data)
                is NetworkResult.Error -> _errorScan.postValue(result.exception)
            }
        }
    }

    fun registerToUser(uniqueId: String, userCode: String) {
        scope.launch {
            when (val result = deviceRepository.regNewDevice(uniqueId, userCode)) {
                is NetworkResult.Success -> _regNewDevice.postValue(result.data)
                is NetworkResult.Error -> _errorScan.postValue(result.exception)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}