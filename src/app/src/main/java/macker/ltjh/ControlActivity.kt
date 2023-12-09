package macker.ltjh

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
class ControlActivity : AppCompatActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var layout: ControlActivityLayout
    companion object {
        const val MAX_NUM_JOYSTICKS = 2 // Define max number of joysticks
    }
    // Set to keep track of active pointers creating joysticks
    private val activePointers = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        layout = findViewById(R.id.control_layout)

        // Initialize BluetoothManager
        bluetoothManager = BluetoothManager(this)

        layout.setOnTouchListener { view, event ->
            view.performClick() // Perform click to ensure that the view receives click events
            handleTouchEvent(event)
            true // Always return true to indicate that the listener has consumed the event.
        }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (activePointers.size < MAX_NUM_JOYSTICKS) { // Only create new joystick if max not reached
                    val pointerIndex = event.actionIndex
                    val pointerId = event.getPointerId(pointerIndex)

                    // Check if pointerId is already associated with a joystick
                    if (!activePointers.contains(pointerId)) {
                        activePointers.add(pointerId)
                        val x = event.getX(pointerIndex)
                        val y = event.getY(pointerIndex)
                        val joystick = createJoystick(x, y).apply { tag = pointerId }
                        layout.addView(joystick)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Move joysticks
                for (i in 0 until event.pointerCount) {
                    val pointerId = event.getPointerId(i)
                    if (activePointers.contains(pointerId)) {
                        findJoystickByPointerId(pointerId)?.updatePosition(event.getX(i), event.getY(i))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // Remove joystick
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (activePointers.contains(pointerId)) {
                    val joystick = findJoystickByPointerId(pointerId)
                    joystick?.let {
                        it.resetPosition()
                        layout.removeView(it)
                    }
                    activePointers.remove(pointerId)
                }
            }
        }
    }

    private fun findJoystickByPointerId(pointerId: Int): Joystick? {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)
            if (view is Joystick && view.tag == pointerId) {
                return view
            }
        }
        return null
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
        bluetoothManager.sendMessage(message)
    }

    private fun calculateMotorControl(angle: Float, strength: Float, motorName: String): String {
        // Use angle and strength values to calculate motor control message
        // ...
        return "DUMMY" // Replace with your motor control message logic
    }
}
