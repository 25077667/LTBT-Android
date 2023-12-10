package macker.ltjh

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class BluetoothManager(private val activity: AppCompatActivity) {
    private lateinit var bluetoothSocket: BluetoothSocket
    private var selectedDevice: BluetoothDevice? = null

    private val startForResult = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data == null) {
                Toast.makeText(activity, "Failed to get device", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            selectedDevice = data.getParcelableExtra<BluetoothDevice>("selected_device")
            selectedDevice?.let { connectToBluetoothDevice(it) }
        } else {
            Toast.makeText(activity, "Failed to get device", Toast.LENGTH_SHORT).show()
        }
    }

    init {
        if (!hasLocationPermission())
            requestLocationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasBluetoothConnectPermission())
            requestBluetoothConnectPermission()

        showBluetoothDeviceList()
    }

    private fun showBluetoothDeviceList() {
        val intent = Intent(activity, BluetoothDeviceListActivity::class.java)
        startForResult.launch(intent)
    }

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
            Companion.LOCATION_PERMISSION_REQUEST
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasBluetoothConnectPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothConnectPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            BLUETOOTH_CONNECT_PERMISSION_REQUEST
        )
    }

    @SuppressLint("MissingPermission")
    fun connectToBluetoothDevice(device: BluetoothDevice) {
        try {
            Log.d("BluetoothManager", "Connecting to device: ${device.name} (${device.address})")
            val sppUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            bluetoothSocket = device.createRfcommSocketToServiceRecord(sppUUID)
            bluetoothSocket.connect()
            Log.d("BluetoothManager", "Connected")
        }
        catch (e: IOException) {
            Toast.makeText(activity, "Failed to connect to device: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            AlertDialog.Builder(activity)
                .setTitle("Unknown Error")
                .setMessage("Failed to connect to device: ${e.message}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun sendMessage(message: ByteArray) {
        try {
            bluetoothSocket.outputStream.write(message)
            bluetoothSocket.outputStream.flush()
        }
        catch (e: IOException) {
            Toast.makeText(activity, "Failed to connect to device: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            AlertDialog.Builder(activity)
                .setTitle("Unknown Error")
                .setMessage("Failed to send message: ${e.message}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST = 2
        const val BLUETOOTH_CONNECT_PERMISSION_REQUEST = 3
    }
}
