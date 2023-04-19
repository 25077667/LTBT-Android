package macker.ltjh.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import macker.ltjh.controlPanel.ControlPanelActivity

object BluetoothService: Service() {
    class LocalBinder(private val service: BluetoothService) : Binder() {
        fun getService(): BluetoothService = service
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i("Bluetooth service", "Service started")
        return localBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("Bluetooth service", "Service created")
        setupBluetoothAdapter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Bluetooth service", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

//    TODO: Use the onReBind function helps us cache the data for binding performance

    override fun onDestroy() {
//        To create a bound service, implement the onBind() callback method to return an IBinder
//        that defines the interface for communication with the service. Other application
//        components can then call bindService() to retrieve the interface and begin calling methods
//        on the service. The service lives only to serve the application component that is bound to
//        it, so when there are no components bound to the service, the system destroys it. You do
//        not need to stop a bound service in the same way that you must when the service is started
//        through onStartCommand().
//        https://developer.android.com/guide/components/services#CreatingBoundService
        Log.i("Bluetooth service", "Service destroyed")
        super.onDestroy()
    }

    private fun setupBluetoothAdapter() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        Log.i("Bluetooth service", "Stop scanning")
        bluetoothAdapter?.cancelDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        Log.i("Bluetooth service", "Start scanning")
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(device : BluetoothDevice) {
        Log.i("Bluetooth service", "Connecting device")

//        Connect as a client
        Thread {
            try {
                // Cancel discovery because it otherwise slows down the connection.
                stopScanning()

//                Create a socket to connect with the given BluetoothDevice
                enableBluetoothTransferor(device)
                //  Go to next page and start to send data
                startActivity(Intent(this, ControlPanelActivity::class.java))
            } catch (e: Exception) {
                Log.e("Bluetooth service", "Error connecting to device")
            }
        }.start()
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetoothTransferor(device : BluetoothDevice) {
        val bluetoothSocket = device.createRfcommSocketToServiceRecord(device.uuids[0].uuid)
        Log.i("Bluetooth service", "Bluetooth socket created")
        bluetoothSocket.connect()
        Log.i("Bluetooth service", "Bluetooth socket connected")

        val bluetoothTransferor = BluetoothTransferor
        bluetoothTransferor.initBluetoothSocket(bluetoothSocket)
    }

    //    Member data
    private val localBinder: IBinder = LocalBinder(this)
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
}


// A BluetoothSocket data transfer singleton service
object BluetoothTransferor : Service() {
    class LocalBinder(private val service: BluetoothTransferor) : Binder() {
        fun getService(): BluetoothTransferor = service
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i("Bluetooth transferor", "Service started")
        return localBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("Bluetooth transferor", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Bluetooth transferor", "Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i("Bluetooth transferor", "Service destroyed")
        super.onDestroy()
    }

//    Init the BluetoothSocket
    fun initBluetoothSocket(bluetoothSocket: BluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket
    }

//    TODO: Implement the data transfer utilities in C++ runtime library for some performance boost
//    We would not open this part of code to the public

    fun write(data: ByteArray) {
        bluetoothSocket?.outputStream?.write(data)
    }

    fun read(): ByteArray {
        return bluetoothSocket?.inputStream?.readBytes()!!
    }

    fun getStatusDescription(): String {
//        TODO: Implement the status description
        return "Status description"
    }

    fun getBluetoothSocket(): BluetoothSocket? {
        return bluetoothSocket
    }

    //    Member data
    private val localBinder: IBinder = LocalBinder(this)
    private var bluetoothSocket: BluetoothSocket? = null
}