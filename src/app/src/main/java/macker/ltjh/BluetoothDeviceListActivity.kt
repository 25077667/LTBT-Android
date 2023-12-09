package macker.ltjh

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast

class BluetoothDeviceListActivity : Activity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val devicesList: MutableList<BluetoothDevice> = mutableListOf()
    private val devicesNameList: MutableList<String> = mutableListOf()
    private lateinit var progressBar: ProgressBar
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices)
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.DeviceListView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesNameList)
        listView.adapter = adapter

        // Start Bluetooth discovery, taking into account permissions
        startDiscovery()

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = devicesList[position]
            val returnIntent = Intent()
            returnIntent.putExtra("selected_device", device)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        if (bluetoothAdapter?.isEnabled == true) {
            // Register for broadcasts when a device is discovered
            progressBar.visibility = View.VISIBLE
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            registerReceiver(receiver, filter)

            bluetoothAdapter.startDiscovery()
        } else {
            // Prompt the user to enable Bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so we can start the discovery
                startDiscovery()
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    devicesList.add(device)

                    // Get the name and the address of the Bluetooth device
                    val deviceName = device.name ?: "Unknown Device"
                    val deviceAddress = device.address // Get MAC address

                    // Combine both the name and the address to display
                    val deviceInfo = "$deviceName ($deviceAddress)"
                    devicesNameList.add(deviceInfo)

                    adapter.notifyDataSetChanged()
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    progressBar.visibility = View.VISIBLE
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        unregisterReceiver(receiver)
    }
}