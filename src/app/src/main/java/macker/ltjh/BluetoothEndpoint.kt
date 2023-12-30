package macker.ltjh

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice


class BluetoothEndpoint(device: Any) : RemoteEndpoint(device) {
    override fun getDevice(): BluetoothDevice {
        return super.getDevice() as BluetoothDevice
    }

    @SuppressLint("MissingPermission")
    override fun show(): String {
        if (getDevice().name == null) {
            return "BluetoothEndpoint"
        }
        return "BluetoothEndpoint: ${getDevice().name} (${getDevice().address})"
    }
}
