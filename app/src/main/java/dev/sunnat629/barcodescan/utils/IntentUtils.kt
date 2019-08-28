package dev.sunnat629.barcodescan.utils

import android.content.Intent
import com.google.android.gms.vision.barcode.Barcode

object IntentUtils {
    /**
     * @return barcode value or empty string
     */
    fun extractBarcodeValue(intent: Intent?): String {
        return intent?.getParcelableExtra<Barcode>(AppConstants.BARCODE_OBJECT)
            ?.displayValue ?: ""
    }
}