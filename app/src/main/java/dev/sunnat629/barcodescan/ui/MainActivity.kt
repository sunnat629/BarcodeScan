package dev.sunnat629.barcodescan.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.sunnat629.barcodescan.BSApplication
import dev.sunnat629.barcodescan.R
import dev.sunnat629.barcodescan.Toaster
import dev.sunnat629.barcodescan.models.entities.DeviceDetails
import dev.sunnat629.barcodescan.network.ConnectionDetector.isConnectingToInternet
import dev.sunnat629.barcodescan.utils.AppConstants
import dev.sunnat629.barcodescan.utils.AppSharedPreferences
import dev.sunnat629.barcodescan.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var deviceID: String

    private lateinit var deviceDetailsObserver: Observer<List<DeviceDetails>>
    private lateinit var errorObserver: Observer<String>

    private lateinit var popupWindow: PopupWindow
    private var deviceList: List<String> = emptyList()

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var pref: AppSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BSApplication.appComponent(this).inject(this)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        popupWindow = PopupWindow(this)

        deviceID = pref.getDeviceId()

        if (deviceID.isNotBlank()) {
            switchToBarcodeCaptureActivity()
        } else {
            initObservers()
            fetchData()
            initializeButtons()
        }
    }

    private fun switchToBarcodeCaptureActivity() {
        val intent = Intent(this, BarcodeCaptureActivity::class.java)
        intent.putExtra(AppConstants.AUTO_FOCUS, true)
        intent.putExtra(AppConstants.USE_FLASH, false)
        startActivityForResult(intent, AppConstants.RC_HANDLE_CAMERA_PERM)
        finish()
    }

    private fun initObservers() {
        deviceDetailsObserver = Observer {
            deviceList = mainViewModel.getAllDeviceNameList(it)
            main_spinner_devices.setTextColor(Color.BLACK)
        }

        errorObserver = Observer {
            toaster.show(R.string.wrong_message)
        }

        mainViewModel.allDevicesDetails.observe(this@MainActivity, deviceDetailsObserver)
        mainViewModel.errorScan.observe(this@MainActivity, errorObserver)
    }

    private fun initializeButtons() {
        scan_button.setOnClickListener {
            if (isConnectingToInternet(applicationContext)) {
                switchToBarcodeCaptureActivity()
            } else {
                displayAlert(getString(R.string.no_internet_title), getString(R.string.no_internet_body))
            }
        }

        confirm_id_button.setOnClickListener {
            if (isConnectingToInternet(applicationContext)) {
                if (main_spinner_devices.text != getString(R.string.select_device_id)) {
                    deviceID = main_spinner_devices.text.toString()
                    pref.setDeviceId(deviceID)
                    toaster.show(R.string.saved_successfully)
                    scan_button.isEnabled = true
                } else {
                    fetchData()
                }
            } else {
                displayAlert(getString(R.string.no_internet_title), getString(R.string.no_internet_body))
            }
        }

        retry_button.setOnClickListener {
            fetchData()
        }

        main_spinner_devices.setOnClickListener {
            if (deviceList.isNotEmpty()) {
                popupWindow().showAsDropDown(it, 0, 0)
            }
        }
    }

    private fun popupWindow(): PopupWindow {
        val adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_text_format, deviceList)
        popupWindow.isFocusable = true
        popupWindow.width = main_spinner_devices.width
        popupWindow.height = resources.getDimension(R.dimen.popup_window_height).toInt()

        val listView = ListView(this)
        listView.adapter = adapter
        listView.onItemClickListener = selectDevice()
        popupWindow.contentView = listView

        return popupWindow
    }

    private fun selectDevice(): AdapterView.OnItemClickListener? {
        return AdapterView.OnItemClickListener { _, _, position, _ ->
            main_spinner_devices.text = deviceList[position]
            scanButtonStatus(main_spinner_devices.text.toString())
            popupWindow.dismiss()
        }
    }

    private fun scanButtonStatus(deviceID: String) {
        if (this.deviceID != deviceID) {
            scan_button.isEnabled = false
        } else {
            scan_button.isEnabled = false
        }
    }

    private fun fetchData() {
        if (isConnectingToInternet(applicationContext)) {
            mainViewModel.fetchAllDeviceList()
            with_internet_layout.visibility = View.VISIBLE
            no_internet_layout.visibility = View.GONE
        } else {
            displayAlert(getString(R.string.no_internet_title), getString(R.string.no_internet_body))
        }
    }

    private fun displayAlert(msgTitle: String, msgBody: String) {
        AlertDialog.Builder(this)
            .setTitle(msgTitle)
            .setMessage(msgBody)
            .setPositiveButton(getString(R.string.ok_label)) { _, _ ->
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.allDevicesDetails.removeObservers(this)
        mainViewModel.errorScan.removeObservers(this)
    }
}