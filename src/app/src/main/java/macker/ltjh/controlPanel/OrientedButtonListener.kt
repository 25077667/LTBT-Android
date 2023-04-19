package macker.ltjh.controlPanel

import android.bluetooth.BluetoothSocket
import android.view.MotionEvent
import android.view.View
import macker.ltjh.commands.CommandBase
import macker.ltjh.commands.LeftRight
import java.util.concurrent.atomic.AtomicBoolean


// Base class of oriented button listener
open class OrientedButtonListener(
    val socket: BluetoothSocket,
    val command: CommandBase,
    private val side: LeftRight
) : View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        Create a thread to keep sending the command and detached from the main thread
        Thread {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    onClick(v)
                }
                MotionEvent.ACTION_UP -> {
                    onRelease(v)
                }
            }
        }.start()
        return true
    }

     fun onClick(v: View?) {
        if (!isPressed.get()) { // If the button is not pressed
            return
        }

        // Send the command
        val outputStream = socket.outputStream
        command["side"] = side.toString()
        outputStream.write(command.toByteArray())
    }

    private fun onRelease(v: View?) {
        isPressed.set(false)
    }

//    perform click
    fun performClick() {
        isPressed.set(true)
        onClick(null)
    }

    private var isPressed = AtomicBoolean(false)
}

operator fun Any.set(s: String, value: String) {
    this[s] = value
}


open class AheadButtonListener(
    socket: BluetoothSocket,
    command: CommandBase,
    side: LeftRight
) : OrientedButtonListener(socket, command, side) {
}

open class BackwardButtonListener(
    socket: BluetoothSocket,
    command: CommandBase,
    side: LeftRight
) : OrientedButtonListener(socket, command, side) {
}

// Left ahead button listener
class LeftAheadButtonListener(
    socket: BluetoothSocket,
    command: CommandBase
) : AheadButtonListener(socket, command, LeftRight.LEFT) {
}

// Right ahead button listener
class RightAheadButtonListener(
    socket: BluetoothSocket,
    command: CommandBase
) : AheadButtonListener(socket, command, LeftRight.RIGHT) {
}

// Left backward button listener
class LeftBackwardButtonListener(
    socket: BluetoothSocket,
    command: CommandBase
) : BackwardButtonListener(socket, command, LeftRight.LEFT) {
}

// Right backward button listener
class RightBackwardButtonListener(
    socket: BluetoothSocket,
    command: CommandBase
) : BackwardButtonListener(socket, command, LeftRight.RIGHT) {
}