package dev.sunnat629.barcodescan.utils

import android.content.Intent
import com.google.android.gms.common.api.CommonStatusCodes
import dev.sunnat629.barcodescan.DeviceInfo
import dev.sunnat629.barcodescan.Toaster

/**
 * Responsible for handling
 */
class ResultHandlerMain(
    private val deviceInfo: DeviceInfo,
    private val toaster: Toaster,
    private val listener: Listener,
    private val intentUtils: IntentUtils
) {

    fun onResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == AppConstants.RC_HANDLE_CAMERA_PERM) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                val userQRCode = intentUtils.extractBarcodeValue(data)
                if (deviceInfo.isInternet()) {
                    listener.showUsername(userQRCode)
                    listener.registerToUser(userQRCode)
                } else {
                    listener.showAlertNoInternet()
                }
            } else {
                toaster.showSomethingWentWrong()
            }
        } else {
            toaster.showSomethingWentWrong()
        }
    }

    interface Listener {
        fun showAlertNoInternet()
        fun showUsername(username: String)
        fun registerToUser(userQRCode: String)
    }
}