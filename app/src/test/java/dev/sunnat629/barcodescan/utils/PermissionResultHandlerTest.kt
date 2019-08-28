package dev.sunnat629.barcodescan.utils

import android.content.pm.PackageManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import dev.sunnat629.barcodescan.utils.AppConstants.RC_HANDLE_CAMERA_PERM
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PermissionResultHandlerTest{

    @Mock
    lateinit var listener: PermissionResultHandler.Listener
    private lateinit var permissionResultHandler: PermissionResultHandler

    private val randomIntArray = intArrayOf()
    private val cameraIntArray = intArrayOf(PackageManager.PERMISSION_GRANTED)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        permissionResultHandler = PermissionResultHandler(
            listener = listener
        )
    }

    @Test
    fun testPermissionGranted() {
        // Act
        permissionResultHandler.onResult(
            requestCode = RC_HANDLE_CAMERA_PERM,
            grantResults = cameraIntArray
        )

        // Assert
        verify(listener).permissionGranted()
    }

    @Test
    fun testPermissionDenied() {
        // Act
        permissionResultHandler.onResult(
            requestCode = RC_HANDLE_CAMERA_PERM,
            grantResults = randomIntArray
        )

        // Assert
        verify(listener).permissionDenied(any(), any())
    }

    @Test
    fun testValidRequestCode() {
        // Act
        permissionResultHandler.onResult(
            requestCode = 0,
            grantResults = cameraIntArray
        )

        // Assert
        verify(listener).permissionDenied(any(), any())
    }
}