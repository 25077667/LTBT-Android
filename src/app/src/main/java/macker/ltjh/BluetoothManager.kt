package macker.ltjh

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.UUID

class BluetoothManager(private val selectedDevice: BluetoothDevice, private val activity: AppCompatActivity) {
    private lateinit var bluetoothSocket: BluetoothSocket
    companion object {
        const val LOCATION_PERMISSION_REQUEST = 2
        const val BLUETOOTH_CONNECT_PERMISSION_REQUEST = 3
    }

    init {
        connectToBluetoothDevice(selectedDevice)
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
}
