package macker.ltjh

import android.os.Bundle
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ControlActivity : AppCompatActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var layout: RelativeLayout // Main layout container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        layout = findViewById(R.id.main_layout)

        // Initialize BluetoothManager
        bluetoothManager = BluetoothManager(this)

        layout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Construct a new joystick at the touched position
                    val joystick = createJoystick(event.x, event.y)
                    layout.addView(joystick)
                    true
                }
                else -> false
            }
        }
    }

    private fun createJoystick(x: Float, y: Float): Joystick {
        val joystick = Joystick(this, null, 0, x, y) // Pass context and coordinates
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        joystick.layoutParams = layoutParams
        joystick.setOnMoveListener(object : Joystick.OnMoveListener {
            override fun onMove(angle: Float, strength: Float) {
                val message = calculateMotorControl(angle, strength, "MOTOR")
                sendMessageToBluetooth(message)
            }
        })
        return joystick
    }


    private fun sendMessageToBluetooth(message: String) {
        val deviceAddress = "YOUR_HC-05_OR_HC-06_ADDRESS_HERE"
        bluetoothManager.sendMessage(deviceAddress, message)
    }

    private fun calculateMotorControl(angle: Float, strength: Float, motorName: String): String {
        // Use angle and strength values to calculate motor control message
        // ...
        return "DUMMY" // Replace with your motor control message logic
    }
}
