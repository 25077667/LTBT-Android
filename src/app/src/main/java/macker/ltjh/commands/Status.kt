package macker.ltjh.commands


class Status(private val data_: String, private val packageType_: PackageType) : CommandBase {
    override val type: CommandType
        get() = CommandType.STATUS
    override val data: String
        get() = data_
    override val packageType: PackageType
        get() = packageType_

    override fun toByteArray(): ByteArray {
        return data.toByteArray()
    }
}