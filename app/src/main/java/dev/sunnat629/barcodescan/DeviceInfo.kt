package dev.sunnat629.barcodescan

import android.content.Context
import dev.sunnat629.barcodescan.network.ConnectionDetector

class DeviceInfo(
    private val context: Context
) {
    fun isInternet(): Boolean = ConnectionDetector.isConnectingToInternet(context)
}