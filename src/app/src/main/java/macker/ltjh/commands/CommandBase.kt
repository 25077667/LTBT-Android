package macker.ltjh.commands

import android.util.Log


// A base interface for all commands, witch define the data layout
interface CommandBase {
    // The command type
    val type: CommandType
    val data: Any
    // This package type is request or a response to/from the target device
    val packageType: PackageType

//    A function return the immutable byte array from data in specific format
    fun toByteArray(): ByteArray
}

// CommandTypes enumeration
enum class CommandType {
    OPERATION,
    SETTING,
    BEACON,
    STATUS,
    ILLEGAL,
}

// PackageTypes enumeration
enum class PackageType {
    REQUEST,
    RESPONSE,
    ILLEGAL,
}

// Left right boolean enumeration
enum class LeftRight {
    LEFT,
    RIGHT;

    override fun toString(): String {
        return when (this) {
            LEFT -> "left"
            RIGHT -> "right"
        }
    }
}

//An empty command for ILLEGAL command type
class IllegalCommand : CommandBase {
    override val type: CommandType
        get() = CommandType.ILLEGAL
    override val data: Any
        get() = Any()
    override val packageType: PackageType
        get() = PackageType.ILLEGAL

    override fun toByteArray(): ByteArray {
        Log.e("CommandBase", "IllegalCommand")
        return byteArrayOf()
    }
}