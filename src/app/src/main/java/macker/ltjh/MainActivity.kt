package macker.ltjh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import macker.ltjh.bluetooth.BluetoothRoutine

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start BluetoothRoutine
        val intent = Intent(this, BluetoothRoutine::class.java)
        startActivity(intent)
    }
}