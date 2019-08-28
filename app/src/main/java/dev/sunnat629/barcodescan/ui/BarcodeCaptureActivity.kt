/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.sunnat629.barcodescan.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import kotlinx.android.synthetic.main.barcode_capture.*
import kotlinx.android.synthetic.main.layout_ask_camera_permission.*
import kotlinx.android.synthetic.main.layout_successful.*
import dev.sunnat629.barcodescan.BSApplication
import dev.sunnat629.barcodescan.R
import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.models.entities.RegDevice
import dev.sunnat629.barcodescan.network.ConnectionDetector
import dev.sunnat629.barcodescan.utils.AppSharedPreferences
import dev.sunnat629.barcodescan.utils.AppConstants.RC_HANDLE_CAMERA_PERM
import dev.sunnat629.barcodescan.utils.DateFormat
import dev.sunnat629.barcodescan.utils.LoggingTags.BARCODE_ACTIVITY
import dev.sunnat629.barcodescan.utils.PermissionResultHandler
import dev.sunnat629.barcodescan.viewmodels.BarcodeCViewModel
import timber.log.Timber
import javax.inject.Inject


class BarcodeCaptureActivity : AppCompatActivity(), QRCodeReaderView.OnQRCodeReadListener,
    PermissionResultHandler.Listener {

    @Inject
    lateinit var pref: AppSharedPreferences

    private lateinit var barcodeCViewModel: BarcodeCViewModel
    private lateinit var deviceID: String

    private lateinit var regToUserObserver: Observer<RegDevice>
    private lateinit var deviceDetailsByID: Observer<DeviceDetails>
    private lateinit var errorObserver: Observer<String>

    private lateinit var currentDeviceName: String
    private lateinit var lastDeviceUser: String
    private var isDataFetched: Boolean = false

    private var alertDialog: AlertDialog? = null

    private lateinit var permissionResultHandler: PermissionResultHandler


    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.barcode_capture)

        BSApplication.appComponent(this).inject(this)

        barcodeCViewModel = ViewModelProviders.of(this).get(BarcodeCViewModel::class.java)
        initQRCodeReader()
        permissionResultHandler = PermissionResultHandler(this)

        deviceID = pref.getDeviceId()
        currentDeviceName = pref.getCurrentDeviceName()
        lastDeviceUser = pref.getLastUser()

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            qr_decoder_view.startCamera()
        } else {
            requestCameraPermission()
        }

        initButtons()
        initObservers()
        showUserDetails()
    }

    private fun initButtons() {
        ask_camera_permission.setOnClickListener {
            requestCameraPermission()
        }
    }

    private fun initQRCodeReader() {
        qr_decoder_view.setOnQRCodeReadListener(this)
        qr_decoder_view.setQRDecodingEnabled(true)
        qr_decoder_view.setAutofocusInterval(2000L)
        qr_decoder_view.setTorchEnabled(true)
        qr_decoder_view.setFrontCamera()
        qr_decoder_view.setBackCamera()
    }

    private fun initObservers() {
        regToUserObserver = Observer {
            Timber.tag(BARCODE_ACTIVITY).v(it.toString())
            barcode_layout_successful.visibility = View.VISIBLE
            val date = DateFormat.smallDateFormat(it.timestamp)
            device_name.text = "${it.user.name} ${it.user.surname} \n$date"
            isDataFetched = true
            qr_decoder_view.stopCamera()
        }

        deviceDetailsByID = Observer {
            currentDeviceName = it.name
            barcode_device_name.text = currentDeviceName
            pref.setCurrentDeviceName(currentDeviceName)

            val date = DateFormat.smallDateFormat(it.lastActivity)
            lastDeviceUser = if (
                !it.lastUser?.name.isNullOrEmpty() &&
                !it.lastUser?.surname.isNullOrEmpty()
            ) {
                "${it.lastUser?.name} ${it.lastUser?.surname} \n$date"
            } else {
                getString(R.string.no_last_user)
            }
            barcode_last_user.text = lastDeviceUser
            pref.setLastUser(lastDeviceUser)
        }

        errorObserver = Observer {
            displayAlert(getString(R.string.error), getString(R.string.unknown_scan))
            barcode_layout_successful.visibility = View.GONE
            qr_decoder_view.stopCamera()
        }

        barcodeCViewModel.deviceDetails.observe(this, deviceDetailsByID)
        barcodeCViewModel.regNewDevice.observe(this, regToUserObserver)
        barcodeCViewModel.errorScan.observe(this, errorObserver)
    }

    private fun showUserDetails() {
        if (ConnectionDetector.isConnectingToInternet(applicationContext)) {
            barcodeCViewModel.getDeviceDetailsByID(deviceID)
        } else {
            barcode_device_name.text = currentDeviceName
            barcode_last_user.text = lastDeviceUser
            displayAlert(getString(R.string.no_internet_title), getString(R.string.no_internet_body))
        }
    }

    /**
     * Handles the requesting of the camera permission.
     */
    private fun requestCameraPermission() {
        Timber.tag(BARCODE_ACTIVITY).w("Camera per mission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.CAMERA)

        ActivityCompat.requestPermissions(
            this, permissions,
            RC_HANDLE_CAMERA_PERM
        )
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        qr_decoder_view.startCamera()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        qr_decoder_view.stopCamera()
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        barcodeCViewModel.regNewDevice.removeObservers(this)
        barcodeCViewModel.deviceDetails.removeObservers(this)
        barcodeCViewModel.errorScan.removeObservers(this)
        qr_decoder_view.stopCamera()
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultHandler.onResult(
            requestCode = requestCode,
            grantResults = grantResults
        )
    }

    override fun permissionGranted() {
        layout_ask_camera_permission.visibility = View.GONE
        barcode_topLayout.visibility = View.VISIBLE
        qr_decoder_view.visibility = View.GONE
        qr_decoder_view.visibility = View.VISIBLE
        qr_decoder_view.startCamera()

        Timber.tag(BARCODE_ACTIVITY).d("Camera permission granted - initialize the camera source")
    }

    override fun permissionDenied(requestCode: Int, grantResults: IntArray) {
        layout_ask_camera_permission.visibility = View.VISIBLE
        barcode_topLayout.visibility = View.GONE

        Timber.tag(BARCODE_ACTIVITY).d("Got unexpected permission result: $requestCode")
        Timber.tag(BARCODE_ACTIVITY).e(
            "Permission not granted: results len = ${grantResults.size} " +
                    "Result code = ${grantResults.firstOrNull()}"
        )
    }

    override fun onQRCodeRead(text: String, points: Array<out PointF>?) {
        Timber.tag(BARCODE_ACTIVITY).v("QR Code value: $text")
        barcodeScanDetection(text)
        qr_decoder_view.stopCamera()
    }

    private fun displayAlert(msgTitle: String, msgBody: String) {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setTitle(msgTitle)
            .setMessage(msgBody)
            .setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                qr_decoder_view.startCamera()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
            .show()
    }

    private fun barcodeScanDetection(best: String) {
        Timber.tag(BARCODE_ACTIVITY).v("best.displayValue:  $best")
        if (ConnectionDetector.isConnectingToInternet(applicationContext)) {
            barcodeCViewModel.registerToUser(uniqueId = deviceID, userCode = best)
        } else {
            displayAlert(getString(R.string.no_internet_title), getString(R.string.no_internet_body))
        }
    }

    override fun onBackPressed() {
        if (isDataFetched) {
            showUserDetails()
            barcode_layout_successful.visibility = View.GONE
            qr_decoder_view.startCamera()
            isDataFetched = false
        } else {
            super.onBackPressed()
        }
    }
}