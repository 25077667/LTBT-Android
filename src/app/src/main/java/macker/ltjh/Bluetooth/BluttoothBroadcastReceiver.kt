package macker.ltjh.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class DeviceListBroadcast(private val onBleDeviceFound: (bluetoothDevice: BluetoothItem) -> Unit = {}) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                //  Discovery has found a device. Get the BluetoothDevice
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                // Error handling
                if (null == device) {
                    Log.e("Bluetooth device", "No device found")
                    return
                }

                val uuidExtra = intent.getStringExtra("UUID_EXTRA")
                if (null != uuidExtra) {
                    onBleDeviceFound.invoke(
                        BluetoothItem(device, uuidExtra ,
                        intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt())
                    )
                }
            }
        }
    }
}
