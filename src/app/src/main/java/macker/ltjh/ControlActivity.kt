package macker.ltjh

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ControlActivity : AppCompatActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var layout: ControlActivityLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        layout = findViewById(R.id.control_layout)

        // Initialize BluetoothManager
        bluetoothManager = BluetoothManager(this)

        layout.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Construct a new joystick at the touched position
                    val joystick = createJoystick(event.x, event.y)
                    layout.addView(joystick)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Update joystick position on move
                    val joystick = layout.getChildAt(layout.childCount - 1) as? Joystick
                    joystick?.let {
                        joystick.updatePosition(event.x, event.y)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Remove joystick on touch release
                    val joystick = layout.getChildAt(layout.childCount - 1) as? Joystick
                    joystick?.let {
                        joystick.resetPosition()
                    }
                    layout.removeViewAt(layout.childCount - 1)
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun createJoystick(x: Float, y: Float): Joystick {
        val joystick = Joystick(this, null, 0, x, y) // Pass context and coordinates
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        joystick.layoutParams = layoutParams
        joystick.setOnMoveListener(object : Joystick.OnMoveListener {
            override fun onMove(angle: Float, strength: Float, isLeftSide: Boolean) {
                val motorName = if (isLeftSide) "MOTOR_L" else "MOTOR_R"
                val message = calculateMotorControl(angle, strength, motorName)
                Log.d("Joystick", "angle: $angle, strength: $strength, isLeftSide: $isLeftSide")
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
