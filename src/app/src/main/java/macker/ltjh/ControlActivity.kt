package macker.ltjh

import android.bluetooth.BluetoothDevice
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
class ControlActivity : AppCompatActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var layout: ControlActivityLayout
    private lateinit var remoteEndpoint: RemoteEndpoint
    private lateinit var menuButton: ImageButton
    private var isMenuOpen = false
    private lateinit var menuLayout: LinearLayout
    companion object {
        const val MAX_NUM_JOYSTICKS = 2 // Define max number of joysticks
    }
    // Set to keep track of active pointers creating joysticks
    private val activePointers = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_control)
        layout = findViewById(R.id.control_layout)

        initRemotedEndpoint()
        menuButton = findViewById(R.id.menuButton)
        menuLayout = findViewById(R.id.menu_layout)
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            if (isMenuOpen) {
                closeMenu()
            } else {
                openMenu()
            }
        }

        layout.setOnTouchListener { view, event ->
            view.performClick() // Perform click to ensure that the view receives click events
            handleTouchEvent(event)
            true // Always return true to indicate that the listener has consumed the event.
        }
    }

    private fun hideSystemUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        // Hide the navigation bar.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun initRemotedEndpoint() {
        // fetch the remoteEndpoint from the MainActivity, it is serialized and passed to the
        // ControlActivity
        val remoteEndpointRaw = RemoteEndpointHolder.get()
        if (remoteEndpointRaw.getDevice() is BluetoothDevice)
            remoteEndpoint =
                RemoteEndpoint.create(remoteEndpointRaw.getDevice()) as BluetoothEndpoint
        else
            throw IllegalArgumentException("Unknown device type")

        // Initialize BluetoothManager
        // Check if remoteEndpoint is a BluetoothEndpoint and initialize BluetoothManager
        if (remoteEndpoint is BluetoothEndpoint) {
            val bluetoothDevice = remoteEndpoint.getDevice() as BluetoothDevice
            bluetoothManager = BluetoothManager(bluetoothDevice, this)
        } else {
            // Handle non-Bluetooth endpoints or throw an exception
            Log.d("ControlActivity", "RemoteEndpoint is not a BluetoothEndpoint")
            throw IllegalStateException("ControlActivity requires a BluetoothEndpoint")
        }
    }

    private fun openMenu() {
        Log.d("ControlActivity", "Menu opened")
        // Load the slide-in animation for the fragment
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_left)

        // Begin the fragment transaction
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).
            replace(R.id.fragment_container, MenuFragment()).
            commit()

        // Apply the slide-in animation to the menu layout
        val menuLayout = findViewById<LinearLayout>(R.id.menu_layout)
        menuLayout.startAnimation(slideInAnimation)
        menuLayout.visibility = View.VISIBLE
        isMenuOpen = true
    }

    private fun closeMenu() {
        Log.d("ControlActivity", "Menu closed")
        val slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_to_left)
        slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Hide the menu after the animation ends
                menuLayout.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        menuLayout.startAnimation(slideOutAnimation)
        isMenuOpen = false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isMenuOpen) {
            closeMenu()
        } else {
            super.onBackPressed()
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

    private fun sendMessageToBluetooth(message: ByteArray) {
        bluetoothManager.sendMessage(message)
    }

    private fun calculateMotorControl(angle: Float, strength: Float, motorName: String): ByteArray
    {
        val newlinePadding = '\n'.code.toByte()
        val magicByte = 0x37.toByte()

//        if angle and strength are 0, stop the motors
        if (angle == 0f && strength == 0f) {
            val checksum = (magicByte + 0x00.toByte() + 0x00.toByte()).toByte()
            // Protocol Format: [Magic Byte (1 byte)][Motor Name (1 Byte)][Speed (1 byte)][Checksum (1 byte)]
            return byteArrayOf(magicByte, 0x00.toByte(), 0x00.toByte(), checksum, newlinePadding)
        }

//        We have 4 motors (MOTOR_0, MOTOR_1, MOTOR_2, MOTOR_3) been mapped from 2 joysticks (MOTOR_L, MOTOR_R)
//        MOTOR_L is mapped to MOTOR_0 and MOTOR_1
//        MOTOR_R is mapped to MOTOR_2 and MOTOR_3

//        We convert the angle and speed to x-y coordinates with Euler's formula
//        x = r * cos(theta)
//        y = r * sin(theta)
//        where r is the speed and theta is the angle
//        We then map the x-y coordinates to the motors
//        if y > 0, MOTOR_0 (left side) will move forward
//        if y < 0, MOTOR_0 (left side) will move backward
//        if x > 0 MOTOR_1 (left side) will move forward
//        if x < 0 MOTOR_1 (left side) will move backward
//        same for MOTOR_2 and MOTOR_3 (right side)

        val isLandscape = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 0
        val x = strength * kotlin.math.cos(angle)
        val y = strength * kotlin.math.sin(angle + isLandscape * kotlin.math.PI.toFloat())

        val motor0Message = byteArrayOf(0x00.toByte(), (y * 127).toInt().toByte())
        val motor1Message = byteArrayOf(0x01.toByte(), (x * 127).toInt().toByte())
        val motor2Message = byteArrayOf(0x02.toByte(), (y * 127).toInt().toByte())
        val motor3Message = byteArrayOf(0x03.toByte(), (x * 127).toInt().toByte())

//        if left side, magicByte + motor0Message + motor1Message
//        if right side, magicByte motor2Message + motor3Message
        val concatenatedMessage = byteArrayOf(magicByte) + when (motorName) {
            "MOTOR_L" -> motor0Message + motor1Message
            "MOTOR_R" -> motor2Message + motor3Message
            else -> byteArrayOf()
        }

//        checksum is the sum of all the bytes in the message
        val checksum = concatenatedMessage.sum().toByte()
        return concatenatedMessage + checksum + newlinePadding
    }
}
