package macker.ltjh

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.util.Log
import java.io.File

class MenuFragment : Fragment() {
    private var settingCallback: SettingCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val menuItems = arrayOf("Setting", "Exit", "About", "Dump debug info")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuItems)
        val listView: ListView = view.findViewById(R.id.menu_list)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> settingClickListener()
                1 -> onExitClick()
                2 -> onAboutClick()
                3 -> dumpJsonInfo()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }

    interface SettingCallback {
        fun onSettingClick()
    }

    fun setOnSettingClickListener(callback: SettingCallback) {
        this.settingCallback = callback
    }

    private fun settingClickListener() {
        Log.d("MenuFragment", "Setting clicked")
        settingCallback?.onSettingClick()
    }

    private fun onExitClick() {
        requireActivity().finish()
        Toast.makeText(requireContext(), "Exit", Toast.LENGTH_SHORT).show()
    }

    private fun onAboutClick() {
        val appInfo = AppInfo().toString()
        val osInfo = OSInfo().toString()
        val deviceInfo = DeviceInfo().toString()

        val msg = "$appInfo\n$osInfo\n$deviceInfo"

        AlertDialog.Builder(requireContext())
            .setTitle("About")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun dumpJsonInfo() {
        val appInfo = AppInfo()
        val osInfo = OSInfo()
        val deviceInfo = DeviceInfo()

        val json = """
            {
                "app": {
                    "versionName": "${appInfo.appVersionName}",
                    "versionCode": ${appInfo.appVersionCode}
                },
                "os": {
                    "androidVersion": ${osInfo.androidVersion},
                    "osVersion": "${osInfo.osVersion}",
                    "osName": "${osInfo.osName}",
                    "osArch": "${osInfo.osArch}"
                },
                "device": {
                    "deviceName": "${deviceInfo.deviceName}",
                    "deviceManufacturer": "${deviceInfo.deviceManufacturer}",
                    "deviceBrand": "${deviceInfo.deviceBrand}",
                    "deviceBoard": "${deviceInfo.deviceBoard}",
                    "deviceHardware": "${deviceInfo.deviceHardware}"
                }
            }
        """.trimIndent()

        // TODO: Hook our logs, and dump them to the file

//        write file to /sdcard/Android/data/macker.ltjh/files
        val file = File(requireContext().getExternalFilesDir(null), "debug_info.dump")
        file.writeText(json)

        Toast.makeText(requireContext(), "Dumped to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
    }
}

class OSInfo {
    val androidVersion = Build.VERSION.SDK_INT
    val osVersion: String? = System.getProperty("os.version")
    val osName: String? = System.getProperty("os.name")
    val osArch: String? = System.getProperty("os.arch")

    override fun toString(): String {
        return "Android $androidVersion\n" +
                "OS Version: $osVersion\n" +
                "OS Name: $osName\n" +
                "OS Arch: $osArch\n"
    }
}

class AppInfo {
    val appVersionName = BuildConfig.VERSION_NAME
    val appVersionCode = BuildConfig.VERSION_CODE

    override fun toString(): String {
        return "App Version: $appVersionName ($appVersionCode)\n"
    }
}

class DeviceInfo {
    val deviceName: String? = Build.MODEL
    val deviceManufacturer: String? = Build.MANUFACTURER
    val deviceBrand: String? = Build.BRAND
    val deviceBoard: String? = Build.BOARD
    val deviceHardware: String? = Build.HARDWARE

    // TODO: get device bluetooth hardware model name
    // It might need to invoke android NDK to get hardware detail info

    override fun toString(): String {
        return "Device Name: $deviceName\n" +
                "Device Manufacturer: $deviceManufacturer\n" +
                "Device Brand: $deviceBrand\n" +
                "Device Board: $deviceBoard\n" +
                "Device Hardware: $deviceHardware\n"
    }
}