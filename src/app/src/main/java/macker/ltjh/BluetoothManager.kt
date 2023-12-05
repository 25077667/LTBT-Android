package macker.ltjh

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothManager(private val activity: Activity) {

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val LOCATION_PERMISSION_REQUEST = 2
    private val BLUETOOTH_CONNECT_PERMISSION_REQUEST = 3
    // ... other variables ...

    // ... sendMessage function ...

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST
        )
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothConnectPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            BLUETOOTH_CONNECT_PERMISSION_REQUEST
        )
    }

    // Modify onRequestPermissionsResult to handle result of BLUETOOTH_CONNECT permission request
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted, proceed with sending the message
                } else {
                    // Location permission denied, handle accordingly or notify the user
                }
            }
            BLUETOOTH_CONNECT_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth Connect permission granted, proceed with sending the message
                } else {
                    // Bluetooth Connect permission denied, handle accordingly or notify the user
                }
            }
        }
    }

    // ... onActivityResult function ...

    // Modify the sendMessage function to check both location and Bluetooth Connect permissions
    fun sendMessage(deviceAddress: String, message: String) {
        // ... existing code ...

        if (!hasLocationPermission() || !hasBluetoothConnectPermission()) {
            requestLocationPermission()
            requestBluetoothConnectPermission()
            return
        }

        // ... existing code ...

        try {
            // ... existing try block ...
        } catch (e: SecurityException) {
            // Handle the case where the user has not granted permission
        } catch (e: java.io.IOException) {
            // Handle the case where Bluetooth connection fails
            e.printStackTrace()
        }
    }

    // ... rest of the class ...
}
