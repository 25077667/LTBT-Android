package macker.ltjh.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BlueToothService: Service() {
    inner class LocalBinder : Binder() {
        internal val service: BlueToothService get() = this@BlueToothService
    }

    override fun onBind(intent: Intent?): IBinder {
        return localBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("Bluetooth device", "Service created")
        setupBluetoothAdapter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Bluetooth device", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Bluetooth device", "Service destroyed")
    }

    private fun setupBluetoothAdapter() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        Log.i("Bluetooth device", "Start scanning")
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        Log.i("Bluetooth device", "Stop scanning")
        bluetoothAdapter?.cancelDiscovery()
    }

    //    Member data
    private val localBinder: IBinder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
    private val defaultScope = CoroutineScope(Dispatchers.Default)
}