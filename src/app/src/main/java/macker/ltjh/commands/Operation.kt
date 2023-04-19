package macker.ltjh.commands

//Implementing Operation class based on CommandBase
class Operation(private val data_: Map<String, Any>, private val packageType_: PackageType) : CommandBase {
    override val type: CommandType
        get() = CommandType.OPERATION
    override val data: Map<String, Any>
        get() = data_
    override val packageType: PackageType
        get() = packageType_

    override fun toByteArray(): ByteArray {
        return data.toString().toByteArray()
    }
}