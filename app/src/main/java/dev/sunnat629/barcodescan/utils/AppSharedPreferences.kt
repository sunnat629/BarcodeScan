package dev.sunnat629.barcodescan.utils

import android.content.Context
import android.content.SharedPreferences
import dev.sunnat629.barcodescan.utils.AppConstants.C_DEVICE_NAME
import dev.sunnat629.barcodescan.utils.AppConstants.BS_PREF
import dev.sunnat629.barcodescan.utils.AppConstants.LAST_USER
import dev.sunnat629.barcodescan.utils.AppConstants.UNIQUE_ID
import javax.inject.Inject


class AppSharedPreferences @Inject constructor(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(BS_PREF, Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        return sharedPreferences.getString(UNIQUE_ID, "")!!
    }

    fun setDeviceId(sessionId: String) {
        sharedPreferences.edit().putString(UNIQUE_ID, sessionId).apply()
    }

    fun getLastUser(): String {
        return sharedPreferences.getString(LAST_USER, "")!!
    }

    fun setLastUser(sessionId: String) {
        sharedPreferences.edit().putString(LAST_USER, sessionId).apply()
    }

    fun getCurrentDeviceName(): String {
        return sharedPreferences.getString(C_DEVICE_NAME, "")!!
    }

    fun setCurrentDeviceName(sessionId: String) {
        sharedPreferences.edit().putString(C_DEVICE_NAME, sessionId).apply()
    }
}