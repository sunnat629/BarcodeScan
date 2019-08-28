package dev.sunnat629.barcodescan.utils

import android.content.Intent
import com.google.android.gms.common.api.CommonStatusCodes
import com.nhaarman.mockitokotlin2.*
import dev.sunnat629.barcodescan.DeviceInfo
import dev.sunnat629.barcodescan.Toaster
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ResultHandlerMainOnResultTest {

    @Mock
    lateinit var deviceInfo: DeviceInfo
    @Mock
    lateinit var toaster: Toaster
    @Mock
    lateinit var listener: ResultHandlerMain.Listener
    @Mock
    lateinit var intentUtils: IntentUtils
    private lateinit var resultHandler: ResultHandlerMain

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        resultHandler = ResultHandlerMain(
            deviceInfo,
            toaster,
            listener,
            intentUtils
        )
    }

    @Test
    fun happyPath() {
        // Assemble
        doReturn("valid_barcode").whenever(intentUtils).extractBarcodeValue(any())
        doReturn(true).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = AppConstants.RC_HANDLE_CAMERA_PERM,
            resultCode = CommonStatusCodes.SUCCESS,
            data = mockValidIntent()
        )

        // Assert
        verify(listener).showUsername(any())
        verify(listener).registerToUser(any())
    }

    @Test
    fun noInternet() {
        // Assemble
        doReturn("valid_barcode").whenever(intentUtils).extractBarcodeValue(any())
        doReturn(false).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = AppConstants.RC_HANDLE_CAMERA_PERM,
            resultCode = CommonStatusCodes.SUCCESS,
            data = mockValidIntent()
        )

        // Assert
        verify(listener).showAlertNoInternet()
    }

    @Test
    fun checkRequestCode() {
        // Assemble
        doReturn("scanned_qr_code").whenever(intentUtils).extractBarcodeValue(any())
        doReturn(true).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = 0,
            resultCode = CommonStatusCodes.SUCCESS,
            data = mockValidIntent()
        )

        // Assert
        verify(toaster).showSomethingWentWrong()
    }

    @Test
    fun checkResultCode() {
        // Assemble
        doReturn("scanned_qr_code").whenever(intentUtils).extractBarcodeValue(any())
        doReturn(true).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = AppConstants.RC_HANDLE_CAMERA_PERM,
            resultCode = CommonStatusCodes.ERROR,
            data = mockValidIntent()
        )

        // Assert
        verify(toaster).showSomethingWentWrong()
    }

    @Test
    fun checkNullData() {
        // Assemble
        doReturn("scanned_qr_code").whenever(intentUtils).extractBarcodeValue(null)
        doReturn(true).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = AppConstants.RC_HANDLE_CAMERA_PERM,
            resultCode = CommonStatusCodes.SUCCESS,
            data = null
        )

        // Assert
        verify(listener).showUsername(any())
    }

    @Test
    fun checkScannedValue() {
        // Assemble
        doReturn("AB").whenever(intentUtils).extractBarcodeValue(any())
        doReturn(true).whenever(deviceInfo).isInternet()

        // Act
        resultHandler.onResult(
            requestCode = AppConstants.RC_HANDLE_CAMERA_PERM,
            resultCode = CommonStatusCodes.SUCCESS,
            data = mockValidIntent()
        )

        // Assert
        verify(listener).showUsername("AB")
        verify(listener).registerToUser("AB")
    }


    //region Mocks

    private fun mockValidIntent(): Intent = mock()

    //endregion

}