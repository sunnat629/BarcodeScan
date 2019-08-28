package dev.sunnat629.barcodescan

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dev.sunnat629.barcodescan.R
import javax.inject.Inject

class Toaster @Inject constructor(
    private val context: Context
) {
    fun show(@StringRes messageResId: Int) {
        Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_LONG).show()
    }

    fun showSomethingWentWrong() = show(R.string.wrong_message)
}