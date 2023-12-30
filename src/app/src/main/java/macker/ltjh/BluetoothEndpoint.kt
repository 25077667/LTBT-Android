package macker.ltjh

import android.bluetooth.BluetoothDevice


class BluetoothEndpoint(device: Any) : RemoteEndpoint(device) {
    override fun getDevice(): BluetoothDevice {
        return super.getDevice() as BluetoothDevice
    }
    override fun show(): String {
        return "BluetoothEndpoint"
    }
}
