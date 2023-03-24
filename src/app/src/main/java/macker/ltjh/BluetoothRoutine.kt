package macker.ltjh

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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

    }

    private fun setupBluetoothAdapter() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            // Pop up a message "Device doesn't support Bluetooth"
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = "Device doesn't support Bluetooth"
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
            val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
                StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    Log.e("Activity result", "OK")
                }
            }
            activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

//    Member data
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothManager: BluetoothManager? = null
}