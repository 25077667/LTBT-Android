package macker.ltjh

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class BluetoothRoutine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_routine)

        setupBluetoothAdapter()
        checkBluetoothPermission()
        checkBluetoothState()
        turnOffTextView()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        findBoundedBluetoothDevices()?.forEach { device ->
            findBluetoothDevices?.add(device)
        }
        Log.i("Bluetooth device", "Start rendering")

        renderBluetoothDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Bluetooth device", "Destroying")
        unregisterReceiver(receiver)
    }

    private fun setupBluetoothAdapter() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            // Pop up a message "Device doesn't support Bluetooth"
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = buildString {
                append("Device doesn't support Bluetooth")
            }
            textView.setTextColor(Color.RED)
        }
    }

    private fun checkBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun checkBluetoothState() {
        if (bluetoothAdapter?.isEnabled == false) {
            val activityResultLauncher = registerForActivityResult(
                StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    Log.e("Activity result", "OK")
                }
            }
            activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    // Turn off the textview to invisible
    private fun turnOffTextView() {
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = ""
    }

    @SuppressLint("MissingPermission")
    private fun renderBluetoothDevices() {
    //  Rendering findBluetoothDevices to the listview
        val listView = findViewById<ListView>(R.id.listView)
        val listViewItems = mutableListOf<String>()
        findBluetoothDevices?.forEach { device ->
//          TODO: Get the RSSI
            val bluetoothItem = BluetoothItem(device.name, device.address, 0)
            listViewItems.add(bluetoothItem.toListViewItem())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listViewItems)

        listView.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    fun findBoundedBluetoothDevices(): Set<BluetoothDevice>? {
        // Get a list of bonded devices
        val pairedDevices = bluetoothAdapter?.bondedDevices

        // If there are paired devices
        if (pairedDevices?.isNotEmpty() == true) {
            // Loop through paired devices
            pairedDevices.forEach { device ->
                Log.i("Bluetooth device", device.name + " " + device.address)
            }
        } else {
            Log.e("Bluetooth device", "No paired devices")
        }

        return pairedDevices
    }

//    Member data
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
    private val findBluetoothDevices: MutableSet<BluetoothDevice>? = null
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.

                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    findBluetoothDevices?.add(device!!)
                }
            }
        }
    }
}


// A bluetooth Item illustrates a bluetooth device
private class BluetoothItem (var name: String, var address: String, var rssi: Int) {
    override fun toString(): String {
        return "$name $address $rssi"
    }

    fun toListViewItem(): String {
        return toString()
    }

}
