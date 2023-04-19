package macker.ltjh.commands

// A factor generating CommandBase objects
class CommandFactory {
    // Generate a request command by command type and data
    fun generateRequest(type: CommandType, data: Any): CommandBase {
        return when (type) {
            CommandType.OPERATION -> Operation(data as Map<String, Any>, PackageType.REQUEST)
            CommandType.SETTING -> Setting(data as Map<String, Any>, PackageType.REQUEST)
            CommandType.BEACON -> Beacon((data as? UInt ?: 0) as UInt, PackageType.REQUEST)
            CommandType.STATUS -> Status(data.toString(), PackageType.REQUEST)
            CommandType.ILLEGAL -> IllegalCommand()
        }
    }
}

// We use command parser to pass the command from the device to the app