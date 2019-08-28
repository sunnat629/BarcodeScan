package dev.sunnat629.barcodescan.utils

import android.content.pm.PackageManager

class PermissionResultHandler(
    private val listener: Listener
) {

    fun onResult(
        requestCode: Int,
        grantResults: IntArray
    ) {

        if (requestCode != AppConstants.RC_HANDLE_CAMERA_PERM) {
            listener.permissionDenied(requestCode, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            listener.permissionGranted()
            return
        } else {
            listener.permissionDenied(requestCode, grantResults)
        }
    }

    interface Listener {
        fun permissionGranted()
        fun permissionDenied(requestCode: Int, grantResults: IntArray)
    }
}