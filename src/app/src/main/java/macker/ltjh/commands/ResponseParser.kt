package macker.ltjh.commands

import android.media.VolumeShaper

// Input a byte array and return a CommandBase object
class ResponseParser {
    companion object {
        fun parse(response: ByteArray): CommandBase {
            // Check the length of response
            if (response.size < 2) {
                return IllegalCommand()
            }
            // Check the command type
            val type = when (response[0].toInt()) {
                0x01 -> CommandType.OPERATION
                0x02 -> CommandType.SETTING
                0x03 -> CommandType.BEACON
                0x04 -> CommandType.STATUS
                else -> CommandType.ILLEGAL
            }
            // Check the command length
            val length = response[1].toInt()
            if (length != response.size - 2) {
                return IllegalCommand()
            }
            // TODO: Parse the command data
            return Beacon(0u, PackageType.RESPONSE)
        }
    }
}