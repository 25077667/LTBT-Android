package macker.ltjh

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.UUID

class BluetoothManager(selectedDevice: BluetoothDevice, private val activity: AppCompatActivity) {
    companion object {
        const val LOCATION_PERMISSION_REQUEST = 2
        const val BLUETOOTH_CONNECT_PERMISSION_REQUEST = 3
    }

    init {
        connectToBluetoothDevice(selectedDevice)
    }

    fun finalize() {

    }

    @SuppressLint("MissingPermission")
    fun connectToBluetoothDevice(device: BluetoothDevice) {
        try {
            Log.d("BluetoothManager", "Connecting to device: ${device.name} (${device.address})")
            val sppUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            if (bluetoothSocketInternalHolder.isInitialized()) {
                bluetoothSocketInternalHolder.get().close()
                bluetoothSocketInternalHolder.destroy()
            }
            bluetoothSocketInternalHolder.set(device.createRfcommSocketToServiceRecord(sppUUID))
            bluetoothSocketInternalHolder.get().connect()
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
            bluetoothSocketInternalHolder.get().outputStream.write(message)
            bluetoothSocketInternalHolder.get().outputStream.flush()
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

object bluetoothSocketInternalHolder {
    private var bluetoothSocket: BluetoothSocket? = null
    fun isInitialized(): Boolean {
        return bluetoothSocket != null
    }

    fun get(): BluetoothSocket {
        return bluetoothSocket!!
    }

    fun set(bluetoothSocket: BluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket
    }

    fun destroy() {
        bluetoothSocket = null
    }
}
