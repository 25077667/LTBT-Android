package macker.ltjh

import android.bluetooth.BluetoothDevice
import android.net.wifi.p2p.WifiP2pManager

abstract class RemoteEndpoint(private val device: Any) {
    companion object {
        fun create(device: Any): RemoteEndpoint {
            return when (device) {
                is BluetoothDevice -> BluetoothEndpoint(device)
                is WifiP2pManager.PeerListListener -> WifiEndpoint(device)
                else -> throw IllegalArgumentException("Unknown device type")
            }
        }
    }

    open fun getDevice(): Any {
        return device
    }
    abstract fun show(): String
}
