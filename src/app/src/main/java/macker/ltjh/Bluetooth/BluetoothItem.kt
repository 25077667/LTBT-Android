package macker.ltjh.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

class BluetoothItem(var bluetoothDevice: BluetoothDevice, private var rssi: Int) {
    @SuppressLint("MissingPermission")
    override fun toString(): String {
        return "${bluetoothDevice.name} ${bluetoothDevice.address} $rssi"
    }
}