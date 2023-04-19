package macker.ltjh.controlPanel

import android.bluetooth.BluetoothSocket
import android.widget.SeekBar
import macker.ltjh.commands.CommandBase
import macker.ltjh.commands.LeftRight

// Seekbar listener, send data to socket when the seekbar is changed
open class SeekBarListener(
    val socket: BluetoothSocket,
    val command: CommandBase,
    private val side: LeftRight
) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // Send the command
        val outputStream = socket.outputStream
//        command set the ["side"] to string-like value: left or right
        command.data["side"] = side.toString()
        outputStream.write(command.toByteArray())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }
}

// Left seekbar listener
class LeftSeekBarListener(
    socket: BluetoothSocket,
    command: CommandBase
) : SeekBarListener(socket, command, LeftRight.LEFT) {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // Send the command
        val outputStream = socket.outputStream
        outputStream.write(command.toByteArray())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }
}

// Right seekbar listener
class RightSeekBarListener(
    socket: BluetoothSocket,
    command: CommandBase
) : SeekBarListener(socket, command, LeftRight.RIGHT) {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // Send the command
        val outputStream = socket.outputStream
        outputStream.write(command.toByteArray())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
    }
}