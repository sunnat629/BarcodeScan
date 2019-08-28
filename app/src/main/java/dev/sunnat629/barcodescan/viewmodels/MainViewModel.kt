package dev.sunnat629.barcodescan.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import dev.sunnat629.barcodescan.BSApplication
import dev.sunnat629.barcodescan.NonNullMediatorLiveData
import dev.sunnat629.barcodescan.api.DeviceRepository
import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.network.NetworkResult
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coRoutinesContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coRoutinesContext)

    private var _allDevices = NonNullMediatorLiveData<List<DeviceDetails>>()
    val allDevicesDetails: LiveData<List<DeviceDetails>>
        get() = _allDevices

    private val _errorScan = NonNullMediatorLiveData<String>()
    val errorScan: LiveData<String>
        get() = _errorScan

    @Inject
    lateinit var deviceRepository: DeviceRepository

    init {
        BSApplication.appComponent(application).inject(this)
    }

    fun fetchAllDeviceList() {
        scope.launch {
            when (val result = deviceRepository.fetchDeviceList()) {
                is NetworkResult.Success -> _allDevices.postValue(result.data)
                is NetworkResult.Error -> _errorScan.postValue(result.exception)
            }
        }
    }

    fun getAllDeviceNameList(it: List<DeviceDetails>): List<String> {
        return it.flatMap { listOf(it.uniqueId) }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}