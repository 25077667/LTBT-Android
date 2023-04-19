package macker.ltjh.commands

// Command for beacon is just a timestamp in 32-bit unsigned integer
// Constructed by an unsigned integer
class Beacon(private val timestamp: UInt, private val packageType_: PackageType) : CommandBase {
    override val type: CommandType
        get() = CommandType.BEACON
    override val data: MutableMap<String, Any>
        get() = mutableMapOf("timestamp" to timestamp)
    override val packageType: PackageType
        get() = packageType_

    override fun toByteArray(): ByteArray {
        return byteArrayOf(
            (timestamp shr 24).toByte(),
            (timestamp shr 16).toByte(),
            (timestamp shr 8).toByte(),
            timestamp.toByte()
        )
    }
}