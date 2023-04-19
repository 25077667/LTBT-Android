package macker.ltjh.controlPanel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import macker.ltjh.R
import macker.ltjh.bluetooth.BluetoothTransferor
import macker.ltjh.commands.CommandFactory
import macker.ltjh.commands.CommandType
import macker.ltjh.commands.LeftRight
import macker.ltjh.commands.Operation

class ControlPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_panel)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setFullScreen()

        setDeviceName()

        registerButtonListener()
    }



    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        //        Check android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

//   Modify the device name textview's value
    private fun setDeviceName() {
        val name = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//          Request permission and try again
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    1
                )
            }
            null
        } else {
            bluetoothTransfer?.getBluetoothSocket()?.remoteDevice?.name
        }

        val deviceNameTextView = findViewById<TextView>(R.id.device_name)
        deviceNameTextView.text = name.toString()
    }

    private fun registerButtonListener() {
        lButtonUp.setOnClickListener {
            bluetoothTransfer.getBluetoothSocket()
                ?.let { it1 -> LeftAheadButtonListener(it1, CommandFactory().generateRequest(
                    CommandType.OPERATION, 1)).onClick(it) }
        }
        lButtonDown.setOnClickListener {
            bluetoothTransfer.getBluetoothSocket()
                ?.let { it1 -> LeftBackwardButtonListener(it1, CommandFactory().generateRequest(
                    CommandType.OPERATION, 1)).onClick(it) }
        }
        rButtonUp.setOnClickListener {
            bluetoothTransfer.getBluetoothSocket()
                ?.let { it1 -> RightAheadButtonListener(it1, CommandFactory().generateRequest(
                    CommandType.OPERATION, 1)).onClick(it) }
        }
        rButtonDown.setOnClickListener {
            bluetoothTransfer.getBluetoothSocket()
                ?.let { it1 -> RightBackwardButtonListener(it1, CommandFactory().generateRequest(
                    CommandType.OPERATION, 1)).onClick(it) }
        }
    }

    private lateinit var bluetoothTransfer: BluetoothTransferor
//    title
    private val settingButton = findViewById<TextView>(R.id.setting_button)
    private val exitButton = findViewById<TextView>(R.id.exit_button)

//  button in body
    private val lButtonUp = findViewById<ImageButton>(R.id.L_button_up)
    private val lButtonDown = findViewById<ImageButton>(R.id.L_button_down)
    private val rButtonUp = findViewById<ImageButton>(R.id.R_button_up)
    private val rButtonDown = findViewById<ImageButton>(R.id.R_button_down)

//    seekbar in body
    private val lSeekBar = findViewById<SeekBar>(R.id.L_seekbar)
    private val rSeekBar = findViewById<SeekBar>(R.id.R_seekbar)
}

