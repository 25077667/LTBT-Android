package macker.ltjh.bluetooth

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import macker.ltjh.R
import java.util.concurrent.atomic.AtomicBoolean

class BluetoothActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_routine)

        checkBluetoothPermission()
        setupBluetoothAdapter()
        checkBluetoothState()
        startBluetoothService()
        turnOffTextView()
        registerListViewItemListener()

//        Clear the listview
        deviceViewModel.clear()

        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
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

    private fun startBluetoothService() {
        val bluetoothService = Intent(this, BluetoothService::class.java)
        startService(bluetoothService)
        Log.i("Bluetooth device", "Start Bluetooth service")
    }

    // Turn off the textview to invisible
    private fun turnOffTextView() {
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = ""
    }

    private fun registerListViewItemListener() {
        val listView = findViewById<ListView>(R.id.listView)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            bluetoothService.connectDevice(deviceViewModel.getDevice(position).bluetoothDevice)
        }
    }

//    Member data
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
    private val receiver = object : BroadcastReceiver() {
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
                        deviceViewModel.addDevice(
                            BluetoothItem(
                                device,
                                intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                            )
                        )
                    }
                }
            }
        }
    }
    private val deviceViewModel: BlueToothDeviceList = BlueToothDeviceList(this, findViewById(R.id.listView))
    private lateinit var bluetoothService: BluetoothService
}


// Listening a fab button asynchronously, the button switching between scanning and stop scanning in
// 12 seconds, It could be emergency stop scanning by pressing the button again.
class ScanningActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private val isScanning = AtomicBoolean(true)
    lateinit var bluetoothService: BluetoothService
        private set

    private val scanningRunnable = object : Runnable {
        override fun run() {
            if (isScanning.get()) {
                bluetoothService.stopScanning()
                fab.setImageResource(R.drawable.baseline_bluetooth_24)
                isScanning.set(false)
            } else {
                bluetoothService.startScanning()
                fab.setImageResource(R.drawable.baseline_bluetooth_searching_24)
                isScanning.set(true)
            }
            fab.postDelayed(this, 12000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_routine)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener { toggleScanning() }
        scanningRunnable.run()
    }

    override fun onStop() {
        super.onStop()
        scanningRunnable.run()
        bluetoothService.stopScanning()
    }

    private fun toggleScanning() {
        isScanning.set(!isScanning.get())
    }

    override fun onDestroy() {
        super.onDestroy()
        fab.removeCallbacks(scanningRunnable)
    }
}

// A coroutine that rendering the bluetooth devices in to the listview
class BlueToothDeviceList(context: Context, listView: ListView) {
    private val bluetoothDeviceList = mutableListOf<BluetoothItem>()
    private val bluetoothDeviceAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, bluetoothDeviceList)

    init {
        listView.adapter = bluetoothDeviceAdapter
    }

    fun addDevice(bluetoothItem: BluetoothItem) {
        bluetoothDeviceList.add(bluetoothItem)
        bluetoothDeviceAdapter.notifyDataSetChanged()
    }

    fun clear() {
        bluetoothDeviceList.clear()
        bluetoothDeviceAdapter.notifyDataSetChanged()
    }

    fun getDevice(getDevice : Int) = bluetoothDeviceList[getDevice]
}
