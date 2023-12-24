package macker.ltjh

import android.net.wifi.p2p.WifiP2pManager

class WifiEndpoint(device: Any) : RemoteEndpoint(device) {
    override fun getDevice(): WifiP2pManager.PeerListListener {
        return super.getDevice() as WifiP2pManager.PeerListListener
    }
    override fun show(): String {
        return "WifiEndpoint"
    }
}